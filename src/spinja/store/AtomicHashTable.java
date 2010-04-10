// Copyright 2010, University of Twente, Formal Methods and Tools group
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package spinja.store;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import spinja.store.hash.HashAlgorithm;
import spinja.store.hash.JenkinsHash;
import spinja.store.hash.HashAlgorithm.HashGenerator;
import sun.misc.Unsafe;

public class AtomicHashTable extends StateStore {
	private static Unsafe unsafe = null;

	private static final int indexOffset, indexScale;

	private static final long nextOffset;

	static {
		try {
			Class<Unsafe> uc = Unsafe.class;
			Field field = uc.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			unsafe = (Unsafe) field.get(uc);

			indexOffset = unsafe.arrayBaseOffset(byte[][].class);
			indexScale = unsafe.arrayIndexScale(byte[][].class);
			nextOffset = unsafe.objectFieldOffset(HashTable.class.getDeclaredField("next"));
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	private final static long rawIndex(int index) {
		return indexOffset + index * indexScale;
	}

	private final class HashTable extends StateStore {
		private final byte[] tombstone = new byte[0];

		private final class DoubleSize implements Runnable {
			private final boolean run;

			public DoubleSize() {
				run = unsafe.compareAndSwapObject(HashTable.this, nextOffset, null, new HashTable(
					size + 1));
				executors.execute(this);
			}

			public void run() {
				if (run) {
					System.err.println("spinja: warning, more than 90% of the hash table is full, increasing size to -w"
										+ (size + 1) + "..");
					for (int i = 0; i < table.length; i++) {
						byte[] state = table[i];
						if (!unsafe.compareAndSwapObject(table, rawIndex(i), null, tombstone)) {
							next.addState(state);
						}
					}
					current = next;
					System.err.println("done with -w" + (size + 1));
				}
			}
		}

		private HashTable next;

		private final byte[][] table;

		private volatile int collisions;

		private volatile int stored;

		private final int size;

		private final int maxIterations;

		private final int maxStored;

		private volatile long bytes;

		private final HashAlgorithm hash;
		
		private final int mask;

		public HashTable(int size) {
			this(size, new JenkinsHash());
		}

		public HashTable(int size, HashAlgorithm hash) {
			table = new byte[1 << size][];
			stored = 0;
			collisions = 0;
			this.size = size;
			maxIterations = table.length;
			maxStored = (int) (table.length * .9);
			bytes = 56 + 4 * table.length;
			this.hash = hash;
			mask = table.length - 1;
		}

		@Override
		public int addState(byte[] state) {
			if (state == null)
				throw new NullPointerException();
			if (next == null && stored >= maxStored) {
				new DoubleSize();
			}

			HashGenerator gen = hash.hash(state);
			int index = gen.currentHash() & mask;
			int cols = 0;
			while (!unsafe.compareAndSwapObject(table, rawIndex(index), null, state)) {
				byte[] item = (byte[]) unsafe.getObjectVolatile(table, rawIndex(index));
				if (item == tombstone) {
					return next.addState(state);
				} else if (Arrays.equals(item, state)) {
					return (-index) - 1;
				} else if (cols >= maxIterations) {
					new DoubleSize();
					return next.addState(state);
				} else {
					cols++;
					index = gen.nextHash() & mask;
				}
			}

			synchronized (this) {
				stored++;
				bytes += 19 + (state.length >> 3 << 3);
				collisions += cols;
			}
			return index;
		}

		@Override
		public long getBytes() {
			return bytes;
		}

		public int getCollisions() {
			return collisions;
		}

		@Override
		public int getStored() {
			return stored;
		}
		
		public void printSummary() {
			System.out.printf("hash conflicts: %d (resolved)\n", getCollisions());
			System.out.println();
		}
	}

	private volatile HashTable current;

	private final ExecutorService executors;

	public AtomicHashTable(int size) {
		current = new HashTable(size);
		executors = Executors.newSingleThreadExecutor();
	}

	@Override
	public int addState(byte[] state) {
		return current.addState(state);
	}

	@Override
	public long getBytes() {
		return current.getBytes();
	}

	@Override
	public int getStored() {
		return current.getStored();
	}
	
	public void printSummary() {
		current.printSummary();
	}

	public int getRealStored() {
		int cnt = 0;
		for (byte[] state : current.table) {
			if (state != null) {
				cnt++;
			}
		}
		return cnt;
	}

	public void shutdown() {
		executors.shutdown();
	}

	public void awaitTermination(int length, TimeUnit unit) throws InterruptedException {
		executors.awaitTermination(length, unit);
	}
}

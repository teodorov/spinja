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

import java.util.Arrays;

import spinja.store.hash.HashAlgorithm;
import spinja.store.hash.JenkinsHash;
import spinja.store.hash.HashAlgorithm.HashGenerator;

/**
 * The ProbingHashTable is a hash table that using probing to solve collisions. This method is very
 * fast, but the main disadvantage is that is will become much slower when the table is filled up by
 * more than 80%. By the time the table is more than 90% full, or there can not be found an empty
 * place after many iteration, the size of this table will be doubled.
 * 
 * @author Marc de Jonge
 */
public final class ProbingHashTable extends StateStore {
	/*
	 * The table is a simple array of states
	 */
	private byte[][] table;

	private int collisions;

	private int stored;

	private int size;

	private int mask;

	private int maxIterations;//, maxStored;

	private long bytes;

	private HashAlgorithm hash;
	
	private ProbingHashTable overflow;

	/**
	 * Constructor of ProbingHashTable.
	 * 
	 * @param size
	 *            The initial size of the {@link ProbingHashTable}.
	 */
	public ProbingHashTable(final int size) {
		this(size, new JenkinsHash());
	}

	/**
	 * Constructor of ProbingHashTable. The size that is given is the 2log of the number of entries.
	 * 
	 * @param size
	 *            The size of the table. The table will get 2^size entries.
	 * @param hash
	 *            The hashfunction that is to be used.
	 */
	public ProbingHashTable(final int size, final HashAlgorithm hash) {
		table = new byte[1 << size][];
		stored = 0;
		collisions = 0;
		this.size = size;

		maxIterations = table.length >>> 7;

		//maxStored = (int) (table.length * .9);

		bytes = 56 + 4 * table.length;

		this.hash = hash;
		this.mask = table.length - 1;
		
		overflow = null; // no overflow table yet
	}

//	private void doubleTableSize() {
//		int newSize = size + 1;
//		int newLength = 1 << newSize;
//		System.err.print("spinja: warning, more than 90% of the hash table is full, increasing size to -w"
//							+ newSize + "..");
//		byte[][] newTable = new byte[newLength][];
//		int newMask = newLength - 1;
//		HashAlgorithm newHash = null;
//		try {
//			newHash = hash.getClass().newInstance();
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(-1);
//		}
//
//		int newCollisions = 0;
//		bytes += 4 * (newLength - table.length);
//
//		for (byte[] state : table) {
//			if (state != null) {
//				// Add to new table
//				HashGenerator gen = newHash.hash(state);
//				int index = gen.currentHash() & newMask;
//				while (newTable[index] != null) {
//					newCollisions++;
//					index = gen.nextHash() & newMask;
//				}
//				newTable[index] = state;
//			}
//		}
//
//		this.table = newTable;
//		this.hash = newHash;
//		this.collisions = newCollisions;
//		this.size = newSize;
//		this.mask = newMask;
//		maxStored = (int) (newLength * .9);
//		maxIterations = newLength;
//		System.err.println("done");
//	}

	/**
	 * @see spinja.store.StateStore#addState(byte[])
	 */
	@Override
	public final synchronized int addState(final byte[] state) {
//		if (stored >= maxStored) {
//			doubleTableSize();
//		}

		// Find the stored state
		return addState(state, hash.hash(state));
	}
	
	private synchronized int addState(final byte[] state, final HashGenerator gen) {
		int index = gen.currentHash() & mask;
		byte[] storedState = table[index];
		int cols = 0;
		while (storedState != null) {
			if (Arrays.equals(storedState, state)) {
				return (-index) - 1;
			}
			cols++;
			if (cols > maxIterations) {
				if(overflow == null) {
					System.out.println("spinja error: Hashtable too small, adding overflow table.");
					overflow = new ProbingHashTable(this.size, hash);
				}
				return overflow.addState(state, gen);
			} else {
				// Find the next stored state
				index = gen.nextHash() & mask;
			}
			storedState = table[index];
		}

		table[index] = state;
		stored++;
		bytes += 19 + state.length >> 3 << 3;
		collisions += cols;

		return index;
	}

	/**
	 * @see spinja.store.StateStore#getBytes()
	 */
	@Override
	public final synchronized long getBytes() {
		return bytes + (overflow == null ? 0 : overflow.getBytes());
	}

	public void printSummary() {
		System.out.printf("hash conflicts: %d (resolved)\n", collisions + (overflow == null ? 0 : overflow.collisions));
		System.out.println();
	}

	/**
	 * @see spinja.store.StateStore#getStored()
	 */
	@Override
	public final synchronized int getStored() {
		return stored;
	}
}

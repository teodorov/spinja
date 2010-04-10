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

import java.lang.reflect.Array;
import java.util.Arrays;

import spinja.store.hash.HashAlgorithm;
import spinja.store.hash.HsiehHash;
import spinja.store.hash.JenkinsHash;
import spinja.store.hash.HashAlgorithm.HashGenerator;

/**
 * The BitstateHashStore implements an store that can possibly return false negatives. This means
 * that sometimes a state is discarded while it was not seen before. The algorithm that is used here
 * comes from Spin (see http://spinroot.com/spin/Doc/WL93.pdf).
 * 
 * @author Marc de Jonge
 */
public class HashCompactStore extends StateStore {
	private abstract class Table extends StateStore {
		protected int stored;

		protected final int mask;

		protected long bytes;

		protected long conflicts;

		public Table(long bytes, int mask) {
			this.bytes = bytes + 12;
			this.mask = mask;
		}

		@Override
		public long getBytes() {
			return bytes;
		}

		@Override
		public final int getStored() {
			return stored;
		}

		public long getConflicts() {
			return conflicts;
		}
	}

	private class HashCompactStore32 extends Table {
		private final int[] table;

		public HashCompactStore32(int entries) {
			super(4l * (1 << entries) + 4, (1 << entries) - 1);
			table = new int[1 << entries];
		}

		@Override
		public int addState(byte[] state) {
			int i = 0;
			int hash = compressingHash.hash(state, i++);
			while (hash == 0) {
				hash = compressingHash.hash(state, i++);
			}

			HashGenerator gen = probingHash.hash(state);
			int index = gen.currentHash() & mask;
			while (table[index] != 0) {
				if (table[index] == hash) {
					// Duplicate state
					return -(hash & 0x7fffffff) - 1;
				}
				index = gen.nextHash() & mask;
				conflicts++;
			}
			table[index] = hash;
			stored++;
			return hash & 0x7fffffff;
		}
	}

	private class HashCompactStore40 extends Table {
		private final int[] table;

		private final byte[] table2;

		public HashCompactStore40(int entries) {
			super(5l * (1 << entries) + 8, (1 << entries) - 1);
			table = new int[1 << entries];
			table2 = new byte[1 << entries];
		}

		@Override
		public int addState(byte[] state) {
			long i = 0;
			long hash = compressingHash.hash(state, i++);
			while ((hash & 0xffffffffffl) == 0) {
				hash = compressingHash.hash(state, i++);
			}
			int hash1 = (int) hash;
			byte hash2 = (byte) (hash >>> 32);

			HashGenerator gen = probingHash.hash(state);
			int index = gen.currentHash() & mask;
			while (table[index] != 0 || table2[index] != 0) {
				if (table[index] == hash1 && table2[index] == hash2) {
					// Duplicate state
					return -(hash1 & 0x7fffffff) - 1;
				}
				index = gen.nextHash() & mask;
				conflicts++;
			}
			table[index] = hash1;
			table2[index] = hash2;
			stored++;
			return hash1 & 0x7fffffff;
		}
	}

	private class HashCompactStore48 extends Table {
		private final int[] table;

		private final short[] table2;

		public HashCompactStore48(int entries) {
			super(6l * (1 << entries) + 8, (1 << entries) - 1);
			table = new int[1 << entries];
			table2 = new short[1 << entries];
		}

		@Override
		public int addState(byte[] state) {
			long i = 0;
			long hash = compressingHash.hash(state, i++);
			while ((hash & 0xffffffffffl) == 0) {
				hash = compressingHash.hash(state, i++);
			}
			int hash1 = (int) hash;
			short hash2 = (short) (hash >>> 32);

			HashGenerator gen = probingHash.hash(state);
			int index = gen.currentHash() & mask;
			while (table[index] != 0 || table2[index] != 0) {
				if (table[index] == hash1 && table2[index] == hash2) {
					// Duplicate state
					return -(hash1 & 0x7fffffff) - 1;
				}
				index = gen.nextHash() & mask;
				conflicts++;
			}
			table[index] = hash1;
			table2[index] = hash2;
			stored++;
			return hash1 & 0x7fffffff;
		}
	}

	private class HashCompactStore56 extends Table {
		private final int[] table;

		private final short[] table2;

		private final byte[] table3;

		public HashCompactStore56(int entries) {
			super(7l * (1 << entries) + 12, (1 << entries) - 1);
			table = new int[1 << entries];
			table2 = new short[1 << entries];
			table3 = new byte[1 << entries];
		}

		@Override
		public int addState(byte[] state) {
			long i = 0;
			long hash = compressingHash.hash(state, i++);
			while ((hash & 0xffffffffffl) == 0) {
				hash = compressingHash.hash(state, i++);
			}
			int hash1 = (int) hash;
			short hash2 = (short) (hash >>> 32);
			byte hash3 = (byte) (hash >>> 48);

			HashGenerator gen = probingHash.hash(state);
			int index = gen.currentHash() & mask;
			while (table[index] != 0 || table2[index] != 0 || table3[index] != 0) {
				if (table[index] == hash1 && table2[index] == hash2 && table3[index] == hash3) {
					// Duplicate state
					return -(hash1 & 0x7fffffff) - 1;
				}
				index = gen.nextHash() & mask;
				conflicts++;
			}
			table[index] = hash1;
			table2[index] = hash2;
			table3[index] = hash3;
			stored++;
			return hash1 & 0x7fffffff;
		}
	}

	private class HashCompactStore64 extends Table {
		private final long[] table;

		public HashCompactStore64(int entries) {
			super(8l * (1 << entries) + 4, (1 << entries) - 1);
			table = new long[1 << entries];
		}

		@Override
		public int addState(byte[] state) {
			long i = 0;
			long hash = compressingHash.hash(state, i++);
			while (hash == 0) {
				hash = compressingHash.hash(state, i++);
			}

			HashGenerator gen = probingHash.hash(state);
			int index = gen.currentHash() & mask;
			while (table[index] != 0) {
				if (table[index] == hash) {
					// Duplicate state
					return -((int) hash & 0x7fffffff) - 1;
				}
				index = gen.nextHash() & mask;
				conflicts++;
			}
			table[index] = hash;
			stored++;
			return (int) hash & 0x7fffffff;
		}
	}

	private class HashCompactStoreAny extends Table {
		private final byte[][] table;

		private final int bytesPerEntry;

		private int filled = 0;

		public HashCompactStoreAny(int entries, int bytesPerEntry) {
			super(4 * (1 << entries) + 12, (1 << entries) - 1);
			this.bytesPerEntry = bytesPerEntry;
			table = new byte[1 << entries][];
		}

		@Override
		public int addState(byte[] state) {
			byte[] hash = new byte[bytesPerEntry];
			HashGenerator compGen = compressingHash.hash(state);
			int i = 0, curr = compGen.currentHash();
			while (i < bytesPerEntry - 3) {
				hash[i++] = (byte) curr;
				hash[i++] = (byte) (curr >> 8);
				hash[i++] = (byte) (curr >> 16);
				hash[i++] = (byte) (curr >> 24);
				curr = compGen.nextHash();
			}
			switch (bytesPerEntry - i) {
				case 3:
					hash[i++] = (byte) curr;
				case 2:
					hash[i++] = (byte) (curr >> 8);
				case 1:
					hash[i++] = (byte) (curr >> 16);
			}

			HashGenerator gen = probingHash.hash(state);
			int index = gen.currentHash() & mask;
			while (table[index] != null) {
				if (Arrays.equals(table[index], hash)) {
					// Duplicate state
					return -(gen.currentHash() & 0x7fffffff) - 1;
				}
				index = gen.nextHash() & mask;
				conflicts++;
			}
			table[index] = hash;
			stored++;
			filled++;
			return gen.currentHash() & 0x7fffffff;
		}

		@Override
		public long getBytes() {
			return super.getBytes() + (bytesPerEntry + 16) * filled;
		}
	}

	final HashAlgorithm probingHash, compressingHash;

	private Table table;

	public HashCompactStore(final int entries, final int bitSize) {
		this(entries, bitSize, new JenkinsHash(), new HsiehHash());
	}

	/**
	 * Constructor of HashCompactStore. 
	 * The size that is given is the 2log of the number of entries.
	 * 
	 * @param bitSize
	 *            The size of the table. The table will get 2^size entries.
	 */
	public HashCompactStore(final int entries, final int bitSize, 
							HashAlgorithm compressingHash, HashAlgorithm probingHash) {
		if (entries < 0 || bitSize < 0) {
			throw new IllegalArgumentException(
				"Both the bitSize as well as the nr of entries must be positive for the hash compaction algorithm.");
		}
		if ((bitSize & 7) != 0) {
			throw new IllegalArgumentException("Only multiple of 8 bits are supported as bitsize.");
		}

		switch (bitSize) {
			case 32:
				table = new HashCompactStore32(entries);
				break;
			case 40:
				table = new HashCompactStore40(entries);
				break;
			case 48:
				table = new HashCompactStore48(entries);
				break;
			case 56:
				table = new HashCompactStore56(entries);
				break;
			case 64:
				table = new HashCompactStore64(entries);
				break;
			default:
				table = new HashCompactStoreAny(entries, bitSize >>> 3);
				break;
		}

		this.probingHash = probingHash;
		this.compressingHash = compressingHash;
	}

	/**
	 * @see spinja.store.StateStore#addState(byte[])
	 */
	@Override
	public int addState(final byte[] state) {
		return table.addState(state);
	}

	/**
	 * @see spinja.store.StateStore#getBytes()
	 */
	@Override
	public long getBytes() {
		return table.getBytes() + 12;
	}

	/**
	 * @see spinja.store.StateStore#getStored()
	 */
	@Override
	public int getStored() {
		return table.getStored();
	}
}

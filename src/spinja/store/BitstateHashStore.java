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

import spinja.store.hash.HashAlgorithm;
import spinja.store.hash.JenkinsHash;
import spinja.store.hash.HashAlgorithm.HashGenerator;
import spinja.store.hash.HashAlgorithm.LongHashGenerator;

/**
 * The BitstateHashStore implements an store that can possibly return false negatives. This means
 * that sometimes a state is discarded while it was not seen before. The algorithm that is used here
 * comes from 3Spin (see http://www.cc.gatech.edu/~manolios/research/spin-3spin.html).
 * 
 * @author Marc de Jonge
 */
public class BitstateHashStore extends StateStore {
	private final long[] table;

	private final int mask;

	private int stored;

	private final int fold;

	private final HashAlgorithm hash;

	private final int[] indices;

	private final long[] keys;

	/**
	 * Constructor of BitstateHashStore. The size that is given is the 2log of the number of
	 * entries.
	 * 
	 * @param size
	 *            The size of the table. The table will get 2^size entries.
	 * @param fold
	 *            The number of hash functions that should be used by this
	 */
	public BitstateHashStore(final int size, final int fold) {
		this(size, fold, new JenkinsHash());
	}

	/**
	 * Constructor of BitstateHashStore. The size that is given is the 2log of the number of
	 * entries.
	 * 
	 * @param size
	 *            The size of the table. The table will get 2^size entries.
	 * @param fold
	 *            The number of hash functions that should be used by this
	 * @param hash
	 *            The hash algorithm that is to be used by this state store
	 */
	public BitstateHashStore(final int size, final int fold, HashAlgorithm hash) {
		if (fold <= 0) {
			throw new IllegalArgumentException("Fold should be at least 1");
		}

		if (size > 37) {
			throw new IllegalArgumentException("The size can not be larger than 2^37 bits");
		} else if (size <= 0) {
			throw new IllegalArgumentException("The size must be a positive number");
		}

		// The table is one of longs, of which each one contains 2^6=64 bits
		table = new long[1 << Math.max(1, size - 6)];
		System.out.println("Size of bitstate table: " + table.length);
		mask = table.length - 1;

		stored = 0;
		this.fold = fold;
		this.indices = new int[fold];
		this.keys = new long[fold];
		this.hash = hash;
	}

	/**
	 * @see spinja.store.StateStore#addState(byte[])
	 */
	@Override
	public int addState(final byte[] state) {
		// Calculate the table indices
		int result = 0;
		if (table.length > (1 << 26)) {
			LongHashGenerator gen = hash.bitstateHash(state);
			for (int i = 0; i < fold; i++) {
				long hash = gen.nextHash();
				if (result == 0) {
					result = (int) hash & 0x7fffffff;
				}

				indices[i] = ((int) (hash >>> 6)) & mask;
				keys[i] = 1L << hash;
			}
		} else {
			HashGenerator gen = hash.hash(state);
			for (int i = 0; i < fold; i++) {
				int hash = gen.nextHash();
				if (result == 0) {
					result = hash & 0x7fffffff;
				}

				indices[i] = (hash >>> 6) & mask;
				keys[i] = 1L << hash;
			}
		}

		boolean found = true;
		// First check if the state was already here
		for (int i = 0; i < fold; i++) {
			if ((table[indices[i]] & keys[i]) == 0) {
				// If any one of the bit is not set, the state was not stored
				found = false;
				break;
			}
		}

		if (found) {
			return -result - 1;
		} else {
			// Not found, so set all the bits
			for (int i = 0; i < fold; i++) {
				table[indices[i]] |= keys[i];
			}
			stored++;

			return result;
		}
	}

	/**
	 * @see spinja.store.StateStore#getBytes()
	 */
	@Override
	public long getBytes() {
		return 24 + table.length * 8;
	}

	/**
	 * @see spinja.store.StateStore#getStored()
	 */
	@Override
	public int getStored() {
		return stored;
	}

	/**
	 * @see spinja.store.StateStore#printSummary()
	 */
	@Override
	public void printSummary() {
		System.out.printf("hash factor: %.4f%n", (table.length * 64.0) / (double) stored);
		System.out.printf("bits set per state: %d (-k%<d)%n", fold);
	}
}
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

import java.util.ArrayList;
import java.util.Arrays;

import spinja.store.hash.HashAlgorithm;
import spinja.store.hash.JenkinsHash;

/**
 * This basic hash table uses chaining by arrays. This uses a technique that is similar to the
 * {@link ArrayList} implementation.
 * 
 * @author Marc de Jonge
 */
public final class HashTable extends StateStore {
	/*
	 * The table is an array of arrays of states.
	 */
	private final byte[][][] table;

	private int collisions;

	private int stored;

	private long bytes;

	private final HashAlgorithm hash;

	private final int mask;

	public HashTable(final int size) {
		this(size, new JenkinsHash());
	}

	/**
	 * Constructor of HashTable. The size that is given is the 2log of the number of entries.
	 * 
	 * @param size
	 *            The size of the table. The table will get 2^size entries.
	 * @param hash
	 *            The hash algorithm that is to be used by this {@link HashTable}
	 */
	public HashTable(final int size, final HashAlgorithm hash) {
		if ((size <= 0) || (size > 31)) {
			throw new IllegalArgumentException("The size should always be between 1 and 31");
		}

		// Create the main table
		table = new byte[1 << size][][];

		collisions = 0;
		stored = 0;

		// Use the default hashing algorithm
		this.hash = hash;
		this.mask = table.length - 1;

		// The number of bytes used at this moment is 40 bytes (for this object) plus 4 bytes per
		// table entry
		bytes = 40 + 4 * table.length;
	}

	/**
	 * @see spinja.store.StateStore#addState(byte[])
	 */
	@Override
	public int addState(final byte[] state) {
		// Calculate the index where the state should be stored
		final int index = hash.hash(state, 0) & mask;
		// Retrieve the bucket to put the state in
		byte[][] bucket = table[index];

		if (bucket == null) {
			// The bucket did not exist yet, so the state can not be in this store
			// First create the bucket (with size 0)
			table[index] = new byte[][] {
				state
			};
			// Add the amount of memory used (16 bytes for the array, and 20 + the number of bytes
			// that are in the state that is stored).
			bytes += 16 + (20 + state.length >> 3 << 3);
			stored++;
		} else {
			// Remember the number of collisions that we detected until now
			int cols = 0;

			// Check all the states in the bucket
			for (cols = 0; (cols < bucket.length) && (bucket[cols] != null); cols++) {
				if (Arrays.equals(bucket[cols], state)) {
					// We have found the state, so return false
					return (-index) - 1;
				}
			}

			// The state is not in the bucket yet, so try to add it
			if (cols >= bucket.length) {
				// Double the size of this list, because the array is too small to add another
				final byte[][] temp = new byte[bucket.length << 1][];
				System.arraycopy(bucket, 0, temp, 0, bucket.length);
				bucket = temp;
				table[index] = temp;
				// Add the number of bytes that are used now
				bytes += 4 * bucket.length;
			}

			// Add the state to the bucket
			bucket[cols] = state;
			collisions += cols;
			bytes += 20 + state.length >> 3 << 3;
			stored++;
		}

		return index;
	}

	/**
	 * @see spinja.store.StateStore#getBytes()
	 */
	@Override
	public long getBytes() {
		return bytes;
	}

	public void printSummary() {
		System.out.printf("hash conflicts: %d (resolved)\n", collisions);
		System.out.println();
	}

	/**
	 * @see spinja.store.StateStore#getStored()
	 */
	@Override
	public int getStored() {
		return stored;
	}
}

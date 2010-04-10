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
import java.util.LinkedList;

import spinja.store.hash.HashAlgorithm;

/**
 * The LinkedHashTable is a hash table that uses chaining by using a technique that is similar to
 * the {@link LinkedList}.
 * 
 * @author Marc de Jonge
 */
public class LinkedHashTable extends StateStore {
	private class Bucket {
		Bucket next;

		final byte[] content;

		Bucket(final byte[] content) {
			this.content = content;
			next = null;
		}
	}

	// The table is simply an array of buckets
	private final Bucket[] table;

	private int collisions;

	private int stored;

	private long bytes;

	private final HashAlgorithm hash;

	private final int mask;

	/**
	 * Constructor of LinkedHashTable. The size that is given is the 2log of the number of entries.
	 * 
	 * @param size
	 *            The size of the table. The table will get 2^size entries.
	 * @param hash
	 *            The hash function that is to be used.
	 */
	public LinkedHashTable(final int size, final HashAlgorithm hash) {
		if ((size <= 0) || (size > 31)) {
			throw new IllegalArgumentException("The size should always be between 1 and 31");
		}

		// Create the table
		table = new Bucket[1 << size];

		collisions = 0;
		stored = 0;

		// Use the default hashing algorithm
		this.hash = hash;
		mask = (1 << size) - 1;

		// The number of bytes used at this moment is 40 bytes (for this object) plus 4 bytes per
		// table entry
		bytes = 40 + 4 * table.length;
	}

	/**
	 * @see spinja.store.StateStore#addState(byte[])
	 */
	@Override
	public int addState(final byte[] state) {
		// Calculate the index of the bucket where it should be stored
		final int index = hash.hash(state, 0) & mask;
		// Retrieve the bucket on that location
		Bucket bucket = table[index];
		if (bucket == null) {
			// There was no bucket yet, so create it
			table[index] = new Bucket(state);
			// Add 16 bytes for the Bucket object, and the number of state bytes plus another 16
			bytes += 32 + state.length >> 3 << 3;
			stored++;
		} else {
			int cols = 0;
			while (bucket.next != null) {
				// Try to see if the current bucket contains the state
				if (Arrays.equals(bucket.content, state)) {
					return (-index) - 1;
				}
				cols++;
				bucket = bucket.next;
			}
			if (Arrays.equals(bucket.content, state)) {
				return (-index) - 1;
			}

			// State not found, add an extra bucket
			bucket.next = new Bucket(state);
			// Add the extra bytes
			bytes += 32 + state.length >> 3 << 3;
			collisions += cols;
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

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

package spinja.search;

import java.util.Arrays;

import spinja.exceptions.SpinJaException;
import spinja.model.Transition;

public class Stack implements SearchableStack {
	//private final HashAlgorithm hash;

	private int top;

	private final int size;

	private final Transition[] lastTransition;

	private final byte[][] encoded;
	
	private final int[] identifiers;
	
	private final int[] hashTable;
	
	private final int hashMask;
	
	private long bytes;

	public Stack(int size) {
		top = -1;
		this.size = size;
		lastTransition = new Transition[size];
		encoded = new byte[size][];
		identifiers = new int[size];

		int hashSize = 0;
		while (size > 0) {
			size >>= 1;
			hashSize++;
		}
		hashSize = 1 << hashSize;
		hashTable = new int[hashSize];
		for(int i= 0; i < hashSize; i++) {
			hashTable[i] = Integer.MIN_VALUE;
		}
		hashMask = hashSize - 1;

		bytes = 128 + 12 * size + 4 * hashSize;

		//hash = HashAlgorithm.getDefaultAlgorithm();
	}

	public void clearStack() {
		while (top >= 0) {
			pop();
		}
	}
	
	public boolean containsState(final byte[] state) {
		for (int i = top; i >= 0; i--) {
			if (Arrays.equals(encoded[i], state)) {
				return true;
			}
		}
		return false;		
	}

	public boolean containsState(final byte[] state, int identifier) {
		int index = identifier & hashMask;
		int incr = identifier | 1;
		int value = 0;
		while((value = hashTable[index]) >= 0) {
			if(Arrays.equals(encoded[value], state)) {
				return true;
			}
			index = (index + incr) & hashMask;
		}
		return false;
	}

	public long getBytes() {
		return bytes;
	}

	public Transition getLastTransition() {
		return lastTransition[top];
	}

	public int getSize() {
		return top + 1;
	}

	public byte[] getTop() {
		return encoded[top];
	}

	public Transition getTransition(final int depth) {
		return lastTransition[depth];
	}

	public byte[] getState(final int depth) {
		return encoded[depth];
	}

	public byte[] pop() {
		if (top == -1) {
			throw new IndexOutOfBoundsException();
		}

		bytes -= 19 + encoded[top].length >> 3 << 3;
		final byte[] res = encoded[top];
		encoded[top] = null;
		lastTransition[top] = null;
		
		int index = identifiers[top] & hashMask;
		int incr = identifiers[top] | 1;
		while(hashTable[index] != top) {
			index = (index + incr) & hashMask;
		}
		hashTable[index] = Integer.MIN_VALUE;

//		// Remove from hash table
//		int[] bucket = hashTable[hash.hash(res, 0) & hashMask];
//		for (int i = 0; i < bucket.length; i++) {
//			if (bucket[i] == top) {
//				bucket[i] = -1;
//				break;
//			}
//		}

		top--;
		return res;
	}

	public boolean push(final byte[] state, int identifier) {
		if (top == size - 1) {
			return false;
		}
		top++;
		this.encoded[top] = state;
		this.identifiers[top] = identifier;
		bytes += 19 + state.length >> 3 << 3;
				
		int index = identifier & hashMask;
		int incr = identifier | 1;
		while(hashTable[index] >= 0) {
			index = (index + incr) & hashMask;
		}
		hashTable[index] = top;

//		// Add to hash table
//		int index = hash.hash(state, 0) & hashMask;
//		if (hashTable[index] == null) {
//			hashTable[index] = new int[] {
//				top
//			};
//			bytes += 8;
//		} else {
//			int i = 0;
//			int[] bucket = hashTable[index];
//			while (true) {
//				if (i >= bucket.length) {
//					int[] temp = new int[bucket.length * 2];
//					System.arraycopy(bucket, 0, temp, 0, bucket.length);
//					temp[bucket.length] = top;
//					for (int j = bucket.length + 1; j < temp.length; j++) {
//						temp[j] = -1;
//					}
//					hashTable[index] = bucket = temp;
//					bytes += 4 * bucket.length;
//					break;
//				} else if (hashTable[index][i] < 0) {
//					hashTable[index][i] = top;
//					break;
//				} else {
//					i++;
//				}
//			}
//		}

		return true;
	}

	public void takeTransition(final Transition transition) throws SpinJaException {
		lastTransition[top] = transition;
		transition.take();
	}
}

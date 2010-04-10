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

/**
 * The NoStore is a store that stores no real states. It therefore will always give false positives.
 * 
 * @author Marc de Jonge
 */
public class NoStore extends StateStore {
	private int count = 0;
	
	private HashAlgorithm hash = HashAlgorithm.getDefaultAlgorithm();

	/**
	 * Always simple returns true and does not really store the state anywhere.
	 * 
	 * @see spinja.store.StateStore#addState(byte[])
	 */
	@Override
	public int addState(final byte[] state) {
		count++;
		return hash.hash(state, 0) & 0x7fffffff;
	}

	/**
	 * @see spinja.store.StateStore#getBytes()
	 */
	@Override
	public long getBytes() {
		return 24;
	}
	
	public void printSummary() {
		System.out.println();
	}

	/**
	 * @see spinja.store.StateStore#getStored()
	 */
	@Override
	public int getStored() {
		return count;
	}
}

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

/**
 * A StateStore is used to store state, which are encoded as byte arrays. The goal of the StateStore
 * is to be able to tell if a state was already stored. This answer can be retrieved when adding a
 * state through the {@link #addState(byte[])} method.
 * 
 * The StateStore does not have to be perfect, in the sense that false positives are allowed. When
 * this state thinks that a state was not seen yet, when it has in fact been seen, it may return a
 * false positive. This makes the search less efficient, but not wrong. False negatives though, are
 * not recommended, because then the search may not be complete.
 * 
 * @author Marc de Jonge
 */
public abstract class StateStore {
	/**
	 * Tries to add the given state to the store. When a state that is equal to the current one was
	 * already stored, false is returned. Otherwise true should be returned.
	 * 
	 * @param state
	 *            The state that should be added to this state.
	 * @return True when the state was added or false when it was already stored.
	 */
	public abstract int addState(byte[] state);

	/**
	 * Returns the number of bytes of internal memory that is used by this StateStore. This may is
	 * an (optimistic) estimation, because of the way Java handles memory and garbage collection.
	 * 
	 * @return The number of bytes of internal memory that is used by this StateStore.
	 */
	public abstract long getBytes();

	// /**
	// * Returns the number of collisions. The standard implementation always returns zero, which
	// may
	// * be correct for some implementations.
	// *
	// * @return The number of collisions that have occurred while storing the states.
	// */
	// public int getCollisions() {
	// return 0;
	// }

	/**
	 * Returns the number of states that are currently stored in this state.
	 * 
	 * @return The number of states that are currently stored in this state.
	 */
	public abstract int getStored();

	/**
	 * Prints a summary of the state. By default it prints nothing.
	 */
	public void printSummary() {
	}
}

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

package spinja.model;

import spinja.search.NestedDepthFirstSearch;

/**
 * This interface describes all the conditions that can be checked. All conditions are simply
 * integers.
 * 
 * @author Marc de Jonge
 */
public interface Condition {
	/**
	 * This {@link ConditionCounter} can be used for generating unique identifying numbers for each
	 * of the conditions.
	 */
	public static final ConditionCounter counter = new ConditionCounter();

	/**
	 * This is a simple synchronized counter, that returns a unique number each time. That is, until
	 * the function is called more than 2^32 times, which is highly unlikely.
	 * 
	 * @author Marc de Jonge
	 */
	public static final class ConditionCounter {
		private int cnt = 5;

		/**
		 * @return The next unique identifier.
		 */
		public synchronized int nextCondition() {
			return cnt++;
		}
	}

	/**
	 * The END_STATE condition holds when the Model is currently in an ending state. In an ending
	 * state there are no more transitions that can be executed, but it is a normal ending and not a
	 * deadlock.
	 */
	public static final int END_STATE = 1;

	/**
	 * The ACCEPT_STATE condition holds when the Model is currently in an acceptance state. The
	 * {@link NestedDepthFirstSearch} is an algorithm that can search for acceptance cycles.
	 */
	public static final int ACCEPT_STATE = 2;

	/**
	 * The PROGRESS_STATE condition holds when the Model is currently in an progress state. At the
	 * moment there is no algorithm that uses this state, but adding a non-progress cycle search is
	 * possible.
	 */
	public static final int PROGRESS_STATE = 3;

	/**
	 * The SHOULD_STORE_STATE conditions holds when the current state of the model may be stored.
	 */
	public static final int SHOULD_STORE_STATE = 4;
}

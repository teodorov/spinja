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

import spinja.search.Algoritm;

/**
 * The {@link Model} in SpinJa is the base class which all models should implement. When that is done
 * correctly, it can be checked using one of the {@link Algoritm}s.
 * 
 * @see spinja.model.ObservableModel
 * @see spinja.model.Storable
 * 
 * @author Marc de Jonge
 * @param <T>
 *            The type of transition that will be returned by this model.
 */
public abstract class Model<T extends Transition> extends ObservableModel implements Storable {

	/**
	 * Returns the name of the model. This name is used to generate the filename of the trail-file.
	 * Therefore it should be a simple name and not be too long.
	 * 
	 * The default implementation returns the name of the class.
	 * 
	 * @return The name of this model.
	 */
	public String getName() {
		return getClass().getName();
	}

	/**
	 * Checks if in the current state of the model a condition holds. This condition is indicated by
	 * a integer value. These values can be retrieved through the {@link Condition} interface. By
	 * default it returns true when the SHOULD_STORE_STATE is requested, or false otherwise.
	 * 
	 * @param condition
	 *            The number of the condition that we want to check
	 * @return True when the condition holds, false otherwise
	 */
	public boolean conditionHolds(int condition) {
		return condition == Condition.SHOULD_STORE_STATE;
	}

	/**
	 * Returns the next transition that can be taken from the current state. It needs the last
	 * transition that has been taken from this state as a parameter. When the last parameter is
	 * null, this function should return the first transition. When it returns null, there are no
	 * more transitions to take.
	 * 
	 * This method implies that there is an order defined between the transitions, which may or may
	 * not be necessary for the implementation of this model.
	 * 
	 * @see spinja.model.Transition
	 * 
	 * @param last
	 *            The previous transition that was returned by this function the last time it was
	 *            called while in the current state. How this method will react when it is called
	 *            with a transition from an other state is undefined. When we look for the first
	 *            transition that can be called, provide null as a parameter.
	 * @return The next transition that can be executed, or null if there are no more transitions
	 *         available.
	 */
	public abstract T nextTransition(T last);

	/**
	 * Prints some information about the current state of the model. This method is called when a
	 * simulation is performed.
	 * 
	 * The default implementation only returns the name of the model.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Each model should implement this model, to be able to use Concurrent search algorithms.
	 * @see java.lang.Object#clone()
	 */
	@Override
	public abstract Model<T> clone();
}

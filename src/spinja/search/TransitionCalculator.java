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

import spinja.model.Model;
import spinja.model.Transition;

/**
 * The {@link TransitionCalculator} is a class that generates transitions from a given model.
 * @author Marc de Jonge
 * @param <M>
 *            The type of model.
 * @param <T>
 *            The type of transition.
 */
public class TransitionCalculator<M extends Model<T>, T extends Transition> {
	/**
	 * Calculates the next {@link Transition} that is executable from the current state of the
	 * model, using the last transition as a base. By default it simply calls the
	 * {@link Model#nextTransition(Transition)} method.
	 * @param model
	 *            The model which is used for calculating the next transition.
	 * @param last
	 *            The transition that was returned the last time this method was called in the
	 *            current state of the model. When last is <code>null</code>, this method will
	 *            return the first transition.
	 * @return The next transition that is calculated or <code>null</code> when there is none.
	 */
	public T next(final M model, final T last) {
		return model.nextTransition(last);
	}

	/**
	 * Indicates to this algorithm that while executing the last transition, the created state was
	 * already visited before. By default this method does not use this information.
	 * @param model
	 *            The model.
	 * @param last
	 *            The transition that was executed just before finding this state.
	 * @param state
	 *            The encoded state.
	 * @param identifier
	 *            A unique identifier for this state, that can be used to speed up storage.
	 * @param stack
	 *            The {@link SearchableStack} that can be used to see if the state also was on the
	 *            stack.
	 */
	public void duplicateState(final M model, final T last, final byte[] state,
		final int identifier, final SearchableStack stack) {
		// Do nothing by default
	}
}

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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import spinja.model.Model;
import spinja.model.Transition;

/**
 * The {@link RandomSimulation} can be used to execute a model, while choosing the
 * @author Marc de Jonge
 */
public class RandomSimulation<M extends Model<T>, T extends Transition> extends Simulation<M, T> {

	private final Random rand;

	private final List<T> enabled = new ArrayList<T>();

	/**
	 * Constructor of RandomSimulation.
	 * 
	 * @param model
	 *            The {@link Model} that we are going to simulate randomly.
	 */
	public RandomSimulation(final M model, final TransitionCalculator<M, T> nextTransition) {
		super(model, nextTransition);
		rand = new Random(System.nanoTime());
	}

	/**
	 * @see spinja.search.Simulation#nextTransition()
	 */
	@Override
	public T nextTransition() {
		T curr = null;
		enabled.clear();

		while ((curr = nextTransition.next(model, curr)) != null) {
			enabled.add(curr);
		}

		if (enabled.isEmpty()) {
			return null;
		} else {
			return enabled.get(rand.nextInt(enabled.size()));
		}
	}
}

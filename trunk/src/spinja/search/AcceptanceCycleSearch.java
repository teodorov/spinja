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

import spinja.model.Condition;
import spinja.model.Model;
import spinja.model.Transition;
import spinja.store.StateStore;

public class AcceptanceCycleSearch<M extends Model<T>, T extends Transition> extends
	NestedDepthFirstSearch<M, T> {
	private static final long serialVersionUID = 7398754730284458428L;

	public AcceptanceCycleSearch(M model, StateStore store, int stackSize,
		boolean errorExceedDepth, boolean checkForDeadlocks, int maxErrors,
		TransitionCalculator<M, T> nextTransition) {
		super(model, store, stackSize, errorExceedDepth, checkForDeadlocks, maxErrors,
			nextTransition);
	}

	@Override
	protected boolean conditionHolds() {
		return model.conditionHolds(Condition.ACCEPT_STATE);
	}

	@Override
	protected String getDescription() {
		return "acceptance cycle";
	}

}

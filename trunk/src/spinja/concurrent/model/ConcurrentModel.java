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

package spinja.concurrent.model;

import spinja.model.Condition;
import spinja.model.Model;

/**
 * A {@link ConcurrentModel} is a {@link Model} that contains one or more {@link Process}es, which
 * can be executed.
 * 
 * @author Marc de Jonge
 * @param <T>
 *            The type of transition that will be returned by this model.
 */
public abstract class ConcurrentModel<T extends ConcurrentTransition<T>> extends Model<T> {

	/**
	 * @return The number of processes that is contained by this {@link ConcurrentModel}.
	 */
	public abstract int getNrProcesses();

	/**
	 * @param index
	 *            The index of the process.
	 * @return The {@link Process} that is stored under the specified index.
	 */
	public abstract Process<T> getProcess(int index);

	/**
	 * @see spinja.model.Model#conditionHolds(int)
	 */
	@Override
	public boolean conditionHolds(int condition) {
		if (condition == Condition.SHOULD_STORE_STATE) {
			return true;
		}

		boolean allNeeded = condition == Condition.END_STATE;
		for (int i = 0; i < getNrProcesses(); i++) {
			if (allNeeded ^ getProcess(i).conditionHolds(condition)) {
				return !allNeeded;
			}
		}
		return allNeeded;
	}
}
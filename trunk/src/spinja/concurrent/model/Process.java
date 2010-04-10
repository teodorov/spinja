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

import spinja.model.Model;

/**
 * This interface represents an process within the model. The model can contain many processes and
 * each can take transitions an have its own state. Each process is in that way a Model on itself.
 * 
 * @author Marc de Jonge
 * @param <T>
 *            The type of transitions that will be return by this process.
 */
public abstract class Process<T extends ConcurrentTransition<T>> extends Model<T> {

	/**
	 * @return A unique identifier for this process. This should be the same as the index it is on
	 *         in the {@link ConcurrentModel}.
	 */
	public abstract int getId();

	/**
	 * @return True when all transitions that can be taken next, are local transitions. This
	 *         information can then be used for optimizing the state space.
	 */
	public boolean onlyLocalTransitions() {
		return false;
	}
}
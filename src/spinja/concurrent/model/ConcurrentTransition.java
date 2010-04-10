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

import spinja.model.Transition;

/**
 * A Transition represents the changes that can be made in to the Model.
 * 
 * @author Marc de Jonge
 * @param <T> This current transition type.
 */
public abstract class ConcurrentTransition<T extends ConcurrentTransition<T>> extends Transition {

	/**
	 * Returns the process identifier that determines to which process this transition belongs. This
	 * may return null, when there is a transition that is linked to the complete model in stead of
	 * one process. But in that case, only such transitions may be returned (so if the first
	 * transition belongs to a process, all should).
	 * 
	 * @return The process identifier that determines to which process this transition belongs.
	 */
	public abstract Process<T> getProcess();

	/**
	 * @return True when this transition only touches local variables, otherwise false.
	 */
	public abstract boolean isLocal();
}

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

/**
 * {@link MetaInfo} is data that can be stored with a {@link Transition}.
 * @author Marc de Jonge
 * @see Transition#setMetaInfo(MetaInfo)
 */
public enum MetaInfo {
	/**
	 * Indicates that on this {@link Transition} the
	 * {@link spinja.concurrent.search.PartialOrderReduction} algorithm has been applied.
	 */
	PartialOrderReduction(1),
	/**
	 * Indicates that after the {@link Transition} was executed, the state of the model was already
	 * found on the stack.
	 */
	StateOnStack(2);

	private final int mask;

	private MetaInfo(int mask) {
		this.mask = mask;
	}

	/**
	 * @return The mask that will be used.
	 */
	int getMask() {
		return mask;
	}
}

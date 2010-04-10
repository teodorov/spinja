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

package spinja.promela.model;

public class ElseTransitionFactory extends PromelaTransitionFactory {

	private final boolean takeAtomic;

	public ElseTransitionFactory(int transId, int stateFrom, int stateTo, boolean takeAtomic) {
		super(false, transId, stateFrom, stateTo, "else");
		this.takeAtomic = takeAtomic;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public boolean isElse() {
		return true;
	}

	@Override
	public boolean isLocal() {
		for (PromelaTransitionFactory holder = getState().getFirst(); holder != null
																		&& holder.getState()
																				.getProcess() == getState().getProcess(); holder = holder.getNext()) {
			if (holder != this && !holder.isLocal()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public PromelaTransition newTransition() {
		return new AtomicTransition(takeAtomic);
	}
}

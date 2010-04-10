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

import spinja.exceptions.ValidationException;

public class NeverEndTransitionFactory extends PromelaTransitionFactory {

	public NeverEndTransitionFactory(int transId) {
		super(true, transId, 0, 0, "-end never-");
	}

	@Override
	public final boolean isEnabled() {
		return true;
	}

	@Override
	public NonAtomicTransition newTransition() {
		return new NonAtomicTransition() {
			@Override
			public void take() throws ValidationException {
				throw new ValidationException("Never claim ended");
			}

			@Override
			public void undo() {
				// Can never be called! And there is nothing changed to the model anyhow
			}
		};
	}

}

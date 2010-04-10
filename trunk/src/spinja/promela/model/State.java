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

public class State {
	protected final PromelaProcess proc;
	
	private final PromelaTransitionFactory first;

	private final boolean endState, progressState, acceptState;

	public State(PromelaProcess proc, PromelaTransitionFactory first, boolean endState, boolean progressState,
		boolean acceptState) {
		this.proc = proc;
		this.first = first;
		first.setState(this);
		this.endState = endState;
		this.progressState = progressState;
		this.acceptState = acceptState;
	}
	
	public PromelaProcess getProcess() {
		return proc;
	}

	public PromelaTransitionFactory getFirst() {
		return first;
	}

	public boolean isEndState() {
		return endState;
	}

	public boolean isProgressState() {
		return progressState;
	}

	public boolean isAcceptState() {
		return acceptState;
	}
}

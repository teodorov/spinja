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

public abstract class PromelaTransitionFactory {

	private PromelaTransitionFactory next;

	protected State state;

	private boolean isLocal;

	protected final int transId;

	private final int stateFrom, stateTo;

	private final String text;

	public PromelaTransitionFactory(boolean isLocal, int transId,
		int stateFrom, int stateTo, String text) {
		state = null;
		next = null;
		this.isLocal = isLocal;
		this.transId = transId;
		this.stateFrom = stateFrom;
		this.stateTo = stateTo;
		this.text = text;
	}

	public void append(PromelaTransitionFactory next) {
		if (this.next == null) {
			this.next = next;
		} else {
			this.next.append(next);
		}
		next.state = state;
	}

	public void setState(State state) {
		this.state = state;
		if (next != null) {
			next.setState(state);
		}
	}

	public State getState() {
		return state;
	}

	public PromelaTransitionFactory getNext() {
		return next;
	}

	public PromelaTransitionFactory calcNext() {
		if (next == null && state.proc._model._exclusive == PromelaModel._NO_PROCESS) {
			PromelaProcess proc = state.proc.nextProcess();
			if (proc != null) {
				return proc.getCurrentState().getFirst();
			} else {
				return null;
			}
		} else {
			return next;
		}
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isElse() {
		return false;
	}

	public boolean isLocal() {
		return isLocal;
	}

	public int[] getRendezvous() {
		return null;
	}

	public boolean canReadRendezvous(final int[] msg) {
		return false;
	}

	@Override
	public String toString() {
		return "Holder for: " + text;
	}

	public abstract PromelaTransition newTransition();

	public class NonAtomicTransition extends PromelaTransition {
		@Override
		public final int getId() {
			return transId;
		}

		@Override
		public final PromelaProcess getProcess() {
			return state.proc;
		}

		@Override
		public final boolean isEnabled() {
			return PromelaTransitionFactory.this.isEnabled();
		}

		@Override
		public boolean isLocal() {
			return PromelaTransitionFactory.this.isLocal();
		}

		@Override
		public void take() throws ValidationException {
			takeImpl();
			assert getProcess()._sid == stateFrom;
			getProcess()._sid = stateTo;
			getProcess()._model.sendTransitionTakenEvent(this);
		}

		protected void takeImpl() throws ValidationException {
			// Empty
		}

		@Override
		public final String toString() {
			return "(proc " + getProcess().getId() + " trans " + getId() + "): " + text;
		}

		@Override
		public void undo() {
			assert getProcess()._sid == stateTo;
			undoImpl();
			getProcess()._sid = stateFrom;
			getProcess()._model.sendTransitionUndoEvent(this);
		}

		protected void undoImpl() {
			// Empty
		}

		public final PromelaTransitionFactory getHolder() {
			return PromelaTransitionFactory.this;
		}

		@Override
		public PromelaTransitionFactory getFactory() {
			return PromelaTransitionFactory.this;
		}
	}

	public class AtomicTransition extends NonAtomicTransition {
		private final boolean takeAtomic;

		private int old_exclusive;

		public AtomicTransition(final boolean takeAtomic) {
			this.takeAtomic = takeAtomic;
		}

		@Override
		public void take() throws ValidationException {
			old_exclusive = getProcess()._model._exclusive;
			takeImpl();
			assert getProcess()._sid == stateFrom;
			getProcess()._sid = stateTo;
			getProcess()._model._exclusive = takeAtomic ? getProcess()._pid : PromelaModel._NO_PROCESS;
			getProcess().sendTransitionTakenEvent(this);
		}

		@Override
		public void undo() {
			assert getProcess()._sid == stateTo;
			undoImpl();
			getProcess()._model._exclusive = old_exclusive;
			getProcess()._sid = stateFrom;
			getProcess().sendTransitionUndoEvent(this);
		}
	}
}

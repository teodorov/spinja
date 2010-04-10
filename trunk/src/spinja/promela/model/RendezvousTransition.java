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

public class RendezvousTransition extends PromelaTransition {

	final PromelaTransition send;

	final PromelaTransition read;

	final PromelaModel model;

	public RendezvousTransition(final PromelaModel model, final PromelaTransition send,
		final PromelaTransition read) {
		this.model = model;
		this.send = send;
		this.read = read;
	}

	@Override
	public int getId() {
		return (send.getId() << 16) | read.getId();
	}

	public RendezvousTransition nextRendezvousTransition() {
		PromelaProcess proc = read.getProcess();
		PromelaTransitionFactory factory = read.getFactory().getNext();
		int[] msg = send.getFactory().getRendezvous();
		outer: while (true) {
			while (factory == null) {
				// Try starting from a next process
				proc = proc.prevProcess();
				if (proc == send.getProcess()) {
					continue;
				} else if (proc == null) {
					break outer;
				} else {
					factory = proc.getCurrentState().getFirst();
				}
			}
			if (factory.canReadRendezvous(msg)) {
				return new RendezvousTransition(model, send, factory.newTransition());
			}
			factory = factory.getNext();
		}
		return null;
	}

	@Override
	public PromelaProcess getProcess() {
		return send.getProcess();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isLocal() {
		return false;
	}

	@Override
	public void take() throws ValidationException {
		model.activateTransitionListener(false);

		send.take();
//		send.getProcess()._model._exclusive = PromelaModel._NO_PROCESS;
		read.take();

		model.activateTransitionListener(true);
		model.sendTransitionTakenEvent(this);
	}

	@Override
	public String toString() {
		return send.toString() + " rendez-vous with " + read.toString();
	}

	@Override
	@SuppressWarnings("unused")
	public void undo() {
		model.activateTransitionListener(false);

		read.undo();
		send.undo();

		model.activateTransitionListener(true);
		model.sendTransitionUndoEvent(this);
	}

	@Override
	public PromelaTransitionFactory getFactory() {
		return send.getFactory();
	}
}

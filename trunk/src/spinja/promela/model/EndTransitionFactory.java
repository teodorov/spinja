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
import spinja.promela.exceptions.TooManyChannelsException;
import spinja.promela.exceptions.TooManyProcessesException;

public class EndTransitionFactory extends PromelaTransitionFactory {
	public EndTransitionFactory(int transId) {
		super(true, transId, 0, 0, "-end-");
	}

	@Override
	public final boolean isEnabled() {
		return state.proc._model._nrProcs == state.proc._pid + 1;
	}

	@Override
	public PromelaTransition newTransition() {
		return new PromelaTransition() {
			private Channel[] _backup_channels;
			
			@Override
			public PromelaTransitionFactory getFactory() {
				return EndTransitionFactory.this;
			}

			@Override
			public PromelaProcess getProcess() {
				return getState().getProcess();
			}

			@Override
			public boolean isEnabled() {
				return EndTransitionFactory.this.isEnabled();
			}

			@Override
			public boolean isLocal() {
				return true;
			}

			@Override
			public int getId() {
				return transId;
			}

			@Override
			public void take() throws ValidationException {
				_backup_channels = getProcess()._model.endProcess();
				getProcess().sendTransitionTakenEvent(this);
			}

			@Override
			public void undo() {
				try {
					getProcess()._model.addProcess(getProcess());
					if(_backup_channels != null) {
						for(Channel c : _backup_channels) {
							getProcess()._model.addChannel(c);
						}
					}
				} catch (final TooManyProcessesException ex) {
					throw new Error("Can not restore model, too many processes.");
				} catch (final TooManyChannelsException ex) {
					throw new Error("Can not restore model, too many channels.");
				}
				getProcess().sendTransitionUndoEvent(this);
			}
			
			@Override
			public String toString() {
				return "-end-";
			}
		};
	}

}

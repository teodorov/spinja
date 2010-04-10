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

import spinja.concurrent.model.ConcurrentModel;
import spinja.exceptions.ValidationException;
import spinja.model.Condition;
import spinja.promela.exceptions.TooManyChannelsException;
import spinja.promela.exceptions.TooManyProcessesException;
import spinja.util.ByteArrayStorage;

/**
 * @author Marc de Jonge
 * @version 1.0
 */
public abstract class PromelaModel extends ConcurrentModel<PromelaTransition> {
	/**
	 * The number that will by stored in the atomic token when no process holds it.
	 */
	public static final int _NO_PROCESS = 255;

	/**
	 * The array with all the processes that are started. This array always has a length of 255,
	 * thus creating more processes is not possible.
	 */
	protected PromelaProcess[] _procs;

	/**
	 * The number of processes that are started. Only _procs[0] until _procs[_nrProcs - 1] can be
	 * trusted to hold a valid process.
	 */
	protected int _nrProcs;

	/**
	 * The name of the model.
	 */
	private final String _name;

	/**
	 * The atomic token. This holds the number of the process that currently holds the atomic token.
	 * When no process has the atomic token, then this contains the value {@value #_NO_PROCESS}.
	 */
	protected int _exclusive;

	/**
	 * The list with channels that are created in the model. The limit for the number of channels is
	 * also 255.
	 */
	protected Channel[] _channels;

	/**
	 * The number of channels that are activated. Only _channels[0] until _channels[_nrChannels - 1]
	 * can be trusted to hold a working channel.
	 */
	protected int _nrChannels;

	/**
	 * If this boolean value is true then all the assert statement are not evaluated.
	 */
	protected boolean _ignore_assert;

	protected boolean _timeout;

	protected final int _global_size;

	protected int _process_size;

	PromelaTransition newEndAtomic() {
		return new PromelaTransition() {
			private int _backup;

			@Override
			public boolean isEnabled() {
				return true;
			}

			@Override
			public PromelaProcess getProcess() {
				return null;
			}

			@Override
			public boolean isLocal() {
				return true;
			}

			@Override
			public int getId() {
				return -2;
			}

			@Override
			public void take() throws ValidationException {
				_backup = _exclusive;
				_exclusive = _NO_PROCESS;
			}

			@Override
			public void undo() {
				_exclusive = _backup;
			}

			@Override
			public String toString() {
				return "-end-atomic-";
			}

			@Override
			public PromelaTransitionFactory getFactory() {
				return null;
			}
		};
	}

	/**
	 * Creates a new model, with the specified listeners and name. Also creates the channel and
	 * process-arrays and resets the tokens.
	 * 
	 * @param name
	 *            The name of the model.
	 * @param globalSize
	 *            The number of bytes that is needed to store the global vector.
	 */
	protected PromelaModel(final String name, final int globalSize) {
		_procs = new PromelaProcess[255];
		_nrProcs = 0;
		_channels = new Channel[255];
		_nrChannels = 0;
		_name = name;
		_exclusive = _NO_PROCESS;
		_ignore_assert = false;
		_global_size = globalSize;
	}

	/**
	 * Enables assertion checking in the model.
	 */
	public void enableAssertions() {
		_ignore_assert = false;
	}

	/**
	 * Disables assertion checking in the model.
	 */
	public void disableAssertions() {
		_ignore_assert = true;
	}

	/**
	 * Adds a channel to the channel list and returns the number for that channel.
	 * 
	 * @param c
	 *            The channel that should be added.
	 * @return The number of the channel inside the model.
	 * @throws TooManyChannelsException
	 */
	public int addChannel(final Channel c) throws TooManyChannelsException {
		if (_nrChannels >= 255) {
			throw new TooManyChannelsException("Maximum number of channels reached: 255");
		}
		_channels[_nrChannels] = c;
		return _nrChannels++;
	}

	/**
	 * Creates a new process within this model.
	 * 
	 * @param proctype
	 *            The new process that should be added to this model.
	 * @return The number of the process that was created.
	 * @throws TooManyProcessesException
	 *             When the maximum number of processes is already reached.
	 */
	public int addProcess(final PromelaProcess proctype) throws TooManyProcessesException {
		if (_nrProcs >= 255) {
			throw new TooManyProcessesException("too many processes");
		}
		_procs[_nrProcs++] = proctype;
		_process_size += proctype.getSize();
		return _nrProcs - 1;
	}

	/**
	 * Deletes the last process. This process should only be ended by the process that is at the end
	 * itself! This should be checked by the Proctype.
	 * 
	 * @return The channels that have been deleted with this process.
	 */
	public Channel[] endProcess() {
		_process_size -= _procs[--_nrProcs].getSize();

		int extraChannels = _nrChannels - _procs[_nrProcs].getNrChannelsBefore();
		_procs[_nrProcs] = null;

		if (extraChannels > 0) {
			Channel[] backup = new Channel[extraChannels];
			while (--extraChannels >= 0) {
				backup[extraChannels] = _channels[--_nrChannels];
				_channels[_nrChannels] = null; // delete channel!
			}
			return backup;
		} else {
			return null;
		}
	}

	/**
	 * @see spinja.concurrent.model.ConcurrentModel#getName()
	 */
	@Override
	public String getName() {
		return _name;
	}

	public PromelaProcess getNever() throws ValidationException {
		return null;
	}

	/**
	 * @return The number of processes that are active in the Model.
	 */
	@Override
	public int getNrProcesses() {
		return _nrProcs;
	}

	@Override
	public PromelaProcess getProcess(final int index) {
		return _procs[index];
	}

	public int getSize() {
		int size = _global_size + _process_size;
		for (int i = 0; i < _nrChannels; i++) {
			size += _channels[i].getSize();
		}
		return size;
	}

	protected void printf(final String msg, final Object... args) {
		sendPrintEvent(String.format(msg, args));
	}

	/**
	 * @see spinja.concurrent.model.ConcurrentModel#conditionHolds(int)
	 */
	@Override
	public boolean conditionHolds(int condition) {
		if (condition == Condition.SHOULD_STORE_STATE) {
			return _exclusive == _NO_PROCESS;
		} else {
			return super.conditionHolds(condition);
		}
	}

	/**
	 * This method be default only returns the name of the model, but it is advised to implement it
	 * in such a way that it can be used for debugging (e.g. printing variable values).
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return _name;
	}

	@Override
	public PromelaTransition nextTransition(PromelaTransition last) {
		PromelaTransition next = null;
		if (_nrProcs > 0) {
			if (_exclusive == _NO_PROCESS) {
				int i = last== null ? _nrProcs - 1 : last.getProcess()._pid;
				PromelaProcess proc = last == null ? _procs[i] : last.getProcess();
				
				next = proc.nextTransition(last);
				while (next == null && i > 0) {
					proc = _procs[--i];
					next = proc.nextTransition(null);
				}
			} else {
				next = _procs[_exclusive].nextTransition(last);
			}
		}

		return next;
	}

	@Override
	public PromelaModel clone() {
		try {
			return getClass().getConstructor(boolean.class).newInstance(_ignore_assert);
		} catch (Exception e) {
			System.err.println("error: kan geen correcte constructor vinden van dit model, er moet een constructor zijn met alleen een boolean als parameter");
			return null;
		}
	}

	private final ByteArrayStorage _store = new ByteArrayStorage();

	public byte[] encode() {
		_store.init(getSize());
		encode(_store);
		return _store.getBuffer();
	}

	public boolean decode(byte[] backup) {
		_store.setBuffer(backup);
		return decode(_store);
	}
}

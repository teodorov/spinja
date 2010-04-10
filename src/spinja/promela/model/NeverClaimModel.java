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
import spinja.model.listener.TransitionEvent;
import spinja.model.listener.TransitionListener;
import spinja.util.DataReader;
import spinja.util.DataWriter;

public class NeverClaimModel extends ConcurrentModel<PromelaTransition> implements TransitionListener {
	public static ConcurrentModel<PromelaTransition> createNever(final PromelaModel model)
		throws ValidationException {
		final PromelaProcess never = model.getNever();
		if (never != null) {
			return new NeverClaimModel(model, never);
		} else {
			return model;
		}
	}

	private final PromelaModel model;

	private final PromelaProcess neverProc;

	private boolean _turn;

	NeverClaimModel(final PromelaModel model, final PromelaProcess neverProc) {
		this.model = model;
		model.addTransitionListener(this);
		this.neverProc = neverProc;
		_turn = true;
	}

	@Override
	public String getName() {
		return model.getName() + "-with-never-claim";
	}

	@Override
	public int getNrProcesses() {
		if (_turn) {
			return 1;
		} else {
			return model.getNrProcesses();
		}
	}

	@Override
	public PromelaProcess getProcess(final int index) {
		if (_turn) {
			if (index == 0) {
				return neverProc;
			} else {
				throw new IndexOutOfBoundsException();
			}
		} else {
			return model.getProcess(index);
		}
	}

	public int getSize() {
		return model.getSize() + neverProc.getSize() + 1;
	}

	@Override
	public NeverClaimModel clone() {
		return new NeverClaimModel(model, neverProc);
	}

	@Override
	public boolean conditionHolds(int condition) {
		if (!_turn) {
			if(condition == Condition.SHOULD_STORE_STATE) {
				return false;
			}
			return neverProc.conditionHolds(condition);
		} else {
			return model.conditionHolds(condition);
		}
	}

	@Override
	public PromelaTransition nextTransition(final PromelaTransition last) {
		if (_turn) {
			return neverProc.nextTransition(last);
		} else {
			return model.nextTransition(last);
		}
	}
	
	public boolean decode(DataReader reader) {
		_turn = reader.readBoolean();
		return neverProc.decode(reader)  && model.decode(reader);
	}
	
	public void encode(DataWriter writer) {
		writer.writeBoolean(_turn);
		neverProc.encode(writer);
		model.encode(writer);
	}

	@Override
	public String toString() {
		return model.toString() + neverProc.toString();
	}

	public void transitionTaken(final TransitionEvent evt) {
		_turn = !_turn;
	}

	public void transitionUndo(final TransitionEvent evt) {
		_turn = !_turn;
	}
}

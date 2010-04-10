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

package spinja.promela.compiler.optimizer;

import java.util.Iterator;

import spinja.promela.compiler.actions.Action;
import spinja.promela.compiler.automaton.Automaton;
import spinja.promela.compiler.automaton.State;
import spinja.promela.compiler.automaton.Transition;

public class StateMerging implements GraphOptimizer {

	public void optimize(final Automaton graph) {
		Iterator<State> it = graph.iterator();
		while (it.hasNext()) {
			final State state = it.next();
			if (graph.getStartState() == state) {
				continue;
			}
			if (state.sizeOut() == 1 && state.sizeIn() == 1) {
				Transition in = state.getIn(0);
				Transition out = state.getOut(0);

				if (in.getActionCount() > 0 && out.getActionCount() > 0 && out.isAlwaysEnabled()
					&& (out.isLocal() || state.isInAtomic()) && !in.hasChannelSendAction()) {
					in.changeTo(out.getTo());
					for (final Action action : out) {
						in.addAction(action);
					}
					state.delete();
					it = graph.iterator();
				}
			}
		}
	}
}

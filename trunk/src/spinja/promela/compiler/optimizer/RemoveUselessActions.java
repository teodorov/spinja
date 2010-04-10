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

import spinja.promela.compiler.automaton.Automaton;
import spinja.promela.compiler.automaton.State;
import spinja.promela.compiler.automaton.Transition;

public class RemoveUselessActions implements GraphOptimizer {
	public void optimize(final Automaton automaton) {
		Iterator<State> it = automaton.iterator();
		while (it.hasNext()) {
			final State state = it.next();
			for (final Transition out : state.output) {
				if (out.isUseless()) {
					final State next = out.getTo();

					for (final Transition t : next.output) {
						t.duplicate().changeFrom(state, out);
					}

					it = automaton.iterator();
					out.delete();					
					break;
				}
			}
		}
	}
}

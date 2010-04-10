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

package spinja.promela.compiler.automaton;

import spinja.util.StringWriter;

/**
 * Represents an ending transition of a neverclaim, that generates to code the end the current
 * neverclaim in Promela.
 * 
 * @author Marc de Jonge
 */
public class NeverEndTransition extends EndTransition {
	/**
	 * Constructor of NeverEndTransition using only the from state. The state where it ends is not
	 * relevant.
	 * 
	 * @param from
	 *            The starting state.
	 */
	public NeverEndTransition(final State from) {
		super(from);
	}

	/**
	 * @see spinja.promela.compiler.automaton.EndTransition#duplicate()
	 */
	@Override
	public Transition duplicate() {
		final Transition t = new NeverEndTransition(getFrom());
		return t;
	}

	/**
	 * @see spinja.promela.compiler.automaton.EndTransition#printTransition(spinja.util.StringWriter)
	 */
	@Override
	public void printTransition(final StringWriter w) {
		w.appendLine("new NeverEndTransitionFactory(",/* from.getAutomaton().getProctype().getName(),
			".this, ",*/ getTransId(), ")");
	}
}

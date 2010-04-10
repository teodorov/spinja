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

/**
 * Represents a goto transition, that generates the code to jump from one state to an other. This
 * transition is in essential meaningless, because it performs no real action.
 * 
 * @author Marc de Jonge
 */
public class GotoTransition extends Transition {
	private final String text;

	/**
	 * Constructor of GotoTransition. Creates a {@link Transition} from one {@link State} to an
	 * other. Both the from and to state must be part of the same automaton.
	 * 
	 * @param from
	 *            The {@link State} where the {@link Transition} must start.
	 * @param to
	 *            The {@link State} where the {@link Transition} must end.
	 * @param text
	 *            The label to where this transition should jump to.
	 */
	public GotoTransition(final State from, final State to, final String text) {
		super(from, to);
		this.text = text;
	}

	/**
	 * @see spinja.promela.compiler.automaton.Transition#duplicate()
	 */
	@Override
	public Transition duplicate() {
		final Transition t = new GotoTransition(getFrom(), getTo(), text);
		return t;
	}

	/**
	 * @see spinja.promela.compiler.automaton.Transition#isLocal()
	 */
	@Override
	public boolean isLocal() {
		return true;
	}

	/**
	 * @see spinja.promela.compiler.automaton.Transition#getText()
	 */
	@Override
	public String getText() {
		return "goto " + text;
	}
	
	@Override
	public boolean isAlwaysEnabled() {
		return true;
	}
}

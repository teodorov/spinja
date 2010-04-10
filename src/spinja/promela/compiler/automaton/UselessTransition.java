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
 * @author Marc de Jonge
 */
public class UselessTransition extends Transition {
	private final String text;

	/**
	 * Constructor of UselessTransition.
	 * 
	 * @param from
	 *            The from state
	 * @param to
	 *            The to state
	 * @param text
	 *            The text for this useless transition
	 */
	public UselessTransition(final State from, final State to, final String text) {
		super(from, to);
		this.text = text;
	}

	/**
	 * @see spinja.promela.compiler.automaton.Transition#duplicate()
	 */
	@Override
	public Transition duplicate() {
		final Transition t = new UselessTransition(getFrom(), getTo(), text);
		return t;
	}

	/**
	 * @see spinja.promela.compiler.automaton.Transition#isUseless()
	 */
	@Override
	public boolean isUseless() {
		return true;
	}

	/**
	 * @see spinja.promela.compiler.automaton.Transition#printTransition(spinja.util.StringWriter)
	 */
	@Override
	public void printTransition(final StringWriter w) {
		throw new IllegalStateException("Useless actions should be removed before generating code!");
	}

	/**
	 * @see spinja.promela.compiler.automaton.Transition#getText()
	 */
	@Override
	public String getText() {
		return text;
	}
}

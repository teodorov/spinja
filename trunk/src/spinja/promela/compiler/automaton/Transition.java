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

import java.util.Collections;
import java.util.Iterator;

import spinja.promela.compiler.actions.Action;
import spinja.promela.compiler.actions.ActionContainer;
import spinja.promela.compiler.actions.ChannelSendAction;
import spinja.promela.compiler.parser.ParseException;
import spinja.util.StringWriter;

/**
 * This abstract class describes a Transition that can be part of a {@link Automaton}. Each
 * {@link Transition} must go from one {@link State} to an other. Also each {@link Transition} that
 * is created gets its own unique identifier.
 * 
 * @author Marc de Jonge
 */
public abstract class Transition implements Iterable<Action>, ActionContainer {
	private static class TransitionIdCounter {
		private static int id = 0;

		private static synchronized int nextId() {
			return TransitionIdCounter.id++;
		}
	}

	private State from, to;

	private final int transId;

	/**
	 * Constructor of Transition. Creates a {@link Transition} from one {@link State} to an other.
	 * Both the from and to state must be part of the same automaton.
	 * 
	 * @param from
	 *            The {@link State} where the {@link Transition} must start.
	 * @param to
	 *            The {@link State} where the {@link Transition} must end.
	 */
	public Transition(final State from, final State to) {
		if (from == null) {
			throw new IllegalArgumentException("A transition must always come from somewhere!");
		}
		if (to != null && from.getAutomaton() != to.getAutomaton()) {
			throw new IllegalArgumentException("Both States must be part of the same Automaton!");
		}
		this.from = from;
		from.addOut(this);
		this.to = to;
		if (to != null) {
			to.addIn(this);
		}
		transId = TransitionIdCounter.nextId();
	}

	/**
	 * Adds a new action to this Transition. The default implementation always throws an
	 * {@link IllegalArgumentException}, because by default these can not be added (some subclasses
	 * may though).
	 * 
	 * @param action
	 *            The action that may be added.
	 */
	public void addAction(Action action) {
		throw new IllegalArgumentException(
			"Please do not add actions to a transition that doesn't support actions");
	}

	/**
	 * Returns the action with the given index. Always returns an {@link IndexOutOfBoundsException}
	 * with the default implementation, because there are no action available.
	 * 
	 * @param index
	 *            The index of the to be retrieved action.
	 * @return The retrieved action.
	 */
	public Action getAction(int index) {
		throw new IndexOutOfBoundsException();
	}

	/**
	 * Changes the State where this Transition originally started from.
	 * 
	 * @param from
	 *            The new {@link State} where this {@link Transition} should start from.
	 */
	public final void changeFrom(final State from) {
		this.from.removeOut(this);
		this.from = from;
		this.from.addOut(this);
	}

	/**
	 * Changes the State when this Transition originally started from. This transition is added
	 * directly after the after transition.
	 * @param from
	 *            The new {@link State} where this {@link Transition} should start from.
	 * @param after
	 *            The {@link Transition} after which this {@link Transition} should be placed.
	 */
	public final void changeFrom(final State from, final Transition after) {
		this.from.removeOut(this);
		this.from = from;
		this.from.addOut(this, after);
	}

	/**
	 * Changes the State where this Transition originally ended in.
	 * 
	 * @param to
	 *            The now {@link State} where this Transition should ended in.
	 */
	public final void changeTo(final State to) {
		State oldTo = this.to;
		this.to = to;
		if (this.to != null) {
			to.addIn(this);
		}
		if (oldTo != null) {
			oldTo.removeIn(this);
		}
	}

	/**
	 * Removes this {@link Transition} completely.
	 */
	public final void delete() {
		from.removeOut(this);
		from = null;
		if (to != null) {
			to.removeIn(this);
			to = null;
		}
	}

	/**
	 * @return true when one of the actions on this transition is handling a channel.
	 */
	public boolean hasChannelSendAction() {
		for (int i = 0; i < getActionCount(); i++) {
			Action action = getAction(i);
			if (action instanceof ChannelSendAction) {
				return true;
			}
		}
		return false;
	}

	void setTo(State to) {
		this.to = to;
	}

	void setFrom(State from) {
		this.from = from;
	}

	/**
	 * Duplicates this Transition. After this function returns, the from State should have to
	 * different Transitions as output that behave exactly the same.
	 * 
	 * @return The duplicated Transition.
	 */
	public abstract Transition duplicate();

	/**
	 * Returns the number of action that were added to this {@link Transition}. By default zero is
	 * always returned.
	 * 
	 * @return The number of action that were added to this {@link Transition}.
	 */
	public int getActionCount() {
		return 0;
	}

	/**
	 * Returns The number of bytes that are needed to make a backup of the changes that this
	 * transition makes. The default implementation always return zero.
	 * 
	 * @return The number of bytes that are needed to make a backup of the changes that this
	 *         transition makes.
	 */
	public int getBackupSize() {
		return 0;
	}

	/**
	 * @return The state where this state should start from.
	 */
	public State getFrom() {
		return from;
	}

	/**
	 * @return A textual representation of actions that this Transition does.
	 */
	public abstract String getText();

	/**
	 * @return The state where this state should end in.
	 */
	public State getTo() {
		return to;
	}

	/**
	 * @return The unique identifier that can be used to identify this Transition.
	 */
	public int getTransId() {
		return transId;
	}

	/**
	 * Returns whether this Transition can be considered a local action or not. The default
	 * implementation always returns false.
	 * 
	 * @return whether this Transition can be considered a local action or not.
	 */
	public boolean isLocal() {
		return false;
	}

	/**
	 * @return True when in the next state the atomic token should be taken.
	 */
	public final boolean takesAtomicToken() {
		return ((getTo() != null) && getTo().isInAtomic());
	}

	/**
	 * Returns whether this Transition does something useful or not. The default implementation
	 * always return false.
	 * 
	 * @return whether this Transition does something useful or not.
	 */
	public boolean isUseless() {
		return false;
	}

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	@SuppressWarnings("unchecked")
	public Iterator<Action> iterator() {
		return Collections.EMPTY_LIST.iterator();
	}

	/**
	 * Returns the transition code using the getJavaStatement(), getJavaExpression() and
	 * getJavaPrint() functions.
	 * 
	 * @param w
	 *            The {@link StringWriter} that can be used to write the java code.
	 * @throws ParseException
	 *             When something went wrong while parsing the input.
	 */
	public void printTransition(final StringWriter w) throws ParseException {
		w.appendLine("new PromelaTransitionFactory(", /*
														 * from.getAutomaton().getProctype().getName(),
														 * ".this, ",
														 */isLocal(), ", ", transId, ", ", from.getStateId(), ", ", to.getStateId(), ", \"",
			getText(), "\") {").indent();
		if (getActionCount() > 0) {
			getAction(0).printEnabledFunction(w);
			getAction(0).printExtraFunctions(w);
		}

		w.appendLine("public final PromelaTransition newTransition() {").indent();
		if (from.getAutomaton().getProctype().getSpecification().usesAtomic()) {
			w.appendLine("return new AtomicTransition(", takesAtomicToken(), ") {").indent();
		} else {
			w.appendLine("return new NonAtomicTransition() {").indent();
		}

		int length = w.length() - 2 - w.getPostfix().length();
		if (!printTransitionImpl(w)) {
			w.setLength(length);
			w.outdent().append(";").appendPostfix();
		} else {
			w.removePostfix().outdent().appendLine("};");
		}
		w.outdent().appendLine("}");
		w.outdent().appendLine("}");
	}

	public boolean printTransitionImpl(final StringWriter w) throws ParseException {
		return false;
	}

	/**
	 * Creates a useful textual representation of this Transition, including the states where it
	 * started from and goes to. Useful for debugging.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String toString() {
		return new StringWriter().appendIf(takesAtomicToken(), "Atomic ").append(
			getClass().getSimpleName()).append(" from ").append(
			from == null ? "nowhere" : from.getStateId()).append(" to ").append(
			to == null ? "nowhere" : to.getStateId()).append(" ").append(getText()).toString();
	}

	public boolean isAlwaysEnabled() {
		return false;
	}
}

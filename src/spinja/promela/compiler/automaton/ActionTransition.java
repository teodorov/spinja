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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import spinja.promela.compiler.actions.Action;
import spinja.promela.compiler.actions.AssignAction;
import spinja.promela.compiler.actions.ExprAction;
import spinja.promela.compiler.actions.Sequence;
import spinja.promela.compiler.expression.Expression;
import spinja.promela.compiler.expression.Identifier;
import spinja.promela.compiler.parser.ParseException;
import spinja.promela.compiler.variable.Variable;
import spinja.util.StringWriter;

/**
 * @author Marc de Jonge
 */
public class ActionTransition extends Transition {
	private final Sequence sequence;

	/**
	 * Constructor of ActionTransition.
	 * 
	 * @param from
	 * @param to
	 */
	public ActionTransition(final State from, final State to) {
		super(from, to);
		sequence = new Sequence();
	}

	/**
	 * @see spinja.promela.compiler.automaton.Transition#addAction(spinja.promela.compiler.actions.Action)
	 */
	@Override
	public void addAction(final Action action) {
		sequence.addAction(action);
	}

	/**
	 * @see spinja.promela.compiler.automaton.Transition#duplicate()
	 */
	@Override
	public Transition duplicate() {
		final ActionTransition t = new ActionTransition(getFrom(), getTo());
		for (final Action a : sequence) {
			t.addAction(a);
		}
		return t;
	}

	/**
	 * @see spinja.promela.compiler.automaton.Transition#getActionCount()
	 */
	@Override
	public int getActionCount() {
		return sequence.getNrActions();
	}

	/**
	 * @see spinja.promela.compiler.automaton.Transition#getText()
	 */
	@Override
	public String getText() {
		final StringWriter w = new StringWriter();
		boolean first = true;
		for (final Action a : this) {
			w.appendIf(!first, "; ").append(a.toString());
			first = false;
		}
		return w.toString();
	}

	/**
	 * @see spinja.promela.compiler.automaton.Transition#isLocal()
	 */
	@Override
	public boolean isLocal() {
		if (takesAtomicToken() || (getFrom() != null && getFrom().isInAtomic())) {
			return false;
		}
		for (final Action a : sequence) {
			if (!a.isLocal(getFrom().getAutomaton().getProctype())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @see spinja.promela.compiler.automaton.Transition#isUseless()
	 */
	@Override
	public boolean isUseless() {
		return false;
	}

	/**
	 * @see spinja.promela.compiler.automaton.Transition#getAction(int)
	 */
	@Override
	public Action getAction(int index) {
		return sequence.getAction(index);
	}

	/**
	 * @see spinja.promela.compiler.automaton.Transition#iterator()
	 */
	@Override
	public Iterator<Action> iterator() {
		return sequence.iterator();
	}

	@Override
	public boolean printTransitionImpl(StringWriter w) throws ParseException {
		Map<Variable, Identifier> changes = new HashMap<Variable, Identifier>();
		boolean isComplex = sequence.getBackupVariables(changes);
		boolean addedSome = false;
		if (isComplex) {
			w.appendLine("private byte[] _backup;").appendLine();
			addedSome = true;
		} else if (!changes.isEmpty()) {
			for (Variable var : changes.keySet()) {
				w.appendLine("private int _backup_", var.getName(), ";");
				addedSome = true;
			}
			w.appendLine();
		}

		w.setSavePoint();
		w.appendLine("public final void takeImpl() throws ValidationException {").indent();
		int length = w.length();
		if (isComplex) {
			w.appendLine("_backup = getProcess().getModel().encode();");
		}
		sequence.printTakeStatement(w);
		w.outdent();
		if (w.length() > length) {
			w.appendLine("}").appendLine();
			addedSome = true;
		} else {
			w.revertToSavePoint();
		}

		w.setSavePoint();
		w.appendLine("public final void undoImpl() {").indent();
		length = w.length();
		if (isComplex) {
			w.appendLine("if(!getProcess().getModel().decode(_backup)) throw new Error(\"Could not decode the backup!\");");
		} else {
			sequence.printUndoStatement(w);
		}
		w.outdent();
		if (w.length() > length) {
			w.appendLine("}").appendLine();
			addedSome = true;
		} else {
			w.revertToSavePoint();
		}

		return addedSome;
	}

	@Override
	public boolean isAlwaysEnabled() {
		if (sequence.getNrActions() >= 1) {
			try {
				Action action = sequence.getAction(0);

				Expression expr = null;
				if (action instanceof ExprAction) {
					expr = ((ExprAction) action).getExpression();
				}
				if (expr != null && expr.getSideEffect() != null) {
					return false;
				}

				return action.getEnabledExpression() == null;
			} catch (ParseException e) {
				return false;
			}
		} else {
			return true;
		}
	}
}
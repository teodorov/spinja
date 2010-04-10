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

package spinja.promela.compiler.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import spinja.promela.compiler.Proctype;
import spinja.promela.compiler.expression.Identifier;
import spinja.promela.compiler.parser.ParseException;
import spinja.promela.compiler.parser.Token;
import spinja.promela.compiler.variable.Variable;
import spinja.util.StringWriter;

public class Sequence extends Action implements ActionContainer, Iterable<Action> {
	private final List<Action> actions;

	public Sequence() {
		super(null);
		actions = new ArrayList<Action>();
	}

	/**
	 * @see spinja.promela.compiler.actions.Action#getToken()
	 */
	@Override
	public Token getToken() {
		if (actions.isEmpty()) {
			return null;
		} else {
			return actions.get(0).getToken();
		}
	}

	public boolean startsWithElse() {
		return !actions.isEmpty() && actions.get(0) instanceof ElseAction;
	}

	public void addAction(Action sub) {
		actions.add(sub);
	}

	public Iterator<Action> iterator() {
		return Collections.unmodifiableList(actions).iterator();
	}

	public int getNrActions() {
		return actions.size();
	}
	
	@Override
	public boolean isLocal(Proctype proc) {
		for(Action action : actions) {
			if(!action.isLocal(proc)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getEnabledExpression() throws ParseException {
		if (actions.isEmpty()) {
			return null;
		} else {
			return actions.get(0).getEnabledExpression();
		}
	}

	@Override
	public void printTakeStatement(StringWriter w) throws ParseException {
		printTakeStatement(w, !isComplex());
	}

	public void printTakeStatement(final StringWriter w, boolean generateBackup)
		throws ParseException {
		boolean first = true;
		for (final Action a : this) {
			if (generateBackup) {
				for (Identifier id : a.getChangedVariables()) {
					w.appendLine("_backup_", id.getVariable().getName(), " = ",
						id.getIntExpression(), ";");
				}
			}
			a.printTakeStatement(w);
			if (!first) {
				String enabled = a.getEnabledExpression();
				if (enabled != null) {
					w.appendLine("if(!", enabled,
						") throw new ValidationException(\"Non-enabled action during a d_step\");");
				}
			}
			first = true;
		}
	}

	@Override
	public void printUndoStatement(StringWriter w) throws ParseException {
		printUndoStatement(w, !isComplex());
	}

	public void printUndoStatement(StringWriter w, boolean generateBackup) throws ParseException {
		for(int i = actions.size() - 1; i >= 0; i--) {
			final Action a = actions.get(i);
			a.printUndoStatement(w);
			if (generateBackup) {
				for (final Identifier id : a.getChangedVariables()) {
					w.appendLine(id.getIntExpression(), " = _backup_", id.getVariable().getName(),
						";");
				}
			}
		}
	}

	@Override
	public boolean isComplex() {
		return getBackupVariables(new HashMap<Variable, Identifier>());
	}

	@Override
	public Collection<Identifier> getChangedVariables() {
		Map<Variable, Identifier> changes = new HashMap<Variable, Identifier>();
		getBackupVariables(changes);
		return changes.values();
	}

	public boolean getBackupVariables(Map<Variable, Identifier> output) {
		return getBackupVariables(output, false);
	}

	private boolean getBackupVariables(Map<Variable, Identifier> output, final boolean isComplex) {
		output.clear();
		for (final Action a : this) {
			if (!isComplex && a.isComplex()) {
				output.clear();
				return getBackupVariables(output, true);
			}
			for (final Identifier id : a.getChangedVariables()) {
				Variable var = id.getVariable();
				if (!output.containsKey(var)) {
					output.put(var, id);
				} else if (!isComplex) {
					output.clear();
					return getBackupVariables(output, true);
				}
			}
		}
		return isComplex;
	}

	public Action getAction(int index) {
		return actions.get(index);
	}

	@Override
	public String toString() {
		if (actions.size() == 1) {
			return actions.get(0).toString();
		} else {
			return "d_step";
		}
	}
}

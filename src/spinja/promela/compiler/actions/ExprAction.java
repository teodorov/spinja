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

import spinja.promela.compiler.Proctype;
import spinja.promela.compiler.expression.Expression;
import spinja.promela.compiler.parser.ParseException;
import spinja.promela.compiler.variable.ChannelVariable;
import spinja.promela.compiler.variable.VariableAccess;
import spinja.util.StringWriter;

public class ExprAction extends Action {
	private final Expression expr;

	public ExprAction(final Expression expr) {
		super(expr.getToken());
		this.expr = expr;
		for (final VariableAccess var : expr.readVariables()) {
			var.getVar().setRead(true);
		}
	}

	@Override
	public String getEnabledExpression() throws ParseException {
		String bool = expr.getBoolExpression();
		if (bool == null || bool.equals("true")) {
			return null;
		} else {
			return bool;
		}
	}

	@Override
	public boolean isLocal(final Proctype proc) {
		for (final VariableAccess va : expr.readVariables()) {
			if (!proc.hasVariable(va.getVar().getName())
				|| (va.getVar() instanceof ChannelVariable)) {
				return false;
			}
		}
		return super.isLocal(proc);
	}

	@Override
	public void printTakeStatement(final StringWriter w) throws ParseException {
		final String sideEffect = expr.getSideEffect();
		if (sideEffect != null) {
			w.appendLine(sideEffect, ";");
		}
	}

	@Override
	public void printUndoStatement(final StringWriter w) throws ParseException {
		if (expr.getSideEffect() != null) {
			w.appendLine("endProcess();");
		}
	}

	@Override
	public String toString() {
		return expr.toString();
	}
	
	public Expression getExpression() {
		return expr;
	}
}

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

import java.util.Collections;
import java.util.List;

import spinja.promela.compiler.Proctype;
import spinja.promela.compiler.expression.Expression;
import spinja.promela.compiler.expression.Identifier;
import spinja.promela.compiler.parser.ParseException;
import spinja.promela.compiler.parser.Token;
import spinja.promela.compiler.variable.VariableAccess;
import spinja.util.StringWriter;

public class AssertAction extends Action {
	private final Expression expr;

	public AssertAction(final Token token, final Expression expr) {
		super(token);
		this.expr = expr;
	}

	@Override
	public String getEnabledExpression() {
		return null;
	}

	@Override
	public boolean isLocal(final Proctype proc) {
		for (final VariableAccess va : expr.readVariables()) {
			if (!proc.hasVariable(va.getVar().getName())) {
				return false;
			}
		}
		return super.isLocal(proc);
	}

	@Override
	public void printTakeStatement(final StringWriter w) throws ParseException {
		w.appendLine("if(!", expr.getBoolExpression(), ") throw new AssertionException(\"",
			expr.toString(), "\");");
	}

	@Override
	public List<Identifier> getChangedVariables() {
		return Collections.emptyList();
	}

	@Override
	public String toString() {
		return "assert " + expr.toString();
	}
}

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

import spinja.promela.compiler.expression.Identifier;
import spinja.promela.compiler.parser.ParseException;
import spinja.promela.compiler.parser.Token;
import spinja.util.StringWriter;

public class ElseAction extends Action {

	public ElseAction(final Token token) {
		super(token);
	}

	@Override
	public String getEnabledExpression() throws ParseException {
		return "false";
	}

	@SuppressWarnings("unused")
	@Override
	public void printTakeStatement(final StringWriter w) throws ParseException {
		// Does nothing
	}

	@Override
	public List<Identifier> getChangedVariables() {
		return Collections.emptyList();
	}

	@Override
	public String toString() {
		return "else";
	}

	@Override
	public void printExtraFunctions(StringWriter w) throws ParseException {
		w.appendLine("public boolean isElse() {").indent();
		w.appendLine("return true;");
		w.outdent().appendLine("}").appendLine();

		w.appendLine("public final boolean isLocal() {").indent();
		w.appendLine(
			"for(PromelaTransitionFactory holder = getState().getFirst(); holder != null && holder.getProcess() == getProcess(); holder = holder.getNext()) {").indent();
		w.appendLine("if(holder != this && !holder.isLocal()) {").indent();
		w.appendLine("return false;");
		w.outdent().appendLine("}");
		w.outdent().appendLine("}");
		w.appendLine("return true;");
		w.outdent().appendLine("}").appendLine();
	}
}

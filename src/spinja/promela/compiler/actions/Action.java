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

import java.util.Collection;
import java.util.Collections;

import spinja.promela.compiler.Proctype;
import spinja.promela.compiler.expression.Identifier;
import spinja.promela.compiler.parser.ParseException;
import spinja.promela.compiler.parser.Token;
import spinja.util.StringWriter;

public abstract class Action {

	private final Token token;

	// private Transition transition;

	public Action(final Token token) {
		this.token = token;
	}

	public abstract String getEnabledExpression() throws ParseException;

	public Token getToken() {
		return token;
	}

	public boolean isLocal(final Proctype proc) {
		return true;
	}

	public void printEnabledFunction(final StringWriter w) throws ParseException {
		final String e = getEnabledExpression();
		if (e != null) {
			w.appendLine("public final boolean isEnabled() { ");
			w.indent();
			w.appendLine("return ", e, ";");
			w.outdent();
			w.appendLine("}");
			w.appendLine();
		}
	}

	@SuppressWarnings("unused")
	public void printExtraFunctions(final StringWriter w) throws ParseException {
		// By default there are no extra functions
	}

	public Collection<Identifier> getChangedVariables() {
		return Collections.emptyList();
	}

	public boolean isComplex() {
		return false;
	}

	public abstract void printTakeStatement(StringWriter w) throws ParseException;

	@SuppressWarnings("unused")
	public void printUndoStatement(final StringWriter w) throws ParseException {
	}

	// public void setTransition(Transition transition) {
	// this.transition = transition;
	// }
	//
	// public Transition getTransition() {
	// return transition;
	// }

	@Override
	public abstract String toString();
}

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
import java.util.List;

import spinja.promela.compiler.Proctype;
import spinja.promela.compiler.expression.CompoundExpression;
import spinja.promela.compiler.expression.Expression;
import spinja.promela.compiler.parser.ParseException;
import spinja.promela.compiler.parser.Token;
import spinja.promela.compiler.variable.ChannelVariable;
import spinja.promela.compiler.variable.VariableAccess;
import spinja.util.StringWriter;

public class PrintAction extends Action implements CompoundExpression {

	private final String string;

	private final List<Expression> exprs;

	public PrintAction(final Token token, final String string) {
		super(token);
		this.string = string;
		exprs = new ArrayList<Expression>();
	}

	public void addExpression(final Expression expr) {
		exprs.add(expr);
	}

	private String escapedString() {
		final StringBuilder sb = new StringBuilder(string.length());
		for (int i = 0; i < string.length(); i++) {
			final char c = string.charAt(i);
			if ((c >= ' ') && (c <= '~')) {
				if (c == '\"') {
					sb.append("\\\"");
				} else if (c == '\\') {
					sb.append("\\\\");
				} else {
					sb.append(c);
				}
			} else {
				sb.append("\\u" + Integer.toHexString(c));
			}
		}
		return sb.toString();
	}

	@Override
	public String getEnabledExpression() {
		return null;
	}

	@Override
	public boolean isLocal(final Proctype proc) {
		for (final Expression expr : exprs) {
			for (final VariableAccess va : expr.readVariables()) {
				if (!proc.hasVariable(va.getVar().getName())
					|| (va.getVar() instanceof ChannelVariable)) {
					return false;
				}
			}
		}
		return super.isLocal(proc);
	}

	@Override
	public void printTakeStatement(final StringWriter w) throws ParseException {
		w.appendPrefix().append("printf(").append(string);
		for (final Expression expr : exprs) {
			w.append(", ").append(expr.getIntExpression());
		}
		w.append(");").appendPostfix();
	}

	@Override
	public String toString() {
		final StringWriter w = new StringWriter();
		try {
			w.append("printf(").append(escapedString());
			for (final Expression expr : exprs) {
				w.append(", ").append(expr.getIntExpression());
			}
			w.append(");");
		} catch (final ParseException e) {
			// do nothing
		}
		return w.toString();
	}
}

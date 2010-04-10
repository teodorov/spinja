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

package spinja.promela.compiler.expression;

import java.util.HashSet;
import java.util.Set;

import spinja.promela.compiler.parser.PromelaConstants;
import spinja.promela.compiler.parser.Token;
import spinja.promela.compiler.variable.VariableAccess;
import spinja.promela.compiler.variable.VariableType;

/**
 * The constant expression is an expression that represents a simple number or other constant (e.g.
 * TRUE, FALSE etc..).
 * 
 * @author Marc de Jonge
 */
public class ConstantExpression extends Expression {
	private static final long serialVersionUID = -5861698132204844795L;

	private final int nr;

	/**
	 * Creates a new ConstantExpression from a specified token and the constant number.
	 * 
	 * @param token
	 *            The token that is stored to see what kind of expression it is.
	 * @param nr
	 *            The constant number that this expression contains.
	 */
	public ConstantExpression(final Token token, final int nr) {
		super(token);
		this.nr = nr;
	}

	@Override
	public String getBoolExpression() {
		switch (getToken().kind) {
			case PromelaConstants.TRUE:
				return "true";
			case PromelaConstants.FALSE:
				return "false";
			case PromelaConstants.SKIP_:
				return "true";
			case PromelaConstants.NUMBER:
				return nr != 0 ? "true" : "false";
			default:
				return "true";
		}
	}

	@Override
	public int getConstantValue() {
		return nr;
	}

	@Override
	public String getIntExpression() {
		switch (getToken().kind) {
			case PromelaConstants.TRUE:
				return "1";
			case PromelaConstants.FALSE:
				return "0";
			case PromelaConstants.SKIP_:
				return "1";
			case PromelaConstants.NUMBER:
				return Integer.toString(getNumber());
			default:
				return "1";
		}
	}

	/**
	 * @return The numeric value of this constant.
	 */
	public int getNumber() {
		return nr;
	}

	@Override
	public VariableType getResultType() {
		return VariableType.INT;
	}

	@Override
	public String getSideEffect() {
		return null;
	}

	@Override
	public Set<VariableAccess> readVariables() {
		return new HashSet<VariableAccess>();
	}

	@Override
	public String toString() {
		switch (getToken().kind) {
			case PromelaConstants.TRUE:
				return "true";
			case PromelaConstants.FALSE:
				return "false";
			case PromelaConstants.SKIP_:
				return "skip";
			case PromelaConstants.NUMBER:
				return Integer.toString(getNumber());
			default:
				return "1";
		}
	}
}

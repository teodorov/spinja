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

import spinja.promela.compiler.parser.ParseException;
import spinja.promela.compiler.parser.Token;
import spinja.promela.compiler.variable.Variable;
import spinja.promela.compiler.variable.VariableAccess;
import spinja.promela.compiler.variable.VariableType;

/**
 * An Identifiers is a reference to a variable. It can possibly also contain an expression which
 * calculated the number in the array of values it must have.
 * 
 * @author Marc de Jonge
 */
public class Identifier extends Expression {
	private static final long serialVersionUID = -5928789117017713005L;

	private Variable var = null;

	private Expression arrayExpr = null;

	/**
	 * Creates a new Identifier that refers to the specified variable.
	 * 
	 * @param token
	 *            The token that is stored for debug reasons.
	 * @param var
	 *            The variable to which this identifier points.
	 */
	public Identifier(final Token token, final Variable var) {
		super(token);
		this.var = var;
		arrayExpr = null;
	}

	/**
	 * Creates a new Identifier that refers to the specified variable on a specified place in the
	 * array (calculated using the expression).
	 * 
	 * @param token
	 *            The token that is stored for debug reasons.
	 * @param var
	 *            The variable to which this identifier points.
	 * @param expr
	 *            The expression that calculates the index in the array.
	 */
	public Identifier(final Token token, final Variable var, final Expression expr) {
		super(token);
		this.var = var;
		arrayExpr = expr;
	}

	/**
	 * @return The expression that is used for the array
	 */
	public Expression getArrayExpr() {
		return arrayExpr;
	}

	@Override
	public String getIntExpression() throws ParseException {
		if (var.getArraySize() > 1) {
			if (arrayExpr != null) {
				return var.getName() + "[" + arrayExpr.getIntExpression() + "]";
			} else {
				return var.getName() + "[0]";
			}
		} else {
			return var.getName();
		}
	}

	@Override
	public VariableType getResultType() throws ParseException {
		return var.getType();
	}

	@Override
	public String getSideEffect() throws ParseException {
		if (arrayExpr != null) {
			return arrayExpr.getSideEffect();
		} else {
			return null;
		}
	}

	/**
	 * @return The variable to which this identifier points.
	 */
	public Variable getVariable() {
		return var;
	}

	@Override
	public Set<VariableAccess> readVariables() {
		final Set<VariableAccess> set = new HashSet<VariableAccess>();
		set.add(new VariableAccess(var, arrayExpr));
		return set;
	}

	@Override
	public String toString() {
		if (var.getArraySize() > 1) {
			if (arrayExpr != null) {
				return var.getName() + "[" + arrayExpr.toString() + "]";
			} else {
				return var.getName() + "[0]";
			}
		} else {
			return var.getName();
		}
	}
}

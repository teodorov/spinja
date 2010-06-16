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

import java.util.Set;

import spinja.promela.compiler.parser.MyParseException;
import spinja.promela.compiler.parser.ParseException;
import spinja.promela.compiler.parser.Token;
import spinja.promela.compiler.variable.VariableAccess;
import spinja.promela.compiler.variable.VariableType;

/**
 * The abstract statement gives some standard functions that should help when implementing the real
 * functionality.
 * 
 * @author Marc de Jonge
 */
public abstract class Expression {
	private final Token token;

	/**
	 * Creates a new Expression using the specified token.
	 * 
	 * @param token
	 *            The token that is stored for debug reasons.
	 */
	public Expression(final Token token) {
		this.token = token;
	}

	/**
	 * Returns the javacode to represent this expression in such a way that the resulting javacode
	 * would return a boolean value. The default implementation is dependant on the
	 * getIntExpression(), so at least one of these two should be implemented (otherwise a endless
	 * loop will be the result!).
	 * 
	 * @return The java code to represent this expression in such a way that the result is a boolean
	 *         value.
	 * @throws ParseException
	 *             When something went wrong while parsing the expression.
	 */
	public String getBoolExpression() throws ParseException {
		return "(" + getIntExpression() + " != 0 )";
	}

	/**
	 * Returns the constant value of this expression. The default implementation throws a
	 * ParseException that says that the constant value of this expression can not be determined on
	 * compile-time.
	 * 
	 * @return The constant value of this expression for as far as they can be calculated by the
	 *         compiler.
	 * @throws ParseException
	 *             When it can not determine what the constant value is.
	 */
	public int getConstantValue() throws ParseException {
		throw new MyParseException("Constant value not be determened on compile-time.", getToken());
	}

	/**
	 * Returns the javacode to represent this expression in such a way that the resulting javacode
	 * would return a integer value. The default implementation is dependent on the
	 * {@link #getBoolExpression()}, so at least one of these two should be implemented (otherwise
	 * a endless loop will be the result!).
	 * 
	 * @return The java code to represent this expression in such a way that the result is a integer
	 *         value.
	 * @throws ParseException
	 *             When something went wrong while parsing the expression.
	 */
	public String getIntExpression() throws ParseException {
		return "(" + getBoolExpression() + " ? 1 : 0)";
	}

	/**
	 * @return The VariableType which determines the normal result type of this expression. Note
	 *         that many types can be cast to each other.
	 * @throws ParseException
	 *             When something went wrong while parsing the expression.
	 */
	public abstract VariableType getResultType() throws ParseException;

	/**
	 * @return The java code which represents the action for the side effect of the expression or
	 *         null if there is none. Note: currently only the run expression can give a side-effect
	 *         and it is advised that future addition should not have any side-effect.
	 * @throws ParseException
	 */
	public String getSideEffect() throws ParseException {
		return null;
	}

	/**
	 * @return The token that this expression is linked to. This is usefull for giving the place
	 *         where an error occurred.
	 */
	public final Token getToken() {
		return token;
	}

	/**
	 * @return The list of variable that are read during the evaluation of this expression.
	 */
	public abstract Set<VariableAccess> readVariables();

	@Override
	public String toString() {
		try {
			return getIntExpression();
		} catch (final ParseException ex) {
			return "";
		}
	}
}

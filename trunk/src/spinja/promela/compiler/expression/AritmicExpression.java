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

import spinja.promela.compiler.parser.MyParseException;
import spinja.promela.compiler.parser.ParseException;
import spinja.promela.compiler.parser.PromelaConstants;
import spinja.promela.compiler.parser.Token;
import spinja.promela.compiler.variable.VariableAccess;
import spinja.promela.compiler.variable.VariableType;

/**
 * The aritmic expression can be any expression that should return an integer value, e.g. any
 * mathematical calculation, binary expression or condition expression.
 * 
 * @author Marc de Jonge
 */
public class AritmicExpression extends Expression {
	private static final long serialVersionUID = -4022528945025403911L;

	private final Expression ex1, ex2, ex3;

	/**
	 * Creates a new AritmicExpression from only one subexpression.
	 * 
	 * @param token
	 *            The token that is used to determine what kind of calculation is does.
	 * @param only
	 *            The only subexpression.
	 */
	public AritmicExpression(final Token token, final Expression only) {
		this(token, only, null, null);
	}

	/**
	 * Creates a new AritmicExpression from two subexpressions.
	 * 
	 * @param token
	 *            The token that is used to determine what kind of calculation is does.
	 * @param left
	 *            The left part of the expression
	 * @param right
	 *            The right part of the expression.
	 */
	public AritmicExpression(final Token token, final Expression left, final Expression right) {
		this(token, left, right, null);
	}

	/**
	 * Creates a new AritmicExpression from three subexpressions.
	 * 
	 * @param token
	 *            The token that is used to determine what kind of calculation is does.
	 * @param first
	 *            The left part of the expression
	 * @param second
	 *            The middle part of the expression.
	 * @param third
	 *            The right part of the expression
	 */
	public AritmicExpression(final Token token, final Expression first, final Expression second,
		final Expression third) {
		super(token);
		ex1 = first;
		ex2 = second;
		ex3 = third;
	}

	@Override
	public String getBoolExpression() throws ParseException {
		if (ex2 == null) {
			return "(" + getToken().image + ex1.getIntExpression() + " != 0)";
		} else if (ex3 == null) {
			return "(" + ex1.getIntExpression() + " " + getToken().image + " "
					+ ex2.getIntExpression() + " != 0)";
		} else { // Can only happen with the x?1:0 expression
			return "(" + ex1.getBoolExpression() + " ? " + ex2.getBoolExpression() + " : "
					+ ex3.getBoolExpression() + ")";
		}
	}

	@Override
	public int getConstantValue() throws ParseException {
		switch (getToken().kind) {
			case PromelaConstants.BAND:
				return ex1.getConstantValue() & ex2.getConstantValue();
			case PromelaConstants.BOR:
				return ex1.getConstantValue() | ex2.getConstantValue();
			case PromelaConstants.BNOT:
				return ~ex1.getConstantValue();
			case PromelaConstants.MINUS:
				if (ex2 == null) {
					return -ex1.getConstantValue();
				} else {
					return ex1.getConstantValue() - ex2.getConstantValue();
				}
			case PromelaConstants.TIMES:
				return ex1.getConstantValue() * ex2.getConstantValue();
			case PromelaConstants.DIVIDE:
				return ex1.getConstantValue() / ex2.getConstantValue();
			case PromelaConstants.MODULO:
				return ex1.getConstantValue() % ex2.getConstantValue();
			case PromelaConstants.PLUS:
				return ex1.getConstantValue() + ex2.getConstantValue();
			case PromelaConstants.XOR:
				return ex1.getConstantValue() ^ ex2.getConstantValue();
			case PromelaConstants.LSHIFT:
				return ex1.getConstantValue() << ex2.getConstantValue();
			case PromelaConstants.RSHIFT:
				return ex1.getConstantValue() >>> ex2.getConstantValue();
		}
		throw new MyParseException("Unimplemented aritmic type: " + getToken().image, getToken());
	}

	@Override
	public String getIntExpression() throws ParseException {
		if (ex2 == null) {
			return "(" + getToken().image + ex1.getIntExpression() + ")";
		} else if (ex3 == null) {
			if (getToken().image.equals("%")) {
				// Modulo takes a special notation to make sure that it
				// returns a positive value
				return "Math.abs(" + ex1.getIntExpression() + " % " + ex2.getIntExpression() + ")";
			} else {
				return "(" + ex1.getIntExpression() + " " + getToken().image + " "
						+ ex2.getIntExpression() + ")";
			}
		} else {
			return "(" + ex1.getBoolExpression() + " ? " + ex2.getIntExpression() + " : "
					+ ex3.getIntExpression() + ")";
		}
	}

	@Override
	public VariableType getResultType() {
		return VariableType.INT;
	}

	@Override
	public String getSideEffect() throws ParseException {
		String effect = ex1.getSideEffect();
		if (ex2 != null) {
			if ((effect == null) || (ex2.getSideEffect() == null)) {
				effect = ex2.getSideEffect();
			} else {
				throw new MyParseException("More than one side-effect found in a single expression!",
					getToken());
			}
		}
		if (ex3 != null) {
			if ((effect == null) || (ex3.getSideEffect() == null)) {
				effect = ex3.getSideEffect();
			} else {
				throw new MyParseException("More than one side-effect found in a single expression!",
					getToken());
			}
		}
		return effect;
	}

	@Override
	public Set<VariableAccess> readVariables() {
		final Set<VariableAccess> rv = new HashSet<VariableAccess>();
		if (ex1 != null) {
			rv.addAll(ex1.readVariables());
		}
		if (ex2 != null) {
			rv.addAll(ex2.readVariables());
		}
		if (ex3 != null) {
			rv.addAll(ex3.readVariables());
		}
		return rv;
	}

	@Override
	public String toString() {
		if (ex2 == null) {
			return "(" + getToken().image + ex1.toString() + ")";
		} else if (ex3 == null) {
			return "(" + ex1.toString() + " " + getToken().image + " " + ex2.toString() + ")";

		} else {
			return "(" + ex1.toString() + " ? " + ex2.toString() + " : " + ex3.toString() + ")";
		}
	}
}

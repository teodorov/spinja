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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spinja.promela.compiler.Proctype;
import spinja.promela.compiler.Specification;
import spinja.promela.compiler.parser.MyParseException;
import spinja.promela.compiler.parser.ParseException;
import spinja.promela.compiler.parser.Token;
import spinja.promela.compiler.variable.VariableAccess;
import spinja.promela.compiler.variable.VariableType;
import spinja.util.StringWriter;

/**
 * The run expression represents the starting of a new proctype. The run expression can be used to
 * read the number of the proctype that has be created (and is therefor the only expression with a
 * sideeffect).
 * 
 * @author Marc de Jonge
 */
public class RunExpression extends Expression implements CompoundExpression {

	private final String id;

	private final Specification specification;

	private final List<Expression> exprs;

	/**
	 * Creates a new RunExpression using the identifier specified to run the proctype.
	 * 
	 * @param token
	 *            The token that is stored for debug reasons.
	 * @param id
	 *            The name of the proctype that is to be started.
	 */
	public RunExpression(final Token token, final Specification spec, final String id) {
		super(token);
		specification = spec;
		this.id = id;
		exprs = new ArrayList<Expression>();
	}

	public void addExpression(final Expression expr) throws ParseException {
		exprs.add(expr);
	}

	private String getArgs() throws ParseException {
		final StringWriter w = new StringWriter();
		for (final Expression expr : exprs) {
			w.append(expr == exprs.get(0) ? "" : ", ").append(expr.getIntExpression());
		}
		return w.toString();
	}

	@Override
	public String getBoolExpression() {
		return "true";
	}

	@Override
	public String getIntExpression() throws ParseException {
		final Proctype proc = specification.getProcess(id);
		if (proc == null) {
			throw new MyParseException("Could not find proctype", getToken());
		}
		return "addProcess(new " + proc.getName() + "(" + getArgs() + "))";
	}

	@Override
	public VariableType getResultType() {
		return VariableType.PID;
	}

	@Override
	public String getSideEffect() throws ParseException {
		return getIntExpression();
	}

	@Override
	public Set<VariableAccess> readVariables() {
		final Set<VariableAccess> rv = new HashSet<VariableAccess>();
		for (final Expression expr : exprs) {
			rv.addAll(expr.readVariables());
		}
		return rv;
	}

	@Override
	public String toString() {
		try {
			return "run " + id + "(" + getArgs() + ")";
		} catch (final Exception ex) {
			return "run " + id + "()";
		}
	}
}

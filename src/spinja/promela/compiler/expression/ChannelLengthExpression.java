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
import spinja.promela.compiler.variable.ChannelType;
import spinja.promela.compiler.variable.VariableAccess;
import spinja.promela.compiler.variable.VariableType;

public class ChannelLengthExpression extends Expression {
	private final Expression expr;

	public ChannelLengthExpression(final Token token, final Expression expr) throws ParseException {
		super(token);
		this.expr = expr;
		if (!expr.getResultType().canConvert(ChannelType.UNASSIGNED_CHANNEL)) {
			throw new MyParseException("A len operator can only be executed on a channel!",
				getToken());
		}
	}

	@Override
	public String getIntExpression() throws ParseException {
		return "_channels[" + expr.getIntExpression() + "].length()";
	}

	@Override
	public VariableType getResultType() {
		return VariableType.INT;
	}

	@Override
	public Set<VariableAccess> readVariables() {
		return expr.readVariables();
	}

	@Override
	public String toString() {
		return "len(" + expr.toString() + ")";
	}
}

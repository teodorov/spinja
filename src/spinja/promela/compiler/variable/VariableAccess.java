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

package spinja.promela.compiler.variable;

import spinja.promela.compiler.expression.Expression;

public class VariableAccess {
	private static boolean equal(final Object o1, final Object o2) {
		if (o1 == null) {
			return o2 == null;
		} else {
			return o1.equals(o2);
		}
	}

	private final Variable var;

	private final Expression arrayExpr;

	public VariableAccess(final Variable var) {
		this(var, null);
	}

	public VariableAccess(final Variable var, final Expression arrayExpr) {
		this.var = var;
		this.arrayExpr = arrayExpr;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof VariableAccess) {
			final VariableAccess a = (VariableAccess) obj;
			return VariableAccess.equal(var, a.var) && VariableAccess.equal(arrayExpr, a.arrayExpr);
		}
		return false;
	}

	public Expression getArrayExpr() {
		return arrayExpr;
	}

	public Variable getVar() {
		return var;
	}

	@Override
	public int hashCode() {
		return var.hashCode() ^ (arrayExpr != null ? arrayExpr.hashCode() : 0xAAAA);
	}
}

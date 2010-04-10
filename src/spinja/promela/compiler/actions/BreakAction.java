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

import spinja.promela.compiler.parser.ParseException;
import spinja.promela.compiler.parser.Token;
import spinja.util.StringWriter;

public class BreakAction extends Action {

	public BreakAction(Token token) {
		super(token);
	}

	@Override
	public String getEnabledExpression() throws ParseException {
		return null;
	}

	@Override
	public void printTakeStatement(StringWriter w) throws ParseException {
		w.appendLine("break;");
	}

	@Override
	public String toString() {
		return "break";
	}

}

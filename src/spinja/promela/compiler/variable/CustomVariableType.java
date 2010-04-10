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

import spinja.promela.compiler.parser.ParseException;
import spinja.util.StringWriter;

public class CustomVariableType extends VariableType {
	private final VariableStore store;

	public CustomVariableType(final String name) {
		super(name, name + "Type", 0);
		store = new VariableStore();
	}

	@Override
	public boolean canConvert(final VariableType type) {
		return type == this;
	}

	public VariableStore getVariableStore() {
		return store;
	}

	@Override
	public void writeVariableClass(final StringWriter w) throws ParseException {
		w.appendLine("class ", getJavaName(), " implements Storable {");
		w.indent();
		store.printDefinitions(w);

		super.writeVariableClass(w);
	}
}

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

package spinja.options;

import java.util.Map;
import java.util.TreeMap;

import spinja.util.StringWriter;

public class MultiStringOption extends Option {
	private final Map<String, String> strings;

	private final Map<String, Boolean> set;

	public MultiStringOption(final char letter, final String description, 
							 final String[] valid, final String[] descriptions) {
		super(letter, description);
		strings = new TreeMap<String, String>();
		set = new TreeMap<String, Boolean>();
		for (int i = 0; i < valid.length; i++) {
			strings.put(valid[i], descriptions[i]);
			set.put(valid[i], false);
		}
	}

	@Override
	public boolean isSet() {
		return false;
	}

	public boolean isSet(final String name) {
		return set.get(name);
	}

	@Override
	public void parseOption(final String rest) {
		if (!set.containsKey(rest)) {
			System.out.println("Warning: -" + getChar() + rest + " not found as an option");
			return;
		}

		set.put(rest, true);
	}

	@Override
	public String toString() {
		final StringWriter all = new StringWriter();
		for (final String s : strings.keySet()) {
			StringWriter w = new StringWriter();
			if (all.length() > 0) 
				all.append("\n"); // add new line for new option
			w.append("   -");
			w.append(getChar());
			w.append(s);
			while (w.length() < Option.TABPOS) {
				w.append(' ');
			}
			w.append(indentedDescription(0, strings.get(s)));
			all.append(w);
		}
		return all.toString();
	}
}

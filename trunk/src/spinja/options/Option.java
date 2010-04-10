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

import java.io.BufferedReader;
import java.io.StringReader;
import spinja.util.StringWriter;

public abstract class Option implements Comparable<Option> {
	private final char letter;
	protected final static int TABPOS = 24;

	private final String description;

	public Option(final char letter, final String description) {
		this.letter = letter;
		this.description = description;
	}

	public int compareTo(final Option o) {
		return getChar() - o.getChar();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Option)) {
			return false;
		}
		return getChar() == ((Option) obj).getChar();
	}

	public final char getChar() {
		return letter;
	}

	public final String getDescription() {
		return description;
	}

	public abstract boolean isSet();

	public abstract void parseOption(String rest);

	@Override
	public String toString() {
		return toString("");
	}

	public String toString(final String text) {
		final StringWriter w = new StringWriter();
		w.append("   -").append(getChar()).append(text);
		w.append(indentedDescription(TABPOS-w.length(), getDescription()));
		return w.toString();
	}

	protected String indentedDescription(int indent, final String descr) {
		final StringWriter w = new StringWriter();
		while (w.length() < indent) {
			w.append(' ');
		}
		try {
			BufferedReader reader = new BufferedReader(new StringReader(descr));
			String s = reader.readLine();
			while (s != null) {
				w.append(s);
				s = reader.readLine();
				if (s != null) {
					w.append("\n");
					for (int i=0; i<TABPOS; i++)
						w.append(' ');
				}
			}
		} catch (java.io.IOException ex) {
			ex.printStackTrace();
		}
		return w.toString();
	}
}

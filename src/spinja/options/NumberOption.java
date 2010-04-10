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

public class NumberOption extends Option {

	private final int defValue, minValue, maxValue;

	private int value;

	public NumberOption(final char letter, final String description, 
						final int defValue, final int minValue, final int maxValue) {
		super(letter, description);
		this.defValue = defValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
		value = minValue - 1;
	}

	public int getValue() {
		if (value == minValue - 1) {
			return defValue;
		} else {
			return value;
		}
	}

	@Override
	public boolean isSet() {
		return value == minValue - 1;
	}

	@Override
	public void parseOption(final String rest) {
		try {
			value = Integer.parseInt(rest);
			if (value < minValue) {
				System.out.println("Warning for option -" + getChar()
									+ ": value below reach, assuming " + minValue);
				value = minValue;
			} else if (value > maxValue) {
				System.out.println("Warning for option -" + getChar()
									+ ": value above reach, assuming " + maxValue);
				value = maxValue;
			}
		} catch (final NumberFormatException ex) {
			System.out.println("Warning for option -" + getChar()
								+ ": value not a valid number, assuming " + defValue);
			value = defValue;
		}

	}

	@Override
	public String toString() {
		String descr = toString("[" + minValue + ".." + maxValue + "]");
		String def   = indentedDescription(TABPOS, "(default: " + defValue + ")");
		return descr + "\n" + def;
	}
}

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

public class VariableType {
	public static VariableType BIT = new VariableType("bit", "int", 1);

	public static VariableType BOOL = new VariableType("bool", "int", 1);

	public static VariableType BYTE = new VariableType("byte", "int", 8);

	public static VariableType PID = new VariableType("pid", "int", 8);

	public static VariableType SHORT = new VariableType("short", "int", 16);

	public static VariableType INT = new VariableType("int", "int", 32);

	public static VariableType MTYPE = new VariableType("mtype", "int", 8) {
		@Override
		public boolean canConvert(VariableType type) {
			return type == VariableType.MTYPE;
		}
	};

	private final String name, javaName;

	private final int bits;

	protected VariableType(final String name, final String javaName, final int bits) {
		this.name = name;
		this.javaName = javaName;
		this.bits = bits;
	}

	public boolean canConvert(final VariableType type) {
		return (type == VariableType.BIT) || (type == VariableType.BOOL)
				|| (type == VariableType.BYTE) || (type == VariableType.PID)
				|| (type == VariableType.SHORT) || (type == VariableType.INT);
	}

	public void generateClass(final StringWriter w) {
		// Do nothing for standard classes :)
	}

	public int getBits() {
		return bits;
	}

	public String getJavaName() {
		return javaName;
	}

	public String getMask() {
		if (bits >= 32) {
			return null;
		} else {
			return "0x" + Integer.toHexString((1 << bits) - 1);
		}
	}
	
	public int getMaskInt() {
		if (bits >= 32) {
			return -1;
		} else {
			return ((1 << bits) - 1);
		}
	}

	public String getName() {
		return name;
	}

	public void writeVariableClass(final StringWriter w) throws ParseException {
		// For standard classes (int, byte etc..) this is not needed.
	}
}

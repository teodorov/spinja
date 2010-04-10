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

import spinja.util.StringWriter;

public class ChannelVariable extends Variable {

	private static boolean channelsUsed = false, rendezvousUsed = false;

	public static boolean isChannelsUsed() {
		return channelsUsed;
	}

	public static boolean isRendezvousUsed() {
		return rendezvousUsed;
	}

	public ChannelVariable(final String name, final int arraySize) {
		super(ChannelType.UNASSIGNED_CHANNEL, name, arraySize);
		ChannelVariable.channelsUsed = true;
	}

	private void addChannelInit(final StringWriter w, final String addition) {
		if (getType() == ChannelType.UNASSIGNED_CHANNEL) {
			w.appendLine(getName(), addition, " = (byte)-1; ");
		} else {
			w.appendLine(getName(), addition, " = ", "addChannel(new Channel", getType().getId(),
				"());");
		}
	}

	@Override
	public ChannelType getType() {
		return (ChannelType) super.getType();
	}

	@Override
	public void setType(VariableType type) {
		if (!(type instanceof ChannelType)) {
			throw new IllegalArgumentException("Type must be a ChannelType");
		}
		super.setType(type);
	}

	@Override
	public void printInitExpr(final StringWriter w) {
		if (getArraySize() > 1) {
			w.appendLine(getName(), " = new ", getType().getJavaName(), "[", getArraySize(), "];");
			w.appendLine("for(int _i = 0; _i < ", getArraySize(), "; _i++) {");
			w.indent();
			addChannelInit(w, "[_i]");
			w.outdent();
			w.appendLine("}");
		} else {
			addChannelInit(w, "");
		}
	}
}

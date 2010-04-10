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

import java.util.ArrayList;
import java.util.List;

import spinja.util.StringWriter;

public class ChannelType extends VariableType {
	public static final VariableType UNASSIGNED_CHANNEL = new ChannelType(-1, -1);

	public static boolean rendezvousUsed = false;

	public static boolean isRendezvousUsed() {
		return rendezvousUsed;
	}

	private final int bufferSize;

	private final VariableStore vars;

	private final List<VariableType> types;

	private final int id;

	public ChannelType(int id, int bufferSize) {
		super("chan", "int", 8);
		vars = new VariableStore();
		types = new ArrayList<VariableType>();
		this.id = id;
		this.bufferSize = bufferSize;
	}

	private void generateStorableMethods(final StringWriter w) {
		// w.appendLine("public int encode(byte[] _buffer, int _cnt) {");
		w.appendLine("public void encode(DataWriter _writer) {");
		w.indent();
		// w.appendLine("_buffer[_cnt++] = ", id, ";");
		w.appendLine("_writer.writeByte(", id, ");");
		if (bufferSize > 0) {
			// w.appendLine("_buffer[_cnt++] = (byte)filled;");
			w.appendLine("_writer.writeByte(filled);");
			w.appendLine("for(int i = 0; i < filled; i++) {");
			w.indent();
			vars.printEncode(w);
			w.outdent();
			w.appendLine("}");
		}
		// w.appendLine("return _cnt;");
		w.outdent();
		w.appendLine("}");
		w.appendLine();
		w.appendLine("public int getSize() {");
		w.indent();
		if (bufferSize > 0) {
			w.appendLine("return 2 + filled * ", vars.getBufferSize(), ";");
		} else {
			w.appendLine("return 1;");
		}
		w.outdent();
		w.appendLine("}");
		w.appendLine();
		// w.appendLine("public int decode(byte[] _buffer, int _cnt) {");
		w.appendLine("public boolean decode(DataReader _reader) {");
		w.indent();
		// w.appendLine("if(_buffer[_cnt++] != ", id, ") return -1;");
		w.appendLine("if(_reader.readByte() != ", id, ") return false;");
		if (bufferSize > 0) {
			// w.appendLine("filled = _buffer[_cnt++] & 0xff;");
			w.appendLine("filled = _reader.readByte();");
			w.appendLine("first = 0;");
			w.appendLine("for(int i = 0; i < filled; i++) {");
			w.indent();
			vars.printDecode(w);
			w.outdent();
			w.appendLine("}");
		}
		// w.appendLine("return _cnt;");
		w.appendLine("return true;");
		w.outdent();
		w.appendLine("}");
	}

	@Override
	public boolean canConvert(VariableType type) {
		return type instanceof ChannelType;
	}

	public int getId() {
		return id;
	}

	@Override
	public void generateClass(StringWriter w) {
		w.appendLine("public static class Channel", id, " extends Channel {");
		w.indent();
		w.appendLine("public Channel", id, "() {");
		w.indent();

		w.appendPrefix().append("super(new int[] {");
		for (VariableType type : types) {
			if (type.getMask() == null) {
				w.append("0xffffffff");
			} else {
				w.append(type.getMask());
			}
			w.append(", ");
		}
		w.setLength(w.length() - 2);
		w.append("}, ").append(bufferSize).append(");").appendPostfix();

		// w.appendLine("super(", types.size(), ", ", bufferSize, ");");
		w.outdent();
		w.appendLine("}");
		w.appendLine();
		generateStorableMethods(w);
		w.outdent();
		w.appendLine("}");
		w.appendLine();

	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void addType(final VariableType type) {
		final Variable var = new Variable(type, "buffer[(i+first)%buffer.length][" + types.size()
												+ "]", 1);
		var.setRead(true);
		var.setWritten(true);
		vars.addVariable(var);
		types.add(type);
	}

}

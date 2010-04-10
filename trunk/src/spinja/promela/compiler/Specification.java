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

package spinja.promela.compiler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import spinja.promela.compiler.parser.ParseException;
import spinja.promela.compiler.variable.ChannelType;
import spinja.promela.compiler.variable.ChannelVariable;
import spinja.promela.compiler.variable.Variable;
import spinja.promela.compiler.variable.VariableStore;
import spinja.util.StringWriter;

public class Specification implements Iterable<Proctype> {
	private final String name;

	private final List<Proctype> procs;

	private final List<ChannelType> channels;

	private Proctype never;

	// private final Map<String, VariableType> usertypes;

	private final VariableStore varStore;

	private final List<String> mtypes;

	public Specification(final String name) {
		this.name = name;
		procs = new ArrayList<Proctype>();
		channels = new ArrayList<ChannelType>();
		// usertypes = new HashMap<String, VariableType>();
		varStore = new VariableStore();
		mtypes = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	/**
	 * Creates a new Channel type for in this Specification.
	 * 
	 * @param bufferSize
	 * @return The new ChannelType
	 */
	public ChannelType newChannelType(int bufferSize) {
		ChannelType type = new ChannelType(channels.size(), bufferSize);
		channels.add(type);
		return type;
	}

	public boolean usesRendezvousChannel() {
		for (ChannelType t : channels) {
			if (t.getBufferSize() == 0) {
				return true;
			}
		}
		return false;
	}

	public void addMType(final String name) {
		mtypes.add(name);
	}

	public void addProc(final Proctype proc) throws ParseException {
		if (getProcess(proc.getName()) != null) {
			throw new ParseException("Duplicate proctype with name: " + proc.getName());
		}
		procs.add(proc);
	}

	// public void addType(final VariableType type) throws ParseException {
	// if (usertypes.containsKey(type.getName())) {
	// throw new ParseException("Duplicate type declaration with name: " + type.getName());
	// }
	// usertypes.put(type.getName(), type);
	// }

	private void generateConstructor(final StringWriter w) throws ParseException {
		w.appendLine("public ", name, "Model() throws SpinJaException {").indent();
		w.appendLine("super(\"", name, "\", ", varStore.getBufferSize() + 1
												+ (ChannelVariable.isChannelsUsed() ? 1 : 0)
												+ (usesAtomic() ? 1 : 0), ");");
		w.appendLine();
		w.appendLine("// Initialize the default values");
		for (final Variable var : varStore.getVariables()) {
			var.printInitExpr(w);
		}
		w.appendLine();
		w.appendLine("// Initialize the starting processes");
		for (final Proctype proc : procs) {
			for (int i = 0; i < proc.getNrActive(); i++) {
				w.appendLine("addProcess(new ", proc.getName(), "());");
			}
		}
		w.appendLine();
		w.outdent().appendLine("}");
		w.appendLine();
	}

	private void generateMain(final StringWriter w) {
		w.appendLine("public static void main(String[] args) {").indent();
		w.appendLine("Run run = new Run();");
		w.appendLine("run.parseArguments(args,\"" + name + "\");");
		w.appendLine("run.search(" + name + "Model.class);");
		w.outdent().appendLine("}");
		w.appendLine();
	}

	/**
	 * Generates the complete Model object and returns it as a String.
	 * 
	 * @return A String that holds the complete Model for this Specification.
	 * @throws ParseException
	 *             When something went wrong while parsing the promela file.
	 */
	public String generateModel() throws ParseException {
		final StringWriter w = new StringWriter();

		// The header
		w.appendLine("package spinja;");
		// [07-Apr-2010 12:10 ruys] was: w.appendLine("package spinja.generated;");
		w.appendLine();
		w.appendLine("import spinja.util.DataReader;");
		w.appendLine("import spinja.util.DataWriter;");
		w.appendLine("import spinja.Run;");
		w.appendLine("import spinja.promela.model.*;");
		w.appendLine("import spinja.exceptions.*;");
		w.appendLine();
		w.appendLine("public class ", name, "Model extends PromelaModel {").indent();
		w.appendLine();

		generateMain(w);
		generateCustomTypes(w);
		generateVariables(w);
		generateConstructor(w);
		// genarateNextTransition(w);
		generateStore(w);
		generateToString(w);
		generateProctypes(w);

		w.outdent().appendLine("}");

		return w.toString();
	}

	private void generateCustomTypes(StringWriter w) {
		for (final ChannelType type : channels) {
			type.generateClass(w);
		}
	}

	private void generateProctypes(final StringWriter w) throws ParseException {
		for (final Proctype proc : procs) {
			w.appendLine();
			proc.generateCode(w);
		}

		// Generate never claim
		if (never != null) {
			w.appendLine("public PromelaProcess getNever() throws ValidationException {").indent();
			w.appendLine("return new never();");
			w.outdent().appendLine("}").appendLine();
			never.generateCode(w);
		}
	}

	private void generateStore(final StringWriter w) {
		w.appendLine("public void encode(DataWriter _writer) {").indent();
		// w.appendLine("_buffer[_cnt++] = (byte)_nrProcs;");
		// w.appendLine("_buffer[_cnt++] = (byte)_exclusive;");
		w.appendLine("_writer.writeByte(_nrProcs);");
		if (usesAtomic()) {
			w.appendLine("_writer.writeByte(_exclusive);");
		}
		if (ChannelVariable.isChannelsUsed()) {
			// w.appendLine("_buffer[_cnt++] = (byte)_nrChannels;");
			w.appendLine("_writer.writeByte(_nrChannels);");
		}
		varStore.printEncode(w);
		if (ChannelVariable.isChannelsUsed()) {
			w.appendLine("for(int _i = 0; _i < _nrChannels; _i++) {");
			w.indent();
			// w.appendLine("_cnt = _channels[_i].encode(_buffer, _cnt);");
			w.appendLine("_channels[_i].encode(_writer);");
			w.outdent();
			w.appendLine("}");
		}
		w.appendLine("for(int _i = 0; _i < _nrProcs; _i++) {");
		w.indent();
		// w.appendLine("_cnt = _procs[_i].encode(_buffer, _cnt);");
		w.appendLine("_procs[_i].encode(_writer);");
		w.outdent();
		w.appendLine("}");
		// w.appendLine("return _cnt;");
		w.outdent().appendLine("}");
		w.appendLine();

		// w.appendLine("public int decode(byte[] _buffer, int _cnt) {").indent();
		w.appendLine("public boolean decode(DataReader _reader) {").indent();
		// w.appendLine("int _start = _cnt;");
		// w.appendLine("_nrProcs = _buffer[_cnt++] & 0xff;");
		// w.appendLine("_exclusive = _buffer[_cnt++] & 0xff;");
		w.appendLine("_nrProcs = _reader.readByte();");
		if (usesAtomic()) {
			w.appendLine("_exclusive = _reader.readByte();");
		}
		if (ChannelVariable.isChannelsUsed()) {
			// w.appendLine("_nrChannels = _buffer[_cnt++] & 0xff;");
			w.appendLine("_nrChannels = _reader.readByte();");
		}
		varStore.printDecode(w);
		if (ChannelVariable.isChannelsUsed()) {
			w.appendLine();
			w.appendLine("for(int _i = 0; _i < _nrChannels; _i++) {");
			{
				w.indent();
				// w.appendLine("int _newCnt = _channels[_i].decode(_buffer, _cnt);");
				w.appendLine("_reader.storeMark();");
				// w.appendLine("if(_newCnt == -1) {");
				w.appendLine("if(_channels[_i] == null || !_channels[_i].decode(_reader)) {");
				{
					w.indent();
					w.appendLine("_reader.resetMark();");
					// w.appendLine("switch(_buffer[_cnt]) {");
					w.appendLine("switch(_reader.peekByte()) {");
					{
						w.indent();
						for (int i = 0; i < channels.size(); i++) {
							w.appendLine("case ", i, ": _channels[_i] = new Channel", channels.get(
								i).getId(), "(); break;");
						}
						w.appendLine("default: return false;");
						w.outdent();
					}
					w.appendLine("}");
					// w.appendLine("_cnt = _channels[_i].decode(_buffer, _cnt);");
					w.appendLine("if(!_channels[_i].decode(_reader)) return false;");
					// w.appendLine("assert(_cnt >= 0);");
					w.outdent();
				}
				// w.appendLine("} else {");
				// {
				// w.indent();
				// w.appendLine("_cnt = _newCnt;");
				// w.outdent();
				// }
				w.appendLine("}");
				w.outdent();
			}
			w.appendLine("}");
		}
		w.appendLine();
		w.appendLine("int _start = _reader.getMark();");
		w.appendLine("for(int _i = 0; _i < _nrProcs; _i++) {");
		{
			w.indent();
			// w.appendLine("int _newCnt = _procs[_i].decode(_buffer, _cnt);");
			w.appendLine("_reader.storeMark();");
			// w.appendLine("if(_newCnt == -1) {");
			w.appendLine("if(_procs[_i] == null || !_procs[_i].decode(_reader)) {");
			{
				w.indent();
				w.appendLine("_reader.resetMark();");
				// w.appendLine("switch(_buffer[_cnt]) {");
				w.appendLine("switch(_reader.peekByte()) {");
				{
					w.indent();
					for (int i = 0; i < procs.size(); i++) {
						w.appendLine("case ", i, ": _procs[_i] = new ", procs.get(i).getName(),
							"(true); break;");
					}
					w.appendLine("default: return false;");
					w.outdent();
				}
				w.appendLine("}");
				// w.appendLine("_cnt = _procs[_i].decode(_buffer, _cnt);");
				// w.appendLine("assert(_cnt >= 0);");
				w.appendLine("if(!_procs[_i].decode(_reader)) return false;");
				w.outdent();
			}
			// w.appendLine("} else {");
			// {
			// w.indent();
			// w.appendLine("_cnt = _newCnt;");
			// w.outdent();
			// }
			w.appendLine("}");
			w.outdent();
		}
		w.appendLine("}");
		// w.appendLine("_buffer_size = _cnt - _start;");
		w.appendLine("_process_size = _reader.getMark() - _start;");
		// w.appendLine("return _cnt;");
		w.appendLine("return true;");
		w.outdent().appendLine("}");
		w.appendLine();
	}

	private void generateToString(final StringWriter w) {
		w.appendLine("public String toString() {").indent();
		w.appendLine("StringBuilder sb = new StringBuilder();");
		w.appendLine("sb.append(\"", name, "Model: \");");
		varStore.printToString(w);
		w.appendLine("for(int i = 0; i < _nrProcs; i++) {");
		w.appendLine("  sb.append(\'\\n\').append(_procs[i]);");
		w.appendLine("}");
		w.appendLine("for(int i = 0; i < _nrChannels; i++) {");
		w.appendLine("  sb.append(\'\\n\').append(_channels[i]);");
		w.appendLine("}");
		w.appendLine("return sb.toString();");
		w.outdent().appendLine("}");
	}

	private void generateVariables(final StringWriter w) {
		// Create the variables
		for (final Variable var : varStore.getVariables()) {
			if (var.getName().charAt(0) != '_') {
				w.appendLine(var.getType().getJavaName(), (var.getArraySize() > 1 ? "[]" : ""),
					" ", var.getName(), ";");
			}
		}

		w.appendLine();

		// // Create the variable classes
		// for (final VariableType c : usertypes.values()) {
		// if (c != null) {
		// c.generateClass(w);
		// }
		// }
	}

	public int getMType(final String name) {
		return mtypes.indexOf(name);
	}

	public Proctype getNever() {
		return never;
	}

	public Proctype getProcess(final String name) {
		for (final Proctype proc : procs) {
			if (proc.getName().equals(name)) {
				return proc;
			}
		}
		return null;
	}

	// public VariableType getType(final String name) throws ParseException {
	// if (usertypes.containsKey(name)) {
	// return usertypes.get(name);
	// } else {
	// throw new ParseException("Could not find a type with name: " + name);
	// }
	// }

	public VariableStore getVariableStore() {
		return varStore;
	}

	public boolean usesAtomic() {
		for (final Proctype p : procs) {
			if (p.getAutomaton().hasAtomic()) {
				return true;
			}
		}
		return false;
	}

	public Iterator<Proctype> iterator() {
		return procs.iterator();
	}

	public void setNever(final Proctype never) throws ParseException {
		if (this.never != null) {
			throw new ParseException("Duplicate never claim");
		}
		this.never = never;
	}
}

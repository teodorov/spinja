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
import java.util.List;

import spinja.promela.compiler.automaton.Automaton;
import spinja.promela.compiler.automaton.State;
import spinja.promela.compiler.expression.Expression;
import spinja.promela.compiler.expression.Identifier;
import spinja.promela.compiler.parser.ParseException;
import spinja.promela.compiler.variable.Variable;
import spinja.promela.compiler.variable.VariableContainer;
import spinja.promela.compiler.variable.VariableStore;
import spinja.util.StringWriter;

/**
 * This object represents a process in the Promela language. It contains a number of 'local'
 * variables, of which the first couple are the arguments. Also it contains a starting Node which
 * points to a complete graph. This graph represents all the actions that can be executed by this
 * process.
 * 
 * @author Marc de Jonge
 */
public class Proctype implements VariableContainer {
	/**
	 * The specification to which this {@link Proctype} belongs
	 */
	private final Specification specification;

	/**
	 * The ID number of this {@link Proctype}.
	 */
	private final int id;

	/**
	 * The number of active processes that are started when the model checking begins
	 */
	private final int nrActive;

	/**
	 * The priority that is only used when ran randomly.
	 */
	@SuppressWarnings("unused")
	private int priority;

	/**
	 * The name of the process in the Model.
	 */
	private final String name;

	/**
	 * The starting Node which points to the complete graph.
	 */
	private final Automaton automaton;

	/**
	 * The store where all the variables are stored.
	 */
	private final VariableStore varStore;

	/**
	 * The expression which can enable or disable all actions.
	 */
	private Expression enabler;

	/**
	 * While this boolean is true, each variable that is added to this {@link Proctype} is assumed
	 * to be an argument.
	 */
	private boolean isArgument;

	/**
	 * Here all the arguments are store. Please note that these are also stored in the
	 * VariableStore!
	 */
	private final List<Variable> arguments;

	private final List<Variable> channelXR = new ArrayList<Variable>();

	private final List<Variable> channelXS = new ArrayList<Variable>();

	/**
	 * Creates a new {@link Proctype} object.
	 * 
	 * @param specification
	 *            The specification in which this {@link Proctype} is defined.
	 * @param id
	 *            The identifying number of this {@link Proctype}
	 * @param nrActive
	 *            The number of processes that should be started when the model checking starts.
	 * @param name
	 *            The name of the {@link Proctype}.
	 */
	public Proctype(final Specification specification, final int id, final int nrActive,
		final String name) {
		this.specification = specification;
		this.id = id;
		this.nrActive = nrActive;
		this.name = name;
		automaton = new Automaton(this);
		varStore = new VariableStore();
		priority = 0;
		enabler = null;
		isArgument = true;
		arguments = new ArrayList<Variable>();
	}

	/**
	 * Adds a new variable to this {@link Proctype}. While the lastArgument() function is not
	 * called this function assumes that every variable that is added is also an argument.
	 * 
	 * @param var
	 *            The variable that is to be added.
	 */
	public void addVariable(final Variable var) {
		varStore.addVariable(var);
		if (isArgument) {
			arguments.add(var);
		}
	}

	/**
	 * Adds an identifier that points to a channel to the list of eXclusive Reads
	 * @param id
	 *            The identifier.
	 */
	public void addXR(final Identifier id) {
		channelXR.add(id.getVariable());
	}

	/**
	 * Adds an identifier that points to a channel to the list of eXclusive Sends
	 * @param id
	 *            The identifier.
	 */
	public void addXS(final Identifier id) {
		channelXS.add(id.getVariable());
	}

	/**
	 * Generates the code for the {@link Proctype}.
	 * @param w
	 *            The StringWriter that is used to output the code.
	 * @throws ParseException
	 *             When something went wrong while creating the source code.
	 */
	public void generateCode(final StringWriter w) throws ParseException {
		w.appendLine("public class ", getName(), " extends PromelaProcess {");
		w.indent();

		generateLocalVars(w);
		generateConstructor(w);
		generateStorable(w);
		generateToString(w);

		if (enabler != null) {
			// TODO
		}

		w.outdent();
		w.appendLine("}");
	}

	protected void generateConstructor(final StringWriter w) throws ParseException {
		w.appendLine("public ", getName(), "(boolean decoding) {").indent();
		{
			w.appendLine("super(", getSpecification().getName(), "Model.this, new State[",
				automaton.size(), "], ", automaton.getStartState().getStateId(), ");");
			w.appendLine();
			// Generate the table
			automaton.generateTable(w);
			w.outdent().appendLine("}").appendLine();
		}

		w.appendLine("public ", getName(), "(", getArgs(), ") throws ValidationException {")
				.indent();
		{
			w.appendLine("this(false);");
			w.appendLine();
			// Initialize default values for non arguments
			for (final Variable var : varStore.getVariables()) {
				if (!arguments.contains(var)) {
					var.printInitExpr(w);
				}
			}
			if (!arguments.isEmpty()) {
				w.appendLine();
				
				// Store arguments
				for (final Variable var : arguments) {
					w.appendLine("this.", var.getName(), " = param_", var.getName(), ";");
				}
			}
			w.outdent().appendLine("}").appendLine();
		}
	}

	protected void generateLocalVars(final StringWriter w) {
		for (final Variable var : varStore.getVariables()) {
			w.appendLine("protected ", var.getType().getJavaName(), (var.getArraySize() > 1	? "[]"
																							: ""),
				" ", var.getName(), ";");
		}
		w.appendLine();
	}

	/*
	 * This part is the implementation of the VariableContainer that this proctype is...
	 */

	protected void generateStorable(final StringWriter w) {
		// The getSize function
		w.appendLine("public int getSize() {");
		w.appendLine("  return " + (varStore.getBufferSize() + 2) + ";");
		w.appendLine("}");
		w.appendLine();

		// The store function
		// w.appendLine("public int encode(byte[] _buffer, int _cnt) {");
		// w.indent();
		// w.appendLine("_buffer[_cnt++] = 0x", Integer.toHexString(id), ";");
		// w.appendLine("_buffer[_cnt++] = (byte)_sid;");
		// varStore.printEncode(w);
		// w.appendLine("return _cnt;");
		// w.outdent();
		// w.appendLine("}");
		// w.appendLine();
		w.appendLine("public void encode(DataWriter _writer) {");
		w.indent();
		w.appendLine("_writer.writeByte(0x", Integer.toHexString(id), ");");
		w.appendLine("_writer.writeByte(_sid);");
		varStore.printEncode(w);
		w.outdent();
		w.appendLine("}");
		w.appendLine();

		// The restore function
		// w.appendLine("public int decode(byte[] _buffer, int _cnt) {");
		// w.indent();
		// w.appendLine("if(_buffer[_cnt++] != 0x", Integer.toHexString(id), ") return -1;");
		// w.appendLine("_sid = _buffer[_cnt++] & 0xff;");
		// varStore.printDecode(w);
		// w.appendLine("return _cnt;");
		// w.outdent();
		// w.appendLine("}");
		// w.appendLine();
		w.appendLine("public boolean decode(DataReader _reader) {");
		w.indent();
		w.appendLine("if(_reader.readByte() != 0x", Integer.toHexString(id), ") return false;");
		w.appendLine("_sid = _reader.readByte();");
		varStore.printDecode(w);
		w.appendLine("return true;");
		w.outdent();
		w.appendLine("}");
		w.appendLine();
	}

	protected void generateToString(final StringWriter w) {
		// The toString function
		w.appendLine("public String toString() {");
		w.indent();
		w.appendLine("StringBuilder sb = new StringBuilder();");
		w.appendLine("if(_exclusive == _pid) sb.append(\"<atomic>\");");
		w.appendLine("sb.append(\"" + getName() + " (\" + _pid + \",\" + _sid + \"): \");");
		varStore.printToString(w);
		w.appendLine("return sb.toString();");
		w.outdent();
		w.appendLine("}");
		w.appendLine();
	}

	protected String getArgs() {
		final StringWriter w = new StringWriter();
		for (final Variable var : arguments) {
			w.append(var == arguments.get(0) ? "" : ", ")
					.append(var.getType().getJavaName())
					.append(" param_")
					.append(var.getName());
		}
		return w.toString();
	}

	/**
	 * @return The Automaton that describes the actions of this {@link Proctype}
	 */
	public Automaton getAutomaton() {
		return automaton;
	}

	/**
	 * @return The name of this {@link Proctype}.
	 */
	public final String getName() {
		return name;
	}

	/* Here all the generating code is places */

	/**
	 * @return The number of active processes of this type that have to be started when the model
	 *         checking starts.
	 */
	public int getNrActive() {
		return nrActive;
	}

	/**
	 * @return The {@link Specification} to which this {@link Proctype} belongs.
	 */
	public Specification getSpecification() {
		return specification;
	}

	/**
	 * @return The starting node which points to the complete graph with all the options.
	 */
	public State getStartState() {
		return automaton.getStartState();
	}

	/**
	 * @see spinja.promela.compiler.variable.VariableContainer#getVariable(java.lang.String)
	 */
	public Variable getVariable(final String name) {
		return varStore.getVariable(name);
	}

	/**
	 * @see spinja.promela.compiler.variable.VariableContainer#getVariables()
	 */
	public List<Variable> getVariables() {
		return varStore.getVariables();
	}

	/**
	 * @see spinja.promela.compiler.variable.VariableContainer#hasVariable(java.lang.String)
	 */
	public boolean hasVariable(final String name) {
		return name.equals("_pid") || varStore.hasVariable(name);
	}

	/* Exclusive send and read functions */

	/**
	 * @param var
	 *            The Variable that has to be tested.
	 * @return True when the given variable is set to be exclusively read by this {@link Proctype}.
	 */
	public boolean isXR(final Variable var) {
		return channelXR.contains(var);
	}

	/**
	 * @param var
	 *            The Variable that has to be tested.
	 * @return True when the given variable is set to be exclusively send by this {@link Proctype}.
	 */
	public boolean isXS(final Variable var) {
		return channelXS.contains(var);
	}

	/**
	 * Indicates that all variables that are added from now on are no longer arguments of this
	 * {@link Proctype}.
	 */
	public void lastArgument() {
		isArgument = false;
	}

	/**
	 * Changes the enabler expression of the process.
	 * 
	 * @param enabler
	 *            The enabler expression.
	 */
	public void setEnabler(final Expression enabler) {
		this.enabler = enabler;
	}

	/**
	 * Changes the priority of the process.
	 * 
	 * @param priority
	 *            The new priority.
	 */
	public void setPriority(final int priority) {
		this.priority = priority;
	}

	/**
	 * Returns the name of the process.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
}

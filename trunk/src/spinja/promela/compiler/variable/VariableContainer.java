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

import java.util.List;

/**
 * A variable container represents any object that can store variables.
 * 
 * @author Marc de Jonge
 */
public interface VariableContainer {
	/**
	 * Adds a new variable to the container.
	 * 
	 * @param var
	 *            The variable that is to be added.
	 */
	public void addVariable(Variable var);

	/**
	 * @param name
	 *            The name of the variable that we seek.
	 * @return The variable that has the name that we seek, or null if there is no such variable.
	 */
	public Variable getVariable(String name);

	/**
	 * @return A copy of the list with all the variables that are stored in this container.
	 */
	public List<Variable> getVariables();

	/**
	 * @param name
	 *            The name of the variable that we seek.
	 * @return true when a variable with that name already exists or otherwise false.
	 */
	public boolean hasVariable(String name);

}

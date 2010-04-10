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

package spinja.search;

/**
 * An Algorithm is one way to execute the Model checking. This can be a search algorithm (see
 * {@link SearchAlgorithm}) or a simulation (see {@link Simulation}).
 * 
 * @author Marc de Jonge
 */
public abstract class Algoritm {
	/**
	 * Executes the algorithm. When this method returns, the algorithm was executed.
	 */
	public abstract void execute();

	/**
	 * Frees as much memory as it can. This method should only be called after {@link #execute()}
	 * was done (either through some error or normally).
	 */
	public abstract void freeMemory();

	/**
	 * @return The number of bytes of internal memory that were used during the execution of the
	 *         algorithm.
	 */
	public abstract long getBytes();

	/**
	 * @return The number of states that were visited.
	 */
	public abstract long getNrStates();

	/**
	 * Prints the summary of the algorithm
	 */
	public abstract void printSummary();
}

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

import java.io.PrintStream;

import spinja.exceptions.ValidationException;
import spinja.model.Model;
import spinja.model.Transition;
import spinja.model.listener.PrintEvent;
import spinja.model.listener.PrintListener;

/**
 * A Simulation is made to create on run through the model. 
 * It should implement the {@link #nextTransition()} method, which 
 * will choose which Transition should be executed next.
 * 
 * @author Marc de Jonge
 */
public abstract class Simulation<M extends Model<T>, 
								 T extends Transition> extends Algoritm {

	protected final M model;
	
	protected final TransitionCalculator<M, T> nextTransition;

	private PrintStream out;

	private long states;

	/**
	 * Constructor of Simulation using an {@link spinja.model.Model}.
	 * 
	 * @param model
	 *            The {@link spinja.model.Model} that we are going to simulate.
	 */
	public Simulation(final M model, final TransitionCalculator<M, T> nextTransition) {
		this.model = model;
		this.nextTransition = nextTransition;

		model.addPrintListener(new PrintListener() {
			public void print(final PrintEvent evt) {
				out.println(evt.getMsg());
			}
		});

		out = System.out;

		states = 0;
	}

	/**
	 * This execution runs the simulation.
	 * 
	 * @see spinja.search.Algoritm#execute()
	 */
	@Override
	public final void execute() {
		while (true) {
			// Print current state
			System.out.println(model.toString());
			try {
				final Transition t = nextTransition();
				if (t == null) { // No next transition, quit
					break;
				}
				out.println("Taking transition: " + t.toString());
				t.take(); // Discard the backup object, we don't need it
				states++; // Increase the number of states that were visited
			} catch (final ValidationException ex) {
				out.println(ex.getMessage());
			}
		}
	}

	/**
	 * @see spinja.search.Algoritm#freeMemory()
	 */
	@Override
	public void freeMemory() {
		// Nothing to free!
	}

	/**
	 * @see spinja.search.Algoritm#getBytes()
	 */
	@Override
	public long getBytes() {
		return 0;
	}

	/**
	 * @see spinja.search.Algoritm#getNrStates()
	 */
	@Override
	public long getNrStates() {
		return states;
	}

	/**
	 * @return The Transition that may be executed now from the current model.
	 */
	public abstract T nextTransition();

	/**
	 * @see spinja.search.Algoritm#printSummary()
	 */
	@Override
	public void printSummary() {
		out.println("Simulation finished");
	}

	/**
	 * Sets the {@link java.io.OutputStream} to the given {@link PrintStream}. 
	 * This way the output of the simulation can be send to some other 
	 * output, instead of the default: {@link System#out}.
	 * 
	 * @param out
	 *            The {@link PrintStream} that functions as an output channel.
	 */
	protected void setOutputStream(final PrintStream out) {
		this.out = out;
	}
}

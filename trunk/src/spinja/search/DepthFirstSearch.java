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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import spinja.exceptions.SpinJaException;
import spinja.model.Model;
import spinja.model.Transition;
import spinja.store.StateStore;

/**
 * The Depth First Search is a basic complete search algorithm. It tries every transition that it
 * can find in a depth first way.
 * 
 * @author Marc de Jonge
 */
public class DepthFirstSearch<M extends Model<T>, T extends Transition> extends
	SearchAlgorithm<M, T> {
	private static final long serialVersionUID = -9191078596196568047L;

	protected final Stack stack;

	/**
	 * Creates a new DepthFirstSearch object that is able to search over the model using a depth
	 * first search algorithm.
	 * 
	 * @param model
	 *            The Model that is to be used for searching.
	 * @param store
	 *            The StateStore which is used for storing all the states that are found.
	 * @param stackSize
	 *            The size of the stack.
	 * @param errorExceedDepth
	 *            True when we consider it to be an error if the depth is exceeded, false otherwise.
	 *            This means that when the depth limit is exceeded the search is only stopped if
	 *            this variable if true, otherwise we just keep on searching.
	 * @param checkForDeadlocks
	 *            When this parameter is set to false, deadlocks (which are states in which the
	 *            model can not take an other step, but it is not the end state) are ignored and not
	 *            reported. Otherwise each deadlock gives a error message.
	 * @param maxErrors
	 *            The number of errors that may be gotten, before the algorithm gives up and quits.
	 * @param nextTransition
	 *            The algorithm to determine the next transition that is to be executed.
	 */
	public DepthFirstSearch(final M model, final StateStore store, final int stackSize,
		final boolean errorExceedDepth, final boolean checkForDeadlocks, final int maxErrors,
		final TransitionCalculator<M, T> nextTransition) {
		super(model, store, checkForDeadlocks, maxErrors, errorExceedDepth, nextTransition);
		this.stack = new Stack(stackSize);
	}

	@Override
	protected boolean addState(final byte[] state, int identifier) {
		return stack.push(state, identifier);
	}

	@Override
	protected boolean checkModelState() {
		final byte[] buffer = storeModel();
		// System.out.println("Checking state:");
		// System.out.println("Real model: " + Arrays.toString(buffer));
		// System.out.println("On stack: " + Arrays.toString(stack.getTop()));
		return Arrays.equals(buffer, stack.getTop());
	}

	/**
	 * @see spinja.search.Algoritm#freeMemory()
	 */
	@Override
	public void freeMemory() {
		stack.clearStack();
	}

	/**
	 * @see spinja.search.SearchAlgorithm#getBytes()
	 */
	@Override
	public long getBytes() {
		return stack.getBytes() + 10 + super.getBytes();
	}

	@Override
	public int getDepth() {
		return stack.getSize();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T getLastTransition() {
		return (T)stack.getLastTransition();
	}

	@Override
	protected Transition nextTransition() {
		assert printTransitionsLeft();
		return nextTransition.next(model, getLastTransition());
	}
	
	@Override
	public SearchableStack getSearchableStack() {
		return stack;
	}

	/**
	 * This function prints the trace to a .trail file so that it can be rerun.
	 * 
	 * @param name
	 *            The name of the file (without extension)
	 */
	@Override
	protected void outputTrace(final String name) {
		try {
			final PrintWriter out = new PrintWriter(
				new BufferedWriter(new FileWriter(name + ".trail")));
			for (int i = 0; i < stack.getSize(); i++) {
				if (stack.getTransition(i) != null) {
					out.println(stack.getTransition(i).getId());
				} else {
					break;
				}
			}
			out.flush();
			out.close();
			System.out.println("spinja: wrote " + name + ".trail\n");
		} catch (final IOException ex) {
			System.out.println("spinja: error while writing " + name + ".trail: " + ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private boolean printTransitionsLeft() {
		int cnt = 0;
		for (T last = nextTransition.next(model, (T) stack.getLastTransition()); last != null; last = nextTransition.next(
			model, last)) {
			System.out.println("== " + last);
			cnt++;
		}
		System.out.println("Possible transitions: " + cnt);
		return true;
	}

	@Override
	protected boolean restoreState() {
		return stack.getSize() > 0;
	}

	@Override
	protected void stateDone() {
		stack.pop();
		if ((stack.getSize() > 0) && (stack.getLastTransition() != null)) {
			assert print("  Undoing transition: " + stack.getLastTransition());
			stack.getLastTransition().undo();
			// assert print(" State: " + Arrays.toString(stack.getTop()));
		}
	}

	@Override
	protected void takeTransition(final Transition next) throws SpinJaException {
		assert print("Taking transition " + next);
		stack.takeTransition(next);
	}

	@Override
	protected void undoTransition() {
		assert print("  Undoing transition: " + stack.getLastTransition());
		stack.getLastTransition().undo();
	}
}

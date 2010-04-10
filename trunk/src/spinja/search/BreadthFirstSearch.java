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
import java.util.LinkedList;
import java.util.List;

import spinja.exceptions.SpinJaException;
import spinja.model.Condition;
import spinja.model.Model;
import spinja.model.Transition;
import spinja.store.StateStore;
import spinja.util.ByteArrayStorage;
import spinja.util.DataReader;

public class BreadthFirstSearch<M extends Model<T>, T extends Transition> extends
	SearchAlgorithm<M, T> {
	private static final long serialVersionUID = 4164987979846L;

	private final Queue queue;

	protected Queue.State fromState;

	private int depth;

	protected T last;

	public BreadthFirstSearch(final M model, final StateStore store,
		final boolean checkForDeadlocks, final int maxErrors, final boolean errorExceedDepth,
		final TransitionCalculator<M, T> nextTransition) {
		this(new Queue(), model, store, checkForDeadlocks, maxErrors, errorExceedDepth,
			nextTransition);
	}

	public BreadthFirstSearch(final Queue queue, final M model, final StateStore store,
		final boolean checkForDeadlocks, final int maxErrors, final boolean errorExceedDepth,
		final TransitionCalculator<M, T> nextTransition) {
		super(model, store, checkForDeadlocks, maxErrors, errorExceedDepth, nextTransition);
		this.queue = queue;
		depth = 0;
	}

	@Override
	public SearchableStack getSearchableStack() {
		return queue.peek();
	}

	@Override
	protected boolean addState(final byte[] state, final int identifier) {
		return queue.add(queue.new State(fromState, state, last == null ? -1 : last.getId()));
	}

	@Override
	protected boolean checkModelState() {
		final byte[] buffer = storeModel();
		return Arrays.equals(buffer, fromState.state);
	}

	@Override
	public void freeMemory() {
		queue.clear();
	}

	@Override
	public int getDepth() {
		return depth;
	}
	
	@Override
	protected T getLastTransition() {
		return last;
	}

	@Override
	protected T nextTransition() {
		final T next = nextTransition.next(model, last);
		last = next;
		return next;
	}

	@Override
	protected void outputTrace(final String name) {
		try {
			final PrintWriter out = new PrintWriter(
				new BufferedWriter(new FileWriter(name + ".trail")));
			final List<Integer> numbers = new LinkedList<Integer>();
			Queue.State state = fromState;
			while (state.previous != null) {
				numbers.add(0, state.transId);
				state = state.previous;
			}
			for (int i = 0; i < numbers.size(); i++) {
				out.println(numbers);
			}
			out.flush();
			out.close();
			System.out.println("spinja: wrote " + name + ".trail\n");
		} catch (final IOException ex) {
			System.out.println("spinja: error while writing " + name + ".trail: " + ex.getMessage());
		}
	}

	@Override
	protected boolean restoreState() {
		if (fromState == null) {
			fromState = queue.remove();
			if (fromState == null) {
				return false;
			} else {
				ByteArrayStorage reader = new ByteArrayStorage();
				reader.setBuffer(fromState.state);
				model.decode(reader);
				last = null;
			}
		} else if (last != null) {
			last.undo();
		}
		return true;
	}

	@Override
	protected void stateDone() {
		fromState = null;
	}

	@Override
	protected void takeTransition(final Transition next) throws SpinJaException {
		assert print("Taking transition: " + next);
		next.take();
	}

	@Override
	protected void undoTransition() {
	}
}

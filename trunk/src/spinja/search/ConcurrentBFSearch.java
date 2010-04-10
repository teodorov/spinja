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

import java.util.ArrayList;
import java.util.Collection;

import spinja.model.Model;
import spinja.model.Transition;
import spinja.store.StateStore;
import spinja.util.ByteArrayStorage;

public class ConcurrentBFSearch<M extends Model<T>, T extends Transition> extends Algoritm {
	private final Queue queue;

	private final StateStore store;

	private final M model;

	private final boolean checkForDeadlocks, errorExceedDepth;

	private final int maxErrors, nrThreads;

	private final Collection<BFSearch> searchThreads;

	private final TransitionCalculator<M, T> nextTransition;

	public ConcurrentBFSearch(int nrThreads, M model, StateStore store, boolean checkForDeadlocks,
		int maxErrors, boolean errorExceedDepth, TransitionCalculator<M, T> nextTransition) {
		queue = new Queue();
		this.model = model;
		this.store = store;
		this.checkForDeadlocks = checkForDeadlocks;
		this.maxErrors = maxErrors;
		this.errorExceedDepth = errorExceedDepth;
		this.nrThreads = nrThreads;
		this.searchThreads = new ArrayList<BFSearch>();
		this.nextTransition = nextTransition;
	}
	
	private final ByteArrayStorage storage = new ByteArrayStorage();

	@Override
	public void execute() {
		storage.init(model.getSize());
		model.encode(storage);
		byte[] buffer = storage.getBuffer();
		queue.add(queue.new State(null, buffer, -1));
		store.addState(buffer);

		startNewThread();
		try {
			synchronized (ConcurrentBFSearch.this) {
				while (!searchThreads.isEmpty()) {
					wait();
				}
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void freeMemory() {

	}

	@Override
	public long getBytes() {
		return store.getBytes();
	}

	@Override
	public long getNrStates() {
		return store.getStored();
	}

	@Override
	public void printSummary() {
		// System.out.println("State-vector " + maxSize + " byte, depth reached " + maxDepth
		// + ", errors: " + nrErrors);
		System.out.printf("%8d states, stored\n", store.getStored());
		// System.out.printf("%8d states, matched\n", statesMatched);
		// System.out.printf("%8d transitions (= stored+matched)\n", store.getStored() +
		// statesMatched);
		// System.out.printf("%8d atomic steps\n", atomicSteps);
		store.printSummary();
	}

	@SuppressWarnings("unchecked")
	private void startNewThread() {
		BFSearch search = new BFSearch(queue, (M)model.clone(), store, checkForDeadlocks, maxErrors,
			errorExceedDepth, nextTransition);
		searchThreads.add(search);
		System.out.println("Started new Thread, now running " + searchThreads.size());
		new Thread(search).run();
	}

	private class BFSearch extends BreadthFirstSearch<M, T> implements Runnable {
		private static final long serialVersionUID = 8891381374394851600L;

		public BFSearch(Queue queue, M model, StateStore store, boolean checkForDeadlocks,
			int maxErrors, boolean errorExceedDepth, TransitionCalculator<M, T> nextTransition) {
			super(queue, model, store, checkForDeadlocks, maxErrors, errorExceedDepth,
				nextTransition);
		}
		
		@Override
		protected boolean restoreState() {
			if(fromState == null) {
				fromState = queue.remove();
				if(fromState == null) {
					return false;
				} else {
					ByteArrayStorage reader = new ByteArrayStorage();
					reader.setBuffer(fromState.state);
					model.decode(reader);
					last = null;
				}
			} else if(last != null) {
				last.undo();
			}
			if (searchThreads.size() < nrThreads) {
				startNewThread();
			}
			return true;
		}

		@Override
		protected boolean print(String msg) {
			return super.print("Thread " + Thread.currentThread().getName() + ":" + msg);
		}

		public void run() {
			execute();
			searchThreads.remove(this);
			synchronized (ConcurrentBFSearch.this) {
				ConcurrentBFSearch.this.notifyAll();
			}
			System.out.println("Ended one thread: now running: " + searchThreads.size());
		}
	}
}

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

import java.util.Arrays;

public class Queue {
	public class State implements SearchableStack {
		public final State previous;

		private State next;

		public final byte[] state;

		public final int transId;

		public State(State previous, byte[] state, int transId) {
			this.previous = previous;
			this.state = state;
			this.transId = transId;
		}
		
		public boolean containsState(byte[] state, int identifier) {
			State s = this;
			while(s != null) {
				if(Arrays.equals(s.state, state)) {
					return true;
				}
				s = s.previous;
			}
			return false;
		}
	}

	private final State nextDepth = new State(null, null, -1);

	private volatile State head, tail;

	private volatile int filled;

	private int depth;

	public Queue() {
		tail = head = nextDepth;
		filled = 0;
		depth = 0;
	}

	public synchronized boolean add(State newState) {
		tail.next = newState;
		tail = newState;
		filled++;
		return true;
	}
	
	public synchronized State peek() {
		return head;
	}

	public synchronized State remove() {
		if (head == null) {
			return null;
		}
		if (head == nextDepth) {
			head = head.next;
			add(nextDepth);
			depth++;
		}
		State res = head;
		head = head.next;
		filled--;
		res.next = null;
		return res;
	}

	public int size() {
		return filled;
	}

	public void clear() {
		// Not implemented yet!
	}
}

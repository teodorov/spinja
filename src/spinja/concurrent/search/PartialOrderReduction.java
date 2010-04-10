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

package spinja.concurrent.search;

import spinja.concurrent.model.ConcurrentModel;
import spinja.concurrent.model.ConcurrentTransition;
import spinja.concurrent.model.Process;
import spinja.model.MetaInfo;
import spinja.search.TransitionCalculator;
import spinja.search.SearchableStack;

/**
 * The {@link PartialOrderReduction} algorithm is implemented as a {@link TransitionCalculator}. It
 * is possible that is some cases this algorithm returns a limited set of transitions, that will
 * result is a smaller state space.
 * @author Marc de Jonge
 * @param <M>
 *            The type of model.
 * @param <T>
 *            The type of transition.
 */
public class PartialOrderReduction<M extends ConcurrentModel<T>, T extends ConcurrentTransition<T>>
	extends TransitionCalculator<M, T> {

	/**
	 * @see spinja.search.TransitionCalculator#next(spinja.model.Model, spinja.model.Transition)
	 */
	@Override
	public T next(final M model, T last) {
		int i = model.getNrProcesses() - 1;
		if (last != null) {
			Process<T> lastProc = last.getProcess();
			if (lastProc == null) {
				return null;
			}

			T next = lastProc.nextTransition(last);
			if (next != null) {
				next.copyMetaInfo(last);
				return next;
			} else if (last.hasMetaInfo(MetaInfo.PartialOrderReduction)
						&& !last.hasMetaInfo(MetaInfo.StateOnStack)) {
				return null;
			}

			i = last.getProcess().getId() - 1;
		}

		if (last == null || last.getProcess().onlyLocalTransitions()) {
			while (i >= 0) {
				final Process<T> proc = model.getProcess(i--);
				if (proc.onlyLocalTransitions()) {
					T next = proc.nextTransition(null);
					if (next != null) {
						next.setMetaInfo(MetaInfo.PartialOrderReduction);
						return next;
					}
				}
			}
			i = model.getNrProcesses() - 1;
		}

		while (i >= 0) {
			final Process<T> proc = model.getProcess(i--);
			if (!proc.onlyLocalTransitions()) {
				T next = proc.nextTransition(null);
				if (next != null) {
					return next;
				}
			}
		}

		return null;
	}

	/**
	 * If the given encoded state was stored on the stack, then the {@link MetaInfo#StateOnStack} is
	 * added to the last transition.
	 * @see TransitionCalculator#duplicateState(spinja.model.Model, spinja.model.Transition, byte[],
	 *      int, SearchableStack)
	 */
	@Override
	public void duplicateState(final M model, final T last, final byte[] state,
		final int identifier, final SearchableStack stack) {
		if (last.hasMetaInfo(MetaInfo.PartialOrderReduction)) {
			if (stack.containsState(state, identifier)) {
				last.setMetaInfo(MetaInfo.StateOnStack);
			}
		}
	}
}

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

package spinja.model;

import spinja.model.listener.PrintEvent;
import spinja.model.listener.PrintListener;
import spinja.model.listener.TransitionEvent;
import spinja.model.listener.TransitionListener;

/**
 * An {@link ObservableModel} is an extension to the {@link Model} that adds two types of listeners.
 * We can have the {@link PrintListener} and {@link TransitionListener}.
 * 
 * @author Marc de Jonge
 */
public abstract class ObservableModel {
	private PrintListener[] printListeners = null;

	private boolean printListenerActive = true;

	private TransitionListener[] transitionListeners = null;

	private boolean transitionListenerActive = true;

	/**
	 * Activates or deactivates any {@link PrintListener} that may be registered in this
	 * {@link ObservableModel}.
	 * 
	 * @param value
	 *            When value is true, they are activates. Otherwise they are deactivated.
	 */
	public void activatePrintListener(final boolean value) {
		printListenerActive = value;
	}

	/**
	 * Activates or deactivates any {@link TransitionListener} that may be registered in this
	 * {@link ObservableModel}.
	 * 
	 * @param value
	 *            When value is true, they are activates. Otherwise they are deactivated.
	 */
	public void activateTransitionListener(final boolean value) {
		transitionListenerActive = value;
	}

	/**
	 * Adds a new {@link PrintListener} to this {@link ObservableModel}. Warning: this method can
	 * be quite costly and should only be called a couple of times while setting up the model. When
	 * this method is called repeatedly during model checking, it will slow things down
	 * considerably.
	 * 
	 * @param pl
	 *            The {@link PrintListener} that is to be added.
	 */
	public void addPrintListener(final PrintListener pl) {
		if (printListeners == null) {
			printListeners = new PrintListener[] {
				pl
			};
		} else {
			final PrintListener[] temp = new PrintListener[printListeners.length + 1];
			System.arraycopy(printListeners, 0, temp, 0, printListeners.length);
			temp[printListeners.length] = pl;
			printListeners = temp;
		}
	}

	/**
	 * Adds a new {@link TransitionListener} to this {@link ObservableModel}. Warning: this method
	 * can be quite costly and should only be called a couple of times while setting up the model.
	 * When this method is called repeatedly during model checking, it will slow things down
	 * considerably.
	 * 
	 * @param tl
	 *            The {@link TransitionListener} that is to be added.
	 */
	public void addTransitionListener(final TransitionListener tl) {
		if (transitionListeners == null) {
			transitionListeners = new TransitionListener[] {
				tl
			};
		} else {
			final TransitionListener[] temp = new TransitionListener[transitionListeners.length + 1];
			System.arraycopy(transitionListeners, 0, temp, 0, transitionListeners.length);
			temp[transitionListeners.length] = tl;
			transitionListeners = temp;
		}
	}

	/**
	 * Sends the {@link PrintEvent} to all registered {@link PrintListener}. If the
	 * {@link PrintListener}s are deactivated through the {@link #activatePrintListener(boolean)}
	 * method, this function will do nothing.
	 * 
	 * @param msg
	 *            The message that we want to send to all the PrintListeners
	 */
	public void sendPrintEvent(final String msg) {
		if (printListenerActive && printListeners != null) {
			PrintEvent evt = new PrintEvent(msg);
			for (final PrintListener pl : printListeners) {
				pl.print(evt);
			}
		}
	}

	/**
	 * Sends the {@link TransitionEvent} to all registered {@link TransitionListener}s, to the
	 * {@link TransitionListener#transitionTaken(TransitionEvent)}. If the
	 * {@link TransitionListener}s are deactivated through the
	 * {@link #activateTransitionListener(boolean)} method, this method will do nothing.
	 * 
	 * @param trans
	 *            The transition has been taken
	 */
	public void sendTransitionTakenEvent(final Transition trans) {
		if (transitionListenerActive && transitionListeners != null) {
			TransitionEvent evt = new TransitionEvent(trans);
			for (final TransitionListener tl : transitionListeners) {
				tl.transitionTaken(evt);
			}
		}
	}

	/**
	 * Sends the {@link TransitionEvent} to all registered {@link TransitionListener}s, to the
	 * {@link TransitionListener#transitionUndo(TransitionEvent)}. If the
	 * {@link TransitionListener}s are deactivated through the
	 * {@link #activateTransitionListener(boolean)} method, this method will do nothing.
	 * 
	 * @param trans
	 *            The transition has been undone
	 */
	public void sendTransitionUndoEvent(final Transition trans) {
		if (transitionListenerActive && transitionListeners != null) {
			TransitionEvent evt = new TransitionEvent(trans);
			for (final TransitionListener tl : transitionListeners) {
				tl.transitionUndo(evt);
			}
		}
	}
}

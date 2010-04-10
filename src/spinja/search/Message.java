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
 * A {@link Message} is something that can be send by the
 * {@link SearchAlgorithm#report(Message, String)} method. It indicated what has happened. Also
 * there is an error level that indicated how bad this was. Higher than 4 is an error (we can not
 * continue, something bad has happened), higher than 2 is a warning (we can continue, be something
 * has to be reported, because it may influence the result) and anything lower is simply
 * information.
 * 
 * @author Marc de Jonge
 */
public enum Message {
	/**
	 * A deadlock has been found.
	 */
	DEADLOCK(4, "invalid end state"),
	/**
	 * There are no more transitions that can be executed.
	 */
	NO_MORE_TRANSITIONS(0, "no more transitions to take"),
	/**
	 * An error occurred in a transition.
	 */
	TRANS_ERROR(4, "transition error"),
	/**
	 * The maximum depth that can be searched is reached, and we can not continue.
	 */
	EXCEED_DEPTH_ERROR(4, "exceeded maximum depth"),
	/**
	 * The maximum depth that can be searched is reached, and we can continue.
	 */
	EXCEED_DEPTH_WARNING(2, "exceeded maximum depth"),
	/**
	 * We have found a state that was reached before.
	 */
	DUPLICATE_STATE(0, "duplicate state found");

	private final int errorLevel;
	
	private final String defaultMessage;
	
	private byte[] state;

	private Message(final int errorLevel, final String defaultMessage) {
		this.errorLevel = errorLevel;
		this.defaultMessage = defaultMessage;
	}

	/**
	 * @return True when this message is very bad.
	 */
	public boolean isError() {
		return errorLevel >= 4;
	}

	/**
	 * @return True when this message is bad, but can be recovered from.
	 */
	public boolean isWarning() {
		return errorLevel >= 2;
	}
	
	public String getDefaultMessage() {
		return defaultMessage;
	}
	
	public Message withState(byte[] state) {
		this.state = state;
		return this;
	}
	
	public byte[] getState() {
		return state;
	}
}

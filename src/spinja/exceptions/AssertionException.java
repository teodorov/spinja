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

package spinja.exceptions;

/**
 * This exception is thrown on runtime level when an assertion has failed.
 * 
 * @author Marc de Jonge
 * @version 1.0
 */
public class AssertionException extends ValidationException {
	private static final long serialVersionUID = 8594770053586303259L;

	/**
	 * Creates a new AssertionException using a specified message.
	 * 
	 * @param msg
	 *            The messages that is to be shown.
	 */
	public AssertionException(final String msg) {
		super("assertion violated " + msg);
	}
}

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

package spinja.promela.exceptions;

import spinja.exceptions.ValidationException;

/**
 * This exception is thrown when there are 255 channels created and it is tried to create a new one.
 * 
 * @author Marc de Jonge
 * @version 1.0
 */
public class TooManyChannelsException extends ValidationException {
	private static final long serialVersionUID = -564858254315348743L;

	/**
	 * Creates a new TooManyChannelsException using a specified message.
	 * 
	 * @param msg
	 *            The message that is to be thrown.
	 */
	public TooManyChannelsException(final String msg) {
		super(msg);
	}

}

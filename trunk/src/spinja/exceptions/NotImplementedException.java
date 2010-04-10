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
 * The {@link NotImplementedException} is thrown when a specific function was not implemented.
 * @author Marc de Jonge
 */
public class NotImplementedException extends RuntimeException {
	private static final long serialVersionUID = -2097114509483370855L;

	/**
	 * Constructor of NotImplementedException.
	 * 
	 * @param msg
	 *            The message.
	 */
	public NotImplementedException(final String msg) {
		super(msg);
	}
}

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

package spinja.util.compiler;

public class ParseException extends Exception {
	private static final long serialVersionUID = -6325542832430323283L;

	/**
	 * Constructs a new ParseException.
	 * 
	 * @param msg
	 *            The message to leave behind.
	 */
	public ParseException(final String msg) {
		super(msg);
	}

	/**
	 * Constructs a new ParseException.
	 * 
	 * @param msg
	 *            The message to leave behind.
	 * @param token
	 *            The token where the error occurred.
	 */
	public ParseException(final String msg, final Token<?> token) {
		super(String.format("(@ line: %d column: %d) %s", token.getLine(), token.getColumn(), msg));
	}

	public ParseException(final String msg, final Throwable tr) {
		super(msg, tr);
	}
}

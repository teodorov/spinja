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

import spinja.util.DataReader;
import spinja.util.DataWriter;

/**
 * A {@link Storable} object is an object that can be stored in a byte array and possibly restored
 * from a byte array.
 * 
 * @author Marc de Jonge
 */
public interface Storable {
	/**
	 * @return The number of bytes that is needed to store the current state of the Storable object.
	 */
	public int getSize();

	/**
	 * Decodes an object state from the given {@link DataReader}
	 * 
	 * @param reader
	 *            The {@link DataReader} that can be used to read the variables from.
	 * @return True when the decoding was a success, or false otherwise.
	 * @throws IndexOutOfBoundsException
	 *             When the buffer does not contain enough bytes to restore the process.
	 */
	public boolean decode(DataReader reader);

	/**
	 * Encodes the current state of this process into the {@link DataWriter} that has been used.
	 * 
	 * @param writer
	 *            the {@link DataWriter} that can be used to write the internal state to.
	 */
	public void encode(DataWriter writer);
}

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

package spinja.util;

/**
 * The {@link DataWriter} interface describes any type of storage to which we can write primitive
 * values using the writeX() functions. It does not describe how it must be stored.
 * 
 * @author Marc de Jonge
 */
public interface DataWriter {
	/**
	 * Writes the given boolean to the internal storage.
	 * @param value
	 *            The boolean value that is written.
	 */
	public void writeBoolean(final boolean value);

	/**
	 * Writes the lowest 8-bits of the value into the internal storage.
	 * @param value
	 *            The value that we want to write.
	 */
	public void writeByte(final int value);

	/**
	 * Writes the lowest 16-bits of the value into the internal storage.
	 * 
	 * @param value
	 *            The value that we want to write.
	 */
	public void writeShort(final int value);

	/**
	 * Writes the full 32-bits of the value into the internal storage.
	 * @param value
	 *            The value that we want to write.
	 */
	public void writeInt(final int value);
}

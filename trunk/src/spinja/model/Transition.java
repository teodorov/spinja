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

import spinja.exceptions.ValidationException;

/**
 * A {@link Transition} object is any transition that can change the state of the {@link Model}.
 * 
 * @author Marc de Jonge
 */
public abstract class Transition {
	/* MetaInformatie storage */

	private int meta = 0;

	/**
	 * Sets the given {@link MetaInfo} on this {@link Transition}.
	 * @param toAdd
	 *            The {@link MetaInfo} to set.
	 */
	public final void setMetaInfo(MetaInfo toAdd) {
		meta |= toAdd.getMask();
	}

	/**
	 * Checks if the given {@link MetaInfo} is set.
	 * @param check
	 *            The {@link MetaInfo} that is to be checked.
	 * @return True when the given {@link MetaInfo} is set.
	 */
	public final boolean hasMetaInfo(MetaInfo check) {
		return (meta & check.getMask()) != 0;
	}

	/**
	 * Copies all {@link MetaInfo} data from the other transition. This will remove any current set
	 * {@link MetaInfo}.
	 * @param other
	 *            The other {@link Transition} from which we are going to copy.
	 */
	public final void copyMetaInfo(Transition other) {
		meta = other.meta;
	}

	/**
	 * @return A unique identifier that can be used to set this Transition apart from any other that
	 *         it can be in the enabled set. This number must always be a positive one.
	 */
	public abstract int getId();

	/**
	 * Takes the transition and thereby changing the state of the model. Also a backup object is
	 * returned to make it possible to undo this action through the undo() function.
	 * 
	 * @throws ValidationException
	 *             When an error occurred while taking this step. For example: an assertion that has
	 *             failed.
	 */
	public abstract void take() throws ValidationException;

	/**
	 * Undoes all the actions that have been taken by the take() function, using the backup object
	 * that has been returned by that function. Make sure that the Model is still in the same state
	 * as it was directly after the take() function was executed.
	 */
	public abstract void undo();
}

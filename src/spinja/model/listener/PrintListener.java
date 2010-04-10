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

package spinja.model.listener;

/**
 * A {@link PrintListener} should implement this one method: {@link #print(PrintEvent)}.
 * @author Marc de Jonge
 */
public interface PrintListener {
	/**
	 * Handles the given {@link PrintEvent}, preferably by printing the given message to some
	 * output.
	 * @param evt
	 *            The {@link PrintEvent} that is to be handled.
	 */
	public void print(PrintEvent evt);
}

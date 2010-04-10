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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import spinja.model.Model;
import spinja.model.Transition;

public class TrailSimulation<M extends Model<T>, T extends Transition> extends Simulation<M, T> {

	private final BufferedReader reader;

	private final String filename;

	public TrailSimulation(final M model, final TransitionCalculator<M, T> nextTransition)
		throws FileNotFoundException {
		super(model, nextTransition);
		filename = model.getName() + ".trail";
		reader = new BufferedReader(new FileReader(filename));
	}

	@Override
	public T nextTransition() {
		T curr = null;

		try {
			final String line = reader.readLine();
			if (line == null) {
				System.out.println("End of trail-file");
				return null;
			}
			final int nr = Integer.parseInt(line);
			while ((curr = nextTransition.next(model, curr)) != null) {
				if (curr.getId() == nr) {
					return curr;
				}
			}
			throw new NumberFormatException("Transition with ID " + nr + " not found");
		} catch (final NumberFormatException ex) {
			System.out.println("Error while reading " + filename + ": " + ex.getMessage());
		} catch (final IOException ex) {
			System.out.println("Error while reading " + filename + ": " + ex.getMessage());
		}
		return null;
	}

}

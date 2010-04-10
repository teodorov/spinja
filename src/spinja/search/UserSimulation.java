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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import spinja.model.Model;
import spinja.model.Transition;

public class UserSimulation<M extends Model<T>, T extends Transition> extends Simulation<M, T> {

	private final BufferedReader reader;

	private final List<T> enabled = new ArrayList<T>(64);

	public UserSimulation(final M model, final TransitionCalculator<M, T> nextTransition) {
		super(model, nextTransition);
		reader = new BufferedReader(new InputStreamReader(System.in));
	}

	@Override
	public T nextTransition() {
		T curr = null;
		enabled.clear();
		
		while ((curr = nextTransition.next(model, curr)) != null) {
			enabled.add(curr);
		}

		if (enabled.size() == 0) {
			System.out.println("Deadlock");
			return null;
		}

		int option = -999;

		while ((option < 0) || (option > enabled.size())) {
			if (option != -999) {
				System.out.println("Please give a number between 1 and " + enabled.size()
									+ " or 0 to stop.");
			}
			for (int i = 0; i < enabled.size(); i++) {
				System.out.println("" + (i + 1) + ") " + enabled.get(i).toString());
			}
			System.out.print("Which transition? ");
			try {
				option = Integer.parseInt(reader.readLine());
			} catch (final NumberFormatException e) {
				option = -1;
			} catch (final IOException e) {
				System.out.println("IO Error: " + e.getMessage());
				option = 0;
			}
		}
		if (option == 0) {
			return null;
		} else {
			return enabled.get(option - 1);
		}
	}
}

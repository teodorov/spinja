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

package spinja.promela.compiler.parser;

import java.util.ArrayList;
import java.util.List;

import spinja.promela.compiler.automaton.State;
import spinja.promela.compiler.automaton.Transition;
import spinja.promela.compiler.automaton.UselessTransition;

public class OptionsResult {
	private final String text;

	private final List<State> optionStates;

	private final List<Transition> breakingTransitions;

	private final List<Transition> endedTransitions;

	public OptionsResult(String text) {
		this.text = text;
		optionStates = new ArrayList<State>();
		breakingTransitions = new ArrayList<Transition>();
		endedTransitions = new ArrayList<Transition>();
	}

	public void addOptionState(State s) {
		optionStates.add(s);
	}

	public void addBreakingTransition(Transition t) {
		breakingTransitions.add(t);
	}

	public void addEndedTransition(Transition t) {
		endedTransitions.add(t);
	}

	public void redirectBreakingTransitions(State to) {
		for (Transition t : breakingTransitions) {
			t.changeTo(to);
		}
	}

	public void redirectEndedTransitions(State to) {
		for (Transition t : endedTransitions) {
			t.changeTo(to);
		}
	}

	public void insertStartingState(State start) {
		for(State s : optionStates) {
			new UselessTransition(start, s, text);
		}
	}
}

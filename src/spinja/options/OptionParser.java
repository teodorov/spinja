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

package spinja.options;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class OptionParser {
	private final SortedMap<Character, Option> options;

	private final List<String> files;

	private final String programName, shortDescr, moreInfo;

	private final boolean acceptsOtherOptions;

	public OptionParser(final String programName, final String shortDescr, final String moreInfo,
		final boolean acceptsOtherOptions) {
		options = new TreeMap<Character, Option>();
		files = new ArrayList<String>();
		this.acceptsOtherOptions = acceptsOtherOptions;
		this.moreInfo = moreInfo;
		this.shortDescr = shortDescr;
		this.programName = programName;
	}

	public void addOption(final Option option) throws IllegalArgumentException {
		if (options.containsKey(option.getChar())) {
			throw new IllegalArgumentException("There already exists an option with the letter "
												+ option.getChar());
		}
		options.put(option.getChar(), option);
	}

	public List<String> getFiles() {
		return new ArrayList<String>(files);
	}

	public void parse(final String[] args) {
		System.out.println(shortDescr + "\n");

		int nr = 0;
		while (nr < args.length) {
			final String arg = args[nr];
			if (!(arg.startsWith("-") && (arg.length() > 1))) {
				break;
			}
			final char c = arg.charAt(1);

			final Option option = options.get(c);
			if (option == null) {
				if (c != '?') {
					System.out.println("Option not recognized: " + arg);
				}
				printUsage();
			}

			option.parseOption(arg.substring(2));
			nr++;
		}

		if (nr < args.length) {
			if (acceptsOtherOptions) {
				for (; nr < args.length; nr++) {
					files.add(args[nr]);
				}
			} else {
				System.out.print("Extra arguments are given, but none are suspected: ");
				for (; nr < args.length; nr++) {
					System.out.print(args[nr]);
					System.out.print(" ");
				}
				System.out.println();
				printUsage();
			}
		}
	}

	public void printUsage() {
		System.out.println();
		System.out.println(moreInfo);
		System.out.println();
		System.out.println("Usage: " + programName + " [options] "
							+ (acceptsOtherOptions ? "filename" : ""));
		System.out.println();
		for (final Option o : options.values()) {
			System.out.println(o.toString());
		}
		System.out.println();
		System.exit(-1);
	}
}
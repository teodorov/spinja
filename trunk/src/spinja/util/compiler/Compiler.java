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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Compiler<TYPE extends Type, TOKEN extends Token<TYPE>> {
	private final Tokenizer<TYPE, TOKEN> tokenizer;

	private TOKEN buffer;

	public Compiler(Tokenizer<TYPE, TOKEN> tokenizer) {
		this.tokenizer = tokenizer;
	}

	protected final TOKEN next() throws ParseException {
		if (buffer == null) {
			buffer = tokenizer.nextToken();
		}
		return buffer;
	}

	protected final boolean nextIs(TYPE... types) throws ParseException {
		for (TYPE type : types) {
			if (next().getType() == type) {
				return true;
			}
		}
		return false;
	}

	protected final TOKEN take() throws ParseException {
		TOKEN token = next();
		buffer = null;
		return token;
	}

	protected final TOKEN expect(TYPE type) throws ParseException {
		TOKEN token = take();
		if (token.getType() != type) {
			throw new ParseException("Expected a " + type + ", got a " + token.getType(), token);
		}
		return token;
	}

	protected final TOKEN expect(TYPE... types) throws ParseException {
		TOKEN token = take();
		for (TYPE type : types) {
			if (token.getType() == type) {
				return token;
			}
		}
		throw new ParseException("Expected one of " + Arrays.toString(types) + ", got a "
									+ token.getType(), token);
	}

	protected final <RESULT> List<RESULT> oneOrMore(Parser<RESULT> parser) throws ParseException {
		final List<RESULT> list = new ArrayList<RESULT>(2);
		list.add(parser.parse());
		while (parser.canParse()) {
			list.add(parser.parse());
		}
		return list;
	}

	protected final <RESULT> List<RESULT> zeroOrMore(Parser<RESULT> parser) throws ParseException {
		final List<RESULT> list = new ArrayList<RESULT>(1);
		while (parser.canParse()) {
			list.add(parser.parse());
		}
		return list;
	}

	protected final <RESULT> RESULT zeroOrOne(Parser<RESULT> parser) throws ParseException {
		if (parser.canParse()) {
			return parser.parse();
		} else {
			return null;
		}
	}

	protected final <RESULT> List<RESULT> separatedList(TYPE separator, Parser<RESULT> callback) throws ParseException {
		final List<RESULT> list = new ArrayList<RESULT>(2);
		list.add(callback.parse());
		while (next().getType() == separator) {
			take(); // throw away
			list.add(callback.parse());
		}
		return list;
	}
}

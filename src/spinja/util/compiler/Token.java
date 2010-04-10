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

public class Token<TYPE extends Type> {
	private TYPE type;

	private String content;

	private int line, column;

	public Token(TYPE type, String content, int line, int column) {
		this.type = type;
		this.content = content;
		this.line = line;
		this.column = column;
	}

	public TYPE getType() {
		return type;
	}

	public String getContent() {
		return content;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	@Override
	public String toString() {
		return String.format("(%d,%d) %s \"%s\"", line, column, type, content);
	}

	@Override
	public int hashCode() {
		return type.hashCode() ^ content.hashCode() ^ (line << 3) ^ (column << 7);
	}
}

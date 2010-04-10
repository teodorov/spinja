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
 * The StringWriter can be used as a replacement for the {@link StringBuilder}, 
 * but with support for adding lines with prefixes (indentation) and 
 * postfixes (newlines).
 * 
 * @author Marc de Jonge
 */
public final class StringWriter {
	private final String prefix;
	private final String postfix;
	private char[] buffer;
	private int used;
	private int indented;
	private int savePoint;

	/**
	 * Constructor of StringWriter with a initial size of 32, a tab character as prefix, and the
	 * line.seperator property as postfix.
	 */
	public StringWriter() {
		this(32);
		savePoint = 0;
	}

	/**
	 * Constructor of StringWriter with a given initial size, a tab character as prefix, and the
	 * line.seperator property as postfix.
	 * 
	 * @param size
	 *            The initial size of the underlying buffer.
	 */
	public StringWriter(final int size) {
		this("\t", System.getProperty("line.separator"), size);
	}

	/**
	 * Constructor of StringWriter.
	 * 
	 * @param prefix
	 *            The prefix string.
	 * @param postfix
	 *            The postfix string.
	 * @param size
	 *            The size of the underlying buffer.
	 */
	public StringWriter(final String prefix, final String postfix, final int size) {
		this.prefix = prefix;
		this.postfix = postfix;
		buffer = new char[size];
		used = 0;
		indented = 0;
	}

	/**
	 * Appends the object converted to a String to this StringWriter.
	 * 
	 * @param obj
	 *            The object that should be appended.
	 * @return This StringWriter
	 */
	public StringWriter append(final Object obj) {
		final String s = String.valueOf(obj);
		final int l = s.length();
		expand(used + l);
		s.getChars(0, l, buffer, used);
		used += l;

		return this;
	}

	/**
	 * Appends the object converted to a String, if the execute boolean is set to true.
	 * 
	 * @param execute
	 *            True when the object should really be added, or false if it should be ignored
	 * @param obj
	 *            The object that should be appended.
	 * @return This {@link StringWriter}
	 */
	public StringWriter appendIf(final boolean execute, final Object obj) {
		if (execute) {
			append(obj);
		}
		return this;
	}

	/**
	 * Adds a new line to the StringWriter. First the prefix is added (see {@link #appendPrefix()}),
	 * then all the objects are written and lastly the postfix is added (see
	 * {@link #appendPostfix()}).
	 * 
	 * @param objs
	 *            The objects that need to be appended to this StringWriter
	 * @return This StringWriter
	 */
	public StringWriter appendLine(final Object... objs) {
		if (objs.length > 0) {
			appendPrefix();
		}
		for (final Object o : objs) {
			append(o);
		}
		appendPostfix();
		return this;
	}

	/**
	 * Appends the postfix to the buffer.
	 * 
	 * @return This StringWriter
	 */
	public StringWriter appendPostfix() {
		append(postfix);
		return this;
	}

	/**
	 * Appends the prefix a number of times to the buffer (see {@link #getIndentLevel()}).
	 * 
	 * @return This StringWriter
	 */
	public StringWriter appendPrefix() {
		for (int i = 0; i < indented; i++) {
			append(prefix);
		}
		return this;
	}

	/**
	 * @param index
	 *            The index of the character that we are looking for.
	 * @return The character that is positioned at that location.
	 * @throws IndexOutOfBoundsException
	 *             When the index is negative or larger than the number of characters that are
	 *             currently in the buffer.
	 */
	public char charAt(final int index) {
		if ((index >= used) || (index < 0)) {
			throw new IndexOutOfBoundsException();
		}
		return buffer[index];
	}

	/**
	 * Clears the buffer, effectively making it empty.
	 */
	public void clear() {
		used = 0;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj.getClass() == StringWriter.class) {
			final StringWriter o = (StringWriter) obj;
			if (used == o.used) {
				for (int i = 0; i < used; i++) {
					if (buffer[i] != o.buffer[i]) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	private final void expand(final int minSize) {
		if (minSize > buffer.length) {
			int newSize = buffer.length * 2;
			while (newSize < minSize) {
				newSize *= 2;
			}

			// Increase buffer
			final char[] temp = new char[newSize];
			System.arraycopy(buffer, 0, temp, 0, buffer.length);
			buffer = temp;
		}
	}

	/**
	 * @return The current indentation level
	 */
	public int getIndentLevel() {
		return indented;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Increases the indentation by 1
	 * 
	 * @return this {@link StringWriter}
	 */
	public StringWriter indent() {
		if (indented < Integer.MAX_VALUE) {
			indented++;
		}
		return this;
	}

	/**
	 * @return The number of characters that are currently in the buffer.
	 */
	public int length() {
		return used;
	}

	/**
	 * Decreases the indentation by 1
	 * 
	 * @return this StringWriter
	 */
	public StringWriter outdent() {
		if (indented > 0) {
			indented--;
		}
		return this;
	}

	/**
	 * Removes the postfix from the buffer. This only works correctly when the last call was to the
	 * appendPostfix function.
	 * 
	 * @return This StringWriter
	 */
	public StringWriter removePostfix() {
		used -= postfix.length();
		return this;
	}

	/**
	 * Resets to the last savepoint, effectively removing all characters that were added.
	 */
	public void revertToSavePoint() {
		used = savePoint;
	}

	/**
	 * Sets the new length of the string that is contained in this StringWriter. When the current
	 * length is bigger than the new one, the last characters are removed. If the current length is
	 * smaller, the rest is padded with spaces.
	 * 
	 * @param length
	 *            The new length of the buffer.
	 */
	public void setLength(final int length) {
		if (length > used) {
			expand(length);
			for (int i = used; i < length; i++) {
				buffer[i] = ' ';
			}
		}
		used = length;
	}

	/**
	 * Sets the savepoint to the end of the current buffer.
	 */
	public void setSavePoint() {
		savePoint = used;
	}

	/**
	 * Returns a new String that represents the current content of this StringWriter.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new String(buffer, 0, used);
	}

	/**
	 * @return The current postfix string.
	 */
	public String getPostfix() {
		return postfix;
	}

	/**
	 * @return The current prefix string.
	 */
	public String getPrefix() {
		return prefix;
	}
}

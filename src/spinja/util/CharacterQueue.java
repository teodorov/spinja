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

import spinja.util.compiler.Tokenizer;

/**
 * A {@link CharacterQueue} is a simple queue that stored characters efficiently, so that they may
 * be handled by a {@link Tokenizer}.
 * @author Marc de Jonge
 */
public class CharacterQueue {
	private int head, tail;

	private int size;

	private char[] buffer;

	/**
	 * Constructor of CharacterQueue. Creates a queue with a initial size of 10.
	 */
	public CharacterQueue() {
		this(10);
	}

	/**
	 * Constructor of CharacterQueue. Creates a queue with a buffer with the specified initial size.
	 * 
	 * @param initialSize
	 *            The initial size of the buffer.
	 */
	public CharacterQueue(int initialSize) {
		buffer = new char[initialSize];
		head = 0;
		tail = 0;
		size = 0;
	}

	/**
	 * Adds a char to the end of the queue.
	 * @param c
	 *            The character that is to be added.
	 */
	public void add(char c) {
		ensureCapacity(size + 1);
		buffer[tail] = c;
		tail = (tail + 1) % buffer.length;
		size++;
	}

	/**
	 * Adds an array of characters to this queue.
	 * @param cs
	 *            The array of characters that is to be added.
	 */
	public void add(char[] cs) {
		add(cs, 0, cs.length);
	}

	/**
	 * Adds a part of the given array of characters to this queue.
	 * @param cs
	 *            The array of characters that is to be added.
	 * @param first
	 *            The index of the first character that is to be added.
	 * @param size
	 *            The number of characters that is to be added.
	 * @throws IndexOutOfBoundsException
	 *             when first + size >= cs.length
	 */
	public void add(char[] cs, int first, int size) {
		ensureCapacity(this.size + size);
		int maxFill = buffer.length - tail;
		if (size < maxFill) {
			System.arraycopy(cs, first, buffer, tail, size);
		} else {
			System.arraycopy(cs, first, buffer, tail, maxFill);
			System.arraycopy(cs, first, buffer, 0, size - maxFill);
		}
		tail = (tail + size) % buffer.length;
		this.size += size;
	}

	/**
	 * Returns the character that is the 'index'th in the queue.
	 * @param index
	 *            The number of characters that we want to look ahead.
	 * @return The character that is the 'index'th in the queue.
	 */
	public char get(int index) {
		return buffer[convertIndex(index)];
	}

	/**
	 * @return The first character of the queue, without removing it.
	 */
	public char peek() {
		return buffer[head];
	}

	/**
	 * @return The first character of the queue, while it was removed.
	 */
	public char remove() {
		char c = buffer[head];
		head = (head + 1) % buffer.length;
		size--;
		return c;
	}

	/**
	 * @return The number of characters that are currently in the queue.
	 */
	public int getSize() {
		return size;
	}

	private void ensureCapacity(int length) {
		if (length > Integer.MAX_VALUE / 2) {
			throw new IllegalArgumentException(
				"length can not be larger than Integer.MAX_VALUE / 2");
		} else if (length <= 0) {
			throw new IllegalArgumentException("length must be larger than zero");
		}
		if (length > buffer.length) {
			int newLength = buffer.length * 2;
			while (length > newLength) {
				newLength *= 2;
			}

			char[] newBuffer = new char[newLength];

			int halfLength = buffer.length - head;
			if (halfLength < size) {
				System.arraycopy(buffer, head, newBuffer, 0, halfLength);
				System.arraycopy(buffer, 0, newBuffer, halfLength, this.size - halfLength);
			} else {
				System.arraycopy(buffer, head, newBuffer, 0, this.size);
			}

			this.head = 0;
			this.tail = this.size;
			this.buffer = newBuffer;
		}
	}

	private final int convertIndex(int index) {
		return (index + head) % buffer.length;
	}
}

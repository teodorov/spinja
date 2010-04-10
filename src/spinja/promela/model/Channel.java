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

package spinja.promela.model;

import java.util.Arrays;

import spinja.model.Storable;

public abstract class Channel implements Storable {
	protected int[][] buffer;

	protected int first, filled;

	private int[] masks;

	private boolean isRendezVous;

	public Channel(final int[] masks, int bufferSize) {
		if (bufferSize == 0) {
			isRendezVous = true;
			bufferSize = 1;
		} else {
			isRendezVous = false;
		}
		buffer = new int[bufferSize][];
		this.masks = masks;
		first = filled = 0;
	}

	public boolean canRead() {
		return filled > 0;
	}

	public boolean canSend() {
		return filled < buffer.length;
	}

	public boolean isEmpty() {
		return filled == 0;
	}

	public boolean isFull() {
		return filled == buffer.length;
	}

	public int getSize() {
		return 0;
	}

	public boolean isRendezVous() {
		return isRendezVous;
	}

	public int length() {
		return filled;
	}

	public boolean isNotEmpty() {
		return filled > 0;
	}

	public boolean isNotFull() {
		return filled < buffer.length;
	}

	public int[] read() {
		final int[] res = buffer[first];
		first = (first + 1) % buffer.length;
		filled--;
		return res;
	}

	public int[] readLast() {
		filled--;
		final int[] res = buffer[(first + filled) % buffer.length];
		return res;
	}

	public int[] peek() {
		return buffer[first];
	}

	public int[] peekLast() {
		return buffer[(first + filled - 1) % buffer.length];
	}

	public void send(final int... values) {
		if (values.length != masks.length) {
			throw new IllegalArgumentException("Wrong nr of values put into this channel!");
		}
		if (!canSend()) {
			throw new IllegalStateException("Can not send into channel!");
		}
		for (int i = 0; i < masks.length; i++) {
			values[i] &= masks[i];
		}
		buffer[(first + filled) % buffer.length] = values;
		filled++;
	}

	public void sendFirst(final int... values) {
		if (values.length != masks.length) {
			throw new IllegalArgumentException("Wrong nr of values put into this channel!");
		}
		if (!canSend()) {
			throw new IllegalStateException("Can not send into channel!");
		}
		for (int i = 0; i < masks.length; i++) {
			values[i] &= masks[i];
		}
		first--;
		if (first < 0) {
			first = buffer.length - 1;
		}
		filled++;
		buffer[first] = values;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("filled: ").append(filled).append('\t');
		for (int i = 0; i < filled; i++) {
			sb.append(Arrays.toString(buffer[i])).append(',');
		}
		return sb.substring(0, sb.length() - 1);
	}
}

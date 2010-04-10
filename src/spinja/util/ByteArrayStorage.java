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

public class ByteArrayStorage implements DataWriter, DataReader {
	private byte[] buffer;

	private int mark;

	private int storedMark = 0;

	public ByteArrayStorage() {
		// do nothing
	}

	public void init(final int size) {
		if (buffer == null || buffer.length != size) {
			this.buffer = new byte[size];
		}
		mark = 0;
		storedMark = 0;
	}

	public int getMark() {
		return mark;
	}

	public void storeMark() {
		storedMark = mark;
	}

	public void resetMark() {
		mark = storedMark;
	}

	public void writeBoolean(final boolean value) {
		buffer[mark++] = (byte) (value ? 1 : 0);
	}

	public void writeByte(final int value) {
		buffer[mark++] = (byte) value;
	}

	public void writeShort(final int value) {
		buffer[mark++] = (byte) value;
		buffer[mark++] = (byte) (value >>> 8);
	}

	public void writeInt(final int value) {
		buffer[mark++] = (byte) value;
		buffer[mark++] = (byte) (value >>> 8);
		buffer[mark++] = (byte) (value >>> 16);
		buffer[mark++] = (byte) (value >>> 24);
	}

	public boolean readBoolean() {
		return buffer[mark++] != 0;
	}

	public boolean peekBoolean() {
		return buffer[mark] != 0;
	}

	public int readByte() {
		return buffer[mark++] & 0xff;
	}

	public int peekByte() {
		return buffer[mark] & 0xff;
	}

	public int peekByte(final int ahead) {
		return buffer[mark + ahead] & 0xff;
	}

	public int readShort() {
		return readByte() | readByte() << 8;
	}

	public int peekShort() {
		return peekByte() | peekByte(1) << 8;
	}

	public int readInt() {
		return readByte() | readByte() << 8 | readByte() << 16 | readByte() << 24;
	}

	public int peekInt() {
		return peekByte() | peekByte(1) << 8 | peekByte(2) << 16 | peekByte(3) << 24;
	}

	public void setBuffer(final byte[] buffer) {
		this.buffer = buffer;
		mark = 0;
		storedMark = 0;
	}

	public byte[] getBuffer() {
		try {
			return buffer;
		} finally {
			buffer = null;
		}
	}
}

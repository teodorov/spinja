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

package spinja.store.hash;

public class MarcHash extends HashAlgorithm {
	private final static int[] values;
	private final static long[] values_long;

	static {
		values = new int[4096 * 256];
		final int add = 1500000001;
		int[] v = new int[9];
		for (int i = 0; i < 4096; i++) {
			v[0] = nextValue(v[8], add);
			v[1] = nextValue(v[0], add);
			v[2] = nextValue(v[1], add);
			v[3] = nextValue(v[2], add);
			v[4] = nextValue(v[3], add);
			v[5] = nextValue(v[4], add);
			v[6] = nextValue(v[5], add);
			v[7] = nextValue(v[6], add);
			v[8] = nextValue(v[7], add);
			for (int j = 0; j < 256; j++) {
				final int index = i * 256 + j;
				values[index] = v[8];
				if ((j & 0x80) != 0)
					values[index] ^= v[0];
				if ((j & 0x40) != 0)
					values[index] ^= v[1];
				if ((j & 0x20) != 0)
					values[index] ^= v[2];
				if ((j & 0x10) != 0)
					values[index] ^= v[3];
				if ((j & 0x8) != 0)
					values[index] ^= v[4];
				if ((j & 0x4) != 0)
					values[index] ^= v[5];
				if ((j & 0x2) != 0)
					values[index] ^= v[6];
				if ((j & 0x1) != 0)
					values[index] ^= v[7];
			}
		}
	}
	
	static {
		values_long = new long[4096 * 256];
		final long add = 8650415921358664919l;
		long[] v = new long[9];
		for (int i = 0; i < 4096; i++) {
			v[0] = nextValue(v[8], add);
			v[1] = nextValue(v[0], add);
			v[2] = nextValue(v[1], add);
			v[3] = nextValue(v[2], add);
			v[4] = nextValue(v[3], add);
			v[5] = nextValue(v[4], add);
			v[6] = nextValue(v[5], add);
			v[7] = nextValue(v[6], add);
			v[8] = nextValue(v[7], add);
			for (int j = 0; j < 256; j++) {
				final int index = i * 256 + j;
				values_long[index] = v[8];
				if ((j & 0x80) != 0)
					values_long[index] ^= v[0];
				if ((j & 0x40) != 0)
					values_long[index] ^= v[1];
				if ((j & 0x20) != 0)
					values_long[index] ^= v[2];
				if ((j & 0x10) != 0)
					values_long[index] ^= v[3];
				if ((j & 0x8) != 0)
					values_long[index] ^= v[4];
				if ((j & 0x4) != 0)
					values_long[index] ^= v[5];
				if ((j & 0x2) != 0)
					values_long[index] ^= v[6];
				if ((j & 0x1) != 0)
					values_long[index] ^= v[7];
			}
		}
	}
	
	private static int nextValue(int value, int add) {
		do {
			value += add;
		} while (!isCorrect(value));
		return value;
	}
	
	private static long nextValue(long value, long add) {
		do {
			value += add;
		} while (!isCorrect(value));
		return value;
	}
	
	private static boolean isCorrect(int value) {
		int cnt = 0;
		int mask = 1;
		while (mask != 0) {
			if ((value & mask) != 0)
				cnt++;
			mask <<= 1;
		}
		return cnt == 16;
	}
	
	private static boolean isCorrect(long value) {
		int cnt = 0;
		long mask = 1;
		while (mask != 0) {
			if ((value & mask) != 0)
				cnt++;
			mask <<= 1;
		}
		return cnt == 32;
	}

	@Override
	public int hash(final byte[] state, final int start) {
		int hash = start;
		for (int i = 0; i < state.length; i++) {
			hash ^= values[(((i << 8) | (state[i] & 0xff)) + start) & 0xfffff];
		}
		return hash;
	}

	@Override
	public long hash(final byte[] state, final long start) {
		long hash = start;
		int off = (int) (start & 0xfffff);
		for (int i = 0; i < state.length; i++) {
			hash ^= values_long[(((i << 8) | (state[i] & 0xff)) + off) & 0xfffff];
		}
		return hash;
	}
}

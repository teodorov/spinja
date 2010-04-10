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

/**
 * The JenskinsHash implements a version of the Jenkins hash (see
 * http://www.burtleburtle.net/bob/hash/doobs.html).
 * 
 * Also an optimization from 3Spin is used to make better use of the Jenkins algorithm (see
 * http://www.cc.gatech.edu/fac/Pete.Manolios/research/spin-3spin.html).
 * 
 * @author Marc de Jonge
 */
public final class JenkinsHash extends HashAlgorithm {
	/**
	 * @see spinja.store.hash.HashAlgorithm#hash(byte[])
	 */
	@Override
	public final int hash(final byte[] state, int val) {
		int a, b, c;
		a = b = c = 0xdeadbeef + state.length + val;
		
		int i = 0;

		while (i + 12 <= state.length) {
			a += state[i++];
			a += state[i++] << 8;
			a += state[i++] << 16;
			a += state[i++] << 24;
			b += state[i++];
			b += state[i++] << 8;
			b += state[i++] << 16;
			b += state[i++] << 24;
			c += state[i++];
			c += state[i++] << 8;
			c += state[i++] << 16;
			c += state[i++] << 24;

			a -= c;  a ^= rot(c, 4);  c += b;
			b -= a;  b ^= rot(a, 6);  a += c;
			c -= b;  c ^= rot(b, 8);  b += a;
			a -= c;  a ^= rot(c,16);  c += b;
			b -= a;  b ^= rot(a,19);  a += c;
			c -= b;  c ^= rot(b, 4);  b += a;
		}

		c += state.length;

		switch (state.length - i) {
			case 11: c += state[i++] << 24;
			case 10: c += state[i++] << 16;
			case 9:  c += state[i++] << 8;
			case 8:  b += state[i++] << 24;
			case 7:  b += state[i++] << 16;
			case 6:  b += state[i++] << 8;
			case 5:  b += state[i++];
			case 4:  a += state[i++] << 24;
			case 3:  a += state[i++] << 16;
			case 2:  a += state[i++] << 8;
			case 1:  a += state[i++];
				c ^= b; c -= rot(b,14);
				a ^= c; a -= rot(c,11);
				b ^= a; b -= rot(a,25);
				c ^= b; c -= rot(b,16);
				a ^= c; a -= rot(c,4);
				b ^= a; b -= rot(a,14);
				c ^= b; c -= rot(b,24);
		}

		return c;
	}
	
	@Override
	public final long hash(final byte[] state, long val) {
		int a, b, c;
		a = b = c = 0xdeadbeef + state.length;
		a ^= (int)(val >>> 32);
		b ^= (int)(val >>> 16);
		c ^= (int)val;
		
		int i = 0;

		while (i + 12 <= state.length) {
			a += state[i++];
			a += state[i++] << 8;
			a += state[i++] << 16;
			a += state[i++] << 24;
			b += state[i++];
			b += state[i++] << 8;
			b += state[i++] << 16;
			b += state[i++] << 24;
			c += state[i++];
			c += state[i++] << 8;
			c += state[i++] << 16;
			c += state[i++] << 24;

			a -= c;  a ^= rot(c, 4);  c += b;
			b -= a;  b ^= rot(a, 6);  a += c;
			c -= b;  c ^= rot(b, 8);  b += a;
			a -= c;  a ^= rot(c,16);  c += b;
			b -= a;  b ^= rot(a,19);  a += c;
			c -= b;  c ^= rot(b, 4);  b += a;
		}

		c += state.length;

		switch (state.length - i) {
			case 11: c += state[i++] << 24;
			case 10: c += state[i++] << 16;
			case 9:  c += state[i++] << 8;
			case 8:  b += state[i++] << 24;
			case 7:  b += state[i++] << 16;
			case 6:  b += state[i++] << 8;
			case 5:  b += state[i++];
			case 4:  a += state[i++] << 24;
			case 3:  a += state[i++] << 16;
			case 2:  a += state[i++] << 8;
			case 1:  a += state[i++];
				c ^= b; c -= rot(b,14);
				a ^= c; a -= rot(c,11);
				b ^= a; b -= rot(a,25);
				c ^= b; c -= rot(b,16);
				a ^= c; a -= rot(c,4);
				b ^= a; b -= rot(a,14);
				c ^= b; c -= rot(b,24);
		}

		return (((long)b) << 32) | c;
	}
}

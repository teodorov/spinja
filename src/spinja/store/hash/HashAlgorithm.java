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
 * An HashAlgorithm describes a way to create a hash from a state, that is stored as a byte[]. It
 * also provides the {@link #getDefaultAlgorithm()}, which will by default return the
 * {@link JenkinsHash}.
 * @author Marc de Jonge
 */
public abstract class HashAlgorithm {
	public static interface HashGenerator {
		public int currentHash();

		public int nextHash();
	}
	
	public static interface LongHashGenerator {
		public long currentHash();
		
		public long nextHash();
	}

	/**
	 * @return The HashAlgorithm that is the default one.
	 */
	public static final HashAlgorithm getDefaultAlgorithm() {
		return new JenkinsHash();
	}

	public abstract int hash(byte[] state, int var);

	public abstract long hash(byte[] state, long var);

	public HashGenerator hash(final byte[] state) {
		return new HashGenerator() {
			private int value = hash(state, 0);

			private final int incr = hash(state, value) | 1;

			public int currentHash() {
				return value;
			}

			public int nextHash() {
				value += incr;
				return value;
			}
		};
	}
	
	public LongHashGenerator bitstateHash(final byte[] state) {
		return new LongHashGenerator() {
			private long value = hash(state, 0l);
			
			public long currentHash() {
				return value;
			}
			
			public long nextHash() {
				value = hash(state, value);
				return value;
			}
		};
	}

	protected final static int rot(final int val, final int nr) {
		return (val << nr) | (val >>> (32 - nr));
	}

	protected final static long rot(final long val, final int nr) {
		return (val << nr) | (val >>> (64 - nr));
	}
}

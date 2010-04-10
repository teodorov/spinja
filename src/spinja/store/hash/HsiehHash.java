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

import spinja.exceptions.NotImplementedException;

public class HsiehHash extends HashAlgorithm {
	@Override
	public long hash(byte[] state, long hash) {
		throw new NotImplementedException("Hashing to 64bit is not yet supported by the Hsieh hash function.");
	}

	@Override
	public int hash(byte[] state, int hash) {
		int tmp;

		if (state == null || state.length == 0)
			return hash;

		int i = 0;
		while(i < state.length - 3) {
			hash += state[i++];
			hash += state[i++] << 8;
			tmp = state[i++] << 11;
			tmp += state[i++] << 19;
			tmp ^= hash;
			hash = (hash << 16) ^ tmp;
			hash += hash >>> 11;
		}

		switch (state.length - i) {
			case 3:
				hash ^= hash << 16;
				hash ^= state[i++] << 18;
				hash += hash >> 11;
				break;
			case 2:
				hash += state[i++];
				hash += state[i++] << 8;
				hash ^= hash << 11;
				hash += hash >>> 17;
				break;
			case 1:
				hash += state[i++];
				hash ^= hash << 10;
				hash += hash >> 1;
		}

		hash ^= hash << 3;
		hash += hash >>> 5;
		hash ^= hash << 4;
		hash += hash >>> 17;
		hash ^= hash << 25;
		hash += hash >>> 6;

		return hash;
	}

}

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

import java.io.IOException;
import java.io.InputStream;

public class InputStreamPrinter extends Thread {
	private final InputStream is;

	public InputStreamPrinter(final InputStream stream) {
		is = stream;
	}

	@Override
	public void run() {
		final byte[] buffer = new byte[1024];
		int i = 0;
		try {
			while ((i = is.read(buffer)) != -1) {
				System.out.println(new String(buffer, 0, i));
			}
		} catch (final IOException ex) {
			System.err.println(ex.getMessage());
		}
	}

}

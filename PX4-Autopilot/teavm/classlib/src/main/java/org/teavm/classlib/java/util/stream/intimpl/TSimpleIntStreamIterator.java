/*
 *  Copyright 2017 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.util.stream.intimpl;

import java.util.NoSuchElementException;
import java.util.PrimitiveIterator;

public class TSimpleIntStreamIterator implements PrimitiveIterator.OfInt {
    private static final byte NEEDS_MORE = 0;
    private static final byte HAS_DATA = 1;
    private static final byte LAST_ELEMENT = 2;
    private static final byte DONE = 3;

    private TSimpleIntStreamImpl stream;
    private int lastElement;
    private byte state;

    public TSimpleIntStreamIterator(TSimpleIntStreamImpl stream) {
        this.stream = stream;
    }

    @Override
    public boolean hasNext() {
        fetchIfNeeded();
        return state != DONE;
    }

    @Override
    public int nextInt() {
        fetchIfNeeded();
        if (state == DONE) {
            throw new NoSuchElementException();
        }
        int result = lastElement;
        state = state == LAST_ELEMENT ? DONE : NEEDS_MORE;
        return result;
    }

    private void fetchIfNeeded() {
        if (state != NEEDS_MORE) {
            return;
        }
        state = NEEDS_MORE;
        while (state == NEEDS_MORE) {
            boolean hasMore = stream.next(e -> {
                lastElement = e;
                state = HAS_DATA;
                return false;
            });
            if (!hasMore) {
                if (state == NEEDS_MORE) {
                    state = DONE;
                } else {
                    state = LAST_ELEMENT;
                }
                stream = null;
            }
        }
    }
}

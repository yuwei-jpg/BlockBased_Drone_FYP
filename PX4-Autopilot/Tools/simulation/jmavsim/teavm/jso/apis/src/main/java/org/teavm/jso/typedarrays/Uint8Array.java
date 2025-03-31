/*
 *  Copyright 2015 Alexey Andreev.
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
package org.teavm.jso.typedarrays;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSClass;
import org.teavm.jso.JSIndexer;

@JSClass
public class Uint8Array extends ArrayBufferView {
    public Uint8Array(int length) {
    }

    public Uint8Array(ArrayBuffer buffer) {
    }

    public Uint8Array(ArrayBufferView buffer) {
    }

    public Uint8Array(ArrayBuffer buffer, int offset, int length) {
    }

    public Uint8Array(ArrayBuffer buffer, int offset) {
    }

    @JSIndexer
    public native short get(int index);

    @JSIndexer
    public native void set(int index, short value);

    @JSBody(params = "length", script = "return new Uint8Array(length);")
    @Deprecated
    public static native Uint8Array create(int length);

    @JSBody(params = "buffer", script = "return new Uint8Array(buffer);")
    @Deprecated
    public static native Uint8Array create(ArrayBuffer buffer);

    @JSBody(params = "buffer", script = "return new Uint8Array(buffer);")
    @Deprecated
    public static native Uint8Array create(ArrayBufferView buffer);

    @JSBody(params = { "buffer", "offset", "length" }, script = "return new Uint8Array(buffer, offset, length);")
    @Deprecated
    public static native Uint8Array create(ArrayBuffer buffer, int offset, int length);

    @JSBody(params = { "buffer", "offset" }, script = "return new Uint8Array(buffer, offset);")
    @Deprecated
    public static native Uint8Array create(ArrayBuffer buffer, int offset);
}

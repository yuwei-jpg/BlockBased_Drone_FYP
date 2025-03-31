/*
 *  Copyright 2024 ihromant.
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
package org.teavm.jso.streams;

import org.teavm.jso.JSClass;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.core.JSArrayReader;
import org.teavm.jso.core.JSPromise;
import org.teavm.jso.core.JSUndefined;

@JSClass
public class ReadableStream implements JSObject {
    private ReadableStream() {
    }

    @JSProperty
    public native boolean isLocked();

    public native JSPromise<JSUndefined> abort(String reason);

    public native JSPromise<JSUndefined> cancel();

    public native JSPromise<JSUndefined> cancel(String reason);

    public native JSArrayReader<? extends ReadableStream> tee();

    public native ReadableStreamDefaultReader getReader();
}

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
package org.teavm.jso.file;

import org.teavm.jso.JSClass;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.core.JSArrayReader;

@JSClass
public class File extends Blob implements JSObject {
    /**
     * Actual elements within array are either {@link Blob} or {@link org.teavm.jso.core.JSString}
     */
    public File(JSArrayReader<JSObject> array, String fileName) {
        super(null);
    }

    /**
     * Actual elements within array are either {@link Blob} or {@link org.teavm.jso.core.JSString}
     */
    public File(JSArrayReader<JSObject> array, String fileName, JSObject options) {
        super(null, null);
    }

    @JSProperty
    public native double getLastModified();

    @JSProperty
    public native String getName();
}

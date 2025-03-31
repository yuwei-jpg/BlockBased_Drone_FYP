/*
 *  Copyright 2024 Alexey Andreev.
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
package org.teavm.backend.wasm.generators.gc;

import org.teavm.backend.wasm.model.WasmFunction;
import org.teavm.backend.wasm.model.WasmLocal;
import org.teavm.backend.wasm.model.WasmType;
import org.teavm.backend.wasm.model.expression.WasmUnreachable;
import org.teavm.model.MethodReference;
import org.teavm.model.ValueType;

public class SystemDoArrayCopyGenerator implements WasmGCCustomGenerator {
    @Override
    public void apply(MethodReference method, WasmFunction function, WasmGCCustomGeneratorContext context) {
        function.add(new WasmLocal(context.typeMapper().mapType(ValueType.object("java.lang.Object"))));
        function.add(new WasmLocal(WasmType.INT32));
        function.add(new WasmLocal(context.typeMapper().mapType(ValueType.object("java.lang.Object"))));
        function.add(new WasmLocal(WasmType.INT32));
        function.add(new WasmLocal(WasmType.INT32));
        function.getBody().add(new WasmUnreachable());
    }
}

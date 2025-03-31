/*
 *  Copyright 2024 konsoletyper.
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
package org.teavm.backend.wasm.intrinsics.gc;

import org.teavm.ast.InvocationExpr;
import org.teavm.backend.wasm.model.WasmFunction;
import org.teavm.backend.wasm.model.WasmNumType;
import org.teavm.backend.wasm.model.WasmType;
import org.teavm.backend.wasm.model.expression.WasmCall;
import org.teavm.backend.wasm.model.expression.WasmConversion;
import org.teavm.backend.wasm.model.expression.WasmExpression;

public class SystemIntrinsic implements WasmGCIntrinsic {
    private WasmFunction workerFunction;

    @Override
    public WasmExpression apply(InvocationExpr invocation, WasmGCIntrinsicContext context) {
        if (workerFunction == null) {
            workerFunction = new WasmFunction(context.functionTypes().of(WasmType.FLOAT64));
            workerFunction.setName("teavm@currentTimeMillis");
            workerFunction.setImportName("currentTimeMillis");
            workerFunction.setImportModule("teavmDate");
            context.module().functions.add(workerFunction);
        }
        var call = new WasmCall(workerFunction);
        return new WasmConversion(WasmNumType.FLOAT64, WasmNumType.INT64, true, call);
    }
}

/*
 *  Copyright 2016 Alexey Andreev.
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
package org.teavm.backend.wasm.transformation;

import org.teavm.backend.wasm.WasmFunctionTypes;
import org.teavm.backend.wasm.model.WasmFunction;
import org.teavm.backend.wasm.model.WasmModule;
import org.teavm.backend.wasm.model.WasmType;
import org.teavm.backend.wasm.model.expression.WasmCall;
import org.teavm.backend.wasm.model.expression.WasmIndirectCall;
import org.teavm.backend.wasm.model.expression.WasmInt32Constant;
import org.teavm.backend.wasm.model.expression.WasmReplacingExpressionVisitor;

public class IndirectCallTraceTransformation {
    private WasmModule module;
    private WasmFunctionTypes functionTypes;

    public IndirectCallTraceTransformation(WasmModule module, WasmFunctionTypes functionTypes) {
        this.module = module;
        this.functionTypes = functionTypes;
    }

    public void apply() {
        var traceFunction = new WasmFunction(functionTypes.of(WasmType.INT32, WasmType.INT32, WasmType.INT32));
        traceFunction.setName("traceIndirectCall");
        traceFunction.setImportModule("debug");
        traceFunction.setImportName("traceIndirectCall");
        module.functions.add(traceFunction);

        int[] positionHolder = { 0 };
        var visitor = new WasmReplacingExpressionVisitor(expression -> {
            if (expression instanceof WasmIndirectCall) {
                var indirectCall = (WasmIndirectCall) expression;
                var call = new WasmCall(traceFunction);
                call.getArguments().add(new WasmInt32Constant(positionHolder[0]++));
                call.getArguments().add(indirectCall.getSelector());
                indirectCall.setSelector(call);
            }
            return expression;
        });
        for (var function : module.functions) {
            visitor.replace(function);
        }
    }
}

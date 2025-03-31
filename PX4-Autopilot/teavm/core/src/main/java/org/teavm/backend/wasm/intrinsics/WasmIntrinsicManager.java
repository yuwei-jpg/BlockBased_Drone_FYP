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
package org.teavm.backend.wasm.intrinsics;

import java.util.List;
import org.teavm.ast.Expr;
import org.teavm.backend.wasm.WasmFunctionRepository;
import org.teavm.backend.wasm.WasmFunctionTypes;
import org.teavm.backend.wasm.binary.BinaryWriter;
import org.teavm.backend.wasm.generate.WasmStringPool;
import org.teavm.backend.wasm.model.WasmFunction;
import org.teavm.backend.wasm.model.WasmLocal;
import org.teavm.backend.wasm.model.WasmTag;
import org.teavm.backend.wasm.model.WasmType;
import org.teavm.backend.wasm.model.expression.WasmExpression;
import org.teavm.diagnostics.Diagnostics;
import org.teavm.model.FieldReference;
import org.teavm.model.MethodReference;
import org.teavm.model.TextLocation;
import org.teavm.model.ValueType;

public interface WasmIntrinsicManager {
    WasmExpression generate(Expr expr);

    BinaryWriter getBinaryWriter();

    WasmStringPool getStringPool();

    Diagnostics getDiagnostics();

    WasmFunctionRepository getFunctions();

    WasmFunctionTypes getFunctionTypes();

    WasmLocal getTemporary(WasmType type);

    int getStaticField(FieldReference field);

    int getClassPointer(ValueType type);

    int getFunctionPointer(WasmFunction function);

    void releaseTemporary(WasmLocal local);

    boolean isManagedMethodCall(MethodReference method);

    CallSiteIdentifier generateCallSiteId(TextLocation location);

    WasmTag getExceptionTag();

    interface CallSiteIdentifier {
        void generateRegister(List<WasmExpression> target, TextLocation location);
    }
}

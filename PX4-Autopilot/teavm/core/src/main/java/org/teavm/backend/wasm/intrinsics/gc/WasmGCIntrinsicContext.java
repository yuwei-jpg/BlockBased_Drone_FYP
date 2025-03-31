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
package org.teavm.backend.wasm.intrinsics.gc;

import java.util.function.Consumer;
import org.teavm.ast.Expr;
import org.teavm.backend.wasm.BaseWasmFunctionRepository;
import org.teavm.backend.wasm.WasmFunctionTypes;
import org.teavm.backend.wasm.gc.PreciseTypeInference;
import org.teavm.backend.wasm.generate.ExpressionCache;
import org.teavm.backend.wasm.generate.TemporaryVariablePool;
import org.teavm.backend.wasm.generate.gc.WasmGCNameProvider;
import org.teavm.backend.wasm.generate.gc.classes.WasmGCClassInfoProvider;
import org.teavm.backend.wasm.generate.gc.classes.WasmGCTypeMapper;
import org.teavm.backend.wasm.generate.gc.strings.WasmGCStringProvider;
import org.teavm.backend.wasm.model.WasmFunction;
import org.teavm.backend.wasm.model.WasmModule;
import org.teavm.backend.wasm.model.WasmTag;
import org.teavm.backend.wasm.model.expression.WasmExpression;
import org.teavm.diagnostics.Diagnostics;
import org.teavm.model.ClassHierarchy;
import org.teavm.model.MethodReference;

public interface WasmGCIntrinsicContext {
    WasmExpression generate(Expr expr);

    WasmModule module();

    WasmFunctionTypes functionTypes();

    PreciseTypeInference types();

    BaseWasmFunctionRepository functions();

    ClassHierarchy hierarchy();

    WasmGCTypeMapper typeMapper();

    WasmGCClassInfoProvider classInfoProvider();

    TemporaryVariablePool tempVars();

    ExpressionCache exprCache();

    WasmGCNameProvider names();

    WasmGCStringProvider strings();

    ClassLoader classLoader();

    WasmTag exceptionTag();

    String entryPoint();

    Diagnostics diagnostics();

    MethodReference currentMethod();

    void addToInitializer(Consumer<WasmFunction> initializerContributor);
}

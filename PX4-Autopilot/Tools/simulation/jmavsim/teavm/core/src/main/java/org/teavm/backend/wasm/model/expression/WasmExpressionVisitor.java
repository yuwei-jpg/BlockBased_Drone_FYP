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
package org.teavm.backend.wasm.model.expression;

public interface WasmExpressionVisitor {
    void visit(WasmBlock expression);

    void visit(WasmBranch expression);

    void visit(WasmNullBranch expression);

    void visit(WasmCastBranch expression);

    void visit(WasmBreak expression);

    void visit(WasmSwitch expression);

    void visit(WasmConditional expression);

    void visit(WasmReturn expression);

    void visit(WasmUnreachable expression);

    void visit(WasmInt32Constant expression);

    void visit(WasmInt64Constant expression);

    void visit(WasmFloat32Constant expression);

    void visit(WasmFloat64Constant expression);

    void visit(WasmNullConstant expression);

    void visit(WasmIsNull expression);

    void visit(WasmGetLocal expression);

    void visit(WasmSetLocal expression);

    void visit(WasmGetGlobal expression);

    void visit(WasmSetGlobal expression);

    void visit(WasmIntBinary expression);

    void visit(WasmFloatBinary expression);

    void visit(WasmIntUnary expression);

    void visit(WasmFloatUnary expression);

    void visit(WasmConversion expression);

    void visit(WasmCall expression);

    void visit(WasmIndirectCall expression);

    void visit(WasmCallReference expression);

    void visit(WasmDrop expression);

    void visit(WasmLoadInt32 expression);

    void visit(WasmLoadInt64 expression);

    void visit(WasmLoadFloat32 expression);

    void visit(WasmLoadFloat64 expression);

    void visit(WasmStoreInt32 expression);

    void visit(WasmStoreInt64 expression);

    void visit(WasmStoreFloat32 expression);

    void visit(WasmStoreFloat64 expression);

    void visit(WasmMemoryGrow expression);

    void visit(WasmFill expression);

    void visit(WasmCopy expression);

    void visit(WasmTry expression);

    void visit(WasmThrow expression);

    void visit(WasmReferencesEqual expression);

    void visit(WasmCast expression);

    void visit(WasmExternConversion expression);

    void visit(WasmTest expression);

    void visit(WasmStructNew expression);

    void visit(WasmStructNewDefault expression);

    void visit(WasmStructGet expression);

    void visit(WasmStructSet expression);

    void visit(WasmArrayNewDefault expression);

    void visit(WasmArrayNewFixed expression);

    void visit(WasmArrayGet expression);

    void visit(WasmArraySet expression);

    void visit(WasmArrayLength expression);

    void visit(WasmArrayCopy expression);

    void visit(WasmFunctionReference expression);

    void visit(WasmInt31Reference expression);

    void visit(WasmInt31Get expression);
}

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
package org.teavm.backend.wasm.parser;

import java.util.ArrayList;
import java.util.List;
import org.teavm.backend.wasm.model.WasmNumType;
import org.teavm.backend.wasm.model.expression.WasmFloatBinaryOperation;
import org.teavm.backend.wasm.model.expression.WasmFloatType;
import org.teavm.backend.wasm.model.expression.WasmFloatUnaryOperation;
import org.teavm.backend.wasm.model.expression.WasmInt32Subtype;
import org.teavm.backend.wasm.model.expression.WasmInt64Subtype;
import org.teavm.backend.wasm.model.expression.WasmIntBinaryOperation;
import org.teavm.backend.wasm.model.expression.WasmIntType;
import org.teavm.backend.wasm.model.expression.WasmIntUnaryOperation;
import org.teavm.backend.wasm.model.expression.WasmSignedType;

public class CodeParser extends BaseSectionParser {
    private CodeListener codeListener;
    private final List<Block> blockStack = new ArrayList<>();

    public void setCodeListener(CodeListener codeListener) {
        this.codeListener = codeListener;
    }

    @Override
    protected void parseContent() {
        parseCode();
    }

    public boolean parseSingleExpression(WasmBinaryReader reader) {
        this.reader = reader;
        try {
            return parseExpressions();
        } finally {
            this.reader = reader;
        }
    }

    private void parseCode() {
        if (!parseExpressions()) {
            codeListener.error(blockStack.size());
            blockStack.clear();
        }
    }

    private boolean parseExpressions() {
        while (reader.data[reader.ptr] != 0x0B) {
            if (!parseExpr()) {
                return false;
            }
        }
        return true;
    }

    private boolean parseExpr() {
        reportAddress();
        switch (reader.data[reader.ptr++] & 0xFF) {
            case 0x00:
                codeListener.opcode(Opcode.UNREACHABLE);
                break;
            case 0x01:
                codeListener.opcode(Opcode.NOP);
                break;
            case 0x02:
                return parseBlock(false);
            case 0x03:
                return parseBlock(true);
            case 0x04:
                return parseConditional();
            case 0x06:
                return parseTryCatch();
            case 0x8:
                codeListener.throwInstruction(readLEB());
                break;
            case 0x0C:
                parseBranch(BranchOpcode.BR);
                break;
            case 0x0D:
                parseBranch(BranchOpcode.BR_IF);
                break;
            case 0x0E:
                parseTableBranch();
                break;
            case 0x0F:
                codeListener.opcode(Opcode.RETURN);
                break;
            case 0x10:
                codeListener.call(readLEB());
                break;
            case 0x11:
                codeListener.indirectCall(readLEB(), readLEB());
                break;
            case 0x14:
                codeListener.callReference(readLEB());
                break;

            case 0x1A:
                codeListener.opcode(Opcode.DROP);
                break;

            case 0x20:
                codeListener.local(LocalOpcode.GET, readLEB());
                break;
            case 0x21:
                codeListener.local(LocalOpcode.SET, readLEB());
                break;

            case 0x23:
                codeListener.getGlobal(readLEB());
                break;
            case 0x24:
                codeListener.setGlobal(readLEB());
                break;

            case 0x28:
                codeListener.loadInt32(WasmInt32Subtype.INT32, 1 << readLEB(), readLEB());
                break;
            case 0x29:
                codeListener.loadInt64(WasmInt64Subtype.INT64, 1 << readLEB(), readLEB());
                break;
            case 0x2A:
                codeListener.loadFloat32(1 << readLEB(), readLEB());
                break;
            case 0x2B:
                codeListener.loadFloat64(1 << readLEB(), readLEB());
                break;
            case 0x2C:
                codeListener.loadInt32(WasmInt32Subtype.INT8, 1 << readLEB(), readLEB());
                break;
            case 0x2D:
                codeListener.loadInt32(WasmInt32Subtype.UINT8, 1 << readLEB(), readLEB());
                break;
            case 0x2E:
                codeListener.loadInt32(WasmInt32Subtype.INT16, 1 << readLEB(), readLEB());
                break;
            case 0x2F:
                codeListener.loadInt32(WasmInt32Subtype.UINT16, 1 << readLEB(), readLEB());
                break;
            case 0x30:
                codeListener.loadInt64(WasmInt64Subtype.INT8, 1 << readLEB(), readLEB());
                break;
            case 0x31:
                codeListener.loadInt64(WasmInt64Subtype.UINT8, 1 << readLEB(), readLEB());
                break;
            case 0x32:
                codeListener.loadInt64(WasmInt64Subtype.INT16, 1 << readLEB(), readLEB());
                break;
            case 0x33:
                codeListener.loadInt64(WasmInt64Subtype.UINT16, 1 << readLEB(), readLEB());
                break;
            case 0x34:
                codeListener.loadInt64(WasmInt64Subtype.INT32, 1 << readLEB(), readLEB());
                break;
            case 0x35:
                codeListener.loadInt64(WasmInt64Subtype.UINT32, 1 << readLEB(), readLEB());
                break;
            case 0x36:
                codeListener.storeInt32(WasmInt32Subtype.INT32, 1 << readLEB(), readLEB());
                break;
            case 0x37:
                codeListener.storeInt64(WasmInt64Subtype.INT64, 1 << readLEB(), readLEB());
                break;
            case 0x38:
                codeListener.storeFloat32(1 << readLEB(), readLEB());
                break;
            case 0x39:
                codeListener.storeFloat64(1 << readLEB(), readLEB());
                break;
            case 0x3A:
                codeListener.storeInt32(WasmInt32Subtype.INT8, 1 << readLEB(), readLEB());
                break;
            case 0x3B:
                codeListener.storeInt32(WasmInt32Subtype.INT16, 1 << readLEB(), readLEB());
                break;
            case 0x3C:
                codeListener.storeInt64(WasmInt64Subtype.INT8, 1 << readLEB(), readLEB());
                break;
            case 0x3D:
                codeListener.storeInt64(WasmInt64Subtype.INT16, 1 << readLEB(), readLEB());
                break;
            case 0x3E:
                codeListener.storeInt64(WasmInt64Subtype.INT32, 1 << readLEB(), readLEB());
                break;
            case 0x40:
                readLEB();
                codeListener.memoryGrow();
                break;

            case 0x41:
                codeListener.int32Constant(readSignedLEB());
                break;
            case 0x42:
                codeListener.int64Constant(readSignedLongLEB());
                break;
            case 0x43:
                codeListener.float32Constant(Float.intBitsToFloat(readFixedInt()));
                break;
            case 0x44:
                codeListener.float64Constant(Double.longBitsToDouble(readFixedLong()));
                break;

            case 0x45:
                codeListener.unary(WasmIntUnaryOperation.EQZ, WasmIntType.INT32);
                break;
            case 0x46:
                codeListener.binary(WasmIntBinaryOperation.EQ, WasmIntType.INT32);
                break;
            case 0x47:
                codeListener.binary(WasmIntBinaryOperation.NE, WasmIntType.INT32);
                break;
            case 0x48:
                codeListener.binary(WasmIntBinaryOperation.LT_SIGNED, WasmIntType.INT32);
                break;
            case 0x49:
                codeListener.binary(WasmIntBinaryOperation.LT_UNSIGNED, WasmIntType.INT32);
                break;
            case 0x4A:
                codeListener.binary(WasmIntBinaryOperation.GT_SIGNED, WasmIntType.INT32);
                break;
            case 0x4B:
                codeListener.binary(WasmIntBinaryOperation.GT_UNSIGNED, WasmIntType.INT32);
                break;
            case 0x4C:
                codeListener.binary(WasmIntBinaryOperation.LE_SIGNED, WasmIntType.INT32);
                break;
            case 0x4D:
                codeListener.binary(WasmIntBinaryOperation.LE_UNSIGNED, WasmIntType.INT32);
                break;
            case 0x4E:
                codeListener.binary(WasmIntBinaryOperation.GE_SIGNED, WasmIntType.INT32);
                break;
            case 0x4F:
                codeListener.binary(WasmIntBinaryOperation.GE_UNSIGNED, WasmIntType.INT32);
                break;

            case 0x50:
                codeListener.unary(WasmIntUnaryOperation.EQZ, WasmIntType.INT64);
                break;
            case 0x51:
                codeListener.binary(WasmIntBinaryOperation.EQ, WasmIntType.INT64);
                break;
            case 0x52:
                codeListener.binary(WasmIntBinaryOperation.NE, WasmIntType.INT64);
                break;
            case 0x53:
                codeListener.binary(WasmIntBinaryOperation.LT_SIGNED, WasmIntType.INT64);
                break;
            case 0x54:
                codeListener.binary(WasmIntBinaryOperation.LT_UNSIGNED, WasmIntType.INT64);
                break;
            case 0x55:
                codeListener.binary(WasmIntBinaryOperation.GT_SIGNED, WasmIntType.INT64);
                break;
            case 0x56:
                codeListener.binary(WasmIntBinaryOperation.GT_UNSIGNED, WasmIntType.INT64);
                break;
            case 0x57:
                codeListener.binary(WasmIntBinaryOperation.LE_SIGNED, WasmIntType.INT64);
                break;
            case 0x58:
                codeListener.binary(WasmIntBinaryOperation.LE_UNSIGNED, WasmIntType.INT64);
                break;
            case 0x59:
                codeListener.binary(WasmIntBinaryOperation.GE_SIGNED, WasmIntType.INT64);
                break;
            case 0x5A:
                codeListener.binary(WasmIntBinaryOperation.GE_UNSIGNED, WasmIntType.INT64);
                break;

            case 0x5B:
                codeListener.binary(WasmFloatBinaryOperation.EQ, WasmFloatType.FLOAT32);
                break;
            case 0x5C:
                codeListener.binary(WasmFloatBinaryOperation.NE, WasmFloatType.FLOAT32);
                break;
            case 0x5D:
                codeListener.binary(WasmFloatBinaryOperation.LT, WasmFloatType.FLOAT32);
                break;
            case 0x5E:
                codeListener.binary(WasmFloatBinaryOperation.GT, WasmFloatType.FLOAT32);
                break;
            case 0x5F:
                codeListener.binary(WasmFloatBinaryOperation.LE, WasmFloatType.FLOAT32);
                break;
            case 0x60:
                codeListener.binary(WasmFloatBinaryOperation.GE, WasmFloatType.FLOAT32);
                break;

            case 0x61:
                codeListener.binary(WasmFloatBinaryOperation.EQ, WasmFloatType.FLOAT64);
                break;
            case 0x62:
                codeListener.binary(WasmFloatBinaryOperation.NE, WasmFloatType.FLOAT64);
                break;
            case 0x63:
                codeListener.binary(WasmFloatBinaryOperation.LT, WasmFloatType.FLOAT64);
                break;
            case 0x64:
                codeListener.binary(WasmFloatBinaryOperation.GT, WasmFloatType.FLOAT64);
                break;
            case 0x65:
                codeListener.binary(WasmFloatBinaryOperation.LE, WasmFloatType.FLOAT64);
                break;
            case 0x66:
                codeListener.binary(WasmFloatBinaryOperation.GE, WasmFloatType.FLOAT64);
                break;

            case 0x67:
                codeListener.unary(WasmIntUnaryOperation.CLZ, WasmIntType.INT32);
                break;
            case 0x68:
                codeListener.unary(WasmIntUnaryOperation.CTZ, WasmIntType.INT32);
                break;
            case 0x69:
                codeListener.unary(WasmIntUnaryOperation.POPCNT, WasmIntType.INT32);
                break;
            case 0x6A:
                codeListener.binary(WasmIntBinaryOperation.ADD, WasmIntType.INT32);
                break;
            case 0x6B:
                codeListener.binary(WasmIntBinaryOperation.SUB, WasmIntType.INT32);
                break;
            case 0x6C:
                codeListener.binary(WasmIntBinaryOperation.MUL, WasmIntType.INT32);
                break;
            case 0x6D:
                codeListener.binary(WasmIntBinaryOperation.DIV_SIGNED, WasmIntType.INT32);
                break;
            case 0x6E:
                codeListener.binary(WasmIntBinaryOperation.DIV_UNSIGNED, WasmIntType.INT32);
                break;
            case 0x6F:
                codeListener.binary(WasmIntBinaryOperation.REM_SIGNED, WasmIntType.INT32);
                break;
            case 0x70:
                codeListener.binary(WasmIntBinaryOperation.REM_UNSIGNED, WasmIntType.INT32);
                break;
            case 0x71:
                codeListener.binary(WasmIntBinaryOperation.AND, WasmIntType.INT32);
                break;
            case 0x72:
                codeListener.binary(WasmIntBinaryOperation.OR, WasmIntType.INT32);
                break;
            case 0x73:
                codeListener.binary(WasmIntBinaryOperation.XOR, WasmIntType.INT32);
                break;
            case 0x74:
                codeListener.binary(WasmIntBinaryOperation.SHL, WasmIntType.INT32);
                break;
            case 0x75:
                codeListener.binary(WasmIntBinaryOperation.SHR_SIGNED, WasmIntType.INT32);
                break;
            case 0x76:
                codeListener.binary(WasmIntBinaryOperation.SHR_UNSIGNED, WasmIntType.INT32);
                break;
            case 0x77:
                codeListener.binary(WasmIntBinaryOperation.ROTL, WasmIntType.INT32);
                break;
            case 0x78:
                codeListener.binary(WasmIntBinaryOperation.ROTR, WasmIntType.INT32);
                break;

            case 0x79:
                codeListener.unary(WasmIntUnaryOperation.CLZ, WasmIntType.INT64);
                break;
            case 0x7A:
                codeListener.unary(WasmIntUnaryOperation.CTZ, WasmIntType.INT64);
                break;
            case 0x7B:
                codeListener.unary(WasmIntUnaryOperation.POPCNT, WasmIntType.INT64);
                break;
            case 0x7C:
                codeListener.binary(WasmIntBinaryOperation.ADD, WasmIntType.INT64);
                break;
            case 0x7D:
                codeListener.binary(WasmIntBinaryOperation.SUB, WasmIntType.INT64);
                break;
            case 0x7E:
                codeListener.binary(WasmIntBinaryOperation.MUL, WasmIntType.INT64);
                break;
            case 0x7F:
                codeListener.binary(WasmIntBinaryOperation.DIV_SIGNED, WasmIntType.INT64);
                break;
            case 0x80:
                codeListener.binary(WasmIntBinaryOperation.DIV_UNSIGNED, WasmIntType.INT64);
                break;
            case 0x81:
                codeListener.binary(WasmIntBinaryOperation.REM_SIGNED, WasmIntType.INT64);
                break;
            case 0x82:
                codeListener.binary(WasmIntBinaryOperation.REM_UNSIGNED, WasmIntType.INT64);
                break;
            case 0x83:
                codeListener.binary(WasmIntBinaryOperation.AND, WasmIntType.INT64);
                break;
            case 0x84:
                codeListener.binary(WasmIntBinaryOperation.OR, WasmIntType.INT64);
                break;
            case 0x85:
                codeListener.binary(WasmIntBinaryOperation.XOR, WasmIntType.INT64);
                break;
            case 0x86:
                codeListener.binary(WasmIntBinaryOperation.SHL, WasmIntType.INT64);
                break;
            case 0x87:
                codeListener.binary(WasmIntBinaryOperation.SHR_UNSIGNED, WasmIntType.INT64);
                break;
            case 0x88:
                codeListener.binary(WasmIntBinaryOperation.SHR_UNSIGNED, WasmIntType.INT64);
                break;
            case 0x89:
                codeListener.binary(WasmIntBinaryOperation.ROTL, WasmIntType.INT64);
                break;
            case 0x8A:
                codeListener.binary(WasmIntBinaryOperation.ROTR, WasmIntType.INT64);
                break;

            case 0x8B:
                codeListener.unary(WasmFloatUnaryOperation.ABS, WasmFloatType.FLOAT32);
                break;
            case 0x8C:
                codeListener.unary(WasmFloatUnaryOperation.NEG, WasmFloatType.FLOAT32);
                break;
            case 0x8D:
                codeListener.unary(WasmFloatUnaryOperation.CEIL, WasmFloatType.FLOAT32);
                break;
            case 0x8E:
                codeListener.unary(WasmFloatUnaryOperation.FLOOR, WasmFloatType.FLOAT32);
                break;
            case 0x8F:
                codeListener.unary(WasmFloatUnaryOperation.TRUNC, WasmFloatType.FLOAT32);
                break;
            case 0x90:
                codeListener.unary(WasmFloatUnaryOperation.NEAREST, WasmFloatType.FLOAT32);
                break;
            case 0x91:
                codeListener.unary(WasmFloatUnaryOperation.SQRT, WasmFloatType.FLOAT32);
                break;
            case 0x92:
                codeListener.binary(WasmFloatBinaryOperation.ADD, WasmFloatType.FLOAT32);
                break;
            case 0x93:
                codeListener.binary(WasmFloatBinaryOperation.SUB, WasmFloatType.FLOAT32);
                break;
            case 0x94:
                codeListener.binary(WasmFloatBinaryOperation.MUL, WasmFloatType.FLOAT32);
                break;
            case 0x95:
                codeListener.binary(WasmFloatBinaryOperation.DIV, WasmFloatType.FLOAT32);
                break;
            case 0x96:
                codeListener.binary(WasmFloatBinaryOperation.MIN, WasmFloatType.FLOAT32);
                break;
            case 0x97:
                codeListener.binary(WasmFloatBinaryOperation.MAX, WasmFloatType.FLOAT32);
                break;
            case 0x98:
                codeListener.unary(WasmFloatUnaryOperation.COPYSIGN, WasmFloatType.FLOAT32);
                break;

            case 0x99:
                codeListener.unary(WasmFloatUnaryOperation.ABS, WasmFloatType.FLOAT64);
                break;
            case 0x9A:
                codeListener.unary(WasmFloatUnaryOperation.NEG, WasmFloatType.FLOAT64);
                break;
            case 0x9B:
                codeListener.unary(WasmFloatUnaryOperation.CEIL, WasmFloatType.FLOAT64);
                break;
            case 0x9C:
                codeListener.unary(WasmFloatUnaryOperation.FLOOR, WasmFloatType.FLOAT64);
                break;
            case 0x9D:
                codeListener.unary(WasmFloatUnaryOperation.TRUNC, WasmFloatType.FLOAT64);
                break;
            case 0x9E:
                codeListener.unary(WasmFloatUnaryOperation.NEAREST, WasmFloatType.FLOAT64);
                break;
            case 0x9F:
                codeListener.unary(WasmFloatUnaryOperation.SQRT, WasmFloatType.FLOAT64);
                break;
            case 0xA0:
                codeListener.binary(WasmFloatBinaryOperation.ADD, WasmFloatType.FLOAT64);
                break;
            case 0xA1:
                codeListener.binary(WasmFloatBinaryOperation.SUB, WasmFloatType.FLOAT64);
                break;
            case 0xA2:
                codeListener.binary(WasmFloatBinaryOperation.MUL, WasmFloatType.FLOAT64);
                break;
            case 0xA3:
                codeListener.binary(WasmFloatBinaryOperation.DIV, WasmFloatType.FLOAT64);
                break;
            case 0xA4:
                codeListener.binary(WasmFloatBinaryOperation.MIN, WasmFloatType.FLOAT64);
                break;
            case 0xA5:
                codeListener.binary(WasmFloatBinaryOperation.MAX, WasmFloatType.FLOAT64);
                break;
            case 0xA6:
                codeListener.unary(WasmFloatUnaryOperation.COPYSIGN, WasmFloatType.FLOAT64);
                break;

            case 0xA7:
                codeListener.convert(WasmNumType.INT64, WasmNumType.INT32, false, false, false);
                break;
            case 0xA8:
                codeListener.convert(WasmNumType.FLOAT32, WasmNumType.INT32, false, false, false);
                break;
            case 0xA9:
                codeListener.convert(WasmNumType.FLOAT32, WasmNumType.INT32, true, false, false);
                break;
            case 0xAA:
                codeListener.convert(WasmNumType.FLOAT64, WasmNumType.INT32, false, false, false);
                break;
            case 0xAB:
                codeListener.convert(WasmNumType.FLOAT64, WasmNumType.INT32, true, false, false);
                break;
            case 0xAC:
                codeListener.convert(WasmNumType.INT32, WasmNumType.INT64, false, false, false);
                break;
            case 0xAD:
                codeListener.convert(WasmNumType.INT32, WasmNumType.INT64, true, false, false);
                break;
            case 0xAE:
                codeListener.convert(WasmNumType.FLOAT32, WasmNumType.INT64, false, false, false);
                break;
            case 0xAF:
                codeListener.convert(WasmNumType.FLOAT32, WasmNumType.INT64, true, false, false);
                break;
            case 0xB0:
                codeListener.convert(WasmNumType.FLOAT64, WasmNumType.INT64, false, false, false);
                break;
            case 0xB1:
                codeListener.convert(WasmNumType.FLOAT64, WasmNumType.INT64, true, false, false);
                break;
            case 0xB2:
                codeListener.convert(WasmNumType.INT32, WasmNumType.FLOAT32, false, false, false);
                break;
            case 0xB3:
                codeListener.convert(WasmNumType.INT32, WasmNumType.FLOAT32, true, false, false);
                break;
            case 0xB4:
                codeListener.convert(WasmNumType.INT64, WasmNumType.FLOAT32, false, false, false);
                break;
            case 0xB5:
                codeListener.convert(WasmNumType.INT64, WasmNumType.FLOAT32, true, false, false);
                break;
            case 0xB6:
                codeListener.convert(WasmNumType.FLOAT64, WasmNumType.FLOAT32, true, false, false);
                break;
            case 0xB7:
                codeListener.convert(WasmNumType.INT32, WasmNumType.FLOAT64, false, false, false);
                break;
            case 0xB8:
                codeListener.convert(WasmNumType.INT32, WasmNumType.FLOAT64, true, false, false);
                break;
            case 0xB9:
                codeListener.convert(WasmNumType.INT64, WasmNumType.FLOAT64, false, false, false);
                break;
            case 0xBA:
                codeListener.convert(WasmNumType.INT64, WasmNumType.FLOAT64, true, false, false);
                break;
            case 0xBC:
                codeListener.convert(WasmNumType.FLOAT32, WasmNumType.INT32, false, true, false);
                break;
            case 0xBD:
                codeListener.convert(WasmNumType.FLOAT64, WasmNumType.INT64, false, true, false);
                break;
            case 0xBE:
                codeListener.convert(WasmNumType.INT32, WasmNumType.FLOAT32, false, true, false);
                break;
            case 0xBF:
                codeListener.convert(WasmNumType.INT64, WasmNumType.FLOAT64, false, true, false);
                break;

            case 0xD0:
                codeListener.nullConstant(reader.readHeapType(true));
                break;
            case 0xD1:
                codeListener.opcode(Opcode.IS_NULL);
                break;

            case 0xD2:
                codeListener.functionReference(readLEB());
                break;

            case 0xD3:
                codeListener.opcode(Opcode.REF_EQ);
                break;

            case 0xD5:
                parseBranch(BranchOpcode.BR_ON_NULL);
                break;
            case 0xD6:
                parseBranch(BranchOpcode.BR_ON_NON_NULL);
                break;

            case 0xFB:
                return parseExtExpr2();
            case 0xFC:
                return parseExtExpr();

            default:
                return false;
        }
        return true;
    }

    private boolean parseExtExpr() {
        switch (readLEB()) {
            case 0:
                codeListener.convert(WasmNumType.INT32, WasmNumType.FLOAT32, true, false, true);
                return true;
            case 1:
                codeListener.convert(WasmNumType.INT32, WasmNumType.FLOAT32, false, false, true);
                return true;
            case 2:
                codeListener.convert(WasmNumType.INT32, WasmNumType.FLOAT64, true, false, true);
                return true;
            case 3:
                codeListener.convert(WasmNumType.INT32, WasmNumType.FLOAT64, false, false, true);
                return true;
            case 4:
                codeListener.convert(WasmNumType.INT64, WasmNumType.FLOAT32, true, false, true);
                return true;
            case 5:
                codeListener.convert(WasmNumType.INT64, WasmNumType.FLOAT32, false, false, true);
                return true;
            case 6:
                codeListener.convert(WasmNumType.INT64, WasmNumType.FLOAT64, true, false, true);
                return true;
            case 7:
                codeListener.convert(WasmNumType.INT64, WasmNumType.FLOAT64, false, false, true);
                return true;

            case 10: {
                if (reader.data[reader.ptr++] != 0 || reader.data[reader.ptr++] != 0) {
                    return false;
                }
                codeListener.memoryCopy();
                return true;
            }
            case 11: {
                if (reader.data[reader.ptr++] != 0) {
                    return false;
                }
                codeListener.memoryFill();
                return true;
            }

            default:
                return false;
        }
    }

    private boolean parseExtExpr2() {
        switch (readLEB()) {
            case 0:
                codeListener.structNew(readLEB());
                return true;

            case 1:
                codeListener.structNewDefault(readLEB());
                return true;

            case 2:
                codeListener.structGet(null, readLEB(), readLEB());
                return true;
            case 3:
                codeListener.structGet(WasmSignedType.SIGNED, readLEB(), readLEB());
                return true;
            case 4:
                codeListener.structGet(WasmSignedType.UNSIGNED, readLEB(), readLEB());
                return true;

            case 5:
                codeListener.structSet(readLEB(), readLEB());
                return true;

            case 7:
                codeListener.arrayNewDefault(readLEB());
                return true;
            case 8:
                codeListener.arrayNewFixed(readLEB(), readLEB());
                return true;

            case 11:
                codeListener.arrayGet(null, readLEB());
                return true;
            case 12:
                codeListener.arrayGet(WasmSignedType.SIGNED, readLEB());
                return true;
            case 13:
                codeListener.arrayGet(WasmSignedType.UNSIGNED, readLEB());
                return true;

            case 14:
                codeListener.arraySet(readLEB());
                return true;

            case 15:
                codeListener.opcode(Opcode.ARRAY_LENGTH);
                return true;

            case 17:
                codeListener.arrayCopy(readLEB(), readLEB());
                return true;

            case 20:
                codeListener.test(reader.readHeapType(false));
                return true;

            case 21:
                codeListener.test(reader.readHeapType(true));
                return true;

            case 22:
                codeListener.cast(reader.readHeapType(false));
                return true;

            case 23:
                codeListener.cast(reader.readHeapType(true));
                return true;

            case 24:
                parseCastBranch(true);
                return true;
            case 25:
                parseCastBranch(false);
                return true;

            case 26:
                codeListener.opcode(Opcode.EXTERN_TO_ANY);
                return true;
            case 27:
                codeListener.opcode(Opcode.ANY_TO_EXTERN);
                return true;

            case 28:
                codeListener.int31Reference();
                return true;

            case 29:
                codeListener.int31Get(WasmSignedType.SIGNED);
                return true;
            case 30:
                codeListener.int31Get(WasmSignedType.UNSIGNED);
                return true;

            default:
                return false;
        }
    }

    private boolean parseBlock(boolean isLoop) {
        var type = reader.readType();
        var token = codeListener.startBlock(isLoop, type);
        blockStack.add(new Block(token));
        if (!parseExpressions()) {
            return false;
        }
        blockStack.remove(blockStack.size() - 1);
        reportAddress();
        codeListener.endBlock(token, isLoop);
        ++reader.ptr;
        return true;
    }

    private boolean parseConditional() {
        var type = reader.readType();
        var token = codeListener.startConditionalBlock(type);
        blockStack.add(new Block(token));
        var hasElse = false;
        loop: while (true) {
            switch (reader.data[reader.ptr]) {
                case 0x0B:
                    break loop;
                case 0x05:
                    if (hasElse) {
                        return false;
                    }
                    reportAddress();
                    codeListener.startElseSection(blockStack.get(blockStack.size() - 1).token);
                    ++reader.ptr;
                    break;
                default:
                    if (!parseExpr()) {
                        return false;
                    }
                    break;
            }
        }
        blockStack.remove(blockStack.size() - 1);
        reportAddress();
        codeListener.endBlock(token, false);
        ++reader.ptr;
        return true;
    }

    private boolean parseTryCatch() {
        var type = reader.readType();
        var token = codeListener.startTry(type);
        blockStack.add(new Block(token));
        loop: while (true) {
            switch (reader.data[reader.ptr]) {
                case 0x0B:
                    break loop;
                case 0x07: {
                    reportAddress();
                    var tagIndex = readLEB();
                    ++reader.ptr;
                    codeListener.startCatch(tagIndex);
                    break;
                }
                default:
                    if (!parseExpr()) {
                        return false;
                    }
                    break;
            }
        }
        blockStack.remove(blockStack.size() - 1);
        reportAddress();
        codeListener.endBlock(token, false);
        ++reader.ptr;
        return true;
    }

    private void parseBranch(BranchOpcode opcode) {
        var depth = readLEB();
        var target = blockStack.get(blockStack.size() - depth - 1);
        codeListener.branch(opcode, depth, target.token);
    }

    private void parseCastBranch(boolean success) {
        var flags = reader.data[reader.ptr++];
        var depth = readLEB();
        var target = blockStack.get(blockStack.size() - depth - 1);
        var sourceType = reader.readHeapType((flags & 1) != 0);
        var targetType = reader.readHeapType((flags & 2) != 0);
        codeListener.castBranch(success, depth, target.token, sourceType, targetType);
    }

    private void parseTableBranch() {
        var count = readLEB();
        var depths = new int[count];
        var targets = new int[count];
        for (var i = 0; i < count; ++i) {
            var depth = readLEB();
            depths[i] = depth;
            targets[i] = blockStack.get(blockStack.size() - depth - 1).token;
        }
        var defaultDepth = readLEB();
        var defaultTarget = blockStack.get(blockStack.size() - defaultDepth - 1).token;
        codeListener.tableBranch(depths, targets, defaultDepth, defaultTarget);
    }

    private static class Block {
        int token;

        Block(int token) {
            this.token = token;
        }
    }
}

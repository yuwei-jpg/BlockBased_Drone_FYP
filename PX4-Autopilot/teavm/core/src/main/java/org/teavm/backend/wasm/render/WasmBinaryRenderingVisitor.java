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
package org.teavm.backend.wasm.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.teavm.backend.wasm.debug.DebugLines;
import org.teavm.backend.wasm.generate.DwarfGenerator;
import org.teavm.backend.wasm.model.WasmModule;
import org.teavm.backend.wasm.model.WasmType;
import org.teavm.backend.wasm.model.expression.WasmArrayCopy;
import org.teavm.backend.wasm.model.expression.WasmArrayGet;
import org.teavm.backend.wasm.model.expression.WasmArrayLength;
import org.teavm.backend.wasm.model.expression.WasmArrayNewDefault;
import org.teavm.backend.wasm.model.expression.WasmArrayNewFixed;
import org.teavm.backend.wasm.model.expression.WasmArraySet;
import org.teavm.backend.wasm.model.expression.WasmBlock;
import org.teavm.backend.wasm.model.expression.WasmBranch;
import org.teavm.backend.wasm.model.expression.WasmBreak;
import org.teavm.backend.wasm.model.expression.WasmCall;
import org.teavm.backend.wasm.model.expression.WasmCallReference;
import org.teavm.backend.wasm.model.expression.WasmCast;
import org.teavm.backend.wasm.model.expression.WasmCastBranch;
import org.teavm.backend.wasm.model.expression.WasmConditional;
import org.teavm.backend.wasm.model.expression.WasmConversion;
import org.teavm.backend.wasm.model.expression.WasmCopy;
import org.teavm.backend.wasm.model.expression.WasmDefaultExpressionVisitor;
import org.teavm.backend.wasm.model.expression.WasmDrop;
import org.teavm.backend.wasm.model.expression.WasmExpression;
import org.teavm.backend.wasm.model.expression.WasmExpressionVisitor;
import org.teavm.backend.wasm.model.expression.WasmExternConversion;
import org.teavm.backend.wasm.model.expression.WasmFill;
import org.teavm.backend.wasm.model.expression.WasmFloat32Constant;
import org.teavm.backend.wasm.model.expression.WasmFloat64Constant;
import org.teavm.backend.wasm.model.expression.WasmFloatBinary;
import org.teavm.backend.wasm.model.expression.WasmFloatUnary;
import org.teavm.backend.wasm.model.expression.WasmFunctionReference;
import org.teavm.backend.wasm.model.expression.WasmGetGlobal;
import org.teavm.backend.wasm.model.expression.WasmGetLocal;
import org.teavm.backend.wasm.model.expression.WasmIndirectCall;
import org.teavm.backend.wasm.model.expression.WasmInt31Get;
import org.teavm.backend.wasm.model.expression.WasmInt31Reference;
import org.teavm.backend.wasm.model.expression.WasmInt32Constant;
import org.teavm.backend.wasm.model.expression.WasmInt64Constant;
import org.teavm.backend.wasm.model.expression.WasmIntBinary;
import org.teavm.backend.wasm.model.expression.WasmIntUnary;
import org.teavm.backend.wasm.model.expression.WasmIsNull;
import org.teavm.backend.wasm.model.expression.WasmLoadFloat32;
import org.teavm.backend.wasm.model.expression.WasmLoadFloat64;
import org.teavm.backend.wasm.model.expression.WasmLoadInt32;
import org.teavm.backend.wasm.model.expression.WasmLoadInt64;
import org.teavm.backend.wasm.model.expression.WasmMemoryGrow;
import org.teavm.backend.wasm.model.expression.WasmNullBranch;
import org.teavm.backend.wasm.model.expression.WasmNullConstant;
import org.teavm.backend.wasm.model.expression.WasmReferencesEqual;
import org.teavm.backend.wasm.model.expression.WasmReturn;
import org.teavm.backend.wasm.model.expression.WasmSetGlobal;
import org.teavm.backend.wasm.model.expression.WasmSetLocal;
import org.teavm.backend.wasm.model.expression.WasmSignedType;
import org.teavm.backend.wasm.model.expression.WasmStoreFloat32;
import org.teavm.backend.wasm.model.expression.WasmStoreFloat64;
import org.teavm.backend.wasm.model.expression.WasmStoreInt32;
import org.teavm.backend.wasm.model.expression.WasmStoreInt64;
import org.teavm.backend.wasm.model.expression.WasmStructGet;
import org.teavm.backend.wasm.model.expression.WasmStructNew;
import org.teavm.backend.wasm.model.expression.WasmStructNewDefault;
import org.teavm.backend.wasm.model.expression.WasmStructSet;
import org.teavm.backend.wasm.model.expression.WasmSwitch;
import org.teavm.backend.wasm.model.expression.WasmTest;
import org.teavm.backend.wasm.model.expression.WasmThrow;
import org.teavm.backend.wasm.model.expression.WasmTry;
import org.teavm.backend.wasm.model.expression.WasmUnreachable;
import org.teavm.model.InliningInfo;
import org.teavm.model.TextLocation;

class WasmBinaryRenderingVisitor implements WasmExpressionVisitor {
    private WasmBinaryWriter writer;
    private WasmModule module;
    private DwarfGenerator dwarfGenerator;
    private DebugLines debugLines;
    private int addressOffset;
    private int depth;
    private Map<WasmBlock, Integer> blockDepths = new HashMap<>();
    private List<InliningInfo> methodStack = new ArrayList<>();
    private List<InliningInfo> currentMethodStack = new ArrayList<>();
    private TextLocation textLocationToEmit;
    private boolean deferTextLocationToEmit;
    private TextLocation lastEmittedLocation;
    private int positionToEmit;
    private List<TextLocation> locationStack = new ArrayList<>();
    private Set<WasmBlock> blocksToPreserve = new HashSet<>();

    WasmBinaryRenderingVisitor(WasmBinaryWriter writer, WasmModule module,
            DwarfGenerator dwarfGenerator, DebugLines debugLines, int addressOffset) {
        this.writer = writer;
        this.module = module;
        this.dwarfGenerator = dwarfGenerator;
        this.addressOffset = addressOffset;
        this.debugLines = debugLines;
    }

    public void setPositionToEmit(int positionToEmit) {
        this.positionToEmit = positionToEmit;
    }

    void preprocess(WasmExpression expression) {
        expression.acceptVisitor(new WasmDefaultExpressionVisitor() {
            @Override
            public void visit(WasmBranch expression) {
                super.visit(expression);
                register(expression.getTarget());
            }

            @Override
            public void visit(WasmNullBranch expression) {
                super.visit(expression);
                register(expression.getTarget());
            }

            @Override
            public void visit(WasmCastBranch expression) {
                super.visit(expression);
                register(expression.getTarget());
            }

            @Override
            public void visit(WasmBreak expression) {
                super.visit(expression);
                register(expression.getTarget());
            }

            @Override
            public void visit(WasmSwitch expression) {
                super.visit(expression);
                for (WasmBlock target : expression.getTargets()) {
                    register(target);
                }
                register(expression.getDefaultTarget());
            }

            private void register(WasmBlock block) {
                blocksToPreserve.add(block);
            }
        });
    }

    @Override
    public void visit(WasmBlock expression) {
        if (blocksToPreserve.contains(expression) || expression.isLoop()) {
            pushLocation(expression);
            pushLocation(expression);
            int blockDepth = 1;
            depth += blockDepth;
            blockDepths.put(expression, depth);
            writer.writeByte(expression.isLoop() ? 0x03 : 0x02);
            writeBlockType(expression.getType());
            for (WasmExpression part : expression.getBody()) {
                part.acceptVisitor(this);
            }
            popLocation();
            writer.writeByte(0x0B);
            popLocation();
            blockDepths.remove(expression);
            depth -= blockDepth;
        } else {
            pushLocation(expression);
            for (var part : expression.getBody()) {
                part.acceptVisitor(this);
            }
            popLocation();
        }
    }

    private void writeBlockType(WasmType type) {
        writer.writeType(type, module);
    }

    @Override
    public void visit(WasmBranch expression) {
        pushLocation(expression);
        if (expression.getResult() != null) {
            expression.getResult().acceptVisitor(this);
        }
        expression.getCondition().acceptVisitor(this);
        writer.writeByte(0x0D);
        writeLabel(expression.getTarget());
        popLocation();
    }

    @Override
    public void visit(WasmNullBranch expression) {
        pushLocation(expression);
        if (expression.getResult() != null) {
            expression.getResult().acceptVisitor(this);
        }
        expression.getValue().acceptVisitor(this);
        switch (expression.getCondition()) {
            case NULL:
                writer.writeByte(0xD5);
                break;
            case NOT_NULL:
                writer.writeByte(0xD6);
                break;
        }
        writeLabel(expression.getTarget());
        popLocation();
    }

    @Override
    public void visit(WasmCastBranch expression) {
        pushLocation(expression);
        if (expression.getResult() != null) {
            expression.getResult().acceptVisitor(this);
        }
        expression.getValue().acceptVisitor(this);
        writer.writeByte(0xFB);
        switch (expression.getCondition()) {
            case SUCCESS:
                writer.writeByte(24);
                break;
            case FAILURE:
                writer.writeByte(25);
                break;
        }
        var flags = 0;
        if (expression.getSourceType().isNullable()) {
            flags |= 1;
        }
        if (expression.getType().isNullable()) {
            flags |= 2;
        }
        writer.writeByte(flags);
        writeLabel(expression.getTarget());
        writer.writeHeapType(expression.getSourceType(), module);
        writer.writeHeapType(expression.getType(), module);
        popLocation();
    }

    @Override
    public void visit(WasmBreak expression) {
        pushLocation(expression);
        if (expression.getResult() != null) {
            expression.getResult().acceptVisitor(this);
        }
        writer.writeByte(0x0C);
        writeLabel(expression.getTarget());
        popLocation();
    }

    @Override
    public void visit(WasmSwitch expression) {
        pushLocation(expression);
        expression.getSelector().acceptVisitor(this);

        writer.writeByte(0x0E);

        writer.writeLEB(expression.getTargets().size());
        for (WasmBlock target : expression.getTargets()) {
            int targetDepth = blockDepths.get(target);
            int relativeDepth = depth - targetDepth;
            writer.writeLEB(relativeDepth);
        }

        int defaultDepth = blockDepths.get(expression.getDefaultTarget());
        int relativeDepth = depth - defaultDepth;
        writer.writeLEB(relativeDepth);
        popLocation();
    }

    @Override
    public void visit(WasmConditional expression) {
        pushLocation(expression);
        pushLocation(expression);
        expression.getCondition().acceptVisitor(this);
        writer.writeByte(0x04);
        writeBlockType(expression.getType());

        ++depth;
        blockDepths.put(expression.getThenBlock(), depth);
        for (WasmExpression part : expression.getThenBlock().getBody()) {
            part.acceptVisitor(this);
        }
        blockDepths.remove(expression.getThenBlock());

        if (!expression.getElseBlock().getBody().isEmpty()) {
            writer.writeByte(0x05);
            blockDepths.put(expression.getElseBlock(), depth);
            for (WasmExpression part : expression.getElseBlock().getBody()) {
                part.acceptVisitor(this);
            }
            blockDepths.remove(expression.getElseBlock());
        }
        --depth;

        popLocation();
        writer.writeByte(0x0B);
        popLocation();
    }

    @Override
    public void visit(WasmReturn expression) {
        pushLocation(expression);
        if (expression.getValue() != null) {
            expression.getValue().acceptVisitor(this);
        }
        writer.writeByte(0x0F);
        popLocation();
    }

    @Override
    public void visit(WasmUnreachable expression) {
        pushLocation(expression);
        writer.writeByte(0x0);
        popLocation();
    }

    @Override
    public void visit(WasmInt32Constant expression) {
        pushLocation(expression);
        writer.writeByte(0x41);
        writer.writeSignedLEB(expression.getValue());
        popLocation();
    }

    @Override
    public void visit(WasmInt64Constant expression) {
        pushLocation(expression);
        writer.writeByte(0x42);
        writer.writeSignedLEB(expression.getValue());
        popLocation();
    }

    @Override
    public void visit(WasmFloat32Constant expression) {
        pushLocation(expression);
        writer.writeByte(0x43);
        writer.writeFixed(Float.floatToRawIntBits(expression.getValue()));
        popLocation();
    }

    @Override
    public void visit(WasmFloat64Constant expression) {
        pushLocation(expression);
        writer.writeByte(0x44);
        writer.writeFixed(Double.doubleToRawLongBits(expression.getValue()));
        popLocation();
    }

    @Override
    public void visit(WasmNullConstant expression) {
        pushLocation(expression);
        writer.writeByte(0xD0);
        writer.writeHeapType(expression.getType(), module);
        popLocation();
    }

    @Override
    public void visit(WasmIsNull expression) {
        pushLocation(expression);
        expression.getValue().acceptVisitor(this);
        writer.writeByte(0xD1);
        popLocation();
    }

    @Override
    public void visit(WasmGetLocal expression) {
        pushLocation(expression);
        writer.writeByte(0x20);
        writer.writeLEB(expression.getLocal().getIndex());
        popLocation();
    }

    @Override
    public void visit(WasmSetLocal expression) {
        pushLocation(expression);
        expression.getValue().acceptVisitor(this);
        writer.writeByte(0x21);
        writer.writeLEB(expression.getLocal().getIndex());
        popLocation();
    }

    @Override
    public void visit(WasmGetGlobal expression) {
        pushLocation(expression);
        writer.writeByte(0x23);
        writer.writeLEB(module.globals.indexOf(expression.getGlobal()));
        popLocation();
    }

    @Override
    public void visit(WasmSetGlobal expression) {
        pushLocation(expression);
        expression.getValue().acceptVisitor(this);
        writer.writeByte(0x24);
        writer.writeLEB(module.globals.indexOf(expression.getGlobal()));
        popLocation();
    }

    @Override
    public void visit(WasmIntBinary expression) {
        pushLocation(expression);
        expression.getFirst().acceptVisitor(this);
        expression.getSecond().acceptVisitor(this);
        render0xD(expression);
        popLocation();
    }

    private void render0xD(WasmIntBinary expression) {
        switch (expression.getType()) {
            case INT32:
                switch (expression.getOperation()) {
                    case ADD:
                        writer.writeByte(0x6A);
                        break;
                    case SUB:
                        writer.writeByte(0x6B);
                        break;
                    case MUL:
                        writer.writeByte(0x6C);
                        break;
                    case DIV_SIGNED:
                        writer.writeByte(0x6D);
                        break;
                    case DIV_UNSIGNED:
                        writer.writeByte(0x6E);
                        break;
                    case REM_SIGNED:
                        writer.writeByte(0x6F);
                        break;
                    case REM_UNSIGNED:
                        writer.writeByte(0x70);
                        break;
                    case AND:
                        writer.writeByte(0x71);
                        break;
                    case OR:
                        writer.writeByte(0x72);
                        break;
                    case XOR:
                        writer.writeByte(0x73);
                        break;
                    case SHL:
                        writer.writeByte(0x74);
                        break;
                    case SHR_SIGNED:
                        writer.writeByte(0x75);
                        break;
                    case SHR_UNSIGNED:
                        writer.writeByte(0x76);
                        break;
                    case ROTL:
                        writer.writeByte(0x77);
                        break;
                    case ROTR:
                        writer.writeByte(0x78);
                        break;
                    case EQ:
                        writer.writeByte(0x46);
                        break;
                    case NE:
                        writer.writeByte(0x47);
                        break;
                    case LT_SIGNED:
                        writer.writeByte(0x48);
                        break;
                    case LT_UNSIGNED:
                        writer.writeByte(0x49);
                        break;
                    case GT_SIGNED:
                        writer.writeByte(0x4A);
                        break;
                    case GT_UNSIGNED:
                        writer.writeByte(0x4B);
                        break;
                    case LE_SIGNED:
                        writer.writeByte(0x4C);
                        break;
                    case LE_UNSIGNED:
                        writer.writeByte(0x4D);
                        break;
                    case GE_SIGNED:
                        writer.writeByte(0x4E);
                        break;
                    case GE_UNSIGNED:
                        writer.writeByte(0x4F);
                        break;
                }
                break;
            case INT64:
                switch (expression.getOperation()) {
                    case ADD:
                        writer.writeByte(0x7C);
                        break;
                    case SUB:
                        writer.writeByte(0x7D);
                        break;
                    case MUL:
                        writer.writeByte(0x7E);
                        break;
                    case DIV_SIGNED:
                        writer.writeByte(0x7F);
                        break;
                    case DIV_UNSIGNED:
                        writer.writeByte(0x80);
                        break;
                    case REM_SIGNED:
                        writer.writeByte(0x81);
                        break;
                    case REM_UNSIGNED:
                        writer.writeByte(0x82);
                        break;
                    case AND:
                        writer.writeByte(0x83);
                        break;
                    case OR:
                        writer.writeByte(0x84);
                        break;
                    case XOR:
                        writer.writeByte(0x85);
                        break;
                    case SHL:
                        writer.writeByte(0x86);
                        break;
                    case SHR_SIGNED:
                        writer.writeByte(0x87);
                        break;
                    case SHR_UNSIGNED:
                        writer.writeByte(0x88);
                        break;
                    case ROTL:
                        writer.writeByte(0x89);
                        break;
                    case ROTR:
                        writer.writeByte(0x8A);
                        break;
                    case EQ:
                        writer.writeByte(0x51);
                        break;
                    case NE:
                        writer.writeByte(0x52);
                        break;
                    case LT_SIGNED:
                        writer.writeByte(0x53);
                        break;
                    case LT_UNSIGNED:
                        writer.writeByte(0x54);
                        break;
                    case GT_SIGNED:
                        writer.writeByte(0x55);
                        break;
                    case GT_UNSIGNED:
                        writer.writeByte(0x56);
                        break;
                    case LE_SIGNED:
                        writer.writeByte(0x57);
                        break;
                    case LE_UNSIGNED:
                        writer.writeByte(0x58);
                        break;
                    case GE_SIGNED:
                        writer.writeByte(0x59);
                        break;
                    case GE_UNSIGNED:
                        writer.writeByte(0x5A);
                        break;
                }
                break;
        }
    }

    @Override
    public void visit(WasmFloatBinary expression) {
        pushLocation(expression);
        expression.getFirst().acceptVisitor(this);
        expression.getSecond().acceptVisitor(this);
        render0xD(expression);
        popLocation();
    }

    private void render0xD(WasmFloatBinary expression) {
        switch (expression.getType()) {
            case FLOAT32:
                switch (expression.getOperation()) {
                    case ADD:
                        writer.writeByte(0x92);
                        break;
                    case SUB:
                        writer.writeByte(0x93);
                        break;
                    case MUL:
                        writer.writeByte(0x94);
                        break;
                    case DIV:
                        writer.writeByte(0x95);
                        break;
                    case MIN:
                        writer.writeByte(0x96);
                        break;
                    case MAX:
                        writer.writeByte(0x97);
                        break;
                    case EQ:
                        writer.writeByte(0x5B);
                        break;
                    case NE:
                        writer.writeByte(0x5C);
                        break;
                    case LT:
                        writer.writeByte(0x5D);
                        break;
                    case GT:
                        writer.writeByte(0x5E);
                        break;
                    case LE:
                        writer.writeByte(0x5F);
                        break;
                    case GE:
                        writer.writeByte(0x60);
                        break;
                }
                break;
            case FLOAT64:
                switch (expression.getOperation()) {
                    case ADD:
                        writer.writeByte(0xA0);
                        break;
                    case SUB:
                        writer.writeByte(0xA1);
                        break;
                    case MUL:
                        writer.writeByte(0xA2);
                        break;
                    case DIV:
                        writer.writeByte(0xA3);
                        break;
                    case MIN:
                        writer.writeByte(0xA4);
                        break;
                    case MAX:
                        writer.writeByte(0xA5);
                        break;
                    case EQ:
                        writer.writeByte(0x61);
                        break;
                    case NE:
                        writer.writeByte(0x62);
                        break;
                    case LT:
                        writer.writeByte(0x63);
                        break;
                    case GT:
                        writer.writeByte(0x64);
                        break;
                    case LE:
                        writer.writeByte(0x65);
                        break;
                    case GE:
                        writer.writeByte(0x66);
                        break;
                }
                break;
        }
    }

    @Override
    public void visit(WasmIntUnary expression) {
        pushLocation(expression);
        expression.getOperand().acceptVisitor(this);
        switch (expression.getType()) {
            case INT32:
                switch (expression.getOperation()) {
                    case EQZ:
                        writer.writeByte(0x45);
                        break;
                    case CLZ:
                        writer.writeByte(0x67);
                        break;
                    case CTZ:
                        writer.writeByte(0x68);
                        break;
                    case POPCNT:
                        writer.writeByte(0x69);
                        break;
                }
                break;
            case INT64:
                switch (expression.getOperation()) {
                    case EQZ:
                        writer.writeByte(0x50);
                        break;
                    case CLZ:
                        writer.writeByte(0x79);
                        break;
                    case CTZ:
                        writer.writeByte(0x7A);
                        break;
                    case POPCNT:
                        writer.writeByte(0x7B);
                        break;
                }
                break;
        }
        popLocation();
    }

    @Override
    public void visit(WasmFloatUnary expression) {
        pushLocation(expression);
        expression.getOperand().acceptVisitor(this);
        render0xD(expression);
        popLocation();
    }

    private void render0xD(WasmFloatUnary expression) {
        switch (expression.getType()) {
            case FLOAT32:
                switch (expression.getOperation()) {
                    case ABS:
                        writer.writeByte(0x8B);
                        break;
                    case NEG:
                        writer.writeByte(0x8C);
                        break;
                    case CEIL:
                        writer.writeByte(0x8D);
                        break;
                    case FLOOR:
                        writer.writeByte(0x8E);
                        break;
                    case TRUNC:
                        writer.writeByte(0x8F);
                        break;
                    case NEAREST:
                        writer.writeByte(0x90);
                        break;
                    case SQRT:
                        writer.writeByte(0x91);
                        break;
                    case COPYSIGN:
                        writer.writeByte(0x98);
                        break;
                }
                break;
            case FLOAT64:
                switch (expression.getOperation()) {
                    case ABS:
                        writer.writeByte(0x99);
                        break;
                    case NEG:
                        writer.writeByte(0x9A);
                        break;
                    case CEIL:
                        writer.writeByte(0x9B);
                        break;
                    case FLOOR:
                        writer.writeByte(0x9C);
                        break;
                    case TRUNC:
                        writer.writeByte(0x9D);
                        break;
                    case NEAREST:
                        writer.writeByte(0x9E);
                        break;
                    case SQRT:
                        writer.writeByte(0x9F);
                        break;
                    case COPYSIGN:
                        writer.writeByte(0xA6);
                        break;
                }
                break;
        }
    }

    @Override
    public void visit(WasmConversion expression) {
        pushLocation(expression);
        expression.getOperand().acceptVisitor(this);
        switch (expression.getSourceType()) {
            case INT32:
                switch (expression.getTargetType()) {
                    case INT32:
                        break;
                    case INT64:
                        writer.writeByte(expression.isSigned() ? 0xAC : 0xAD);
                        break;
                    case FLOAT32:
                        if (expression.isReinterpret()) {
                            writer.writeByte(0xBE);
                        } else {
                            writer.writeByte(expression.isSigned() ? 0xB2 : 0xB3);
                        }
                        break;
                    case FLOAT64:
                        writer.writeByte(expression.isSigned() ? 0xB7 : 0xB8);
                        break;
                }
                break;
            case INT64:
                switch (expression.getTargetType()) {
                    case INT32:
                        writer.writeByte(0xA7);
                        break;
                    case INT64:
                        break;
                    case FLOAT32:
                        writer.writeByte(expression.isSigned() ? 0xB4 : 0xB5);
                        break;
                    case FLOAT64:
                        if (expression.isReinterpret()) {
                            writer.writeByte(0xBF);
                        } else {
                            writer.writeByte(expression.isSigned() ? 0xB9 : 0xBA);
                        }
                        break;
                }
                break;
            case FLOAT32:
                switch (expression.getTargetType()) {
                    case INT32:
                        if (expression.isReinterpret()) {
                            writer.writeByte(0xBC);
                        } else if (expression.isNonTrapping()) {
                            writer.writeByte(0xFC);
                            writer.writeByte(expression.isSigned() ? 0 : 1);
                        } else {
                            writer.writeByte(expression.isSigned() ? 0xA8 : 0xA9);
                        }
                        break;
                    case INT64:
                        if (expression.isNonTrapping()) {
                            writer.writeByte(0xFC);
                            writer.writeByte(expression.isSigned() ? 4 : 5);
                        } else {
                            writer.writeByte(expression.isSigned() ? 0xAE : 0xAF);
                        }
                        break;
                    case FLOAT32:
                        break;
                    case FLOAT64:
                        writer.writeByte(0xBB);
                        break;
                }
                break;
            case FLOAT64:
                switch (expression.getTargetType()) {
                    case INT32:
                        if (expression.isNonTrapping()) {
                            writer.writeByte(0xFC);
                            writer.writeByte(expression.isSigned() ? 2 : 3);
                        } else {
                            writer.writeByte(expression.isSigned() ? 0xAA : 0xAB);
                        }
                        break;
                    case INT64:
                        if (expression.isReinterpret()) {
                            writer.writeByte(0xBD);
                        } else if (expression.isNonTrapping()) {
                            writer.writeByte(0xFC);
                            writer.writeByte(expression.isSigned() ? 6 : 7);
                        } else {
                            writer.writeByte(expression.isSigned() ? 0xB0 : 0xB1);
                        }
                        break;
                    case FLOAT32:
                        writer.writeByte(0xB6);
                        break;
                    case FLOAT64:
                        break;
                }
                break;
        }
        popLocation();
    }

    @Override
    public void visit(WasmCall expression) {
        pushLocation(expression);
        for (WasmExpression argument : expression.getArguments()) {
            argument.acceptVisitor(this);
        }
        var functionIndex = module.functions.indexOf(expression.getFunction());

        writer.writeByte(0x10);
        writer.writeLEB(functionIndex);
        popLocation();
    }

    @Override
    public void visit(WasmIndirectCall expression) {
        pushLocation(expression);
        for (WasmExpression argument : expression.getArguments()) {
            argument.acceptVisitor(this);
        }
        expression.getSelector().acceptVisitor(this);
        writer.writeByte(0x11);
        writer.writeLEB(module.types.indexOf(expression.getType()));

        writer.writeByte(0);
        popLocation();
    }

    @Override
    public void visit(WasmCallReference expression) {
        pushLocation(expression);
        for (var argument : expression.getArguments()) {
            argument.acceptVisitor(this);
        }
        expression.getFunctionReference().acceptVisitor(this);
        writer.writeByte(0x14);
        writer.writeLEB(module.types.indexOf(expression.getType()));
        popLocation();
    }

    @Override
    public void visit(WasmDrop expression) {
        pushLocation(expression);
        expression.getOperand().acceptVisitor(this);
        writer.writeByte(0x1A);
        popLocation();
    }

    @Override
    public void visit(WasmLoadInt32 expression) {
        pushLocation(expression);
        expression.getIndex().acceptVisitor(this);
        switch (expression.getConvertFrom()) {
            case INT8:
                writer.writeByte(0x2C);
                break;
            case UINT8:
                writer.writeByte(0x2D);
                break;
            case INT16:
                writer.writeByte(0x2E);
                break;
            case UINT16:
                writer.writeByte(0x2F);
                break;
            case INT32:
                writer.writeByte(0x28);
                break;
        }
        writer.writeByte(alignment(expression.getAlignment()));
        writer.writeLEB(expression.getOffset());
        popLocation();
    }

    @Override
    public void visit(WasmLoadInt64 expression) {
        pushLocation(expression);
        expression.getIndex().acceptVisitor(this);
        switch (expression.getConvertFrom()) {
            case INT8:
                writer.writeByte(0x30);
                break;
            case UINT8:
                writer.writeByte(0x31);
                break;
            case INT16:
                writer.writeByte(0x32);
                break;
            case UINT16:
                writer.writeByte(0x33);
                break;
            case INT32:
                writer.writeByte(0x34);
                break;
            case UINT32:
                writer.writeByte(0x35);
                break;
            case INT64:
                writer.writeByte(0x29);
                break;
        }
        writer.writeByte(alignment(expression.getAlignment()));
        writer.writeLEB(expression.getOffset());
        popLocation();
    }

    @Override
    public void visit(WasmLoadFloat32 expression) {
        pushLocation(expression);
        expression.getIndex().acceptVisitor(this);
        writer.writeByte(0x2A);
        writer.writeByte(alignment(expression.getAlignment()));
        writer.writeLEB(expression.getOffset());
        popLocation();
    }

    @Override
    public void visit(WasmLoadFloat64 expression) {
        pushLocation(expression);
        expression.getIndex().acceptVisitor(this);
        writer.writeByte(0x2B);
        writer.writeByte(alignment(expression.getAlignment()));
        writer.writeLEB(expression.getOffset());
        popLocation();
    }

    @Override
    public void visit(WasmStoreInt32 expression) {
        pushLocation(expression);
        expression.getIndex().acceptVisitor(this);
        expression.getValue().acceptVisitor(this);
        switch (expression.getConvertTo()) {
            case INT8:
            case UINT8:
                writer.writeByte(0x3A);
                break;
            case INT16:
            case UINT16:
                writer.writeByte(0x3B);
                break;
            case INT32:
                writer.writeByte(0x36);
                break;
        }
        writer.writeByte(alignment(expression.getAlignment()));
        writer.writeLEB(expression.getOffset());
        popLocation();
    }

    @Override
    public void visit(WasmStoreInt64 expression) {
        pushLocation(expression);
        expression.getIndex().acceptVisitor(this);
        expression.getValue().acceptVisitor(this);
        switch (expression.getConvertTo()) {
            case INT8:
            case UINT8:
                writer.writeByte(0x3C);
                break;
            case INT16:
            case UINT16:
                writer.writeByte(0x3D);
                break;
            case INT32:
            case UINT32:
                writer.writeByte(0x3E);
                break;
            case INT64:
                writer.writeByte(0x37);
                break;
        }
        writer.writeByte(alignment(expression.getAlignment()));
        writer.writeLEB(expression.getOffset());
        popLocation();
    }

    @Override
    public void visit(WasmStoreFloat32 expression) {
        pushLocation(expression);
        expression.getIndex().acceptVisitor(this);
        expression.getValue().acceptVisitor(this);
        writer.writeByte(0x38);
        writer.writeByte(alignment(expression.getAlignment()));
        writer.writeLEB(expression.getOffset());
        popLocation();
    }

    @Override
    public void visit(WasmStoreFloat64 expression) {
        pushLocation(expression);
        expression.getIndex().acceptVisitor(this);
        expression.getValue().acceptVisitor(this);
        writer.writeByte(0x39);
        writer.writeByte(alignment(expression.getAlignment()));
        writer.writeLEB(expression.getOffset());
        popLocation();
    }

    @Override
    public void visit(WasmMemoryGrow expression) {
        pushLocation(expression);
        expression.getAmount().acceptVisitor(this);
        writer.writeByte(0x40);
        writer.writeByte(0);
        popLocation();
    }

    @Override
    public void visit(WasmFill expression) {
        pushLocation(expression);
        expression.getIndex().acceptVisitor(this);
        expression.getValue().acceptVisitor(this);
        expression.getCount().acceptVisitor(this);
        writer.writeByte(0xFC);
        writer.writeLEB(11);
        writer.writeByte(0);
        popLocation();
    }

    @Override
    public void visit(WasmCopy expression) {
        pushLocation(expression);
        expression.getDestinationIndex().acceptVisitor(this);
        expression.getSourceIndex().acceptVisitor(this);
        expression.getCount().acceptVisitor(this);
        writer.writeByte(0xFC);
        writer.writeLEB(10);
        writer.writeByte(0);
        writer.writeByte(0);
        popLocation();
    }

    @Override
    public void visit(WasmTry expression) {
        pushLocation(expression);
        writer.writeByte(0x06);
        writeBlockType(expression.getType());
        ++depth;
        for (var part : expression.getBody()) {
            part.acceptVisitor(this);
        }
        --depth;
        for (var catchClause : expression.getCatches()) {
            writer.writeByte(0x07);
            writer.writeLEB(catchClause.getTag().getIndex());
            for (var catchVar : catchClause.getCatchVariables()) {
                if (catchVar == null) {
                    writer.writeByte(0x1A);
                } else {
                    writer.writeByte(0x21);
                    writer.writeLEB(catchVar.getIndex());
                }
            }
            for (var part : catchClause.getBody()) {
                part.acceptVisitor(this);
            }
        }
        writer.writeByte(0xB);
        popLocation();
    }

    @Override
    public void visit(WasmThrow expression) {
        pushLocation(expression);
        for (var arg : expression.getArguments()) {
            arg.acceptVisitor(this);
        }
        writer.writeByte(0x8);
        writer.writeLEB(expression.getTag().getIndex());
        popLocation();
    }

    @Override
    public void visit(WasmReferencesEqual expression) {
        pushLocation(expression);
        expression.getFirst().acceptVisitor(this);
        expression.getSecond().acceptVisitor(this);
        writer.writeByte(0xd3);
        popLocation();
    }

    @Override
    public void visit(WasmCast expression) {
        pushLocation(expression);
        expression.getValue().acceptVisitor(this);
        writer.writeByte(0xfb);
        writer.writeByte(expression.getTargetType().isNullable() ? 23 : 22);
        writer.writeHeapType(expression.getTargetType(), module);
        popLocation();
    }

    @Override
    public void visit(WasmTest expression) {
        pushLocation(expression);
        expression.getValue().acceptVisitor(this);
        writer.writeByte(0xfb);
        writer.writeByte(expression.getTestType().isNullable() ? 21 : 20);
        writer.writeHeapType(expression.getTestType(), module);
        popLocation();
    }

    @Override
    public void visit(WasmExternConversion expression) {
        pushLocation(expression);
        expression.getValue().acceptVisitor(this);
        writer.writeByte(0xfb);
        switch (expression.getType()) {
            case EXTERN_TO_ANY:
                writer.writeByte(26);
                break;
            case ANY_TO_EXTERN:
                writer.writeByte(27);
                break;
        }
        popLocation();
    }

    @Override
    public void visit(WasmStructNew expression) {
        pushLocation(expression);
        for (var initializer : expression.getInitializers()) {
            initializer.acceptVisitor(this);
        }
        writer.writeByte(0xfb);
        writer.writeByte(0);
        writer.writeLEB(module.types.indexOf(expression.getType()));
        popLocation();
    }

    @Override
    public void visit(WasmStructNewDefault expression) {
        pushLocation(expression);
        writer.writeByte(0xfb);
        writer.writeByte(1);
        writer.writeLEB(module.types.indexOf(expression.getType()));
        popLocation();
    }

    @Override
    public void visit(WasmStructGet expression) {
        pushLocation(expression);
        expression.getInstance().acceptVisitor(this);
        writer.writeByte(0xfb);
        if (expression.getSignedType() == null) {
            writer.writeByte(2);
        } else {
            switch (expression.getSignedType()) {
                case SIGNED:
                    writer.writeByte(3);
                    break;
                case UNSIGNED:
                    writer.writeByte(4);
                    break;
            }
        }
        writer.writeLEB(module.types.indexOf(expression.getType()));
        writer.writeLEB(expression.getFieldIndex());
        popLocation();
    }

    @Override
    public void visit(WasmStructSet expression) {
        pushLocation(expression);
        expression.getInstance().acceptVisitor(this);
        expression.getValue().acceptVisitor(this);
        writer.writeByte(0xfb);
        writer.writeByte(5);
        writer.writeLEB(module.types.indexOf(expression.getType()));
        writer.writeLEB(expression.getFieldIndex());
        popLocation();
    }

    @Override
    public void visit(WasmArrayNewDefault expression) {
        pushLocation(expression);
        expression.getLength().acceptVisitor(this);
        writer.writeByte(0xfb);
        writer.writeByte(7);
        writer.writeLEB(module.types.indexOf(expression.getType()));
        popLocation();
    }

    @Override
    public void visit(WasmArrayNewFixed expression) {
        pushLocation(expression);
        for (var element : expression.getElements()) {
            element.acceptVisitor(this);
        }
        writer.writeByte(0xfb);
        writer.writeByte(8);
        writer.writeLEB(module.types.indexOf(expression.getType()));
        writer.writeLEB(expression.getElements().size());
        popLocation();
    }

    @Override
    public void visit(WasmArrayGet expression) {
        pushLocation(expression);
        expression.getInstance().acceptVisitor(this);
        expression.getIndex().acceptVisitor(this);
        writer.writeByte(0xfb);
        if (expression.getSignedType() == null) {
            writer.writeByte(11);
        } else {
            switch (expression.getSignedType()) {
                case SIGNED:
                    writer.writeByte(12);
                    break;
                case UNSIGNED:
                    writer.writeByte(13);
                    break;
            }
        }
        writer.writeLEB(module.types.indexOf(expression.getType()));
        popLocation();
    }

    @Override
    public void visit(WasmArraySet expression) {
        pushLocation(expression);
        expression.getInstance().acceptVisitor(this);
        expression.getIndex().acceptVisitor(this);
        expression.getValue().acceptVisitor(this);
        writer.writeByte(0xfb);
        writer.writeByte(14);
        writer.writeLEB(module.types.indexOf(expression.getType()));
        popLocation();
    }

    @Override
    public void visit(WasmArrayLength expression) {
        pushLocation(expression);
        expression.getInstance().acceptVisitor(this);
        writer.writeByte(0xfb);
        writer.writeByte(15);
        popLocation();
    }

    @Override
    public void visit(WasmArrayCopy expression) {
        pushLocation(expression);
        expression.getTargetArray().acceptVisitor(this);
        expression.getTargetIndex().acceptVisitor(this);
        expression.getSourceArray().acceptVisitor(this);
        expression.getSourceIndex().acceptVisitor(this);
        expression.getSize().acceptVisitor(this);
        writer.writeByte(0xfb);
        writer.writeByte(17);
        writer.writeLEB(module.types.indexOf(expression.getTargetArrayType()));
        writer.writeLEB(module.types.indexOf(expression.getSourceArrayType()));
        popLocation();
    }

    @Override
    public void visit(WasmFunctionReference expression) {
        pushLocation(expression);
        writer.writeByte(0xd2);
        writer.writeLEB(module.functions.indexOf(expression.getFunction()));
        popLocation();
    }

    @Override
    public void visit(WasmInt31Reference expression) {
        pushLocation(expression);
        expression.getValue().acceptVisitor(this);
        writer.writeByte(0xfb);
        writer.writeByte(28);
        popLocation();
    }

    @Override
    public void visit(WasmInt31Get expression) {
        pushLocation(expression);
        expression.getValue().acceptVisitor(this);
        writer.writeByte(0xfb);
        writer.writeByte(expression.getSignedType() == WasmSignedType.SIGNED ? 29 : 30);
        popLocation();
    }

    private int alignment(int value) {
        return 31 - Integer.numberOfLeadingZeros(Math.max(1, value));
    }

    private void writeLabel(WasmBlock target) {
        int blockDepth = blockDepths.get(target);
        writer.writeLEB(depth - blockDepth);
    }

    private void pushLocation(WasmExpression expression) {
        var location = expression.getLocation() != null
                ? expression.getLocation()
                : locationStack.isEmpty() ? null : locationStack.get(locationStack.size() - 1);
        locationStack.add(location);
        if (location != null) {
            emitLocation(location);
        } else {
            emitDeferredLocation();
        }
    }

    private void popLocation() {
        var location = locationStack.remove(locationStack.size() - 1);
        if (location != null) {
            emitLocation(location);
        }
    }

    private void emitLocation(TextLocation location) {
        if (deferTextLocationToEmit) {
            if (location != null) {
                textLocationToEmit = location;
                deferTextLocationToEmit = false;
            } else {
                return;
            }
        }
        flushLocation();
        textLocationToEmit = location;
    }

    private void emitDeferredLocation() {
        if (textLocationToEmit != null) {
            flushLocation();
        }
        textLocationToEmit = null;
        deferTextLocationToEmit = true;
    }

    public void endLocation() {
        textLocationToEmit = null;
        deferTextLocationToEmit = false;
        flushLocation();
        if (debugLines != null) {
            debugLines.advance(writer.getPosition() + addressOffset);
            while (!methodStack.isEmpty()) {
                methodStack.remove(methodStack.size() - 1);
                debugLines.end();
            }
            debugLines.end();
        }
    }

    private void flushLocation() {
        if (writer.getPosition() != positionToEmit) {
            if (!Objects.equals(lastEmittedLocation, textLocationToEmit)) {
                doEmitLocation();
            }
            lastEmittedLocation = textLocationToEmit;
            positionToEmit = writer.getPosition();
        }
    }

    private void doEmitLocation() {
        var address = positionToEmit + addressOffset;
        if (dwarfGenerator != null) {
            if (textLocationToEmit == null || textLocationToEmit.getFileName() == null) {
                dwarfGenerator.endLineNumberSequence(address);
            } else {
                dwarfGenerator.lineNumber(address, textLocationToEmit.getFileName(), textLocationToEmit.getLine());
            }
        }
        if (debugLines != null) {
            debugLines.advance(address);
            var loc = textLocationToEmit;
            var inlining = loc != null ? loc.getInlining() : null;
            while (inlining != null) {
                currentMethodStack.add(inlining);
                inlining = inlining.getParent();
            }
            Collections.reverse(currentMethodStack);
            var commonPart = 0;
            while (commonPart < currentMethodStack.size() && commonPart < methodStack.size()
                    && currentMethodStack.get(commonPart) == methodStack.get(commonPart)) {
                ++commonPart;
            }
            while (methodStack.size() > commonPart) {
                debugLines.end();
                methodStack.remove(methodStack.size() - 1);
            }
            while (commonPart < currentMethodStack.size()) {
                var method = currentMethodStack.get(commonPart++);
                methodStack.add(method);
                debugLines.location(method.getFileName(), method.getLine());
                debugLines.start(method.getMethod());
            }
            currentMethodStack.clear();
            if (loc != null) {
                debugLines.location(loc.getFileName(), loc.getLine());
            } else {
                debugLines.emptyLocation();
            }
        }
    }
}

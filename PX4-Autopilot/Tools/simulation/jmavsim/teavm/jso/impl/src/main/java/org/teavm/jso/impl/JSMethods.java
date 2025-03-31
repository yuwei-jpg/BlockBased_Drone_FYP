/*
 *  Copyright 2018 Alexey Andreev.
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
package org.teavm.jso.impl;

import java.util.Arrays;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.core.JSArrayReader;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodReference;
import org.teavm.model.ValueType;

public final class JSMethods {
    public static final MethodReference GET = new MethodReference(JS.class, "get", JSObject.class,
            JSObject.class, JSObject.class);
    public static final MethodReference GET_PURE = new MethodReference(JS.class, "getPure", JSObject.class,
            JSObject.class, JSObject.class);
    public static final MethodReference SET = new MethodReference(JS.class, "set", JSObject.class, JSObject.class,
            JSObject.class, void.class);
    public static final MethodReference SET_PURE = new MethodReference(JS.class, "setPure", JSObject.class,
            JSObject.class, JSObject.class, void.class);
    public static final MethodReference APPLY = new MethodReference(JS.class, "apply", JSObject.class, JSObject.class,
            JSArray.class, JSObject.class);
    public static final MethodReference FUNCTION = new MethodReference(JS.class, "function", JSObject.class,
            JSObject.class, JSObject.class);
    public static final MethodReference ARRAY_DATA = new MethodReference(JS.class, "arrayData",
            Object.class, JSObject.class);
    public static final MethodReference CONCAT_ARRAY = new MethodReference(JS.class, "concatArray",
            JSObject.class, JSObject.class, JSObject.class);
    public static final MethodReference ARRAY_MAPPER = new MethodReference(JS.class, "arrayMapper",
            JS.WrapFunction.class, JS.WrapFunction.class);
    public static final MethodReference BOOLEAN_ARRAY_WRAPPER = new MethodReference(JS.class, "booleanArrayWrapper",
            JS.WrapFunction.class);
    public static final MethodReference BYTE_ARRAY_WRAPPER = new MethodReference(JS.class, "byteArrayWrapper",
            JS.WrapFunction.class);
    public static final MethodReference SHORT_ARRAY_WRAPPER = new MethodReference(JS.class, "shortArrayWrapper",
            JS.WrapFunction.class);
    public static final MethodReference CHAR_ARRAY_WRAPPER = new MethodReference(JS.class, "charArrayWrapper",
            JS.WrapFunction.class);
    public static final MethodReference INT_ARRAY_WRAPPER = new MethodReference(JS.class, "intArrayWrapper",
            JS.WrapFunction.class);
    public static final MethodReference FLOAT_ARRAY_WRAPPER = new MethodReference(JS.class, "floatArrayWrapper",
            JS.WrapFunction.class);
    public static final MethodReference DOUBLE_ARRAY_WRAPPER = new MethodReference(JS.class, "doubleArrayWrapper",
            JS.WrapFunction.class);
    public static final MethodReference STRING_ARRAY_WRAPPER = new MethodReference(JS.class, "stringArrayWrapper",
            JS.WrapFunction.class);
    public static final MethodReference ARRAY_WRAPPER = new MethodReference(JS.class, "arrayWrapper",
            JS.WrapFunction.class);
    public static final MethodReference ARRAY_UNMAPPER = new MethodReference(JS.class, "arrayUnmapper",
            Class.class, JS.UnwrapFunction.class, JS.UnwrapFunction.class);
    public static final MethodReference UNMAP_ARRAY = new MethodReference(JS.class, "unmapArray", Class.class,
            JSArrayReader.class, JS.UnwrapFunction.class, Object[].class);
    public static final MethodReference UNWRAP_BOOLEAN_ARRAY = new MethodReference(JS.class, "unwrapBooleanArray",
            JSArrayReader.class, boolean[].class);
    public static final MethodReference UNWRAP_BYTE_ARRAY = new MethodReference(JS.class, "unwrapByteArray",
            JSArrayReader.class, byte[].class);
    public static final MethodReference UNWRAP_SHORT_ARRAY = new MethodReference(JS.class, "unwrapShortArray",
            JSArrayReader.class, short[].class);
    public static final MethodReference UNWRAP_CHAR_ARRAY = new MethodReference(JS.class, "unwrapCharArray",
            JSArrayReader.class, char[].class);
    public static final MethodReference UNWRAP_INT_ARRAY = new MethodReference(JS.class, "unwrapIntArray",
            JSArrayReader.class, int[].class);
    public static final MethodReference UNWRAP_FLOAT_ARRAY = new MethodReference(JS.class, "unwrapFloatArray",
            JSArrayReader.class, float[].class);
    public static final MethodReference UNWRAP_DOUBLE_ARRAY = new MethodReference(JS.class, "unwrapDoubleArray",
            JSArrayReader.class, double[].class);
    public static final MethodReference UNWRAP_STRING_ARRAY = new MethodReference(JS.class, "unwrapStringArray",
            JSArrayReader.class, String[].class);
    public static final MethodReference UNWRAP_ARRAY = new MethodReference(JS.class, "unwrapArray", Class.class,
            JSArrayReader.class, JSObject[].class);
    public static final MethodReference BOOLEAN_ARRAY_UNWRAPPER = new MethodReference(JS.class,
            "booleanArrayUnwrapper", JS.UnwrapFunction.class);
    public static final MethodReference BYTE_ARRAY_UNWRAPPER = new MethodReference(JS.class,
            "byteArrayUnwrapper", JS.UnwrapFunction.class);
    public static final MethodReference SHORT_ARRAY_UNWRAPPER = new MethodReference(JS.class,
            "shortArrayUnwrapper", JS.UnwrapFunction.class);
    public static final MethodReference CHAR_ARRAY_UNWRAPPER = new MethodReference(JS.class,
            "charArrayUnwrapper", JS.UnwrapFunction.class);
    public static final MethodReference INT_ARRAY_UNWRAPPER = new MethodReference(JS.class,
            "intArrayUnwrapper", JS.UnwrapFunction.class);
    public static final MethodReference FLOAT_ARRAY_UNWRAPPER = new MethodReference(JS.class,
            "floatArrayUnwrapper", JS.UnwrapFunction.class);
    public static final MethodReference DOUBLE_ARRAY_UNWRAPPER = new MethodReference(JS.class,
            "doubleArrayUnwrapper", JS.UnwrapFunction.class);
    public static final MethodReference STRING_ARRAY_UNWRAPPER = new MethodReference(JS.class,
            "stringArrayUnwrapper", JS.UnwrapFunction.class);
    public static final MethodReference ARRAY_UNWRAPPER = new MethodReference(JS.class,
            "arrayUnwrapper", Class.class, JS.UnwrapFunction.class);

    public static final MethodReference DATA_TO_BYTE_ARRAY = new MethodReference(JS.class,
            "dataToByteArray", JSObject.class, byte[].class);
    public static final MethodReference DATA_TO_SHORT_ARRAY = new MethodReference(JS.class,
            "dataToShortArray", JSObject.class, short[].class);
    public static final MethodReference DATA_TO_CHAR_ARRAY = new MethodReference(JS.class,
            "dataToCharArray", JSObject.class, char[].class);
    public static final MethodReference DATA_TO_INT_ARRAY = new MethodReference(JS.class,
            "dataToIntArray", JSObject.class, int[].class);
    public static final MethodReference DATA_TO_FLOAT_ARRAY = new MethodReference(JS.class,
            "dataToFloatArray", JSObject.class, float[].class);
    public static final MethodReference DATA_TO_DOUBLE_ARRAY = new MethodReference(JS.class,
            "dataToDoubleArray", JSObject.class, double[].class);
    public static final MethodReference DATA_TO_ARRAY = new MethodReference(JS.class,
            "dataToArray", JSObject.class, JSObject[].class);

    public static final MethodReference WRAP_STRING = new MethodReference(JS.class, "wrap",
            String.class, JSObject.class);

    public static final MethodReference FUNCTION_AS_OBJECT = new MethodReference(JS.class, "functionAsObject",
            JSObject.class, JSObject.class, JSObject.class);

    public static final MethodReference GLOBAL = new MethodReference(JS.class, "global", String.class, JSObject.class);
    public static final MethodReference IMPORT_MODULE = new MethodReference(JS.class, "importModule",
            String.class, JSObject.class);

    public static final MethodReference INSTANCE_OF = new MethodReference(JS.class, "instanceOf", JSObject.class,
            JSObject.class, boolean.class);
    public static final MethodReference INSTANCE_OF_OR_NULL = new MethodReference(JS.class, "instanceOfOrNull",
            JSObject.class, JSObject.class, boolean.class);
    public static final MethodReference IS_PRIMITIVE = new MethodReference(JS.class, "isPrimitive", JSObject.class,
            JSObject.class, boolean.class);
    public static final MethodReference THROW_CCE_IF_FALSE = new MethodReference(JS.class, "throwCCEIfFalse",
            boolean.class, JSObject.class, JSObject.class);
    public static final MethodReference ARGUMENTS_BEGINNING_AT = new MethodReference(JS.class,
            "argumentsBeginningAt", int.class, JSObject.class);

    public static final ValueType JS_OBJECT = ValueType.object(JSObject.class.getName());
    public static final ValueType OBJECT = ValueType.object("java.lang.Object");
    public static final ValueType JS_ARRAY = ValueType.object(JSArray.class.getName());
    private static final MethodReference[] INVOKE_METHODS = new MethodReference[13];
    private static final MethodReference[] CONSTRUCT_METHODS = new MethodReference[13];
    private static final MethodReference[] ARRAY_OF_METHODS = new MethodReference[13];

    public static final MethodReference WRAP = new MethodReference(JSWrapper.class, "wrap", JSObject.class,
            Object.class);
    public static final MethodReference MAYBE_WRAP = new MethodReference(JSWrapper.class, "maybeWrap", Object.class,
            Object.class);
    public static final MethodReference UNWRAP = new MethodReference(JSWrapper.class, "unwrap", Object.class,
            JSObject.class);
    public static final MethodReference MAYBE_UNWRAP = new MethodReference(JSWrapper.class, "maybeUnwrap",
            Object.class, JSObject.class);
    public static final MethodReference IS_JS = new MethodReference(JSWrapper.class, "isJs",
            Object.class, boolean.class);
    public static final MethodReference WRAPPER_IS_PRIMITIVE = new MethodReference(JSWrapper.class, "isPrimitive",
            Object.class, JSObject.class, boolean.class);
    public static final MethodReference WRAPPER_INSTANCE_OF = new MethodReference(JSWrapper.class, "instanceOf",
            Object.class, JSObject.class, boolean.class);

    public static final String JS_MARSHALLABLE = JSMarshallable.class.getName();
    public static final MethodDescriptor MARSHALL_TO_JS = new MethodDescriptor("marshallToJs", JS_OBJECT);

    static {
        for (int i = 0; i < INVOKE_METHODS.length; ++i) {
            var signature = new ValueType[i + 3];
            Arrays.fill(signature, JS_OBJECT);
            INVOKE_METHODS[i] = new MethodReference(JS.class.getName(), "invoke", signature);

            var constructSignature = new ValueType[i + 2];
            Arrays.fill(constructSignature, JS_OBJECT);
            CONSTRUCT_METHODS[i] = new MethodReference(JS.class.getName(), "construct", constructSignature);

            var arrayOfSignature = new ValueType[i + 1];
            Arrays.fill(arrayOfSignature, JS_OBJECT);
            ARRAY_OF_METHODS[i] = new MethodReference(JS.class.getName(), "arrayOf", arrayOfSignature);
        }
    }

    private JSMethods() {
    }

    public static MethodReference invoke(int parameterCount) {
        return INVOKE_METHODS[parameterCount];
    }

    public static MethodReference construct(int parameterCount) {
        return CONSTRUCT_METHODS[parameterCount];
    }

    public static MethodReference arrayOf(int parameterCount) {
        return ARRAY_OF_METHODS[parameterCount];
    }
}

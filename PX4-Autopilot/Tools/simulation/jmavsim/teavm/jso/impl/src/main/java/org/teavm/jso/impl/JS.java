/*
 *  Copyright 2014 Alexey Andreev.
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

import java.lang.reflect.Array;
import org.teavm.backend.javascript.spi.GeneratedBy;
import org.teavm.backend.javascript.spi.InjectedBy;
import org.teavm.dependency.PluggableDependency;
import org.teavm.interop.Import;
import org.teavm.interop.NoSideEffects;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.core.JSArrayReader;
import org.teavm.jso.core.JSBoolean;
import org.teavm.jso.core.JSNumber;
import org.teavm.jso.core.JSString;

public final class JS {
    private JS() {
    }

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    public static native JSObject arrayData(Object array);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "concatArray", module = "teavmJso")
    public static native JSObject concatArray(JSObject a, JSObject b);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    public static native byte[] dataToByteArray(JSObject obj);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    public static native char[] dataToCharArray(JSObject obj);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    public static native short[] dataToShortArray(JSObject obj);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    public static native int[] dataToIntArray(JSObject obj);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    public static native float[] dataToFloatArray(JSObject obj);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    public static native double[] dataToDoubleArray(JSObject obj);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    public static native JSObject[] dataToArray(JSObject obj);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "wrapByte", module = "teavmJso")
    public static native JSObject wrap(byte value);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "wrapShort", module = "teavmJso")
    public static native JSObject wrap(short value);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "wrapInt", module = "teavmJso")
    public static native JSObject wrap(int value);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "wrapChar", module = "teavmJso")
    public static native JSObject wrap(char value);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "wrapFloat", module = "teavmJso")
    public static native JSObject wrap(float value);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "wrapDouble", module = "teavmJso")
    public static native JSObject wrap(double value);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "wrapBoolean", module = "teavmJso")
    public static native JSObject wrap(boolean value);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    public static native JSObject wrap(String value);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "unwrapByte", module = "teavmJso")
    public static native byte unwrapByte(JSObject value);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "unwrapChar", module = "teavmJso")
    public static native char unwrapCharacter(JSObject value);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "unwrapShort", module = "teavmJso")
    public static native short unwrapShort(JSObject value);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "unwrapInt", module = "teavmJso")
    public static native int unwrapInt(JSObject value);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "unwrapFloat", module = "teavmJso")
    public static native float unwrapFloat(JSObject value);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "unwrapDouble", module = "teavmJso")
    public static native double unwrapDouble(JSObject value);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "unwrapBoolean", module = "teavmJso")
    public static native boolean unwrapBoolean(JSObject value);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    public static native String unwrapString(JSObject value);

    public static <T extends JSObject> JSArray<T> wrap(T[] array) {
        if (array == null) {
            return null;
        }
        var result = new JSArray<T>(array.length);
        for (int i = 0; i < array.length; ++i) {
            result.set(i, array[i]);
        }
        return result;
    }

    public static <T> JSArray<T> wrap(T[] array) {
        if (array == null) {
            return null;
        }
        var result = new JSArray<T>(array.length);
        for (int i = 0; i < array.length; ++i) {
            result.set(i, array[i]);
        }
        return result;
    }

    public static <T extends JSObject> WrapFunction<T[], JSArray<T>> arrayWrapper() {
        return JS::wrap;
    }

    public static <T extends JSObject, S> JSArray<T> map(S[] array, WrapFunction<S, T> f) {
        if (array == null) {
            return null;
        }
        var result = new JSArray<T>(array.length);
        for (int i = 0; i < array.length; ++i) {
            result.set(i, f.apply(array[i]));
        }
        return result;
    }

    public interface WrapFunction<S, T extends JSObject> {
        T apply(S obj);
    }

    public interface UnwrapFunction<S extends JSObject, T> {
        T apply(S obj);
    }

    public static <T extends JSObject, S> WrapFunction<S[], JSArray<T>> arrayMapper(WrapFunction<S, T> f) {
        return array -> map(array, f);
    }

    public static JSArray<JSBoolean> wrap(boolean[] array) {
        if (array == null) {
            return null;
        }
        var result = new JSArray<JSBoolean>(array.length);
        for (int i = 0; i < array.length; ++i) {
            result.set(i, JSBoolean.valueOf(array[i]));
        }
        return result;
    }

    public static WrapFunction<boolean[], JSArray<JSBoolean>> booleanArrayWrapper() {
        return JS::wrap;
    }

    public static JSArray<JSNumber> wrap(byte[] array) {
        if (array == null) {
            return null;
        }
        var result = new JSArray<JSNumber>(array.length);
        for (int i = 0; i < array.length; ++i) {
            result.set(i, JSNumber.valueOf(array[i]));
        }
        return result;
    }

    public static WrapFunction<byte[], JSArray<JSNumber>> byteArrayWrapper() {
        return JS::wrap;
    }

    public static JSArray<JSNumber> wrap(short[] array) {
        if (array == null) {
            return null;
        }
        var result = new JSArray<JSNumber>(array.length);
        for (int i = 0; i < array.length; ++i) {
            result.set(i, JSNumber.valueOf(array[i]));
        }
        return result;
    }

    public static WrapFunction<short[], JSArray<JSNumber>> shortArrayWrapper() {
        return JS::wrap;
    }

    public static JSArray<JSNumber> wrap(char[] array) {
        if (array == null) {
            return null;
        }
        var result = new JSArray<JSNumber>(array.length);
        for (int i = 0; i < array.length; ++i) {
            result.set(i, JSNumber.valueOf(array[i]));
        }
        return result;
    }

    public static WrapFunction<char[], JSArray<JSNumber>> charArrayWrapper() {
        return JS::wrap;
    }

    public static JSArray<JSNumber> wrap(int[] array) {
        if (array == null) {
            return null;
        }
        var result = new JSArray<JSNumber>(array.length);
        for (int i = 0; i < array.length; ++i) {
            result.set(i, JSNumber.valueOf(array[i]));
        }
        return result;
    }

    public static WrapFunction<int[], JSArray<JSNumber>> intArrayWrapper() {
        return JS::wrap;
    }

    @NoSideEffects
    public static JSArray<JSString> wrap(String[] array) {
        if (array == null) {
            return null;
        }
        var result = new JSArray<JSString>(array.length);
        for (int i = 0; i < array.length; ++i) {
            result.set(i, JSString.valueOf(array[i]));
        }
        return result;
    }

    public static WrapFunction<String[], JSArray<JSString>> stringArrayWrapper() {
        return JS::wrap;
    }

    public static JSArray<JSNumber> wrap(float[] array) {
        if (array == null) {
            return null;
        }
        var result = new JSArray<JSNumber>(array.length);
        for (int i = 0; i < array.length; ++i) {
            result.set(i, JSNumber.valueOf(array[i]));
        }
        return result;
    }

    public static WrapFunction<float[], JSArray<JSNumber>> floatArrayWrapper() {
        return JS::wrap;
    }

    public static JSArray<JSNumber> wrap(double[] array) {
        if (array == null) {
            return null;
        }
        var result = new JSArray<JSNumber>(array.length);
        for (int i = 0; i < array.length; ++i) {
            result.set(i, JSNumber.valueOf(array[i]));
        }
        return result;
    }

    public static WrapFunction<double[], JSArray<JSNumber>> doubleArrayWrapper() {
        return JS::wrap;
    }

    public static <T extends JSObject> T[] unwrapArray(Class<T> type, JSArrayReader<T> array) {
        if (array == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(type, array.getLength());
        for (int i = 0; i < result.length; ++i) {
            result[i] = array.get(i);
        }
        return result;
    }

    public static <T extends JSObject> UnwrapFunction<JSArrayReader<T>, T[]> arrayUnwrapper(Class<T> type) {
        return array -> unwrapArray(type, array);
    }

    public static <S extends JSObject, T> T[] unmapArray(Class<T> type, JSArrayReader<S> array,
            UnwrapFunction<S, T> f) {
        if (array == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Array.newInstance(type, array.getLength());
        for (int i = 0; i < result.length; ++i) {
            result[i] = f.apply(array.get(i));
        }
        return result;
    }

    public static <T, S extends JSObject> UnwrapFunction<JSArray<S>, T[]> arrayUnmapper(Class<T> type,
            UnwrapFunction<S, T> f) {
        return array -> unmapArray(type, array, f);
    }

    public static boolean[] unwrapBooleanArray(JSArrayReader<JSBoolean> array) {
        if (array == null) {
            return null;
        }
        boolean[] result = new boolean[array.getLength()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = array.get(i).booleanValue();
        }
        return result;
    }

    public static UnwrapFunction<JSArrayReader<JSBoolean>, boolean[]> booleanArrayUnwrapper() {
        return JS::unwrapBooleanArray;
    }

    public static byte[] unwrapByteArray(JSArrayReader<JSNumber> array) {
        if (array == null) {
            return null;
        }
        byte[] result = new byte[array.getLength()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = array.get(i).byteValue();
        }
        return result;
    }

    public static UnwrapFunction<JSArrayReader<JSNumber>, byte[]> byteArrayUnwrapper() {
        return JS::unwrapByteArray;
    }

    public static short[] unwrapShortArray(JSArrayReader<JSNumber> array) {
        if (array == null) {
            return null;
        }
        short[] result = new short[array.getLength()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = array.get(i).shortValue();
        }
        return result;
    }

    public static UnwrapFunction<JSArrayReader<JSNumber>, short[]> shortArrayUnwrapper() {
        return JS::unwrapShortArray;
    }

    public static int[] unwrapIntArray(JSArrayReader<JSNumber> array) {
        if (array == null) {
            return null;
        }
        int[] result = new int[array.getLength()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = array.get(i).intValue();
        }
        return result;
    }

    public static UnwrapFunction<JSArrayReader<JSNumber>, int[]> intArrayUnwrapper() {
        return JS::unwrapIntArray;
    }

    public static char[] unwrapCharArray(JSArrayReader<JSNumber> array) {
        if (array == null) {
            return null;
        }
        char[] result = new char[array.getLength()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = array.get(i).charValue();
        }
        return result;
    }

    public static UnwrapFunction<JSArrayReader<JSNumber>, char[]> charArrayUnwrapper() {
        return JS::unwrapCharArray;
    }

    public static float[] unwrapFloatArray(JSArrayReader<JSNumber> array) {
        if (array == null) {
            return null;
        }
        float[] result = new float[array.getLength()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = array.get(i).floatValue();
        }
        return result;
    }

    public static UnwrapFunction<JSArrayReader<JSNumber>, float[]> floatArrayUnwrapper() {
        return JS::unwrapFloatArray;
    }

    public static double[] unwrapDoubleArray(JSArrayReader<JSNumber> array) {
        if (array == null) {
            return null;
        }
        double[] result = new double[array.getLength()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = array.get(i).doubleValue();
        }
        return result;
    }

    public static UnwrapFunction<JSArrayReader<JSNumber>, double[]> doubleArrayUnwrapper() {
        return JS::unwrapDoubleArray;
    }

    public static String[] unwrapStringArray(JSArrayReader<JSString> array) {
        if (array == null) {
            return null;
        }
        String[] result = new String[array.getLength()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = array.get(i).stringValue();
        }
        return result;
    }

    public static UnwrapFunction<JSArrayReader<JSString>, String[]> stringArrayUnwrapper() {
        return JS::unwrapStringArray;
    }

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "callMethod0", module = "teavmJso")
    public static native JSObject invoke(JSObject instance, JSObject method);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "callMethod1", module = "teavmJso")
    public static native JSObject invoke(JSObject instance, JSObject method, JSObject a);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "callMethod2", module = "teavmJso")
    public static native JSObject invoke(JSObject instance, JSObject method, JSObject a, JSObject b);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "callMethod3", module = "teavmJso")
    public static native JSObject invoke(JSObject instance, JSObject method, JSObject a, JSObject b, JSObject c);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "callMethod4", module = "teavmJso")
    public static native JSObject invoke(JSObject instance, JSObject method, JSObject a, JSObject b, JSObject c,
            JSObject d);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "callMethod5", module = "teavmJso")
    public static native JSObject invoke(JSObject instance, JSObject method, JSObject a, JSObject b, JSObject c,
            JSObject d, JSObject e);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "callMethod6", module = "teavmJso")
    public static native JSObject invoke(JSObject instance, JSObject method, JSObject a, JSObject b, JSObject c,
            JSObject d, JSObject e, JSObject f);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "callMethod7", module = "teavmJso")
    public static native JSObject invoke(JSObject instance, JSObject method, JSObject a, JSObject b, JSObject c,
            JSObject d, JSObject e, JSObject f, JSObject g);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "callMethod8", module = "teavmJso")
    public static native JSObject invoke(JSObject instance, JSObject method, JSObject a, JSObject b, JSObject c,
            JSObject d, JSObject e, JSObject f, JSObject g, JSObject h);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "callMethod9", module = "teavmJso")
    public static native JSObject invoke(JSObject instance, JSObject method, JSObject a, JSObject b, JSObject c,
            JSObject d, JSObject e, JSObject f, JSObject g, JSObject h, JSObject i);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "callMethod10", module = "teavmJso")
    public static native JSObject invoke(JSObject instance, JSObject method, JSObject a, JSObject b, JSObject c,
            JSObject d, JSObject e, JSObject f, JSObject g, JSObject h, JSObject i, JSObject j);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "callMethod11", module = "teavmJso")
    public static native JSObject invoke(JSObject instance, JSObject method, JSObject a, JSObject b, JSObject c,
            JSObject d, JSObject e, JSObject f, JSObject g, JSObject h, JSObject i, JSObject j, JSObject k);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "callMethod12", module = "teavmJso")
    public static native JSObject invoke(JSObject instance, JSObject method, JSObject a, JSObject b, JSObject c,
            JSObject d, JSObject e, JSObject f, JSObject g, JSObject h, JSObject i, JSObject j, JSObject k,
            JSObject l);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "callMethod13", module = "teavmJso")
    public static native JSObject invoke(JSObject instance, JSObject method, JSObject a, JSObject b, JSObject c,
            JSObject d, JSObject e, JSObject f, JSObject g, JSObject h, JSObject i, JSObject j, JSObject k,
            JSObject l, JSObject m);

    @InjectedBy(JSNativeInjector.class)
    @Import(name = "apply", module = "teavmJso")
    public static native JSObject apply(JSObject instance, JSObject method, JSArray<JSObject> v);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "arrayOf1", module = "teavmJso")
    public static native JSObject arrayOf(JSObject a);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "arrayOf2", module = "teavmJso")
    public static native JSObject arrayOf(JSObject a, JSObject b);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "arrayOf3", module = "teavmJso")
    public static native JSObject arrayOf(JSObject a, JSObject b, JSObject c);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "arrayOf4", module = "teavmJso")
    public static native JSObject arrayOf(JSObject a, JSObject b, JSObject c, JSObject d);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "arrayOf5", module = "teavmJso")
    public static native JSObject arrayOf(JSObject a, JSObject b, JSObject c, JSObject d, JSObject e);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "arrayOf6", module = "teavmJso")
    public static native JSObject arrayOf(JSObject a, JSObject b, JSObject c, JSObject d, JSObject e, JSObject f);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "arrayOf7", module = "teavmJso")
    public static native JSObject arrayOf(JSObject a, JSObject b, JSObject c, JSObject d, JSObject e, JSObject f,
            JSObject g);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "arrayOf8", module = "teavmJso")
    public static native JSObject arrayOf(JSObject a, JSObject b, JSObject c, JSObject d, JSObject e, JSObject f,
            JSObject g, JSObject h);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "arrayOf9", module = "teavmJso")
    public static native JSObject arrayOf(JSObject a, JSObject b, JSObject c, JSObject d, JSObject e, JSObject f,
            JSObject g, JSObject h, JSObject i);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "arrayOf10", module = "teavmJso")
    public static native JSObject arrayOf(JSObject a, JSObject b, JSObject c, JSObject d, JSObject e, JSObject f,
            JSObject g, JSObject h, JSObject i, JSObject j);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "arrayOf11", module = "teavmJso")
    public static native JSObject arrayOf(JSObject a, JSObject b, JSObject c, JSObject d, JSObject e, JSObject f,
            JSObject g, JSObject h, JSObject i, JSObject j, JSObject k);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "arrayOf12", module = "teavmJso")
    public static native JSObject arrayOf(JSObject a, JSObject b, JSObject c, JSObject d, JSObject e, JSObject f,
            JSObject g, JSObject h, JSObject i, JSObject j, JSObject k, JSObject l);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "arrayOf13", module = "teavmJso")
    public static native JSObject arrayOf(JSObject a, JSObject b, JSObject c, JSObject d, JSObject e, JSObject f,
            JSObject g, JSObject h, JSObject i, JSObject j, JSObject k, JSObject l, JSObject m);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "construct0", module = "teavmJso")
    public static native JSObject construct(JSObject cls);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "construct1", module = "teavmJso")
    public static native JSObject construct(JSObject cls, JSObject a);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "construct2", module = "teavmJso")
    public static native JSObject construct(JSObject cls, JSObject a, JSObject b);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "construct3", module = "teavmJso")
    public static native JSObject construct(JSObject cls, JSObject a, JSObject b, JSObject c);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "construct4", module = "teavmJso")
    public static native JSObject construct(JSObject cls, JSObject a, JSObject b, JSObject c, JSObject d);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "construct5", module = "teavmJso")
    public static native JSObject construct(JSObject cls, JSObject a, JSObject b, JSObject c, JSObject d, JSObject e);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "construct6", module = "teavmJso")
    public static native JSObject construct(JSObject cls, JSObject a, JSObject b, JSObject c, JSObject d, JSObject e,
            JSObject f);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "construct7", module = "teavmJso")
    public static native JSObject construct(JSObject cls, JSObject a, JSObject b, JSObject c, JSObject d, JSObject e,
            JSObject f, JSObject g);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "construct8", module = "teavmJso")
    public static native JSObject construct(JSObject cls, JSObject a, JSObject b, JSObject c, JSObject d, JSObject e,
            JSObject f, JSObject g, JSObject h);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "construct9", module = "teavmJso")
    public static native JSObject construct(JSObject cls, JSObject a, JSObject b, JSObject c, JSObject d, JSObject e,
            JSObject f, JSObject g, JSObject h, JSObject i);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "construct10", module = "teavmJso")
    public static native JSObject construct(JSObject cls, JSObject a, JSObject b, JSObject c, JSObject d, JSObject e,
            JSObject f, JSObject g, JSObject h, JSObject i, JSObject j);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "construct11", module = "teavmJso")
    public static native JSObject construct(JSObject cls, JSObject a, JSObject b, JSObject c, JSObject d, JSObject e,
            JSObject f, JSObject g, JSObject h, JSObject i, JSObject j, JSObject k);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "construct12", module = "teavmJso")
    public static native JSObject construct(JSObject cls, JSObject a, JSObject b, JSObject c, JSObject d, JSObject e,
            JSObject f, JSObject g, JSObject h, JSObject i, JSObject j, JSObject k, JSObject l);

    @InjectedBy(JSNativeInjector.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "construct13", module = "teavmJso")
    public static native JSObject construct(JSObject cls, JSObject a, JSObject b, JSObject c, JSObject d, JSObject e,
            JSObject f, JSObject g, JSObject h, JSObject i, JSObject j, JSObject k, JSObject l, JSObject m);


    @InjectedBy(JSNativeInjector.class)
    @JSBody(params = { "instance", "index" }, script = "return instance[index];")
    public static native JSObject get(JSObject instance, JSObject index);

    @InjectedBy(JSNativeInjector.class)
    @JSBody(params = { "instance", "index" }, script = "return instance[index];")
    @NoSideEffects
    public static native JSObject getPure(JSObject instance, JSObject index);

    @InjectedBy(JSNativeInjector.class)
    @JSBody(params = { "instance", "index", "obj" }, script = "instance[index] = obj;")
    @Import(name = "setProperty", module = "teavmJso")
    public static native void set(JSObject instance, JSObject index, JSObject obj);

    @InjectedBy(JSNativeInjector.class)
    @JSBody(params = { "instance", "index", "obj" }, script = "instance[index] = obj;")
    @NoSideEffects
    @Import(name = "setPropertyPure", module = "teavmJso")
    public static native void setPure(JSObject instance, JSObject index, JSObject obj);

    @GeneratedBy(JSNativeGenerator.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "asFunction", module = "teavmJso")
    public static native JSObject function(JSObject instance, JSObject property);

    @GeneratedBy(JSNativeGenerator.class)
    @PluggableDependency(JSNativeInjector.class)
    @Import(name = "functionAsObject", module = "teavmJso")
    public static native JSObject functionAsObject(JSObject instance, JSObject property);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    public static native JSObject global(String name);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    public static native JSObject importModule(String name);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "instanceOf", module = "teavmJso")
    public static native boolean instanceOf(JSObject obj, JSObject cls);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "instanceOfOrNull", module = "teavmJso")
    public static native boolean instanceOfOrNull(JSObject obj, JSObject cls);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "isPrimitive", module = "teavmJso")
    public static native boolean isPrimitive(JSObject obj, JSObject primitive);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    public static native JSObject throwCCEIfFalse(boolean value, JSObject o);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    public static native JSObject argumentsBeginningAt(int index);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    @Import(name = "sameRef", module = "teavmJso")
    public static native boolean sameRef(JSObject a, JSObject b);

    @InjectedBy(JSNativeInjector.class)
    @NoSideEffects
    public static native boolean isNull(JSObject o);

    @NoSideEffects
    public static native Object jsArrayItem(Object array, int index);
}

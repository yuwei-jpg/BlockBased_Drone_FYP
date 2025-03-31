/*
 *  Copyright 2023 Alexey Andreev.
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

import org.teavm.classlib.PlatformDetector;
import org.teavm.interop.Import;
import org.teavm.interop.NoSideEffects;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSClass;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSBoolean;
import org.teavm.jso.core.JSFinalizationRegistry;
import org.teavm.jso.core.JSMap;
import org.teavm.jso.core.JSNumber;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.core.JSString;
import org.teavm.jso.core.JSUndefined;
import org.teavm.jso.core.JSWeakMap;
import org.teavm.jso.core.JSWeakRef;

public final class JSWrapper {
    private static class Helper {
        private static final JSWeakMap<JSObject, JSTransparentInt> hashCodes = new JSWeakMap<>();
        private static final JSWeakMap<JSObject, JSWeakRef<JSObject>> wrappers = JSWeakRef.isSupported()
                ? new JSWeakMap<>() : null;
        private static final JSMap<JSString, JSWeakRef<JSObject>> stringWrappers = JSWeakRef.isSupported()
                ? new JSMap<>() : null;
        private static final JSMap<JSNumber, JSWeakRef<JSObject>> numberWrappers = JSWeakRef.isSupported()
                ? new JSMap<>() : null;
        private static JSWeakRef<JSObject> undefinedWrapper;
        private static JSFinalizationRegistry stringFinalizationRegistry;
        private static JSFinalizationRegistry numberFinalizationRegistry;
        private static int hashCodeGen;

        static {
            stringFinalizationRegistry = stringWrappers != null
                    ? new JSFinalizationRegistry(token -> stringWrappers.delete((JSString) token))
                    : null;
            numberFinalizationRegistry = numberWrappers != null
                    ? new JSFinalizationRegistry(token -> numberWrappers.delete((JSNumber) token))
                    : null;
        }
    }

    public final JSObject js;

    private JSWrapper(JSObject js) {
        this.js = js;
    }

    public static Object wrap(JSObject o) {
        if (o == null) {
            return null;
        }
        var type = JSObjects.typeOf(o);
        var isObject = type.equals("object") || type.equals("function");
        var wrappers = Helper.wrappers;
        if (wrappers != null) {
            if (isObject) {
                var existingRef = get(wrappers, o);
                var existing = !isUndefined(existingRef) ? deref(existingRef) : JSUndefined.instance();
                if (isUndefined(existing)) {
                    var wrapper = new JSWrapper(o);
                    set(wrappers, o, createWeakRef(wrapperToJs(wrapper)));
                    return wrapper;
                } else {
                    return jsToWrapper(existing);
                }
            } else if (type.equals("string")) {
                var jsString = (JSString) o;
                var stringWrappers = Helper.stringWrappers;
                var stringFinalizationRegistry = Helper.stringFinalizationRegistry;
                var existingRef = get(stringWrappers, jsString);
                var existing = !isUndefined(existingRef) ? deref(existingRef) : JSUndefined.instance();
                if (isUndefined(existing)) {
                    var wrapper = new JSWrapper(o);
                    var wrapperAsJs = wrapperToJs(wrapper);
                    set(stringWrappers, jsString, createWeakRef(wrapperAsJs));
                    register(stringFinalizationRegistry, wrapperAsJs, jsString);
                    return wrapper;
                } else {
                    return jsToWrapper(existing);
                }
            } else if (type.equals("number")) {
                var jsNumber = (JSNumber) o;
                var numberWrappers = Helper.numberWrappers;
                var numberFinalizationRegistry = Helper.numberFinalizationRegistry;
                var existingRef = get(numberWrappers, jsNumber);
                var existing = !isUndefined(existingRef) ? deref(existingRef) : JSUndefined.instance();
                if (isUndefined(existing)) {
                    var wrapper = new JSWrapper(o);
                    var wrapperAsJs = wrapperToJs(wrapper);
                    set(numberWrappers, jsNumber, createWeakRef(wrapperAsJs));
                    register(numberFinalizationRegistry, wrapperAsJs, jsNumber);
                    return wrapper;
                } else {
                    return jsToWrapper(existing);
                }
            } else if (type.equals("undefined")) {
                var existingRef = Helper.undefinedWrapper;
                var existing = existingRef != null ? deref(existingRef) : JSUndefined.instance();
                if (isUndefined(existing)) {
                    var wrapper = new JSWrapper(o);
                    var wrapperAsJs = wrapperToJs(wrapper);
                    Helper.undefinedWrapper = createWeakRef(wrapperAsJs);
                    return wrapper;
                } else {
                    return jsToWrapper(existing);
                }
            }
        }
        return new JSWrapper(o);
    }

    @JSBody(params = "target", script = "return new WeakRef(target);")
    @NoSideEffects
    private static native JSWeakRef<JSObject> createWeakRef(JSObject target);

    @JSBody(params = "target", script = "return target.deref();")
    @NoSideEffects
    private static native JSObject deref(JSWeakRef<JSObject> target);

    @JSBody(params = { "registry", "target", "token" }, script = "return registry.register(target, token);")
    @NoSideEffects
    private static native void register(JSFinalizationRegistry registry, JSObject target, JSObject token);

    @JSBody(params = { "map", "key" }, script = "return map.get(key);")
    @NoSideEffects
    private static native JSWeakRef<JSObject> get(JSMap<? extends JSObject, JSWeakRef<JSObject>> map, JSObject key);

    @JSBody(params = { "map", "key", "value" }, script = "map.set(key, value);")
    @NoSideEffects
    private static native void set(JSMap<? extends JSObject, JSWeakRef<JSObject>> map, JSObject key, JSObject value);

    @JSBody(params = { "map", "key" }, script = "return map.get(key);")
    @NoSideEffects
    private static native JSWeakRef<JSObject> get(JSWeakMap<? extends JSObject, JSWeakRef<JSObject>> map,
            JSObject key);

    @JSBody(params = { "map", "key", "value" }, script = "map.set(key, value);")
    @NoSideEffects
    private static native void set(JSWeakMap<? extends JSObject, JSWeakRef<JSObject>> map, JSObject key,
            JSObject value);

    @NoSideEffects
    public static Object maybeWrap(Object o) {
        return o == null || isJava(o) ? o : wrap(directJavaToJs(o));
    }

    @NoSideEffects
    public static native JSObject directJavaToJs(Object obj);

    @NoSideEffects
    public static native JSObject marshallJavaToJs(Object obj);

    @NoSideEffects
    @Import(name = "unwrapJavaObject", module = "teavmJso")
    public static native Object unmarshallJavaFromJs(JSObject obj);

    @NoSideEffects
    public static native JSObject dependencyJavaToJs(Object obj);

    @NoSideEffects
    public static native Object dependencyJsToJava(JSObject obj);

    @NoSideEffects
    private static native JSObject wrapperToJs(JSWrapper obj);

    @NoSideEffects
    private static native JSWrapper jsToWrapper(JSObject obj);

    @NoSideEffects
    public static native boolean isJava(Object obj);

    @NoSideEffects
    public static native boolean isJava(JSObject obj);

    public static JSObject unwrap(Object o) {
        if (o == null) {
            return null;
        }
        return (!(o instanceof JSWrapper)) ? marshallJavaToJs(o) : ((JSWrapper) o).js;
    }

    public static JSObject maybeUnwrap(Object o) {
        if (o == null) {
            return null;
        }
        return isJava(o) ? unwrap(o) : marshallJavaToJs(o);
    }

    public static JSObject javaToJs(Object o) {
        if (o == null) {
            return null;
        }
        return isJava(o) && o instanceof JSWrapper ? unwrap(o) : dependencyJavaToJs(o);
    }

    public static Object jsToJava(JSObject o) {
        if (o == null) {
            return null;
        }
        return !isJava(o) ? wrap(o) : dependencyJsToJava(o);
    }

    public static boolean isJs(Object o) {
        if (o == null) {
            return false;
        }
        return !isJava(o) || o instanceof JSWrapper;
    }

    public static boolean isPrimitive(Object o, JSObject primitive) {
        if (PlatformDetector.isWebAssemblyGC()) {
            JSObject js;
            if (o instanceof JSWrapper) {
                js = ((JSWrapper) o).js;
            } else if (o instanceof JSMarshallable) {
                js = ((JSMarshallable) o).marshallToJs();
            } else {
                return false;
            }
            return JS.isPrimitive(js, primitive);
        }
        return isJs(o) && JS.isPrimitive(maybeUnwrap(o), primitive);
    }

    public static boolean instanceOf(Object o, JSObject type) {
        if (PlatformDetector.isWebAssemblyGC()) {
            JSObject js;
            if (o instanceof JSWrapper) {
                js = ((JSWrapper) o).js;
            } else if (o instanceof JSMarshallable) {
                js = ((JSMarshallable) o).marshallToJs();
            } else {
                return false;
            }
            return JS.instanceOf(js, type);
        }
        return isJs(o) && JS.instanceOf(maybeUnwrap(o), type);
    }

    @Override
    public int hashCode() {
        if (PlatformDetector.isWebAssemblyGC()) {
            return wasmGcHashCode(js);
        }
        var type = JSObjects.typeOf(js);
        if (type.equals("object") || type.equals("symbol") || type.equals("function")) {
            var code = Helper.hashCodes.get(js);
            if (isUndefined(code)) {
                code = JSTransparentInt.valueOf(++Helper.hashCodeGen);
                Helper.hashCodes.set(js, code);
            }
            return code.intValue();
        } else if (type.equals("number")) {
            return ((JSNumber) js).intValue();
        } else if (type.equals("bigint")) {
            return bigintTruncate(js);
        } else if (type.equals("string")) {
            var s = (JSString) js;
            var hashCode = 0;
            for (var i = 0; i < s.getLength(); ++i) {
                hashCode = 31 * hashCode + s.charCodeAt(i);
            }
            return hashCode;
        } else if (type.equals("boolean")) {
            return js == JSBoolean.valueOf(true) ? 1 : 0;
        } else {
            return 0;
        }
    }

    @Import(name = "hashCode", module = "teavmJso")
    private static native int wasmGcHashCode(JSObject o);

    @JSBody(params = "bigint", script = "return BigInt.asIntN(bigint, 32);")
    @NoSideEffects
    private static native int bigintTruncate(JSObject bigint);

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof JSWrapper)) {
            return false;
        }
        return js == ((JSWrapper) obj).js;
    }

    @Override
    public String toString() {
        return isUndefined(js) ? "undefined" : JSObjects.toString(js);
    }

    @JSClass(transparent = true)
    static abstract class JSTransparentInt implements JSObject {
        @JSBody(script = "return this;")
        native int intValue();

        @JSBody(params = "value", script = "return value;")
        static native JSTransparentInt valueOf(int value);
    }

    @JSBody(params = "obj", script = "return typeof obj == 'undefined'")
    private static native boolean isUndefined(JSObject obj);
}

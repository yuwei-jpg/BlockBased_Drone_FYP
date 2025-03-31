/*
 *  Copyright 2019 Alexey Andreev.
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
package org.teavm.model.analysis;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.teavm.dependency.DependencyInfo;
import org.teavm.dependency.MethodDependencyInfo;
import org.teavm.model.MethodReference;
import org.teavm.model.ValueType;

public class ClassMetadataRequirements {
    private static final MethodReference GET_NAME_METHOD = new MethodReference(Class.class, "getName", String.class);
    private static final MethodReference GET_SIMPLE_NAME_METHOD = new MethodReference(Class.class,
            "getSimpleName", String.class);
    private static final MethodReference GET_SUPERCLASS_METHOD = new MethodReference(Class.class, "getSuperclass",
            Class.class);
    private static final MethodReference IS_ASSIGNABLE_METHOD = new MethodReference(Class.class, "isAssignableFrom",
            Class.class, boolean.class);
    private static final MethodReference GET_DECLARING_CLASS_METHOD = new MethodReference(Class.class,
            "getDeclaringClass", Class.class);
    private static final MethodReference GET_ENCLOSING_CLASS_METHOD = new MethodReference(Class.class,
            "getEnclosingClass", Class.class);
    private static final MethodReference NEW_ARRAY = new MethodReference(Array.class,
            "newInstance", Class.class, int.class, Object.class);
    private static final MethodReference ARRAY_GET = new MethodReference(Array.class,
            "get", Object.class, int.class, Object.class);
    private static final MethodReference ARRAY_LENGTH = new MethodReference(Array.class,
            "getLength", Object.class, int.class);
    private static final MethodReference ARRAY_COPY = new MethodReference(System.class,
            "arraycopy", Object.class, int.class, Object.class, int.class, int.class, void.class);
    private static final ClassInfo EMPTY_INFO = new ClassInfo();
    private Map<ValueType, ClassInfo> requirements = new HashMap<>();
    private boolean hasArrayGet;
    private boolean hasArrayLength;
    private boolean hasArrayCopy;
    private boolean hasEnumConstants;
    private boolean hasSuperclass;
    private boolean hasIsAssignable;
    private boolean hasNewInstance;
    private boolean hasEnclosingClass;
    private boolean hasDeclaringClass;
    private boolean hasSimpleName;
    private boolean hasName;

    public ClassMetadataRequirements(DependencyInfo dependencyInfo) {
        MethodDependencyInfo getNameMethod = dependencyInfo.getMethod(GET_NAME_METHOD);
        if (getNameMethod != null) {
            hasName = true;
            addClassesRequiringName(requirements, getNameMethod.getVariable(0).getClassValueNode().getTypes());
        }

        MethodDependencyInfo getSimpleNameMethod = dependencyInfo.getMethod(GET_SIMPLE_NAME_METHOD);
        if (getSimpleNameMethod != null) {
            hasSimpleName = true;
            String[] classNames = getSimpleNameMethod.getVariable(0).getClassValueNode().getTypes();
            addClassesRequiringName(requirements, classNames);
            for (String className : classNames) {
                ClassInfo classInfo = requirements.computeIfAbsent(decodeType(className), k -> new ClassInfo());
                classInfo.simpleName = true;
                classInfo.enclosingClass = true;
            }
        }

        var getSuperclassMethod = dependencyInfo.getMethod(GET_SUPERCLASS_METHOD);
        if (getSuperclassMethod != null) {
            hasSuperclass = true;
            var classNames = getSuperclassMethod.getVariable(0).getClassValueNode().getTypes();
            for (var className : classNames) {
                requirements.computeIfAbsent(decodeType(className), k -> new ClassInfo()).superclass = true;
            }
        }

        var isAssignableMethod = dependencyInfo.getMethod(IS_ASSIGNABLE_METHOD);
        if (isAssignableMethod != null) {
            hasIsAssignable = true;
            var classNames = isAssignableMethod.getVariable(0).getClassValueNode().getTypes();
            for (var className : classNames) {
                requirements.computeIfAbsent(decodeType(className), k -> new ClassInfo()).isAssignable = true;
            }
        }

        MethodDependencyInfo getDeclaringClassMethod = dependencyInfo.getMethod(GET_DECLARING_CLASS_METHOD);
        if (getDeclaringClassMethod != null) {
            hasDeclaringClass = true;
            String[] classNames = getDeclaringClassMethod.getVariable(0).getClassValueNode().getTypes();
            for (String className : classNames) {
                requirements.computeIfAbsent(decodeType(className), k -> new ClassInfo()).declaringClass = true;
            }
        }

        MethodDependencyInfo getEnclosingClassMethod = dependencyInfo.getMethod(GET_ENCLOSING_CLASS_METHOD);
        if (getEnclosingClassMethod != null) {
            hasEnclosingClass = true;
            String[] classNames = getEnclosingClassMethod.getVariable(0).getClassValueNode().getTypes();
            for (String className : classNames) {
                requirements.computeIfAbsent(decodeType(className), k -> new ClassInfo()).enclosingClass = true;
            }
        }

        var newArrayMethod = dependencyInfo.getMethod(NEW_ARRAY);
        if (newArrayMethod != null) {
            hasNewInstance = true;
            var classNames = newArrayMethod.getVariable(1).getClassValueNode().getTypes();
            for (var className : classNames) {
                requirements.computeIfAbsent(decodeType(className), k -> new ClassInfo()).newArray = true;
            }
        }

        var arrayGet = dependencyInfo.getMethod(ARRAY_GET);
        if (arrayGet != null) {
            hasArrayGet = arrayGet.isUsed();
            var classNames = arrayGet.getVariable(1).getTypes();
            for (var className : classNames) {
                requirements.computeIfAbsent(decodeType(className), k -> new ClassInfo()).arrayGet = true;
            }
        }

        var arrayLength = dependencyInfo.getMethod(ARRAY_LENGTH);
        if (arrayLength != null) {
            hasArrayLength = arrayLength.isUsed();
            var classNames = arrayLength.getVariable(1).getTypes();
            for (var className : classNames) {
                requirements.computeIfAbsent(decodeType(className), k -> new ClassInfo()).arrayLength = true;
            }
        }

        var arrayCopy = dependencyInfo.getMethod(ARRAY_COPY);
        if (arrayCopy != null) {
            hasArrayCopy = arrayCopy.isUsed();
            var classNames = arrayCopy.getVariable(1).getTypes();
            for (var className : classNames) {
                requirements.computeIfAbsent(decodeType(className), k -> new ClassInfo()).arrayCopy = true;
            }
        }

        var clone = dependencyInfo.getMethod(new MethodReference(Object.class, "cloneObject", Object.class));
        if (clone != null) {
            var classNames = clone.getVariable(0).getTypes();
            for (var className : classNames) {
                requirements.computeIfAbsent(decodeType(className), k -> new ClassInfo()).cloneMethod = true;
            }
        }

        var enumConstants = Arrays.asList(
            dependencyInfo.getMethod(new MethodReference("org.teavm.platform.Platform", "getEnumConstants",
                    ValueType.object("org.teavm.platform.PlatformClass"), ValueType.parse(Enum[].class))),
            dependencyInfo.getMethod(new MethodReference("org.teavm.classlib.impl.reflection.ClassSupport",
                    "getEnumConstants", ValueType.parse(Class.class), ValueType.parse(Enum[].class)))
        );
        for (var enumConstantsDep : enumConstants) {
            if (enumConstantsDep != null) {
                hasEnumConstants = true;
                var classNames = enumConstantsDep.getVariable(1).getClassValueNode().getTypes();
                for (var className : classNames) {
                    requirements.computeIfAbsent(decodeType(className), k -> new ClassInfo()).enumConstants = true;
                }
            }
        }
    }

    public Info getInfo(String className) {
        return getInfo(ValueType.object(className));
    }

    public Info getInfo(ValueType className) {
        ClassInfo result = requirements.get(className);
        if (result == null) {
            result = EMPTY_INFO;
        }
        return result;
    }

    public boolean hasArrayGet() {
        return hasArrayGet;
    }

    public boolean hasArrayLength() {
        return hasArrayLength;
    }

    public boolean hasArrayCopy() {
        return hasArrayCopy;
    }

    public boolean hasEnumConstants() {
        return hasEnumConstants;
    }

    public boolean hasSuperclass() {
        return hasSuperclass;
    }

    public boolean hasIsAssignable() {
        return hasIsAssignable;
    }

    public boolean hasArrayNewInstance() {
        return hasNewInstance;
    }

    public boolean hasEnclosingClass() {
        return hasEnclosingClass;
    }

    public boolean hasDeclaringClass() {
        return hasDeclaringClass;
    }

    public boolean hasSimpleName() {
        return hasSimpleName;
    }

    public boolean hasName() {
        return hasName;
    }

    private void addClassesRequiringName(Map<ValueType, ClassInfo> target, String[] source) {
        for (String typeName : source) {
            target.computeIfAbsent(decodeType(typeName), k -> new ClassInfo()).name = true;
        }
    }

    private ValueType decodeType(String typeName) {
        if (typeName.startsWith("[")) {
            return ValueType.parseIfPossible(typeName);
        } else if (typeName.startsWith("~")) {
            return ValueType.parseIfPossible(typeName.substring(1));
        } else {
            return ValueType.object(typeName);
        }
    }

    static class ClassInfo implements Info {
        boolean name;
        boolean simpleName;
        boolean declaringClass;
        boolean enclosingClass;
        boolean superclass;
        boolean isAssignable;
        boolean newArray;
        boolean arrayLength;
        boolean arrayGet;
        boolean arrayCopy;
        boolean cloneMethod;
        boolean enumConstants;

        @Override
        public boolean name() {
            return name;
        }

        @Override
        public boolean simpleName() {
            return simpleName;
        }

        @Override
        public boolean declaringClass() {
            return declaringClass;
        }

        @Override
        public boolean enclosingClass() {
            return enclosingClass;
        }

        @Override
        public boolean superclass() {
            return superclass;
        }

        @Override
        public boolean isAssignable() {
            return isAssignable;
        }

        @Override
        public boolean newArray() {
            return newArray;
        }

        @Override
        public boolean arrayLength() {
            return arrayLength;
        }

        @Override
        public boolean arrayCopy() {
            return arrayCopy;
        }

        @Override
        public boolean arrayGet() {
            return arrayGet;
        }

        @Override
        public boolean cloneMethod() {
            return cloneMethod;
        }

        @Override
        public boolean enumConstants() {
            return enumConstants;
        }
    }

    public interface Info {
        boolean name();

        boolean simpleName();

        boolean declaringClass();

        boolean enclosingClass();

        boolean superclass();

        boolean isAssignable();

        boolean newArray();

        boolean arrayLength();

        boolean arrayGet();

        boolean arrayCopy();

        boolean cloneMethod();

        boolean enumConstants();
    }
}

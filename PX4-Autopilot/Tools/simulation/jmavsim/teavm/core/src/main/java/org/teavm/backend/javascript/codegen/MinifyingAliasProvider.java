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
package org.teavm.backend.javascript.codegen;

import java.util.HashSet;
import java.util.Set;
import org.teavm.backend.javascript.rendering.RenderingUtil;
import org.teavm.model.FieldReference;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodReference;

public class MinifyingAliasProvider implements AliasProvider {
    private static final String startLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String startVirtualLetters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final int maxTopLevelNames;
    private int lastSuffix;
    private int lastInstanceSuffix;
    private int topLevelNames;
    private boolean additionalScopeStarted;
    private final Set<String> usedAliases = new HashSet<>();

    public MinifyingAliasProvider(int maxTopLevelNames) {
        this.maxTopLevelNames = maxTopLevelNames;
    }

    @Override
    public String getFieldAlias(FieldReference field) {
        return createInstanceName();
    }

    @Override
    public ScopedName getStaticFieldAlias(FieldReference field) {
        return createTopLevelName();
    }

    @Override
    public ScopedName getStaticMethodAlias(MethodReference method) {
        return createTopLevelName();
    }

    @Override
    public String getMethodAlias(MethodDescriptor method) {
        return createInstanceName();
    }

    @Override
    public ScopedName getClassAlias(String className) {
        return createTopLevelName();
    }

    @Override
    public ScopedName getFunctionAlias(String className) {
        return createTopLevelName();
    }

    @Override
    public ScopedName getClassInitAlias(String className) {
        return createTopLevelName();
    }

    @Override
    public String getAdditionalScopeName() {
        return createTopLevelName().name;
    }

    @Override
    public void reserveName(String name) {
        usedAliases.add(name);
    }

    private ScopedName createTopLevelName() {
        if (!additionalScopeStarted && topLevelNames >= maxTopLevelNames) {
            additionalScopeStarted = true;
            lastSuffix = 0;
        }
        String result;
        do {
            result = RenderingUtil.indexToId(lastSuffix++, startLetters);
        } while ((!additionalScopeStarted && usedAliases.contains(result)) || RenderingUtil.KEYWORDS.contains(result));
        ++topLevelNames;
        return new ScopedName(result, additionalScopeStarted);
    }

    private String createInstanceName() {
        String result;
        do {
            result = RenderingUtil.indexToId(lastInstanceSuffix++, startVirtualLetters);
        } while (RenderingUtil.KEYWORDS.contains(result));
        return result;
    }
}

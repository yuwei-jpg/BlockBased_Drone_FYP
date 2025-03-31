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
package org.teavm.backend.wasm.model;

public abstract class WasmCompositeType extends WasmEntity {
    private String name;
    private WasmType.CompositeReference reference;
    private WasmType.CompositeReference nonNullReference;
    int recursiveTypeCount = -1;

    WasmCompositeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public WasmType.CompositeReference getReference() {
        if (reference == null) {
            reference = new WasmType.CompositeReference(this, true);
        }
        return reference;
    }

    public WasmType.CompositeReference getNonNullReference() {
        if (nonNullReference == null) {
            nonNullReference = new WasmType.CompositeReference(this, false);
        }
        return nonNullReference;
    }

    public int getRecursiveTypeCount() {
        return recursiveTypeCount;
    }

    public abstract void acceptVisitor(WasmCompositeTypeVisitor visitor);
}

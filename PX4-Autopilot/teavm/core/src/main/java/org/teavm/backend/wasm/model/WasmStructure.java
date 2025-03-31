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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WasmStructure extends WasmCompositeType {
    private Consumer<List<WasmField>> fieldsSupplier;
    private List<WasmField> fieldsStorage = new ArrayList<>();
    private WasmStructure supertype;
    private boolean indexesValid = true;
    private boolean nominal;

    public WasmStructure(String name) {
        super(name);
    }

    public WasmStructure(String name, Consumer<List<WasmField>> fieldsSupplier) {
        super(name);
        this.fieldsSupplier = fieldsSupplier;
    }

    public List<WasmField> getFields() {
        return fields;
    }

    public WasmStructure getSupertype() {
        return supertype;
    }

    public void setSupertype(WasmStructure supertype) {
        this.supertype = supertype;
    }

    public boolean isSupertypeOf(WasmStructure subtype) {
        while (subtype != null) {
            if (subtype == this) {
                return true;
            }
            subtype = subtype.getSupertype();
        }
        return false;
    }

    public boolean isNominal() {
        return nominal;
    }

    public void setNominal(boolean nominal) {
        this.nominal = nominal;
    }

    void ensureIndexes() {
        if (!indexesValid) {
            indexesValid = true;
            for (var i = 0; i < fieldsStorage.size(); ++i) {
                fieldsStorage.get(i).index = i;
            }
        }
    }

    public void init() {
        if (fieldsSupplier != null) {
            var supplier = fieldsSupplier;
            fieldsSupplier = null;
            supplier.accept(fieldsStorage);
            for (var field : fieldsStorage) {
                field.structure = this;
            }
            indexesValid = false;
        }
    }

    @Override
    public void acceptVisitor(WasmCompositeTypeVisitor visitor) {
        visitor.visit(this);
    }

    private List<WasmField> fields = new AbstractList<>() {
        @Override
        public WasmField get(int index) {
            init();
            return fieldsStorage.get(index);
        }

        @Override
        public int size() {
            init();
            return fieldsStorage.size();
        }

        @Override
        public void add(int index, WasmField element) {
            init();
            if (element.structure != null) {
                throw new IllegalArgumentException("This field already belongs to structure");
            }
            element.structure = WasmStructure.this;
            indexesValid = false;
            fieldsStorage.add(index, element);
        }

        @Override
        public WasmField remove(int index) {
            init();
            var result = fieldsStorage.remove(index);
            indexesValid = false;
            result.structure = null;
            return result;
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            init();
            var sublist = fieldsStorage.subList(fromIndex, toIndex);
            for (var field : sublist) {
                field.structure = null;
            }
            indexesValid = false;
            sublist.clear();
        }

        @Override
        public void clear() {
            fieldsSupplier = null;
            for (var field : fieldsStorage) {
                field.structure = null;
            }
            indexesValid = true;
            fieldsStorage.clear();
        }

        @Override
        public WasmField set(int index, WasmField element) {
            init();
            if (element.structure != null) {
                throw new IllegalArgumentException("This field already belongs to structure");
            }
            var former = fieldsStorage.set(index, element);
            former.structure = null;
            if (indexesValid) {
                element.index = former.index;
            }
            element.structure = WasmStructure.this;
            return former;
        }
    };
}

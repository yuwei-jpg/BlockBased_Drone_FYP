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
package org.teavm.dependency;

import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

class TypeSet {
    private static final int SMALL_TYPES_THRESHOLD = 3;
    static final DependencyType[] EMPTY_TYPES = new DependencyType[0];
    private DependencyAnalyzer dependencyAnalyzer;
    DependencyNode origin;
    private int[] smallTypes;
    private BitSet types;
    private int typesCount;

    private Object domain;
    ObjectArrayList<Transition> transitions;
    ArrayList<ConsumerWithNode> consumers;

    TypeSet(DependencyAnalyzer dependencyAnalyzer, DependencyNode origin) {
        this.dependencyAnalyzer = dependencyAnalyzer;
        this.origin = origin;
        domain = origin;
    }

    void addType(DependencyType type) {
        if (types == null) {
            if (smallTypes == null) {
                smallTypes = new int[] { type.index };
                return;
            }
        }
        if (smallTypes != null) {
            if (smallTypes.length == SMALL_TYPES_THRESHOLD) {
                types = new BitSet(dependencyAnalyzer.types.size() * 2);
                for (int existingType : smallTypes) {
                    types.set(existingType);
                }
                typesCount = smallTypes.length;
                smallTypes = null;
            } else {
                smallTypes = Arrays.copyOf(smallTypes, smallTypes.length + 1);
                smallTypes[smallTypes.length - 1] = type.index;
                return;
            }
        }
        types.set(type.index);
        typesCount++;
    }

    DependencyType[] getTypes() {
        if (this.types != null) {
            DependencyType[] types = new DependencyType[this.types.cardinality()];
            int j = 0;
            for (int index = this.types.nextSetBit(0); index >= 0; index = this.types.nextSetBit(index + 1)) {
                DependencyType type = dependencyAnalyzer.types.get(index);
                types[j++] = type;
            }
            return types;
        } else if (this.smallTypes != null) {
            DependencyType[] types = new DependencyType[smallTypes.length];
            for (int i = 0; i < types.length; ++i) {
                DependencyType type = dependencyAnalyzer.types.get(smallTypes[i]);
                types[i] = type;
            }
            return types;
        } else {
            return EMPTY_TYPES;
        }
    }

    boolean hasMoreTypesThan(int limit, Predicate<DependencyType> filter) {
        if (this.types != null) {
            if (filter == null) {
                return this.types.cardinality() > limit;
            }
            for (int index = this.types.nextSetBit(0); index >= 0; index = this.types.nextSetBit(index + 1)) {
                DependencyType type = dependencyAnalyzer.types.get(index);
                if (filter.test(type)) {
                    if (--limit < 0) {
                        return true;
                    }
                }
            }
            return false;
        } else if (this.smallTypes != null) {
            if (this.smallTypes.length <= limit) {
                return false;
            }
            if (filter == null) {
                return true;
            }
            for (int i = 0; i < smallTypes.length; ++i) {
                DependencyType type = dependencyAnalyzer.types.get(smallTypes[i]);
                if (filter.test(type)) {
                    if (--limit < 0) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return false;
        }
    }

    DependencyType[] getTypesForNode(DependencyNode sourceNode, DependencyNode targetNode,
            DependencyTypeFilter filter) {
        int j = 0;
        DependencyType[] types;
        if (this.types != null) {
            int[] filteredTypes = null;
            if (typesCount > 15) {
                filteredTypes = filter != null ? filter.tryExtract(this.types) : null;
                if (filteredTypes == null) {
                    filteredTypes = sourceNode.getFilter().tryExtract(this.types);
                }
                if (filteredTypes == null) {
                    filteredTypes = targetNode.getFilter().tryExtract(this.types);
                }
            }
            if (filteredTypes != null) {
                types = new DependencyType[filteredTypes.length];
                for (int index : filteredTypes) {
                    DependencyType type = dependencyAnalyzer.types.get(index);
                    if (sourceNode.filter(type) && !targetNode.hasType(type) && targetNode.filter(type)
                            && (filter == null || filter.match(type))) {
                        types[j++] = type;
                    }
                }
            } else {
                types = new DependencyType[typesCount];
                for (int index = this.types.nextSetBit(0); index >= 0; index = this.types.nextSetBit(index + 1)) {
                    DependencyType type = dependencyAnalyzer.types.get(index);
                    if (sourceNode.filter(type) && !targetNode.hasType(type) && targetNode.filter(type)
                            && (filter == null || filter.match(type))) {
                        types[j++] = type;
                    }
                }
            }
        } else if (this.smallTypes != null) {
            types = new DependencyType[smallTypes.length];
            for (int i = 0; i < types.length; ++i) {
                DependencyType type = dependencyAnalyzer.types.get(smallTypes[i]);
                if (sourceNode.filter(type) && !targetNode.hasType(type) && targetNode.filter(type)
                        && (filter == null || filter.match(type))) {
                    types[j++] = type;
                }
            }
        } else {
            return EMPTY_TYPES;
        }

        if (j == 0) {
            return EMPTY_TYPES;
        }
        if (j < types.length) {
            types = Arrays.copyOf(types, j);
        }
        return types;
    }

    boolean hasType(DependencyType type) {
        if (smallTypes != null) {
            for (int i = 0; i < smallTypes.length; ++i) {
                if (smallTypes[i] == type.index) {
                    return true;
                }
            }
            return false;
        }
        return types != null && types.get(type.index);
    }

    boolean hasAnyType() {
        return types != null || smallTypes != null;
    }

    TypeSet copy(DependencyNode origin) {
        TypeSet result = new TypeSet(dependencyAnalyzer, origin);
        result.types = types != null ? (BitSet) types.clone() : null;
        result.smallTypes = smallTypes;
        result.typesCount = typesCount;
        return result;
    }

    void invalidate() {
        transitions = null;
        consumers = null;
    }

    Set<? extends DependencyNode> domain() {
        if (domain == null) {
            return Set.of();
        } else if (domain instanceof DependencyNode) {
            return Set.of((DependencyNode) domain);
        } else {
            //noinspection unchecked
            return (Set<? extends DependencyNode>) domain;
        }
    }

    void addDomain(DependencyNode node) {
        if (domain == null) {
            domain = node;
        } else if (domain instanceof DependencyNode) {
            if (domain == node) {
                return;
            }
            domain = new HashSet<>(Set.of((DependencyNode) domain));
        }
        @SuppressWarnings("unchecked")
        var set = (Set<DependencyNode>) domain;
        set.add(node);
    }

    void addDomain(Collection<? extends DependencyNode> nodes) {
        if (nodes.isEmpty()) {
            return;
        }
        if (domain == null) {
            if (nodes.size() == 1) {
                domain = nodes.iterator().next();
            }
            return;
        } else if (domain instanceof DependencyNode) {
            if (nodes.contains(domain) && nodes.size() == 1) {
                return;
            }
            domain = new HashSet<>(Set.of((DependencyNode) domain));
        }
        @SuppressWarnings("unchecked")
        var set = (Set<DependencyNode>) domain;
        set.addAll(nodes);
    }

    void removeDomain(Collection<? extends DependencyNode> nodes) {
        if (domain == null || nodes.isEmpty()) {
            return;
        }
        if (domain instanceof DependencyNode) {
            if (nodes.contains(domain)) {
                domain = null;
            }
            return;
        }

        @SuppressWarnings("unchecked")
        var set = (Set<DependencyNode>) domain;
        set.removeAll(nodes);
        if (set.isEmpty()) {
            domain = null;
        } else if (set.size() == 1) {
            domain = set.iterator().next();
        }
    }

    ObjectArrayList<Transition> getTransitions() {
        if (transitions == null) {
            transitions = new ObjectArrayList<>();
            for (DependencyNode node : domain()) {
                if (node.transitions != null) {
                    for (ObjectCursor<Transition> cursor : node.transitionList) {
                        Transition transition = cursor.value;
                        if (transition.filter != null || transition.destination.typeSet != this) {
                            transitions.add(transition);
                        }
                    }
                }
            }
        }
        return transitions;
    }

    List<ConsumerWithNode> getConsumers() {
        if (domain == null) {
            return List.of();
        } else if (domain instanceof DependencyNode) {
            var node = (DependencyNode) domain;
            if (node.followers == null) {
                return List.of();
            } else {
                return List.of(new ConsumerWithNode(node.followers.toArray(new DependencyConsumer[0]), node));
            }
        } else {
            if (consumers == null) {
                consumers = new ArrayList<>();
                for (var node : domain()) {
                    if (node.followers != null) {
                        consumers.add(new ConsumerWithNode(node.followers.toArray(new DependencyConsumer[0]), node));
                    }
                }
                consumers.trimToSize();
            }
            return consumers;
        }
    }

    int typeCount() {
        return smallTypes != null ? smallTypes.length : types != null ? typesCount : 0;
    }

    void cleanup() {
        origin = null;
        domain = null;
        transitions = null;
        consumers = null;
    }
}

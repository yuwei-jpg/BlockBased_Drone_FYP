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
package org.teavm.backend.wasm.generate.gc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.teavm.backend.wasm.BaseWasmFunctionRepository;
import org.teavm.backend.wasm.WasmFunctionTypes;
import org.teavm.backend.wasm.gc.vtable.WasmGCVirtualTableProvider;
import org.teavm.backend.wasm.generate.gc.classes.WasmGCClassGenerator;
import org.teavm.backend.wasm.generate.gc.classes.WasmGCClassInfoProvider;
import org.teavm.backend.wasm.generate.gc.classes.WasmGCCustomTypeMapperFactory;
import org.teavm.backend.wasm.generate.gc.classes.WasmGCSupertypeFunctionProvider;
import org.teavm.backend.wasm.generate.gc.classes.WasmGCTypeMapper;
import org.teavm.backend.wasm.generate.gc.methods.WasmGCCustomGeneratorProvider;
import org.teavm.backend.wasm.generate.gc.methods.WasmGCIntrinsicProvider;
import org.teavm.backend.wasm.generate.gc.methods.WasmGCMethodGenerator;
import org.teavm.backend.wasm.generate.gc.strings.WasmGCStringProvider;
import org.teavm.backend.wasm.model.WasmFunction;
import org.teavm.backend.wasm.model.WasmModule;
import org.teavm.backend.wasm.model.WasmTag;
import org.teavm.dependency.DependencyInfo;
import org.teavm.diagnostics.Diagnostics;
import org.teavm.model.ClassHierarchy;
import org.teavm.model.ClassReaderSource;
import org.teavm.model.ListableClassHolderSource;
import org.teavm.model.MethodReference;
import org.teavm.model.analysis.ClassInitializerInfo;
import org.teavm.model.analysis.ClassMetadataRequirements;
import org.teavm.model.classes.TagRegistry;
import org.teavm.model.classes.VirtualTableBuilder;

public class WasmGCDeclarationsGenerator {
    public final ClassHierarchy hierarchy;
    public final WasmModule module;
    public final WasmFunctionTypes functionTypes;
    private final WasmGCClassGenerator classGenerator;
    private final WasmGCMethodGenerator methodGenerator;
    private List<WasmGCInitializerContributor> initializerContributors = new ArrayList<>();

    public WasmGCDeclarationsGenerator(
            WasmModule module,
            ListableClassHolderSource classes,
            ClassReaderSource originalClasses,
            ClassLoader classLoader,
            ClassInitializerInfo classInitializerInfo,
            DependencyInfo dependencyInfo,
            Diagnostics diagnostics,
            WasmGCCustomGeneratorProvider customGenerators,
            WasmGCIntrinsicProvider intrinsics,
            List<WasmGCCustomTypeMapperFactory> customTypeMapperFactories,
            Predicate<MethodReference> isVirtual,
            boolean strict,
            String entryPoint
    ) {
        this.module = module;
        hierarchy = new ClassHierarchy(classes);
        var virtualTables = createVirtualTableProvider(classes, isVirtual);
        functionTypes = new WasmFunctionTypes(module);
        var names = new WasmGCNameProvider();
        methodGenerator = new WasmGCMethodGenerator(
                module,
                hierarchy,
                classes,
                classLoader,
                virtualTables,
                classInitializerInfo,
                functionTypes,
                names,
                diagnostics,
                customGenerators,
                intrinsics,
                strict,
                entryPoint,
                initializerContributors::add
        );
        var tags = new TagRegistry(classes, hierarchy);
        var metadataRequirements = new ClassMetadataRequirements(dependencyInfo);
        classGenerator = new WasmGCClassGenerator(
                module,
                classes,
                originalClasses,
                hierarchy,
                dependencyInfo,
                functionTypes,
                tags,
                metadataRequirements,
                virtualTables,
                methodGenerator,
                names,
                classInitializerInfo,
                customTypeMapperFactories
        );
        methodGenerator.setClassInfoProvider(classGenerator);
        methodGenerator.setStrings(classGenerator.strings);
        methodGenerator.setSupertypeFunctions(classGenerator.getSupertypeProvider());
        methodGenerator.setStandardClasses(classGenerator.standardClasses);
        methodGenerator.setTypeMapper(classGenerator.typeMapper);
    }

    public void setFriendlyToDebugger(boolean friendlyToDebugger) {
        methodGenerator.setFriendlyToDebugger(friendlyToDebugger);
    }

    public WasmGCClassInfoProvider classInfoProvider() {
        return classGenerator;
    }

    public WasmGCTypeMapper typeMapper() {
        return classGenerator.typeMapper;
    }

    public BaseWasmFunctionRepository functions() {
        return methodGenerator;
    }

    public WasmGCSupertypeFunctionProvider supertypeFunctions() {
        return classGenerator.getSupertypeProvider();
    }

    public void generate() {
        boolean somethingGenerated;
        do {
            somethingGenerated = false;
            somethingGenerated |= methodGenerator.process();
            somethingGenerated |= classGenerator.process();
        } while (somethingGenerated);
    }

    public void contributeToInitializer(WasmFunction function) {
        var contributors = new ArrayList<>(List.of(classGenerator, classGenerator.strings));
        contributors.addAll(initializerContributors);
        for (var contributor : contributors) {
            contributor.contributeToInitializerDefinitions(function);
        }
        for (var contributor : contributors) {
            contributor.contributeToInitializer(function);
        }
    }

    private static WasmGCVirtualTableProvider createVirtualTableProvider(ListableClassHolderSource classes,
            Predicate<MethodReference> isVirtual) {
        return new WasmGCVirtualTableProvider(classes, VirtualTableBuilder.getMethodsUsedOnCallSites(classes, true),
                isVirtual);
    }

    public WasmFunction dummyInitializer() {
        return methodGenerator.getDummyInitializer();
    }

    public WasmGCNameProvider names() {
        return methodGenerator.names;
    }

    public WasmGCStringProvider strings() {
        return classGenerator.strings;
    }

    public WasmTag exceptionTag() {
        return methodGenerator.getGenerationContext().getExceptionTag();
    }

    public void addToInitializer(Consumer<WasmFunction> contributor) {
        methodGenerator.getGenerationContext().addToInitializer(contributor);
    }
}

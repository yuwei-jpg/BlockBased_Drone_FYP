/*
 *  Copyright 2015 Alexey Andreev.
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
package org.teavm.platform.plugin;

import java.lang.annotation.Annotation;
import org.teavm.backend.javascript.codegen.SourceWriter;
import org.teavm.backend.javascript.spi.Generator;
import org.teavm.backend.javascript.spi.GeneratorContext;
import org.teavm.backend.javascript.spi.Injector;
import org.teavm.backend.javascript.spi.InjectorContext;
import org.teavm.backend.javascript.templating.JavaScriptTemplate;
import org.teavm.backend.javascript.templating.JavaScriptTemplateFactory;
import org.teavm.dependency.DependencyAgent;
import org.teavm.dependency.DependencyPlugin;
import org.teavm.dependency.MethodDependency;
import org.teavm.dependency.MethodDependencyInfo;
import org.teavm.model.ClassReader;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodReader;
import org.teavm.model.MethodReference;
import org.teavm.model.ValueType;
import org.teavm.platform.Platform;
import org.teavm.platform.PlatformClass;
import org.teavm.platform.PlatformRunnable;

public class PlatformGenerator implements Generator, Injector, DependencyPlugin {
    private JavaScriptTemplate template;

    @Override
    public void methodReached(DependencyAgent agent, MethodDependency method) {
        switch (method.getReference().getName()) {
            case "asJavaClass":
                method.getResult().propagate(agent.getType("java.lang.Class"));
                return;
            case "clone":
                method.getVariable(1).connect(method.getResult());
                break;
            case "startThread":
            case "schedule": {
                MethodDependency launchMethod = agent.linkMethod(new MethodReference(Platform.class,
                        "launchThread", PlatformRunnable.class, void.class));
                method.getVariable(1).connect(launchMethod.getVariable(1));
                launchMethod.use();
                break;
            }
            case "getCurrentThread":
                method.getResult().propagate(agent.getType("java.lang.Thread"));
                break;
            case "getEnumConstants":
                method.getResult().propagate(agent.getType("[Ljava/lang/Enum;"));
                break;
            case "getName":
            case "getSimpleName":
                method.getResult().propagate(agent.getType("java.lang.String"));
                break;
            case "getEnclosingClass":
            case "getDeclaringClass":
                method.getResult().propagate(agent.getType("java.lang.Class"));
                break;
        }
    }

    @Override
    public void generate(InjectorContext context, MethodReference methodRef) {
        switch (methodRef.getName()) {
            case "asJavaClass":
            case "classFromResource":
            case "objectFromResource":
            case "marshall":
            case "getPlatformObject":
                context.writeExpr(context.getArgument(0));
                break;
            case "initClass":
                context.writeExpr(context.getArgument(0));
                context.getWriter().append(".$clinit()");
                break;
        }
    }

    @Override
    public void generate(GeneratorContext context, SourceWriter writer, MethodReference methodRef) {
        switch (methodRef.getName()) {
            case "prepareNewInstance":
                generatePrepareNewInstance(context, writer);
                break;
            case "lookupClass":
                generateLookup(context, writer);
                break;
            case "getEnumConstants":
                generateEnumConstants(context, writer);
                break;
            case "getAnnotations":
                generateAnnotations(context, writer);
                break;
            default:
                generateWithTemplate(context, writer, methodRef);
                break;
        }
    }

    private void generateWithTemplate(GeneratorContext context, SourceWriter writer, MethodReference methodRef) {
        if (template == null) {
            template = new JavaScriptTemplateFactory(context.getClassLoader(), context.getClassSource())
                    .createFromResource("org/teavm/platform/plugin/Platform.js");
        }
        template.builder(methodRef.getName()).withContext(context).build().write(writer, 0);
    }

    private void generatePrepareNewInstance(GeneratorContext context, SourceWriter writer) {
        MethodDependencyInfo newInstanceMethod = context.getDependency().getMethod(
                new MethodReference(Platform.class, "newInstanceImpl", PlatformClass.class, Object.class));
        writer.append("let c").ws().append("=").ws().append("'$$constructor$$';").softNewLine();
        if (newInstanceMethod != null) {
            for (String clsName : newInstanceMethod.getResult().getTypes()) {
                ClassReader cls = context.getClassSource().get(clsName);
                if (cls == null) {
                    continue;
                }
                MethodReader method = cls.getMethod(new MethodDescriptor("<init>", void.class));
                if (method != null) {
                    writer.appendClass(clsName).append("[c]").ws().append("=").ws()
                            .appendMethod(method.getReference()).append(";").softNewLine();
                }
            }
        }
    }

    private void generateLookup(GeneratorContext context, SourceWriter writer) {
        String param = context.getParameterName(1);
        writer.append("switch").ws().append("(").appendFunction("$rt_ustr").append("(" + param + "))")
                .ws().append("{").softNewLine().indent();
        for (String name : context.getClassSource().getClassNames()) {
            writer.append("case \"" + name + "\":").ws().appendClass(name).append(".$clinit();").ws()
                    .append("return ").appendClass(name).append(";").softNewLine();
        }
        writer.append("default:").ws().append("return null;").softNewLine();
        writer.outdent().append("}").softNewLine();
    }


    private void generateEnumConstants(GeneratorContext context, SourceWriter writer) {
        writer.append("let c").ws().append("=").ws().append("'$$enumConstants$$';").softNewLine();
        for (String clsName : context.getClassSource().getClassNames()) {
            ClassReader cls = context.getClassSource().get(clsName);
            MethodReader method = cls.getMethod(new MethodDescriptor("values",
                    ValueType.arrayOf(ValueType.object(clsName))));
            if (method != null) {
                writer.appendClass(clsName).append("[c]").ws().append("=").ws();
                writer.appendMethod(method.getReference());
                writer.append(";").softNewLine();
            }
        }

        MethodReference selfRef = new MethodReference(Platform.class, "getEnumConstants",
                PlatformClass.class, Enum[].class);
        writer.appendMethod(selfRef).ws().append("=").ws().append("cls").sameLineWs().append("=>").ws()
                .append("{").softNewLine().indent();
        writer.append("if").ws().append("(!cls.hasOwnProperty(c))").ws().append("{").indent().softNewLine();
        writer.append("return null;").softNewLine();
        writer.outdent().append("}").softNewLine();
        writer.append("if").ws().append("(typeof cls[c]").ws().append("===").ws().append("\"function\")").ws()
                .append("{").indent().softNewLine();
        writer.append("cls[c]").ws().append("=").ws().append("cls[c]();").softNewLine();
        writer.outdent().append("}").softNewLine();
        writer.append("return cls[c];").softNewLine();
        writer.outdent().append("};").softNewLine();

        writer.append("return ").appendMethod(selfRef).append("(").append(context.getParameterName(1))
                .append(");").softNewLine();
    }

    private void generateAnnotations(GeneratorContext context, SourceWriter writer) {
        writer.append("let c").ws().append("=").ws().append("'$$annotations$$';").softNewLine();
        for (String clsName : context.getClassSource().getClassNames()) {
            ClassReader annotCls = context.getClassSource().get(clsName + "$$__annotations__$$");
            if (annotCls != null) {
                writer.appendClass(clsName).append("[c]").ws().append("=").ws();
                MethodReference ctor = new MethodReference(annotCls.getName(), "<init>", ValueType.VOID);
                writer.appendInit(ctor);
                writer.append("();").softNewLine();
            }
        }

        MethodReference selfRef = new MethodReference(Platform.class, "getAnnotations", PlatformClass.class,
                Annotation[].class);
        writer.appendMethod(selfRef).ws().append("=").ws().append("cls").sameLineWs().append("=>").ws()
                .append("{").softNewLine().indent();
        writer.append("if").ws().append("(!cls.hasOwnProperty(c))").ws().append("{").indent().softNewLine();
        writer.append("return null;").softNewLine();
        writer.outdent().append("}").softNewLine();
        writer.append("return cls[c].").appendVirtualMethod("getAnnotations", Annotation[].class).append("();")
                .softNewLine();
        writer.outdent().append("};").softNewLine();

        writer.append("return ").appendMethod(selfRef).append("(").append(context.getParameterName(1))
                .append(");").softNewLine();
    }
}

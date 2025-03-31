/*
 *  Copyright 2023 konsoletyper.
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
package org.teavm.jso.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSBodyImport;
import org.teavm.junit.AttachJavaScript;
import org.teavm.junit.EachTestCompiledSeparately;
import org.teavm.junit.JsModuleTest;
import org.teavm.junit.OnlyPlatform;
import org.teavm.junit.ServeJS;
import org.teavm.junit.SkipJVM;
import org.teavm.junit.SkipPlatform;
import org.teavm.junit.TeaVMTestRunner;
import org.teavm.junit.TestPlatform;

@RunWith(TeaVMTestRunner.class)
@SkipJVM
@OnlyPlatform({TestPlatform.JAVASCRIPT, TestPlatform.WEBASSEMBLY_GC})
@EachTestCompiledSeparately
public class ImportModuleTest {
    @Test
    @AttachJavaScript({
            "org/teavm/jso/test/amd.js",
            "org/teavm/jso/test/amdModule.js"
    })
    @SkipPlatform(TestPlatform.WEBASSEMBLY_GC)
    public void amd() {
        assertEquals(23, runTestFunction());
    }

    @Test
    @AttachJavaScript("org/teavm/jso/test/commonjs.js")
    @SkipPlatform(TestPlatform.WEBASSEMBLY_GC)
    public void commonjs() {
        assertEquals(23, runTestFunction());
    }

    @Test
    @JsModuleTest
    @ServeJS(from = "org/teavm/jso/test/es2015.js", as = "testModule.js")
    public void es2015() {
        assertEquals(23, runTestFunction());
    }

    @Test
    @JsModuleTest
    @ServeJS(from = "org/teavm/jso/test/classWithConstructorInModule.js", as = "testModule.js")
    public void classConstructor() {
        var o = new ClassWithConstructorInModule();
        assertEquals(99, o.getFoo());
        assertEquals("bar called", o.bar());

        o = new ClassWithConstructorInModule(23);
        assertEquals(23, o.getFoo());
    }

    @Test
    @JsModuleTest
    @ServeJS(from = "org/teavm/jso/test/classWithConstructorInModule.js", as = "testModule.js")
    public void topLevel() {
        assertEquals("top level", ClassWithConstructorInModule.topLevelFunction());
        assertEquals("top level prop", ClassWithConstructorInModule.getTopLevelProperty());
    }

    @JSBody(
            script = "return testModule.foo();",
            imports = @JSBodyImport(alias = "testModule", fromModule = "./testModule.js")
    )
    private static native int runTestFunction();
}

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
package org.teavm.tooling.sources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.teavm.debugging.information.SourceFileResolver;
import org.teavm.tooling.TeaVMSourceFilePolicy;

public class DefaultSourceFileResolver implements SourceFileResolver {
    private File targetDir;
    private List<SourceFileProvider> sourceFileProviders;
    private TeaVMSourceFilePolicy sourceFilePolicy = TeaVMSourceFilePolicy.DO_NOTHING;

    public DefaultSourceFileResolver(File targetDir, List<SourceFileProvider> sourceFileProviders) {
        this.targetDir = targetDir;
        this.sourceFileProviders = sourceFileProviders;
    }

    public void setSourceFilePolicy(TeaVMSourceFilePolicy sourceFilePolicy) {
        this.sourceFilePolicy = sourceFilePolicy;
    }

    public void open() throws IOException {
        for (var provider : sourceFileProviders) {
            provider.open();
        }
    }

    @Override
    public String resolveFile(String file) throws IOException {
        for (var provider : sourceFileProviders) {
            var sourceFile = provider.getSourceFile(file);
            if (sourceFile != null) {
                if (sourceFilePolicy == TeaVMSourceFilePolicy.COPY || sourceFile.getFile() == null) {
                    var outputFile = new File(targetDir, file);
                    outputFile.getParentFile().mkdirs();
                    try (var input = sourceFile.open();
                            var output = new FileOutputStream(outputFile)) {
                        input.transferTo(output);
                    }
                    if (sourceFilePolicy == TeaVMSourceFilePolicy.LINK_LOCAL_FILES) {
                        return "file://" + outputFile.getCanonicalPath();
                    }
                } else {
                    return "file://" + sourceFile.getFile().getCanonicalPath();
                }
                break;
            }
        }
        return null;
    }

    public void close() throws IOException {
        for (var provider : sourceFileProviders) {
            provider.close();
        }
    }
}

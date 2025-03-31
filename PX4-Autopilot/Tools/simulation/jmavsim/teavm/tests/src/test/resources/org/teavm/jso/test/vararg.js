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

class TestClass {
    static allVararg(...args) {
        let result = "va";
        for (const arg of args) {
            result += ":" + arg;
        }
        return result;
    }

    static restVararg(a, b, ...args) {
        let result = `a:${a},b:${b},va`;
        for (const arg of args) {
            result += ":" + arg;
        }
        return result;
    }
}

function topLevelVararg(...args) {
    let result = "tva";
    for (const arg of args) {
        result += ":" + arg;
    }
    return result;
}
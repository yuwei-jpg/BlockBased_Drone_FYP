let generatedCode = '';
let fullCode = '';

const { spawn } = require("child_process");
let serverProcess = null;
let simulatorProcess = null;

// // 运行 server.js 和模拟器
// async function runServer() {
//     if (serverProcess) {
//         console.log("Server is already running!");
//         return;
//     }
//
//     console.log("Starting server.js...");
//     serverProcess = spawn("node", ["server.js"], {
//         detached: true, // 让进程在后台运行
//         stdio: "ignore"
//     });
//
//     serverProcess.unref(); // 让进程独立于当前脚本运行
//
//     console.log("Server started successfully.");
//
//     console.log("Starting simulator...");
//     simulatorProcess = spawn("./simulator.sh", [], {
//         detached: true,
//         stdio: "ignore"
//     });
//
//     simulatorProcess.unref();
//     console.log("Simulator started successfully.");
// }
//
// // 终止 `server.js` 和模拟器
// function stopServer() {
//     if (serverProcess) {
//         console.log("Stopping server.js...");
//         serverProcess.kill(); // 杀死进程
//         serverProcess = null;
//     }
//
//     if (simulatorProcess) {
//         console.log("Stopping simulator...");
//         simulatorProcess.kill();
//         simulatorProcess = null;
//     }
// }
//
// module.exports = { runServer, stopServer };

/*--------------------------------------------------------------------------------------------------------*/

async function runCode() {
    try {
        // 首先启动模拟器
        showNotification('Starting emulator...', 'info');

        // 检查是否已经有模拟器在运行
        const simResponse = await fetch("http://localhost:3000/run_simulator", {
            method: "POST"
        });

        if (!simResponse.ok) {
            const errorData = await simResponse.json();
            throw new Error(errorData.message || 'Simulator startup failed');
        }

        // 等待模拟器就绪
        showNotification('Waiting for simulator to be ready...', 'info');
        
        // 轮询检查模拟器状态
        while (true) {
            const statusResponse = await fetch("http://localhost:3000/simulator_status");
            const statusData = await statusResponse.json();
            
            if (statusData.status === "ready") {
                break;
            }
            
            // 每秒检查一次
            await new Promise(resolve => setTimeout(resolve, 1000));
        }

        showNotification('Simulator ready, executing code...', 'success');

        // 如果没有生成代码，先生成
        if (!generatedCode) {
            generatedCode = await generateCode();
        }
        console.log("Generated code:", generatedCode); // 调试日志

        if (!generatedCode || generatedCode.trim() === '') {
            throw new Error('No code generated from blocks');
        }

        // 发送代码到 MAVSDK
        const mavsdkResponse = await fetch("http://localhost:3000/run_MAVSDK", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ code: generatedCode })
        });

        if (!mavsdkResponse.ok) {
            const errorData = await mavsdkResponse.json();
            throw new Error(errorData.message || '代码执行失败');
        }

        showNotification('代码开始执行', 'success');
        monitorExecution();

    } catch (error) {
        console.error('执行错误:', error);
        showNotification(error.message, 'error');
    }
}

/*--------------------------------------------------------------------------------------------------------*/
// Notify the system
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.remove();
    }, 3000);
}


function monitorExecution() {
    const blocks = workspace.getAllBlocks();
    let currentIndex = 0;

    function highlightNextBlock() {
        if (currentIndex < blocks.length) {
            const block = blocks[currentIndex];

            // 高亮当前块
            block.setColour("#ffcc00");

            // 滚动到当前块
            workspace.centerOnBlock(block.id);

            // 500ms 后恢复颜色
            setTimeout(() => {
                block.setColour(block.originalColour || null);
                currentIndex++;
                highlightNextBlock();
            }, 1000); // 调整时间以匹配实际执行时间
        }
    }

    // 保存原始颜色
    blocks.forEach(block => {
        block.originalColour = block.getColour();
    });

    highlightNextBlock();
}

/*--------------------------------------------------------------------------------------------------------*/

// 点击“Copy”按钮后，复制内容到剪贴板
function copyCode() {
    const content = document.getElementById('modalContent').innerText;
    // 调用 Clipboard API
    navigator.clipboard.writeText(content)
        .then(() => {
            // 复制成功后，您可以弹个提示，也可以不弹
            alert('Copied to clipboard!');
        })
        .catch(err => {
            console.error('Failed to copy: ', err);
        });
}

async function generateCode(event) {

    try {
        // Dynamically importing modules within a function
        const module = await import('../src/generators/python.js');
        const variableMap = new Map();
        module.pythonGenerator.ORDER_ATOMIC = 0;         // 0 "" ...
        module.pythonGenerator.ORDER_NONE = 99;          // (...)
        module.pythonGenerator.ORDER_FUNCTION_CALL = 1;  // f(x)
        module.pythonGenerator.ORDER_ADDITIVE = 4
        module.pythonGenerator.ORDER_RELATIONAL = 6;
        module.pythonGenerator.ORDER_LOGICAL = 7;
        module.pythonGenerator.nameDB_ = new Blockly.Names('VARIABLE');
        // module.pythonGenerator.nameDB_ = new Blockly.Names(Blockly.Names.DEVELOPER_VARIABLE_TYPE);
        module.pythonGenerator.nameDB_.variablePrefix = ''; // 移除变量前缀

        function getAllStatements(block) {
            let code = ''; // 用于存储所有生成的代码
            while (block) {
                const blockCode = module.pythonGenerator.blockToCode(block); // 生成当前 Block 的代码
                if (Array.isArray(blockCode)) {
                    code += blockCode[0]; // 如果是数组，取第一项（代码部分）
                } else {
                    code += blockCode; // 否则直接拼接代码
                }
                block = block.getNextBlock(); // 获取下一个连接的 Block
            }
            return code; // return all spliced codes
        }


        module.pythonGenerator.forBlock['start'] = function(block) {
        block.getFieldValue('Start-message');
        // TODO: Assemble python into the code variable
         return 'await drone.action.arm()\n';
         }

         // Override the getName method to return the original variable name
        module.pythonGenerator.nameDB_.getName = function(name) {
            return name;
        };

        // Define a generator for a math block
        module.pythonGenerator.forBlock['math_number'] = function(block) {
            const code = Number(block.getFieldValue('NUM'));
            return [code, module.pythonGenerator.ORDER_ATOMIC];
        };

        module.pythonGenerator.forBlock['math_single'] = function (block) {
            const value = module.pythonGenerator.valueToCode(block, 'NUM', module.pythonGenerator.ORDER_NONE) || '0';
            return [`math.sqrt(${value})`, module.pythonGenerator.ORDER_FUNCTION_CALL];
        };

        module.pythonGenerator.forBlock['math_trig'] = function (block) {
            const value = module.pythonGenerator.valueToCode(block, 'NUM', module.pythonGenerator.ORDER_NONE) || '0';
            return [`math.sin(math.radians(${value}))`, module.pythonGenerator.ORDER_FUNCTION_CALL];
        };

        module.pythonGenerator.forBlock['math_random_int'] = function (block) {
            const min = module.pythonGenerator.valueToCode(block, 'FROM', module.pythonGenerator.ORDER_NONE) || '0';
            const max = module.pythonGenerator.valueToCode(block, 'TO', module.pythonGenerator.ORDER_NONE) || '100';
            return [`random.randint(${min}, ${max})`, module.pythonGenerator.ORDER_FUNCTION_CALL];
        };

        // Define a generator for a core module
        module.pythonGenerator.forBlock['controls_if'] = function (block) {
            // 1. read the number of else-if / else
            const elseifCount = block.elseifCount_ || 0;
            const elseCount = block.elseCount_ || 0;

            // Finally, it is concatenated into multiple lines of Python code
            let code = '';

            // 2. handle the first branch of if
            let conditionCode = module.pythonGenerator.valueToCode(
                block,
                'IF0',
                module.pythonGenerator.ORDER_NONE
            ) || 'False';

            // Get the statement block corresponding to the first if
            let doBlock = block.getInputTargetBlock('DO0');
            let ifStatements = getAllStatements(doBlock);
            if (!ifStatements) {
                ifStatements = 'pass\n';
            } else {
                // For indentation
                ifStatements = module.pythonGenerator.prefixLines(ifStatements, '    ');
            }
            code += `if ${conditionCode}:\n${ifStatements}\n`;

            // 3. Handling multiple else if branches
            for (let i = 1; i <= elseifCount; i++) {
                let cond = module.pythonGenerator.valueToCode(
                    block,
                    'IF' + i,
                    module.pythonGenerator.ORDER_NONE
                ) || 'False';

                let doBlk = block.getInputTargetBlock('DO' + i);
                let elifStatements = getAllStatements(doBlk);
                if (!elifStatements) {
                    elifStatements = 'pass\n';
                } else {
                    elifStatements = module.pythonGenerator.prefixLines(elifStatements, '    ');
                }
                code += `elif ${cond}:\n${elifStatements}\n`;
            }

            // 4. If there is an else branch, generate else:
            if (elseCount === 1) {
                let elseDoBlock = block.getInputTargetBlock('ELSE');
                let elseStatements = getAllStatements(elseDoBlock);
                if (!elseStatements) {
                    elseStatements = 'pass\n';
                } else {
                    elseStatements = module.pythonGenerator.prefixLines(elseStatements, '    ');
                }
                code += `else:\n${elseStatements}\n`;
            }
            return code;
        };


        module.pythonGenerator.forBlock['repeat_while'] = function (block) {
            const mode = block.getFieldValue('MODE'); // Get the mode selected by the user ('WHILE' or 'UNTIL')
            const condition = module.pythonGenerator.valueToCode(block, 'CONDITION', module.pythonGenerator.ORDER_NONE) || 'False';
            let doBlock = block.getInputTargetBlock('DO');
            let statements = getAllStatements(doBlock);

            // **Make sure conditions and statements are indented correctly**
            if (statements) {
                if (mode === 'WHILE') {
                    return `while ${condition}:\n${module.pythonGenerator.prefixLines(statements, "    ")}\n`;
                } else if (mode === 'UNTIL') {
                    return `while not ${condition}:\n${module.pythonGenerator.prefixLines(statements, "    ")}\n`;
                } else {
                    return `# ERROR: Unknown loop mode\n`;
                }
            } else {
                if (mode === 'WHILE') {
                    return `while ${condition}:\n    pass\n`;
                } else if (mode === 'UNTIL') {
                    return `while not ${condition}:\n    pass\n`;
                }
            }
        };

        module.pythonGenerator.forBlock['check_altitude'] = function (block) {
            const target_altitude = block.getFieldValue('TARGET_ALTITUDE');
            let onReachBlock = block.getInputTargetBlock('ON_REACH');
            let statement1 = getAllStatements(onReachBlock);
            let onNotReachBlock = block.getInputTargetBlock('ON_NOT_REACH');
            let statement2 = getAllStatements(onNotReachBlock);
            return `
async for position1 in drone.telemetry.position():
    if position1.relative_altitude_m >= ${target_altitude}:
${module.pythonGenerator.prefixLines(statement1, "        ")}
        break
    else:
${module.pythonGenerator.prefixLines(statement2, "        ")}
        break
`;
        };


        module.pythonGenerator.forBlock['logic_compare'] = function (block) {
            const operatorMap = {
                'EQ': '==',
                'NEQ': '!=',
                'LT': '<',
                'LTE': '<=',
                'GT': '>',
                'GTE': '>='
            };
            const operator = operatorMap[block.getFieldValue('OP')];
            const left = module.pythonGenerator.valueToCode(block, 'A', module.pythonGenerator.ORDER_NONE) || '0';
            const right = module.pythonGenerator.valueToCode(block, 'B', module.pythonGenerator.ORDER_NONE) || '0';
            const code = left + ' ' + operator + ' ' + right;
            return [code, module.pythonGenerator.ORDER_RELATIONAL];
        };

        module.pythonGenerator.forBlock['logic_operation'] = function (block) {
            const operator = block.getFieldValue('OP');
            const left = module.pythonGenerator.valueToCode(block, 'A', module.pythonGenerator.ORDER_NONE) || 'False';
            const right = module.pythonGenerator.valueToCode(block, 'B', module.pythonGenerator.ORDER_NONE) || 'False';
            const code = left + ' ' + operator + ' ' + right;
            return [code, module.pythonGenerator.ORDER_LOGICAL];
        };

        module.pythonGenerator.forBlock['logic_boolean'] = function (block) {
            const code = block.getFieldValue('BOOL') === 'TRUE' ? 'True' : 'False';
            return [code, module.pythonGenerator.ORDER_ATOMIC];
        };

        // Generators for the Variables class

        module.pythonGenerator.forBlock['variables_get'] = function (block) {
            // Use the original variable name directly
            const varId = block.getFieldValue('VAR');
            // Get the variable instance
            const variable = block.workspace.getVariableById(varId);
            // Use the actual name of variable
            const varName = variable ? variable.name : varId;
            return [varName, module.pythonGenerator.ORDER_ATOMIC];
        };

        module.pythonGenerator.forBlock['variables_set'] = function (block) {
            const argument0 = module.pythonGenerator.valueToCode(block, 'VALUE', module.pythonGenerator.ORDER_NONE) || '0';
            const varId = block.getFieldValue('VAR');
            const variable = block.workspace.getVariableById(varId);
            const varName = variable ? variable.name : varId;
            return `${varName} = float(${argument0})\n`;
        };

        module.pythonGenerator.forBlock['math_change'] = function (block) {
            const varId = block.getFieldValue('VAR');
            const variable = block.workspace.getVariableById(varId);
            const varName = variable ? variable.name : varId;
            const delta = module.pythonGenerator.valueToCode(block, 'DELTA', module.pythonGenerator.ORDER_ADDITIVE) || '1';
            return `${varName} += ${delta}\n`;
        };


        module.pythonGenerator.forBlock['offboard'] = function (block) {
            block.getFieldValue('offboard');
            // TODO: Assemble python into the code variable
            return 'await drone.offboard.start()\n';
        }
        module.pythonGenerator.forBlock['offend'] = function (block) {
            block.getFieldValue('offend');
            // TODO: Assemble python into the code variable
            return 'await drone.offboard.stop()\n';
        }

        module.pythonGenerator.forBlock['position'] = function (block){
            const front = block.getFieldValue('forward');
            const right = block.getFieldValue('right');
            const down = block.getFieldValue('down');
            const angle = block.getFieldValue('angle');
            return `await drone.offboard.set_position_ned(PositionNedYaw(${front},${right},${down},${angle}))\n`;
        }
        module.pythonGenerator.forBlock['position2'] = function (block) {
            const front = block.getFieldValue('forward');
            const right = block.getFieldValue('right');
            const down = block.getFieldValue('down');
            const angle = block.getFieldValue('angle');
            return `await drone.offboard.set_velocity_body(VelocityBodyYawspeed(${front},${right},${down},${angle}))\n`;
        }

        module.pythonGenerator.forBlock['acceleration'] = function (block) {
            const north = block.getFieldValue('north');
            const east = block.getFieldValue('east');
            const down = block.getFieldValue('down');

            return `await drone.offboard.set_acceleration_ned(AccelerationNed(${north},${east},${down}))\n`;
        }

        module.pythonGenerator.forBlock['position3'] = function (block){
            const front = block.getFieldValue('forward');
            const right = block.getFieldValue('right');
            const down = block.getFieldValue('down');
            const angle = block.getFieldValue('angle');
            const fronts = block.getFieldValue('speed1');
            const rights = block.getFieldValue('speed2');
            const downs = block.getFieldValue('speed3');
            return `await drone.offboard.set_position_velocity_ned(PositionNedYaw(${front},${right},${down},${angle}),
            VelocityNedYaw(${fronts},${rights},${downs},${angle}))\n`;
        }

         module.pythonGenerator.forBlock['attitude'] = function (block){
            const roll = block.getFieldValue('roll');
            const pitch = block.getFieldValue('pitch');
            const yaw = block.getFieldValue('yaw');
            const push = block.getFieldValue('push');
            return `await drone.offboard.set_attitude(Attitude(${roll},${pitch},${yaw},${push}))\n`;
        }

         module.pythonGenerator.forBlock['attitude_rate'] = function (block){
            const roll = block.getFieldValue('roll');
            const pitch = block.getFieldValue('pitch');
            const yaw = block.getFieldValue('yaw');
            const push = block.getFieldValue('push');
            return `await drone.offboard.set_attitude_rate(AttitudeRate(${roll},${pitch},${yaw},${push}))\n`;
        }

         module.pythonGenerator.forBlock['position3_acc'] = function (block){
            const front = block.getFieldValue('front');
            const right = block.getFieldValue('right');
            const down = block.getFieldValue('down');
            const angle = block.getFieldValue('angle');
            const fronts = block.getFieldValue('speed1');
            const rights = block.getFieldValue('speed2');
            const downs = block.getFieldValue('speed3');
            const north = block.getFieldValue('north');
            const east = block.getFieldValue('east');
            const down2 = block.getFieldValue('down2');
            return `await drone.offboard.set_position_velocity_acceleration_ned(PositionNedYaw(${front},${right},${down},${angle}),
            VelocityNedYaw(${fronts},${rights},${downs},${angle}),AccelerationNed(${north},${east},${down2})\n`;
        }


        module.pythonGenerator.forBlock['takeoff'] = function (block) {
            block.getFieldValue('take-off');
            // TODO: Assemble python into the code variable
            return 'await drone.action.takeoff()\n';
        }

        module.pythonGenerator.forBlock['takeoff2'] = function (block) {
            const height = block.getFieldValue('altitude');
            return `await drone.action.set_takeoff_altitude(${height})\n`;
        }

        module.pythonGenerator.forBlock['hold'] = function (block) {
            // TODO: Assemble python into the code variable
            return `await drone.action.hold()\n`;
        }


        module.pythonGenerator.forBlock['orbit'] = function (block) {
            const speed = module.pythonGenerator.valueToCode(block, 'velocity', module.pythonGenerator.ORDER_ATOMIC) ||
                block.getFieldValue('velocity') || '0';
            const radius2 = module.pythonGenerator.valueToCode(block, 'radius', module.pythonGenerator.ORDER_ATOMIC) ||
                block.getFieldValue('radius') || '0';
            // TODO: Assemble python into the code variable
            return `
async for position in drone.telemetry.position():
    latitude = position.latitude_deg
    longitude = position.longitude_deg
    altitude = position.absolute_altitude_m
    break  
await drone.action.do_orbit(${radius2},${speed}, OrbitYawBehavior.HOLD_INITIAL_HEADING, latitude, longitude, altitude)\n`;
        }


        module.pythonGenerator.forBlock['go_to_location'] = function (block) {
            const longitudes = block.getFieldValue("longitude");
            const latitudes = block.getFieldValue("latitude");
            const altitudes = block.getFieldValue("altitude");
            return `await drone.action.goto_location(${longitudes},${latitudes},${altitudes},0)\n`;
        }

        module.pythonGenerator.forBlock['setCurrentSpeed'] = function (block) {
            const speed = module.pythonGenerator.valueToCode(block, 'SPEED', module.pythonGenerator.ORDER_ATOMIC) ||
                block.getFieldValue('SPEED') || '0';
            return `await drone.action.set_current_speed(${speed})\n`;
        }

        module.pythonGenerator.forBlock['setMaxSpeed'] = function (block) {
            const speed = module.pythonGenerator.valueToCode(block, 'maxSpeed', module.pythonGenerator.ORDER_ATOMIC) ||
                block.getFieldValue('maxSpeed') || '0';
            // const speed = block.getFieldValue('maxSpeed');
            // TODO: Assemble python into the code variable
            return `await drone.action.set_maximum_speed(${speed})\n`;
        }

        module.pythonGenerator.forBlock['returnToLaunch'] = function (block) {
            // TODO: Assemble python into the code variable
            return `await drone.action.return_to_launch()\n`;
        }

        module.pythonGenerator.forBlock['returnToLaunchAltitude'] = function (block) {
            // TODO: Assemble python into the code variable
            const altitude2 = module.pythonGenerator.valueToCode(block, 'Altitude', module.pythonGenerator.ORDER_ATOMIC) ||
                block.getFieldValue('Altitude') || '0';
            return `await drone.action.set_return_to_launch_altitude(${altitude2})\n`;
        }

        module.pythonGenerator.forBlock['sleep'] = function (block) {
            const sleep = block.getFieldValue('sleep');
            // TODO: Assemble python into the code variable
            return `await asyncio.sleep(${sleep})\n`;
        }

        module.pythonGenerator.forBlock['end'] = function (block) {
            block.getFieldValue('End-message');
            // TODO: Assemble python into the code variable
            return 'await drone.action.land()\n';
        }

         module.pythonGenerator.forBlock['Spiral_Upward'] = function (block){
             const getValue = (inputName) => {
                 const childBlock = block.getInput(inputName).connection.targetBlock();
                 if (!childBlock || childBlock.type !== 'child_block') {
                     throw new Error(`Missing required ${inputName} child block`);
                 }
                 return module.pythonGenerator.valueToCode(childBlock, 'VALUE', module.pythonGenerator.ORDER_ATOMIC)||
                     childBlock.getFieldValue("VALUE")||'0';
             };

             const radius = getValue('RADIUS_INPUT');
             const speed = getValue('SPEED_INPUT');
             const angle_speed = getValue('ANGLE_INPUT');

             return `await spiral_ascend(drone, 50, ${radius}, ${speed}, ${angle_speed})`
         }

          module.pythonGenerator.forBlock['Zigzag'] = function (block){
             const getValue2 = (inputName) => {
                 const childBlock2 = block.getInput(inputName).connection.targetBlock();
                 if (!childBlock2 || childBlock2.type !== 'child_block') {
                     throw new Error(`Missing required ${inputName} child block`);
                 }
                 return module.pythonGenerator.valueToCode(childBlock2, 'VALUE', module.pythonGenerator.ORDER_ATOMIC)||
                     childBlock2.getFieldValue("VALUE")||'0';
             };

             const amplitude = getValue2('AMPLITUDE_INPUT');
             const speed = getValue2('SPEED_INPUT');

             return `await zigzag_flight(drone, 50, ${amplitude}, ${speed})`
         }

         // Iterate over all top-level blocks and recursively generate the complete code
        // const code = await module.pythonGenerator.workspaceToCode(workspace);
        const generateBlockCode = (block) => {
            if (!block) return ''; // If the current block is empty, returns an empty string
            let code = module.pythonGenerator.blockToCode(block);
            if (Array.isArray(code)) {
                code = code[0]; // If it is an array, take the first code part
            }
            // Recursively process the next block of connections
            const nextBlock = block.getNextBlock();
            return code + generateBlockCode(nextBlock);
        };
        const topBlocks = workspace.getTopBlocks(true);
        let fullCode = '';
        topBlocks.forEach((block) => {
            fullCode += generateBlockCode(block);
        });

        console.log(fullCode);

        const blocks = workspace.getAllBlocks();
        if (blocks.length === 0) {
            document.getElementById('modalContent').value = 'Please add some blocks to generate code.';
            return;
        }

         const modal = document.getElementById('codeModal');
         const backdrop = document.getElementById('modalBackdrop');
         const content = document.getElementById('modalContent');
         content.textContent = fullCode; // insert new generated code
         modal.style.display = 'block';
         backdrop.style.display = 'block';

         return fullCode;
        // // send code to the back end
        // sendCodeToBackend(fullCode);
    } catch (error) {
        console.error('模块导入失败:', error);
    }
}

// Clearing generated code when workspace changes
workspace.addChangeListener(function(event) {
    generatedCode = '';
});

// Clear code when closing the modal
function closeModal() {
    document.getElementById('codeModal').style.display = 'none';
    document.getElementById('modalBackdrop').style.display = 'none';
    generatedCode = ''; // clear the generated code
}


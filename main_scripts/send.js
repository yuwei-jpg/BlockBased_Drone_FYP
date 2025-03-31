let generatedCode = '';
let fullCode = '';

const { spawn } = require("child_process");
const {block} = require("blockly/core/tooltip");
let serverProcess = null;
let simulatorProcess = null;

// async function runServer() {
//     if (serverProcess) {
//         console.log("Server is already running!");
//         return;
//     }
//
//     console.log("Starting server.js...");
//     serverProcess = spawn("node", ["server.js"], {
//         detached: true,
//         stdio: "ignore"
//     });
//
//     serverProcess.unref();
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

// function stopServer() {
//     if (serverProcess) {
//         console.log("Stopping server.js...");
//         serverProcess.kill();
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
        showNotification('Starting emulator...', 'info');

        const simResponse = await fetch("http://localhost:3000/run_simulator", {
            method: "POST"
        });

        if (!simResponse.ok) {
            const errorData = await simResponse.json();
            throw new Error(errorData.message || 'Simulator startup failed');
        }

        showNotification('Waiting for simulator to be ready...', 'info');

        while (true) {
            const statusResponse = await fetch("http://localhost:3000/simulator_status");
            const statusData = await statusResponse.json();
            
            if (statusData.status === "ready") {
                break;
            }

            await new Promise(resolve => setTimeout(resolve, 1000));
        }

        showNotification('Simulator ready, executing code...', 'success');

        if (!generatedCode) {
            generatedCode = await generateCode();
        }
        console.log("Generated code:", generatedCode);

        if (!generatedCode || generatedCode.trim() === '') {
            throw new Error('No code generated from blocks');
        }

        const mavsdkResponse = await fetch("http://localhost:3000/run_MAVSDK", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ code: generatedCode })
        });

        if (!mavsdkResponse.ok) {
            const errorData = await mavsdkResponse.json();
            throw new Error(errorData.message || 'Code execution failed');
        }

        showNotification('Code starts executing', 'success');
        monitorExecution();

    } catch (error) {
        console.error('Execution Error:', error);
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

            block.setColour("#ffcc00");

            workspace.centerOnBlock(block.id);

            setTimeout(() => {
                block.setColour(block.originalColour || null);
                currentIndex++;
                highlightNextBlock();
            }, 1000);
        }
    }

    blocks.forEach(block => {
        block.originalColour = block.getColour();
    });

    highlightNextBlock();
}

/*--------------------------------------------------------------------------------------------------------*/

function copyCode() {
    const content = document.getElementById('modalContent').innerText;
    // 调用 Clipboard API
    navigator.clipboard.writeText(content)
        .then(() => {
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
        module.pythonGenerator.nameDB_.variablePrefix = '';

        function getAllStatements(block) {
            let code = '';
            while (block) {
                const blockCode = module.pythonGenerator.blockToCode(block);
                if (Array.isArray(blockCode)) {
                    code += blockCode[0];
                } else {
                    code += blockCode;
                }
                block = block.getNextBlock();
            }
            return code; // return all spliced codes
        }

        function getValue(block, inputName) {
            const childBlock = block.getInput(inputName).connection.targetBlock();
            if (!childBlock || childBlock.type !== 'child_block') {
                throw new Error(`Missing required ${inputName} child block`);
            }
            return module.pythonGenerator.valueToCode(childBlock, 'VALUE', module.pythonGenerator.ORDER_ATOMIC) ||
                childBlock.getFieldValue("VALUE") || '0';
        }


        module.pythonGenerator.forBlock['start'] = function(block) {
        block.getFieldValue('Start-message');
        // TODO: Assemble python into the code variable
         return 'await drone.action.arm()\n';
         }

         // Override the getName method to return the original variable name
        module.pythonGenerator.nameDB_.getName = function(name) {
            return name;
        }

        // Define a generator for a math block
        module.pythonGenerator.forBlock['math_number'] = function(block) {
            const code = Number(block.getFieldValue('NUM'));
            return [code, module.pythonGenerator.ORDER_ATOMIC];
        }

        // module.pythonGenerator.forBlock['math_single'] = function (block) {
        //     const value = module.pythonGenerator.valueToCode(block, 'NUM', module.pythonGenerator.ORDER_NONE) || '0';
        //     return [`math.sqrt(${value})`, module.pythonGenerator.ORDER_FUNCTION_CALL];
        // }
        module.pythonGenerator.forBlock['math_single'] = function (block) {
            const operator = block.getFieldValue('OP'); // 获取操作类型
            const arg = module.pythonGenerator.valueToCode(block, 'NUM', module.pythonGenerator.ORDER_NONE) || '0';

            let code;
            switch (operator) {
                case 'ROOT': // square root
                    code = `math.sqrt(${arg})`;
                    break;
                case 'ABS': // absolute
                    code = `abs(${arg})`;
                    break;
                case 'NEG': // negative (-x)
                    code = `-${arg}`;
                    break;
                case 'LN': // natural log
                    code = `math.log(${arg})`;
                    break;
                case 'LOG10': // log base 10
                    code = `math.log10(${arg})`;
                    break;
                case 'EXP': // e^x
                    code = `math.exp(${arg})`;
                    break;
                case 'POW10': // 10^x
                    code = `math.pow(10, ${arg})`;
                    break;
                default:
                    code = arg;
            }
            return [code, module.pythonGenerator.ORDER_FUNCTION_CALL];
        };


        // module.pythonGenerator.forBlock['math_trig'] = function (block) {
        //     const value = module.pythonGenerator.valueToCode(block, 'NUM', module.pythonGenerator.ORDER_NONE) || '0';
        //     return [`math.sin(math.radians(${value}))`, module.pythonGenerator.ORDER_FUNCTION_CALL];
        // }
        module.pythonGenerator.forBlock['math_trig'] = function (block) {
            const operator = block.getFieldValue('OP');
            const arg = module.pythonGenerator.valueToCode(block, 'NUM', module.pythonGenerator.ORDER_NONE) || '0';
            let code;

            switch (operator) {
                case 'SIN':
                    code = `math.sin(math.radians(${arg}))`;
                    break;
                case 'COS':
                    code = `math.cos(math.radians(${arg}))`;
                    break;
                case 'TAN':
                    code = `math.tan(math.radians(${arg}))`;
                    break;
                case 'ASIN':
                    code = `math.degrees(math.asin(${arg}))`;
                    break;
                case 'ACOS':
                    code = `math.degrees(math.acos(${arg}))`;
                    break;
                case 'ATAN':
                    code = `math.degrees(math.atan(${arg}))`;
                    break;
                default:
                    code = arg;
            }
            return [code, module.pythonGenerator.ORDER_FUNCTION_CALL];
        };

        module.pythonGenerator.forBlock['math_random_int'] = function (block) {
            const min = module.pythonGenerator.valueToCode(block, 'FROM', module.pythonGenerator.ORDER_NONE) || '0';
            const max = module.pythonGenerator.valueToCode(block, 'TO', module.pythonGenerator.ORDER_NONE) || '100';
            return [`random.randint(${min}, ${max})`, module.pythonGenerator.ORDER_FUNCTION_CALL];
        }

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
        }


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
        }

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
        }


        module.pythonGenerator.forBlock['logic_compare'] = function (block) {
            const operatorMap = {
                'EQ': '==',
                'NEQ': '!=',
                'LT': '<',
                'LTE': '<=',
                'GT': '>',
                'GTE': '>='
            }
            const operator = operatorMap[block.getFieldValue('OP')];
            const left = module.pythonGenerator.valueToCode(block, 'A', module.pythonGenerator.ORDER_NONE) || '0';
            const right = module.pythonGenerator.valueToCode(block, 'B', module.pythonGenerator.ORDER_NONE) || '0';
            const code = left + ' ' + operator + ' ' + right;
            return [code, module.pythonGenerator.ORDER_RELATIONAL];
        }

        module.pythonGenerator.forBlock['logic_operation'] = function (block) {
            const operator = block.getFieldValue('OP');
            const left = module.pythonGenerator.valueToCode(block, 'A', module.pythonGenerator.ORDER_NONE) || 'False';
            const right = module.pythonGenerator.valueToCode(block, 'B', module.pythonGenerator.ORDER_NONE) || 'False';
            const code = left + ' ' + operator + ' ' + right;
            return [code, module.pythonGenerator.ORDER_LOGICAL];
        }

        module.pythonGenerator.forBlock['logic_boolean'] = function (block) {
            const code = block.getFieldValue('BOOL') === 'TRUE' ? 'True' : 'False';
            return [code, module.pythonGenerator.ORDER_ATOMIC];
        }

        // Generators for the Variables class

        module.pythonGenerator.forBlock['variables_get'] = function (block) {
            // Use the original variable name directly
            const varId = block.getFieldValue('VAR');
            // Get the variable instance
            const variable = block.workspace.getVariableById(varId);
            // Use the actual name of variable
            const varName = variable ? variable.name : varId;
            return [varName, module.pythonGenerator.ORDER_ATOMIC];
        }

        module.pythonGenerator.forBlock['variables_set'] = function (block) {
            const argument0 = module.pythonGenerator.valueToCode(block, 'VALUE', module.pythonGenerator.ORDER_NONE) || '0';
            const varId = block.getFieldValue('VAR');
            const variable = block.workspace.getVariableById(varId);
            const varName = variable ? variable.name : varId;
            return `${varName} = float(${argument0})\n`;
        }

        module.pythonGenerator.forBlock['math_change'] = function (block) {
            const varId = block.getFieldValue('VAR');
            const variable = block.workspace.getVariableById(varId);
            const varName = variable ? variable.name : varId;
            const delta = module.pythonGenerator.valueToCode(block, 'DELTA', module.pythonGenerator.ORDER_ADDITIVE) || '1';
            return `${varName} += ${delta}\n`;
        }


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
            VelocityNedYaw(${fronts},${rights},${downs},${angle}),AccelerationNed(${north},${east},${down2}))\n`;
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
            const forward = block.getFieldValue("longitude");
            const right = block.getFieldValue("latitude");
            const up = block.getFieldValue("altitude");
            return `await move_relative(drone, ${forward}, ${right}, ${up})\n`;
            // return `await drone.action.goto_location(${longitudes},${latitudes},${altitudes},0)\n`;
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

        module.pythonGenerator.forBlock['csv'] = function (block) {

            const shape = block.getFieldValue('shape');
            let filename = "";
            let header = "";

            switch (shape) {
                case 'ROLLER':
                    filename = "roller_coaster.csv";
                    header = "roller_coaster_data";
                    return `generate_roller_coaster_trajectory_csv()\n`;
                case 'SPIRAL':
                    filename = "spiral_ascend.csv";
                    header = "spiral_ascend_data";
                    return `generate_spiral_ascend_trajectory_csv()\n`;
                case 'ZIGZAG':
                    filename = "zigzag.csv";
                    header = "zigzag_data";
                    return `generate_z_trajectory_csv()\n`;
                case 'S_SHAPE':
                    filename = "s_shape.csv";
                    header = "s_shape_data";
                    return `generate_s_shape_trajectory_csv()\n`;
                case 'L_SHAPE':
                    filename = "l_shape.csv";
                    header = "l_shape_data";
                    return `generate_l_shape_trajectory_csv()\n`;
                case 'CIRCLE':
                    filename = "circle.csv";
                    header = "circle_data";
                    return `generate_circle_trajectory_csv()\n`;
                case 'TRIANGLE':
                    filename = "triangle.csv";
                    header = "triangle_data";
                    return `generate_triangle_trajectory_csv()\n`;
                case 'SQUARE':
                    filename = "square.csv";
                    header = "square_data";
                    return `generate_square_trajectory_csv()\n`;
                default:
                   return `\n`;
            }

        }

        module.pythonGenerator.forBlock['Spiral_Upward'] = function (block) {
            const time_step = getValue(block,'RADIUS_INPUT');
            const mode = getValue(block,'SPEED_INPUT');
            return `await execute_trajectory("spiral_ascend_trajectory.csv",${mode},${time_step},drone)\n`;

        }

         module.pythonGenerator.forBlock['Roller_Coaster'] = function (block){
             const time_step = getValue(block,'TIME_STEP');
             const mode = getValue(block,'MODE');
             return `await execute_trajectory("roller_coaster_trajectory.csv",${mode},${time_step},drone)\n`;
         }

        module.pythonGenerator.forBlock['Zigzag'] = function (block) {
            const time_step = getValue(block, 'AMPLITUDE_INPUT');
            const mode = getValue(block, 'SPEED_INPUT');
            return `await execute_trajectory("z_trajectory.csv",${mode},${time_step},drone)\n`;
        }

        module.pythonGenerator.forBlock['ALL'] = function (block) {
            const shape = block.getFieldValue("shape2");
            const file = block.getFieldValue("csv_file");

            const fileMapping = {
                'l': "l_shape_trajectory.csv",
                's': "s_shape_trajectory.csv",
                'circle': "circle_trajectory.csv",
                'square': "square_trajectory.csv",
                'triangle': "triangle_trajectory.csv"
            };

            const shapeMapping = {
                "L": "l",
                "S": "s",
                "C": "circle",
                "SQUA": "square",
                "TRI": "triangle"
            };

            if (shapeMapping[shape] === file) {
                const fullFileName = fileMapping[file];
                return `await execute_trajectory_other("${fullFileName}", drone)\n`;
            } else {
                return "";
            }
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
        console.error('Module import failed:', error);
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


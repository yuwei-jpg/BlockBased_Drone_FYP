import {pythonGenerator} from "../src/generators/python.js";

// const pythonGenerator =require("blockly/python.mjs")

pythonGenerator.forBlock['start'] = function(block,generator) {
    block.getFieldValue('Start-message');
// TODO: Assemble python into the code variable
    return 'await drone.action.arm()\n';
}

pythonGenerator.forBlock['direction'] = function(block,generator) {
    block.getFieldValue('Name');
// TODO: Assemble python into the code variable
    return 'await drone.action.arm()\n';
}




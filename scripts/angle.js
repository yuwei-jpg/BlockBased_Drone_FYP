// import * as Blockly from 'blockly';
// import {registerFieldAngle} from '@blockly/field-angle';
//
// registerFieldAngle();
Blockly.defineBlocksWithJsonArray([
    {
        "type": "angle",
        "tooltip": "You can set angle for the flying path!",
        "helpUrl": "",
        "message0": " and set angle to %1 %2",
        "args0": [
            {
                "type": "field_angle",
                "name": "Angle-message",
                "angle": 90
            },
            {
                "type": "input_value",
                "name": "NAME"
            }
        ],
        "previousStatement": null,
        "colour": 270
    }


])

// pythonGenerator.forBlock['angle'] = function() {
//     const angle_anglemessage = block.getFieldValue('Angle-message');
//     // TODO: change Order.ATOMIC to the correct operator precedence strength
//     const value_name = generator.valueToCode(block, 'NAME', Order.ATOMIC);
//
//     // TODO: Assemble python into the code variable.
//     const code = '...';
//     return code;
// }
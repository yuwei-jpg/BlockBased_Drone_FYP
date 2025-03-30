'use strict';

Blockly.defineBlocksWithJsonArray([
    {
        "type": "start",
        "tooltip": "",
        "helpUrl": "",
        "message0": "%1 %2",
        "args0": [
            {
                "type": "field_label_serializable",
                "text": "Start Action",
                "name": "Start-message"
            },
            {
                "type": "input_dummy",
                "name": "NAME"
            }
        ],
        "previousStatement": null,
        "nextStatement": null,
        "colour": 225
        // 'style':'start_action'
    }
]);


Blockly.defineBlocksWithJsonArray([
    {
        "type": "offboard",
        "tooltip": "",
        "helpUrl": "",
        "message0": "%1 %2",
        "args0": [
            {
                "type": "field_label_serializable",
                "text": "Start Offboard",
                "name": "offboard"
            },
            {
                "type": "input_end_row",
                "name": "NAME"
            }
        ],
        "previousStatement": null,
        "nextStatement": null,
        "colour": 225
    }

])


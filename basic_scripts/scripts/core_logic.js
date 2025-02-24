Blockly.defineBlocksWithJsonArray([
    {
        "type": "check_altitude",
        "message0": "Detect the height of the drone %1 m",
        "args0": [
            {
                "type": "field_number",
                "name": "TARGET_ALTITUDE",
                "value": 10,
                "min": 1,  // 设置最小值，防止无效输入
                "precision": 1
            }
        ],
        "message1": "If you reach the height %1",
        "args1": [
            {
                "type": "input_statement",
                "name": "ON_REACH"
            }
        ],
        "message2": "If you don't reach the height %1",
        "args2": [
            {
                "type": "input_statement",
                "name": "ON_NOT_REACH"
            }
        ],
        "colour": 230,
        "previousStatement": null,
        "nextStatement": null,
        "tooltip": "Detect whether the drone has reached the specified height and perform corresponding operations",
        "helpUrl": ""
    }
]);

Blockly.defineBlocksWithJsonArray([
    {
        "type": "repeat_while",
        "tooltip": "",
        "helpUrl": "",
        "message0": "repeat %1 %2 do %3",
        "args0": [
            {
                "type": "field_dropdown",
                "name": "MODE",
                "options": [
                    [
                        "while",
                        "WHILE"
                    ],
                    [
                        "until",
                        "UNTIL"
                    ]
                ]
            },
            {
                "type": "input_value",
                "name": "CONDITION",
                "check": "Boolean"
            },
            {
                "type": "input_statement",
                "name": "DO"
            }
        ],
        "previousStatement": null,
        "nextStatement": null,
        "colour": 120
    }

]);


    // {
    //     "type": "repeat_while",
    //     "message0": "repeat while %1",
    //     "args0": [
    //         {
    //             "type": "input_value",
    //             "name": "CONDITION",
    //             "check": "Boolean"
    //         }
    //     ],
    //     "message1": "do %1",
    //     "args1": [
    //         {
    //             "type": "input_statement",
    //             "name": "DO"
    //         }
    //     ],
    //     "colour": 120,
    //     "tooltip": "当条件为真时重复执行",
    //     "helpUrl": "",
    //     "previousStatement": null,
    //     "nextStatement": null,
    // }

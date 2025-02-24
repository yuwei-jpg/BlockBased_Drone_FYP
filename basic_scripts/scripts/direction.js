Blockly.defineBlocksWithJsonArray([
    {
        "type": "direction",
        "tooltip": "You can use it to change your direction!",
        "helpUrl": "",
        "message0": "Move: %1 %2",
        "args0": [
            {
                "type": "field_dropdown",
                "name": "Direction-message",
                "options": [
                    [
                        "Left",
                        "Left"
                    ],
                    [
                        "Right",
                        "Right"
                    ],
                    [
                        "Up",
                        "Up"
                    ],
                    [
                        "Down",
                        "Down"
                    ],
                    [
                        "Front",
                        "Front"
                    ],
                    [
                        "Back",
                        "Back"
                    ]
                ]
            },
            {
                "type": "input_statement",
                "name": "NAME",
                "check": "Number"
            }
        ],
        "previousStatement": null,
        "nextStatement": null,
        "colour": 330
    }

])


Blockly.defineBlocksWithJsonArray([
    {
        "type": "ALL",
        "tooltip": "",
        "helpUrl": "",
        "message0": "%1 %2 shape with %3 %4",
        "args0": [
            {
                "type": "field_label_serializable",
                "text": "Fly in ",
                "name": "SHAPE"
            },
            {
                "type": "field_dropdown",
                "name": "shape2",
                "options": [
                    [
                        "L",
                        "L"
                    ],
                    [
                        "S",
                        "S"
                    ],
                    [
                        "circle",
                        "C"
                    ],
                    [
                        "square",
                        "SQUA"
                    ],
                    [
                        "triangle",
                        "TRI"
                    ]
                ]
            },
            {
                "type": "field_dropdown",
                "name": "csv_file",
                "options": [
                    [
                        "l_shape_trajectory.csv",
                        "l"
                    ],
                    [
                        "s_shape_trajectory.csv",
                        "s"
                    ],
                    [
                        "circle_trajectory.csv",
                        "circle"
                    ],
                    [
                        "square_trajectory.csv",
                        "square"
                    ],
                    [
                        "triangle_trajectory.csv",
                        "triangle"
                    ]
                ]
            },
            {
                "type": "input_dummy",
                "name": "NAME"
            }
        ],
        "previousStatement": null,
        "nextStatement": null,
        "colour": '#78677A'
    }
])
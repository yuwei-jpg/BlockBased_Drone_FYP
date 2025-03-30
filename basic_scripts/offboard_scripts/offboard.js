Blockly.defineBlocksWithJsonArray([

    {
        "type": "acceleration",
        "tooltip": "",
        "helpUrl": "",
        "message0": "Set acceleration  %1 north: %2 m/s² %3 east: %4 m/s² %5 down: %6 m/s² %7",
        "args0": [
            {
                "type": "input_dummy",
                "name": "NAME"
            },
            {
                "type": "field_input",
                "name": "north",
                "text": "0"
            },
            {
                "type": "input_dummy",
                "name": "NAME"
            },
            {
                "type": "field_input",
                "name": "east",
                "text": "0"
            },
            {
                "type": "input_dummy",
                "name": "NAME"
            },
            {
                "type": "field_input",
                "name": "down",
                "text": "0"
            },
            {
                "type": "input_dummy",
                "name": "NAME"
            }
        ],
        "previousStatement": null,
        "nextStatement": null,
        "colour": 90
    }

])

Blockly.defineBlocksWithJsonArray([
    {
        "type": "attitude",
        "tooltip": "Type for attitude body angles in NED reference frame (roll, pitch, yaw and thrust)",
        "helpUrl": "",
        "message0": "Set attitude %1 roll angle: %2 %3 pitch angle: %4 %5 yaw angle: %6 %7 push: %8 F %9",
        "args0": [
            {
                "type": "input_dummy",
                "name": "NAME"
            },
            {
                "type": "field_angle",
                "name": "roll",
                "angle": 90
            },
            {
                "type": "input_dummy",
                "name": "NAME"
            },
            {
                "type": "field_angle",
                "name": "pitch",
                "angle": 90
            },
            {
                "type": "input_dummy",
                "name": "NAME"
            },
            {
                "type": "field_angle",
                "name": "yaw",
                "angle": 90
            },
            {
                "type": "input_dummy",
                "name": "NAME"
            },
            {
                "type": "field_number",
                "name": "push",
                "value": 0,
                "min": 0,
                "max": 1,
                "precision": 0.001
            },
            {
                "type": "input_dummy",
                "name": "NAME"
            }
        ],
        "previousStatement": null,
        "nextStatement": null,
        "colour": 0
    }

])

Blockly.defineBlocksWithJsonArray([
    {
        "type": "attitude_rate",
        "tooltip": "Type for attitude rate commands in body coordinates (roll, pitch, yaw angular rate and thrust)",
        "helpUrl": "",
        "message0": "Set attitude speed %1 roll speed: %2 d/s %3 pitch speed: %4 d/s %5 yaw angle: %6 d/s %7 push: %8 F %9",
        "args0": [
            {
                "type": "input_dummy",
                "name": "NAME"
            },
            {
                "type": "field_input",
                "name": "roll",
                "text": "0"
            },
            {
                "type": "input_dummy",
                "name": "NAME"
            },
            {
                "type": "field_input",
                "name": "pitch",
                "text": "0"
            },
            {
                "type": "input_dummy",
                "name": "NAME"
            },
            {
                "type": "field_input",
                "name": "yaw",
                "text": "0"
            },
            {
                "type": "input_dummy",
                "name": "NAME"
            },
            {
                "type": "field_number",
                "name": "push",
                "value": 0,
                "min": 0,
                "max": 1,
                "precision": 0.001
            },
            {
                "type": "input_dummy",
                "name": "NAME"
            }
        ],
        "previousStatement": null,
        "nextStatement": null,
        "colour": 0
    }

])

Blockly.defineBlocksWithJsonArray([
    {
        "type": "position3_acc",
        "tooltip": "Set the position, velocity and acceleration in NED coordinates, with velocity and acceleration used as feed-forward.",
        "helpUrl": "",
        "message0": "Navigate to position %1 front: %2 m with speed %3 m/s and acceleration  %4 m/s² %5 right: %6 m with speed %7 m/s and acceleration  %8 m/s² %9 down: %10 m with speed %11 m/s and acceleration  %12 m/s² %13 face angle: %14 %15",
        "args0": [
            {
                "type": "input_dummy",
                "name": "NAME1"
            },
            {
                "type": "field_input",
                "name": "front",
                "text": "0"
            },
            {
                "type": "field_input",
                "name": "speed1",
                "text": "0"
            },
            {
                "type": "field_number",
                "name": "north",
                "value": 0
            },
            {
                "type": "input_dummy",
                "name": "NAME2"
            },
            {
                "type": "field_input",
                "name": "right",
                "text": "0"
            },
            {
                "type": "field_input",
                "name": "speed2",
                "text": "0"
            },
            {
                "type": "field_number",
                "name": "east",
                "value": 0
            },
            {
                "type": "input_dummy",
                "name": "NAME3"
            },
            {
                "type": "field_input",
                "name": "down",
                "text": "0"
            },
            {
                "type": "field_input",
                "name": "speed3",
                "text": "0"
            },
            {
                "type": "field_number",
                "name": "down2",
                "value": 0
            },
            {
                "type": "input_dummy",
                "name": "NAME4"
            },
            {
                "type": "field_angle",
                "name": "angle",
                "angle": 90
            },
            {
                "type": "input_dummy",
                "name": "NAME5"
            }
        ],
        "previousStatement": null,
        "nextStatement": null,
        "colour": 90
    }
])
'use strict';

Blockly.defineBlocksWithJsonArray([
    {
  "type": "takeoff",
  "tooltip": "",
  "helpUrl": "",
  "message0": "%1 %2",
  "args0": [
    {
      "type": "field_label_serializable",
      "text": "Take off with fixed altitude ",
      "name": "take-off"
    },
    {
      "type": "input_dummy",
      "name": "NAME"
    }
  ],
  "previousStatement": null,
  "nextStatement": null,
  "colour": 150
}
])

Blockly.defineBlocksWithJsonArray([
  {
  "type": "takeoff2",
  "tooltip": "",
  "helpUrl": "",
  "message0": "Take off %1m %2",
  "args0": [
    {
      "type": "field_number",
      "name": "altitude",
      "value": 0
    },
    {
      "type": "input_end_row",
      "name": "NAME"
    }
  ],
  "previousStatement": null,
  "nextStatement": null,
  "colour": 150
}

])

Blockly.defineBlocksWithJsonArray([
   {
  "type": "hold",
  "tooltip": "",
  "helpUrl": "",
  "message0": "%1 %2",
  "args0": [
    {
      "type": "field_label_serializable",
      "text": "Hold",
      "name": "hold"
    },
    {
      "type": "input_end_row",
      "name": "NAME"
    }
  ],
  "previousStatement": null,
  "nextStatement": null,
  "colour": 150
}

])

Blockly.defineBlocksWithJsonArray([
{
  "type": "orbit",
  "tooltip": "",
  "helpUrl": "",
  "message0": "%1 %2 radius: %3 m %4 velocity: %5 m/s %6 wait:  %7 %8",
  "args0": [
    {
      "type": "field_label_serializable",
      "text": "Fly in a circle",
      "name": "orbit"
    },
    {
      "type": "input_end_row",
      "name": "NAME"
    },
    {
      "type": "input_value",
      "name": "radius",
      "check": "Number"
    },
    {
      "type": "input_end_row",
      "name": "NAME"
    },
     {
      "type": "input_value",
      "name": "velocity",
      "check": "Number"  // 确保只接受数字类型
    },
    {
      "type": "input_end_row",
      "name": "NAME"
    },
    {
      "type": "field_checkbox",
      "name": "NAME",
      "checked": "TRUE"
    },
    {
      "type": "input_end_row",
      "name": "NAME"
    }
  ],
  "previousStatement": null,
  "nextStatement": null,
  "colour": 150
}


])

Blockly.defineBlocksWithJsonArray([
    {
  "type": "go_to_location",
  "tooltip": "",
  "helpUrl": "longitude 180 latitudes 90 means go to north",
  "message0": "Go to location %1 forward:  %2 m %3 turn right: %4 m %5 go up: %6 m %7 ",
  "args0": [
    {
      "type": "input_dummy",
      "name": "input1"
    },
    {
      "type": "field_number",
      "name": "longitude",
      "value": 0,
      "precision": 0.001
    },
    {
      "type": "input_dummy",
      "name": "input2"
    },
    {
      "type": "field_number",
      "name": "latitude",
      "value": 0,
      "precision": 0.001
    },
    {
      "type": "input_dummy",
      "name": "input3"
    },
    {
      "type": "field_number",
      "name": "altitude",
      "value": 0,
      "precision": 0.001
    },

    {
      "type": "input_dummy",
      "name": "input4"
    }
  ],
  "previousStatement": null,
  "nextStatement": null,
  "colour": 150
}

])
Blockly.defineBlocksWithJsonArray([
    {
  "type": "sleep",
  "tooltip": "",
  "helpUrl": "",
  "message0": "Sleep for %1 s %2",
  "args0": [
    {
      "type": "field_number",
      "name": "sleep",
      "value": 0
    },
    {
      "type": "input_end_row",
      "name": "NAME"
    }
  ],
  "previousStatement": null,
  "nextStatement": null,
  "colour": 345
}


])
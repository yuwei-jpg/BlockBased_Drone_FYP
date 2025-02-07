Blockly.defineBlocksWithJsonArray([
{
  "type": "setCurrentSpeed",
  "tooltip": "",
  "helpUrl": "",
  "message0": "%1 %2 %3",
  "args0": [
    {
      "type": "field_label_serializable",
      "text": "set current speed:",
      "name": "LABEL"
    },
    {
        "type": "input_value",  // 改为 input_value
        "name": "SPEED",
        "check": "Number"  // 确保只接受数字类型
    },
    {
      "type": "field_label_serializable",
      "text": "m/s",
      "name": "UNIT"
    }
  ],
  "previousStatement": null,
  "nextStatement": null,
  "colour": 180
}
])

Blockly.defineBlocksWithJsonArray([
{
  "type": "setMaxSpeed",
  "tooltip": "",
  "helpUrl": "",
  "message0": "set maximum speed: %1 %2 %3",
  "args0": [
    {
      "type": "input_value",
      "name": "maxSpeed",
      "check": "Number"  // 确保只接受数字类型
    },
    {
      "type": "field_label_serializable",
      "text": "m/s",
      "name": "LABEL"
    },
    {
      "type": "input_dummy",
      "name": "NAME"
    }
  ],
  "previousStatement": null,
  "nextStatement": null,
  "colour": 180
}

])

Blockly.defineBlocksWithJsonArray([
{
  "type": "returnToLaunch",
  "tooltip": "Send command to return to the launch (takeoff) position and land.",
  "helpUrl": "",
  "message0": "return to launch %1",
  "args0": [
    {
      "type": "input_dummy",
      "name": "NAME"
    }
  ],
  "previousStatement": null,
  "nextStatement": null,
  "colour": 225
}
])

Blockly.defineBlocksWithJsonArray([
    {
  "type": "returnToLaunchAltitude",
  "tooltip": "Set the return to launch minimum return altitude (in meters).",
  "helpUrl": "",
  "message0": "set altitude relative to takeoff location %1 m %2",
  "args0": [
    {
      "type": "input_value",
      "name": "Altitude",
      "check": "Number"  // 确保只接受数字类型
    },
    {
      "type": "input_dummy",
      "name": "NAME"
    }
  ],
  "colour": 180,
      "previousStatement": null,
  "nextStatement": null,
}

    ]

)
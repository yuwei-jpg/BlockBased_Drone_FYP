Blockly.defineBlocksWithJsonArray([
  {
    "type": "Spiral_Upward",
    "message0": "Spiral Ascend %1 time step %2 mode %3 ",
    "args0": [
      {
        "type": "input_dummy",
        "name": "DUMMY_HEAD"
      },
      {
        "type": "input_value",
        "name": "RADIUS_INPUT",
        "check": "child_block"
      },
      {
        "type": "input_value",
        "name": "SPEED_INPUT",
        "check": "child_block"
      },
      // {
      //   "type": "input_value",
      //   "name": "ANGLE_INPUT",
      //   "check": "child_block"
      // }
    ],
    "colour": '#78677A',
    "previousStatement": null,
    "nextStatement": null,
    "init": function() {
      // Initialize the default connection
      ['RADIUS_INPUT', 'SPEED_INPUT'].forEach(input => {
        const child = Blockly.serialization.blocks.append(
          {
            "type": "child_block",
            "fields": {"VALUE": this.getDefaultValue(input)}
          },
          this.workspace
        );
        this.getInput(input).connection.connect(child.outputConnection);
      });
    }
  },
  {
    "type": "child_block",
    "message0": "%1",
    "args0": [
      {
        "type": "field_number",
        "name": "VALUE",
        "value": 0
      }
    ],
    "output": "child_block",
    "colour": '#BEA8AA',
    "movable": false,
    "deletable": false
  }
]);

// Set the default value
Blockly.Blocks['Spiral_Upward'].getDefaultValue = function(inputName) {
  return {
    'RADIUS_INPUT': 0.1,
    'SPEED_INPUT': 0
  }[inputName] || 0;
};

// Connection protection logic
Blockly.Blocks['Spiral_Upward'].onchange = function(event) {
  if (event instanceof Blockly.Events.BlockMove) {
    ['RADIUS_INPUT', 'SPEED_INPUT'].forEach(input => {
      const conn = this.getInput(input).connection;
      if (!conn.targetConnection) {
        const child = Blockly.serialization.blocks.append(
          {
            "type": "child_block",
            "fields": {"VALUE": this.getFieldValue(input) || this.getDefaultValue(input)}
          },
          this.workspace
        );
        conn.connect(child.outputConnection);
      }
    });
  }
};



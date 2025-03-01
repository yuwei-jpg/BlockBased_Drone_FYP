Blockly.defineBlocksWithJsonArray([
  {
    "type": "Roller_Coaster",
    "message0": "Roller Coaster %1 time step %2 mode %3",
    "args0": [
      {
        "type": "input_dummy",
        "name": "DUMMY_HEAD"
      },
      {
        "type": "input_value",
        "name": "TIME_STEP",
        "check": "child_block"
      },
      {
        "type": "input_value",
        "name": "MODE",
        "check": "child_block"
      }
    ],
    "colour": '#78677A',
    "previousStatement": null,
    "nextStatement": null,
    "init": function() {
      // Initialize the default connection
      ['TIME_STEP', 'MODE'].forEach(input => {
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
Blockly.Blocks['Roller_Coaster'].getDefaultValue = function(inputName) {
  return {
    'TIME_STEP': 0.1,
    'MODE': 0
  }[inputName] || 0;
};

// Connection protection logic
Blockly.Blocks['Roller_Coaster'].onchange = function(event) {
  if (event instanceof Blockly.Events.BlockMove) {
    ['TIME_STEP', 'MODE'].forEach(input => {
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
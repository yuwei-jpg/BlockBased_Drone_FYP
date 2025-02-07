Blockly.defineBlocksWithJsonArray([
  {
    "type": "Spiral_Upward",
    "message0": "Spiral Ascend %1 radius %2 speed %3 angle speed %4",
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
      {
        "type": "input_value",
        "name": "ANGLE_INPUT",
        "check": "child_block"
      }
    ],
    "colour": '#78677A',
    "previousStatement": null,
    "nextStatement": null,
    "init": function() {
      // 初始化默认连接
      ['RADIUS_INPUT', 'SPEED_INPUT', 'ANGLE_INPUT'].forEach(input => {
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

// 设置默认值方法
Blockly.Blocks['Spiral_Upward'].getDefaultValue = function(inputName) {
  return {
    'RADIUS_INPUT': 5.0,
    'SPEED_INPUT': 3.0,
    'ANGLE_INPUT': 8.0
  }[inputName] || 0;
};

// 连接保护逻辑
Blockly.Blocks['Spiral_Upward'].onchange = function(event) {
  if (event instanceof Blockly.Events.BlockMove) {
    ['RADIUS_INPUT', 'SPEED_INPUT', 'ANGLE_INPUT'].forEach(input => {
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



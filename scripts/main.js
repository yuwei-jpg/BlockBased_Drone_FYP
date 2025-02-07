let workspace = null;
Blockly.Themes.Halloween = Blockly.Theme.defineTheme('halloween', {
   'base': Blockly.Themes.Classic,
   'categoryStyles': {
       'core': {
      'colour': '#a5745b',
    },
       'Control': {
      'colour': '#745ba5',
    },
       'Logic': {
      'colour': '#5b80a5',
    },
       'Action': {
      'colour': '#5ba55b',
    },
       'Direction': {
      'colour': '#5b432b',
    },
       'Position': {
      'colour': '#8D91AA',
    },
       'Movement': {
           'colour': '#78677A',
       },
       'Example': {
           'colour': '#696969',
       },
       'End': {
      'colour': '#5b122b',
    },
    },
    "blockStyles": {
       'start_action': {
           colourPrimary: '#5b80a5',
           colourSecondary: '#bdccdb',
           colourTertiary: '#496684',
       // 'colourPrimary': '#a5745b',
       // 'colourSecondary': '#dbc7bd',
       // 'colourTertiary': '#845d49',
       'hat': 'cap'

    },
    position: {
      colourPrimary: '#745ba5',
      colourSecondary: '#c7bddb',
      colourTertiary: '#5d4984',
    },
    start_end: {
      colourPrimary: '#5b80a5',
      colourSecondary: '#bdccdb',
      colourTertiary: '#496684',
    },
    loop_blocks: {
      colourPrimary: '#5ba55b',
      colourSecondary: '#bddbbd',
      colourTertiary: '#498449',
    },
    math_blocks: {
      colourPrimary: '#5b67a5',
      colourSecondary: '#bdc2db',
      colourTertiary: '#495284',
    },
    procedure_blocks: {
      colourPrimary: '#995ba5',
      colourSecondary: '#d6bddb',
      colourTertiary: '#7a4984',
    },
    text_blocks: {
      colourPrimary: '#5ba58c',
      colourSecondary: '#bddbd1',
      colourTertiary: '#498470',
    },
    },
 'componentStyles': {
    'workspaceBackgroundColour': '#EFF0EA',          // Light beige-gray for a warm, neutral workspace
    'toolboxBackgroundColour': '#C4BAB1',            // Soft brown-gray for the toolbox
    'toolboxForegroundColour': '#161D15',            // Darker muted brown for toolbox text
    'flyoutBackgroundColour': '#d6ccc2',             // Slightly lighter beige for the flyout background
    'flyoutForegroundColour': '#4e4b46',             // Muted dark gray for flyout block text
    'flyoutOpacity': 0.95,                           // Slight transparency for softness
    'scrollbarColour': '#a39b8b',                    // Soft taupe for the scrollbar
    'insertionMarkerColour': '#9b8e7e',              // Warm gray for insertion marker
    'insertionMarkerOpacity': 0.4,                   // Soft transparency to blend well
    'scrollbarOpacity': 0.5,                         // Semi-transparent scrollbar for a subtle look
    'cursorColour': '#5e5348',                       // Muted, dark beige for cursor
    'blackBackground': '#9a9189'                     // Muted dark taupe for contrast in darker areas
}


});
function work()
{

    // Create the main workspace.
    workspace = Blockly.inject('blocklyDiv', {
        toolbox: document.getElementById('toolbox'),
        theme:Blockly.Themes.Halloween,
        maxTrashcanContents: 20,  // 设置垃圾桶最大容量
        zoom: {
            controls: true,
            wheel: true,
            startScale: 1.0,
            maxScale: 3,
            minScale: 0.3,
            scaleSpeed: 1.2
        },
        trashcan: true,
        move: {
            scrollbars: true,
            drag: true,
            wheel: true
        },
        grid: {
        spacing: 20,
        length: 3,
        colour: '#ccc',
        snap: true
        }
    });
    fetchReadme();
    loadBlocksFromLocalStorage()

    document.querySelector('.tab button.active').click();
    Blockly.svgResize(workspace);

    //  // 更新 Toolbox，确保 Example 类别生效
    // const toolbox = document.getElementById("toolbox");
    // workspace.updateToolbox(toolbox);

    workspace.addChangeListener((event) => {
        if (event.type !== Blockly.Events.BLOCK_CHANGE && event.type !== Blockly.Events.BLOCK_CREATE) {
            return;
        }
        console.log("Blocks changed, but not saving automatically.");
    });
}
/*--------------------------------------------------------------------------------------------------------*/

// function saveBlocksToLocalStorage() {
//     const workspaceXml = Blockly.Xml.workspaceToDom(workspace);
//     const workspaceXmlText = Blockly.Xml.domToText(workspaceXml);
//
//     localStorage.setItem("savedExampleBlocks", workspaceXmlText);
//     console.log("Blocks saved to localStorage!");
//
//     loadBlocksFromLocalStorage()
// }
function saveBlocksToLocalStorage() {
    // 获取当前 `Workspace` 的 XML
    const workspaceXml = Blockly.Xml.workspaceToDom(workspace);
    const workspaceXmlText = Blockly.Xml.domToText(workspaceXml);

    // **获取 `localStorage` 里已有的块**
    let existingBlocks = localStorage.getItem("savedExampleBlocks");

    // **如果已有数据，则合并新块和旧块**
    if (existingBlocks) {
        const parser = new DOMParser();
        const existingXml = parser.parseFromString(existingBlocks, "text/xml");

        // **将新块追加到已有的 XML**
        while (workspaceXml.firstChild) {
            existingXml.documentElement.appendChild(workspaceXml.firstChild);
        }

        // **转换回字符串**
        const updatedXmlText = new XMLSerializer().serializeToString(existingXml);
        localStorage.setItem("savedExampleBlocks", updatedXmlText);
    } else {
        // **如果 `localStorage` 为空，直接存入**
        localStorage.setItem("savedExampleBlocks", workspaceXmlText);
    }

    console.log("All blocks saved to localStorage!");
}
function loadBlocksFromLocalStorage() {
    const savedBlocks = localStorage.getItem("savedExampleBlocks");
    if (!savedBlocks) {
        console.log("No saved blocks found for Example category.");
        return;
    }

    const toolbox = document.getElementById("toolbox");
    const exampleCategory = toolbox.querySelector("category[name='Example']");

    if (!exampleCategory) {
        console.error("Example category not found in Toolbox!");
        return;
    }

    // **解析 XML 并去掉 `<xml>` 标签**
    let parser = new DOMParser();
    let xmlDoc = parser.parseFromString(savedBlocks, "text/xml");

    let xmlElement = xmlDoc.documentElement;
    if (xmlElement.nodeName === "xml") {
        xmlElement = xmlElement.firstElementChild; // 取出真正的 <block> 结构
    }

    while (xmlElement) {
        exampleCategory.appendChild(xmlElement.cloneNode(true));
        xmlElement = xmlElement.nextElementSibling;
    }

    console.log("Blocks restored to Example category in Toolbox!");

    // **刷新 Toolbox**
    workspace.updateToolbox(toolbox);
}

function deleteBlockFromLocalStorage(blockType) {
    const savedXmlText = localStorage.getItem("savedExampleBlocks");
    if (!savedXmlText) {
        console.log("No saved blocks found.");
        return;
    }

    // **解析 `localStorage` 里的 XML**
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(savedXmlText, "text/xml");

    // **查找指定类型的块**
    const blocks = xmlDoc.getElementsByTagName("block");
    for (let i = blocks.length - 1; i >= 0; i--) {
        if (blocks[i].getAttribute("type") === blockType) {
            blocks[i].parentNode.removeChild(blocks[i]); // **删除主块**
        }
    }

    // **存回 `localStorage`**
    const updatedXmlText = new XMLSerializer().serializeToString(xmlDoc);
    localStorage.setItem("savedExampleBlocks", updatedXmlText);
    console.log(`Blocks of type "${blockType}" and its children have been deleted!`);

    // **刷新 Blockly**
    loadBlocksFromLocalStorage();
}

function clearSavedBlocks() {
    localStorage.removeItem("savedExampleBlocks"); // 清空 localStorage
    console.log("All saved blocks have been deleted!");

    // **清空 Blockly 工作区**
    workspace.clear();

    // **刷新 Toolbox**
    workspace.updateToolbox(document.getElementById("toolbox"));
}

/*--------------------------------------------------------------------------------------------------------*/
let debounceTimer; // Declare debounceTimer in the outer scope

workspace.addChangeListener((event) => {
    // Filter out event types that do not need to be processed
    if (event.type !== Blockly.Events.BLOCK_CHANGE && event.type !== Blockly.Events.BLOCK_CREATE) {
        return;
    }

    // Clear the current anti-shake timer
    clearTimeout(debounceTimer);

    // Set up a new anti-shake timer
    debounceTimer = setTimeout(() => {
        generateCode().then(() => {
            console.log("Code generation complete.");
        });
    }, 300); // delay 300ms
});
/*--------------------------------------------------------------------------------------------------------*/
// Function to switch between tabs
    function openTab(evt, tabName) {
        let i, tabcontent, tablinks;
        tabcontent = document.getElementsByClassName("tabcontent");
        for (i = 0; i < tabcontent.length; i++) {
            tabcontent[i].style.display = "none";
        }
        tablinks = document.getElementsByClassName("tablinks");
        for (i = 0; i < tablinks.length; i++) {
            tablinks[i].className = tablinks[i].className.replace(" active", "");
        }
        document.getElementById(tabName).style.display = "block";
        evt.currentTarget.className += " active";
    }
/*--------------------------------------------------------------------------------------------------------*/
    // Fetch README content from a local file (adjust a path if necessary)
    function fetchReadme() {
        fetch('./README.md')
  .then((response) => {
    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }
    return response.text(); // Convert file contents to text
  })
  .then((markdownText) => {
    const converter = new showdown.Converter(); // Initialize the Showdown converter
     // Convert to HTML
      document.getElementById('readmeContent').innerHTML = converter.makeHtml(markdownText);
  })
  .catch((error) => {
    console.error('Error fetching README.md:', error);
    document.getElementById('readmeContent').innerHTML =
      '<h1>Failed to load README</h1>';
  });
    }
/*--------------------------------------------------------------------------------------------------------*/
    // // Set the default open tab
    // document.getElementById("defaultOpen").click();

// 添加窗口大小改变事件监听
window.addEventListener('resize', function() {
    if (workspace) {
        Blockly.svgResize(workspace);
    }
});
/*--------------------------------------------------------------------------------------------------------*/
// 初始化时设置工作区大小
// 确保在 DOM 加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    work();
    // 自定义右键菜单
    Blockly.ContextMenuRegistry.registry.register({
        id: "save_block",
        weight: 100, // 确保权重高于默认菜单项
        scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK, // 作用域为块
        displayText: "Save this block", // 菜单项的显示文本
        preconditionFn: function(scope) {
            return "enabled"; // 预条件函数，返回菜单项是否可用
        },
        callback: function(scope) {
            let block = scope.block; // 获取当前块
            let blockXml = Blockly.Xml.blockToDom(block); // 将块转换为 XML
            let blockText = Blockly.Xml.domToText(blockXml); // 将 XML 转换为文本

            // 从 localStorage 获取已有的块
            let savedBlocks = localStorage.getItem("savedExampleBlocks") || "";
            // 将新块追加到 localStorage
            localStorage.setItem("savedExampleBlocks", savedBlocks + blockText);
            console.log("Block saved!"); // 打印保存成功的消息
        }
    });
});




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
        'workspaceBackgroundColour': '#EFF0EA',          // Light beige-gray for a warm, neutral workspace #ffffff '#EFF0EA'
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

function work() {
    // Create the main workspace.
    workspace = Blockly.inject('blocklyDiv', {
        toolbox: document.getElementById('toolbox'),
        theme: Blockly.Themes.Halloween,
        maxTrashcanContents: 20,
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

    //  // Update Toolbox to ensure that the Example category is effective
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

//     const toggleBtn = document.getElementById('toggleButton');
//     toggleBtn.addEventListener('click', () => {
//       const panel = document.getElementById('rightPanel');
//       panel.classList.toggle('open');
//       if (panel.classList.contains('open')) {
//         toggleBtn.textContent = '>';
//       } else {
//         toggleBtn.textContent = '<';
//       }
//     });

// document.getElementById('toggleButton').addEventListener('click', function() {
// const panel = document.getElementById('rightPanel');
// panel.classList.toggle('open');
//
// if (Blockly.getMainWorkspace()) {
//     Blockly.getMainWorkspace().resize();
// }
// });
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
    const workspaceXml = Blockly.Xml.workspaceToDom(workspace);
    const workspaceXmlText = Blockly.Xml.domToText(workspaceXml);

    let existingBlocks = localStorage.getItem("savedExampleBlocks");

    if (existingBlocks) {
        const parser = new DOMParser();
        const existingXml = parser.parseFromString(existingBlocks, "text/xml");

        while (workspaceXml.firstChild) {
            existingXml.documentElement.appendChild(workspaceXml.firstChild);
        }

        const updatedXmlText = new XMLSerializer().serializeToString(existingXml);
        localStorage.setItem("savedExampleBlocks", updatedXmlText);
    } else {
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

    let parser = new DOMParser();
    let xmlDoc = parser.parseFromString(savedBlocks, "text/xml");

    let xmlElement = xmlDoc.documentElement;
    if (xmlElement.nodeName === "xml") {
        xmlElement = xmlElement.firstElementChild;
    }

    while (xmlElement) {
        exampleCategory.appendChild(xmlElement.cloneNode(true));
        xmlElement = xmlElement.nextElementSibling;
    }

    console.log("Blocks restored to Example category in Toolbox!");

    workspace.updateToolbox(toolbox);
}

function deleteBlockFromLocalStorage(blockType) {
    const savedXmlText = localStorage.getItem("savedExampleBlocks");
    if (!savedXmlText) {
        console.log("No saved blocks found.");
        return;
    }

    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(savedXmlText, "text/xml");

    const blocks = xmlDoc.getElementsByTagName("block");
    for (let i = blocks.length - 1; i >= 0; i--) {
        if (blocks[i].getAttribute("type") === blockType) {
            blocks[i].parentNode.removeChild(blocks[i]); //
        }
    }

    const updatedXmlText = new XMLSerializer().serializeToString(xmlDoc);
    localStorage.setItem("savedExampleBlocks", updatedXmlText);
    console.log(`Blocks of type "${blockType}" and its children have been deleted!`);

    loadBlocksFromLocalStorage();
}

function clearSavedBlocks() {
    localStorage.removeItem("savedExampleBlocks");
    console.log("All saved blocks have been deleted!");

    workspace.clear();

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

window.addEventListener('resize', function () {
    if (workspace) {
        Blockly.svgResize(workspace);
    }
});
/*--------------------------------------------------------------------------------------------------------*/

document.addEventListener('DOMContentLoaded', function () {
    work();
    Blockly.ContextMenuRegistry.registry.register({
        id: "save_block",
        weight: 100,
        scopeType: Blockly.ContextMenuRegistry.ScopeType.BLOCK,
        displayText: "Save this block",
        preconditionFn: function (scope) {
            return "enabled";
        },
        callback: function (scope) {
            let block = scope.block;
            let blockXml = Blockly.Xml.blockToDom(block);
            let blockText = Blockly.Xml.domToText(blockXml);
            let savedBlocks = localStorage.getItem("savedExampleBlocks") || "";
            localStorage.setItem("savedExampleBlocks", savedBlocks + blockText);
            console.log("Block saved!");
        }
    });
});


/*--------------------------------------------------------------------------------------------------------*/

function linspace(start, stop, num) {
    const arr = [];
    const step = (stop - start) / (num - 1);
    for (let i = 0; i < num; i++) {
        arr.push(start + step * i);
    }
    return arr;
}

function buildCSV(t_values, px_values, py_values, pz_values) {
    const total_points = t_values.length;
    const vx = new Array(total_points).fill(0);
    const vy = new Array(total_points).fill(0);
    const vz = new Array(total_points).fill(0);
    const yaw = new Array(total_points).fill(0);
    const mode = new Array(total_points).fill(70);
    let csvContent = "idx,t,px,py,pz,vx,vy,vz,ax,ay,az,yaw,mode\n";
    for (let i = 0; i < total_points; i++) {
        const row = [
            i,
            t_values[i],
            px_values[i],
            py_values[i],
            pz_values[i],
            vx[i],
            vy[i],
            vz[i],
            0, // ax
            0, // ay
            0, // az
            yaw[i],
            mode[i]
        ];
        csvContent += row.join(",") + "\n";
    }
    return csvContent;
}

function generateZShapeData() {
    const z_length = 20, z_width = 10, num_segments = 3, points_per_segment = 50;
    const total_points = num_segments * points_per_segment;
    const duration = 20, z_height = 5;
    const t_values = linspace(0, duration, total_points);
    let px_values = [];
    px_values = px_values.concat(linspace(0, z_width, points_per_segment));
    px_values = px_values.concat(linspace(z_width, 0, points_per_segment));
    px_values = px_values.concat(linspace(0, z_width, points_per_segment));
    let py_values = [];
    py_values = py_values.concat(linspace(0, z_length / 2, points_per_segment));
    py_values = py_values.concat(linspace(z_length / 2, z_length, points_per_segment));
    py_values = py_values.concat(linspace(z_length, z_length * 1.5, points_per_segment));
    const pz_values = new Array(total_points).fill(-z_height);
    const csv = buildCSV(t_values, px_values, py_values, pz_values);
    return {csv: csv, x: px_values, y: py_values};
}

function generateSShapeData() {
    const total_points = 150, duration = 20, amplitude = 5, length = 30;
    const t_values = linspace(0, duration, total_points);
    const py_values = linspace(0, length, total_points);
    const px_values = t_values.map(t => amplitude * Math.sin((2 * Math.PI / duration) * t));
    const pz_values = new Array(total_points).fill(-5);
    const csv = buildCSV(t_values, px_values, py_values, pz_values);
    return {csv: csv, x: px_values, y: py_values};
}

function generateLShapeData() {
    const points_horizontal = 75, points_vertical = 75;
    const total_points = points_horizontal + points_vertical;
    const duration = 20, line_length = 20;
    const t_values = linspace(0, duration, total_points);
    const px_horizontal = linspace(0, line_length, points_horizontal);
    const py_horizontal = new Array(points_horizontal).fill(0);
    const px_vertical = new Array(points_vertical).fill(line_length);
    const py_vertical = linspace(0, line_length, points_vertical);
    const px_values = px_horizontal.concat(px_vertical);
    const py_values = py_horizontal.concat(py_vertical);
    const pz_values = new Array(total_points).fill(-5);
    const csv = buildCSV(t_values, px_values, py_values, pz_values);
    return {csv: csv, x: px_values, y: py_values};
}

function generateCircleData() {
    const total_points = 200, duration = 20, radius = 10;
    const t_values = linspace(0, duration, total_points);
    const theta_values = linspace(0, 2 * Math.PI, total_points);
    const px_values = theta_values.map(theta => radius * Math.cos(theta));
    const py_values = theta_values.map(theta => radius * Math.sin(theta));
    const pz_values = new Array(total_points).fill(-5);
    const csv = buildCSV(t_values, px_values, py_values, pz_values);
    return {csv: csv, x: px_values, y: py_values};
}

function generateSquareData() {
    const points_per_side = 50;
    const total_points = points_per_side * 4;
    const duration = 20, side = 20;
    const t_values = linspace(0, duration, total_points);
    let px_values = [];
    let py_values = [];
    px_values = px_values.concat(linspace(0, side, points_per_side));
    py_values = py_values.concat(new Array(points_per_side).fill(0));
    px_values = px_values.concat(new Array(points_per_side).fill(side));
    py_values = py_values.concat(linspace(0, side, points_per_side));
    px_values = px_values.concat(linspace(side, 0, points_per_side));
    py_values = py_values.concat(new Array(points_per_side).fill(side));
    px_values = px_values.concat(new Array(points_per_side).fill(0));
    py_values = py_values.concat(linspace(side, 0, points_per_side));
    const pz_values = new Array(total_points).fill(-5);
    const csv = buildCSV(t_values, px_values, py_values, pz_values);
    return {csv: csv, x: px_values, y: py_values};
}

function generateTriangleData() {
    const points_per_side = 50;
    const total_points = points_per_side * 3;
    const duration = 20, side = 20;
    const t_values = linspace(0, duration, total_points);
    let px_values = [];
    let py_values = [];
    const A = {x: 0, y: 0};
    const B = {x: side, y: 0};
    const C = {x: side / 2, y: side * Math.sin(Math.PI / 3)};
    px_values = px_values.concat(linspace(A.x, B.x, points_per_side));
    py_values = py_values.concat(linspace(A.y, B.y, points_per_side));
    px_values = px_values.concat(linspace(B.x, C.x, points_per_side));
    py_values = py_values.concat(linspace(B.y, C.y, points_per_side));
    px_values = px_values.concat(linspace(C.x, A.x, points_per_side));
    py_values = py_values.concat(linspace(C.y, A.y, points_per_side));
    const pz_values = new Array(total_points).fill(-5);
    const csv = buildCSV(t_values, px_values, py_values, pz_values);
    return {csv: csv, x: px_values, y: py_values};
}


function drawCSVShape(xArray, yArray) {
  const canvas = document.getElementById('csvPreviewCanvas');
  const ctx = canvas.getContext('2d');
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  const minX = Math.min(...xArray);
  const maxX = Math.max(...xArray);
  const minY = Math.min(...yArray);
  const maxY = Math.max(...yArray);
  const padding = 20;
  const scaleX = (canvas.width - 2 * padding) / (maxX - minX);
  const scaleY = (canvas.height - 2 * padding) / (maxY - minY);
  const scale = Math.min(scaleX, scaleY);

  function transformX(x) {
    return padding + (x - minX) * scale;
  }
  function transformY(y) {
    return canvas.height - padding - (y - minY) * scale;
  }

  /*
  ctx.beginPath();
  ctx.lineWidth = 2;
  ctx.strokeStyle = "#e74c3c";
  ctx.moveTo(transformX(xArray[0]), transformY(yArray[0]));
  for (let i = 1; i < xArray.length; i++) {
    ctx.lineTo(transformX(xArray[i]), transformY(yArray[i]));
  }
  ctx.stroke();
  */

  const transformedPoints = xArray.map((x, i) => {
    return { x: transformX(x), y: transformY(yArray[i]) };
  });

  animateDroneMovement(transformedPoints);
}


function animateDroneMovement(points) {
  let currentPointIndex = 0;
  let drawnPoints = [];

  let svg = document.getElementById('droneSVG');
  if (!svg) {
    svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttribute("id", "droneSVG");
    svg.style.position = "absolute";
    svg.style.left = "0";
    svg.style.top = "0";
    svg.style.width = "100%";
    svg.style.height = "100%";
    document.getElementById('csvPreviewContainer').appendChild(svg);
  }
  let polyline = document.getElementById('droneTrail');
  if (!polyline) {
    polyline = document.createElementNS("http://www.w3.org/2000/svg", "polyline");
    polyline.setAttribute("id", "droneTrail");
    polyline.setAttribute("fill", "none");
    polyline.setAttribute("stroke", "#e74c3c");
    polyline.setAttribute("stroke-width", "2");
    svg.appendChild(polyline);
  }

  let droneImage = document.getElementById('droneImage');
  if (!droneImage) {
    droneImage = document.createElement('img');
    droneImage.setAttribute("id", "droneImage");
    droneImage.src = "drone.png";
    droneImage.style.position = "absolute";
    droneImage.style.width = "50px";
    droneImage.style.height = "50px";
    droneImage.style.opacity = "0";
    document.getElementById('csvPreviewContainer').appendChild(droneImage);
  }

  const drawInterval = setInterval(() => {
    if (currentPointIndex < points.length) {
      const currentPoint = points[currentPointIndex];

      if (currentPointIndex === 0) {
        droneImage.style.opacity = "1";
      }

      droneImage.style.left = `${currentPoint.x - 25}px`;
      droneImage.style.top = `${currentPoint.y - 25}px`;
      droneImage.style.transition = "all 1s ease";

      setTimeout(() => {
        drawnPoints.push(currentPoint);
        const pointsStr = drawnPoints.map(p => `${p.x},${p.y}`).join(" ");
        polyline.setAttribute("points", pointsStr);
      }, 30);

      currentPointIndex++;
    } else {
      clearInterval(drawInterval);
    }
  }, 450);
}

function handleCSV() {
    const shapeInput = document.getElementById('csvShape').value.trim().toLowerCase();
    let data;
    switch (shapeInput) {
        case "z shape":
            data = generateZShapeData();
            break;
        case "s shape":
            data = generateSShapeData();
            break;
        case "l shape":
            data = generateLShapeData();
            break;
        case "circle":
            data = generateCircleData();
            break;
        case "square":
            data = generateSquareData();
            break;
        case "triangle":
            data = generateTriangleData();
            break;
        default:
            alert("Not Supported!");
            return;
    }

    const blob = new Blob([data.csv], {type: 'text/csv;charset=utf-8;'});
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = shapeInput + "_trajectory.csv";
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    document.getElementById('csvResult').textContent = "CSV file generation completed, downloading...";
    drawCSVShape(data.x, data.y);
}


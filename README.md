## System Guidelines 

### ✨ Core Features
- **Visual Programming**: Drag-and-drop blocks for flight logic construction
- **Dual Control Modes**: Combine basic action blocks with CSV path generation
- **Real-Time Simulation**: Integrated jMAVSim/PX4 3D flight visualization
- **Education-First Design**: Built-in error handling with a focus on logic building
- **Industrial Compatibility**: MAVLink protocol support for real drones


### 📚 Usage Examples

  <img src="html_page/usage1.png" alt="Image 1" style="max-width: 100px;" />

## 🚀 Installation Guide
### Prerequisites
- macOS/Linux/Windows
- Python 3.11+
- Node.js 18+
- PX4 Autopilot v1.15
### Quick Setup
```bash
# 1. Clone repository
git clone https://github.com/yuwei-jpg/BlockBased_Drone_FYP.git
```
```bash
# 2. Open the HTML page and build your path(for macOS or Linux)
cd BlockBased_Drone_FYP
npx serve . -l 63342  
# open http://localhost:63342 on Google and click the html_page/
```
```bash
# 3. Clone or download the PX4-Autopilot repository
git clone https://github.com/PX4/PX4-Autopilot.git
```
```bash
# 4. Set up the PX4-Autopilot environment
cd PX4-Autopilot
git submodule update --init --recursive
```
```bash
# 5. Check the simulator environment
make px4_sitl jmavsim
```
```bash
# 6. Start the server
cd BlockBased_Drone_FYP/main_scripts
node server.js

# 7. Build up your blocks on the website and click the Run Simulator button
```

## ❓ FAQ
### How to solve the [qt library issue](https://github.com/PX4/PX4-Autopilot/issues/19146) when opening the simulator? 
```bash
brew uninstall --ignore-dependencies QT
```
### How to solve the block connection errors?
- Check the documentation or send me the questions through email

### How to use the JMAVSim simulation environment?
- Check the [website](https://docs.px4.io/main/en/sim_jmavsim/index.html)

***
## Feedback
* Your feedback is crucial!
* Please contact yuweiji4@gmail.com if you have any problems!
***

## System Design Diagram
![System Architecture](html_page/system.png)

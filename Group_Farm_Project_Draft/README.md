# Farm Management System
### Group 2
### Modules 2 & 3 Group Project | Java GUI (Custom Swing)

---

## Overview
This is the working draft of the Farm Management System. It contains all development history, extra comments, and the seedData() fallback method used during testing. This version shows the development process.

---

## How to Run

### Prerequisites
- Java JDK 17+ installed
- Visual Studio Code with "Extension Pack for Java" (Microsoft)

### Steps
1. Open VS Code and go to **File > Open Folder** and open the folder containing this file
2. Make sure `farm_data.csv` is in the same folder
3. Open `JavaCode_GroupProject_02_WORKING_KandulaAryan.java`
4. Click the **Run** button at the top right, OR run in the terminal:
   ```
   javac JavaCode_GroupProject_02_WORKING_KandulaAryan.java
   java JavaCode_GroupProject_02_WORKING_KandulaAryan
   ```

---

## Features

| Module | What It Does |
|---|---|
| Store / Inventory | Add items, view stock, sell items, restock |
| Animal Sales | Add farm/breeder animals, sell animals, view availability |
| Services & Payments | Schedule vet/grooming services, track payments |
| Reports | Summary of inventory, animals sold, revenue |
| Save Data to File | Writes all current data back to farm_data.csv |

---

## Class Design

| Class | Purpose |
|---|---|
| `JavaCode_GroupProject_02_WORKING_KandulaAryan` | Main class, all menus, program entry point |
| `StoreItem` | Represents a store product (name, price, quantity) |
| `Animal` | Represents a farm/breeder animal for sale |
| `ServiceRecord` | Represents a scheduled service and payment status |
| `FarmTheme` | Centralized color and font constants for the UI |
| `FarmButton` | Reusable styled button with gradient and hover effect |
| `FarmBackground` | Paints the farm scene background on the splash screen |
| `FarmDialog` | Custom themed dialogs replacing standard JOptionPane |

---

## Project Files

| File | Description |
|---|---|
| `JavaCode_GroupProject_02_WORKING_KandulaAryan.java` | This file — working draft |
| `JavaCode_GroupProject_02_POLISHED_KandulaAryan.java` | Polished final version |
| `farm_data.csv` | Data file — inventory, animals, and services load from here |
| `AI_GroupProject_02_KandulaAryan.md` | AI prompts used when consulting AI tools |

---

## Notes
- This working version includes seedData() as a fallback if farm_data.csv is missing
- Place `farm_data.csv` in the same folder before running
- AI tools were used as a learning resource; all code was reviewed and understood by the group
- Developed in VS Code as required by Module 1 setup instructions

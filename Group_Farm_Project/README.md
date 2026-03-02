# Farm Management System
### Group 2
### Modules 2 & 3 Group Project | Java GUI (Custom Swing)

---

## Overview
This is the polished final version of the Farm Management System. All unnecessary comments, extra spacing, and development-only code have been removed. Data loads exclusively from farm_data.csv with no hardcoded fallback.

---

## How to Run

### Prerequisites
- Java JDK 17+ installed
- Visual Studio Code with "Extension Pack for Java" (Microsoft)

### Steps
1. Open VS Code and go to **File > Open Folder** and open the folder containing this file
2. Make sure `farm_data.csv` is in the same folder
3. Open `JavaCode_GroupProject_02_POLISHED.java`
4. Click the **Run** button at the top right, OR run in the terminal:
   ```
   javac JavaCode_GroupProject_02_POLISHED.java
   java JavaCode_GroupProject_02_POLISHED
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
| `JavaCode_GroupProject_02_POLISHED` | Main class, all menus, program entry point |
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
| `JavaCode_GroupProject_02_POLISHED.java` | This file — polished final version |
| `JavaCode_GroupProject_02_WORKING.java` | Working draft with full development history |
| `farm_data.csv` | Data file — inventory, animals, and services load from here |
| `AI_GroupProject_02_KandulaAryan.md` | AI prompts used when consulting AI tools |

---

## Notes
- farm_data.csv must be in the same folder as the Java file or the program will show a File Not Found message
- All data entered during a session can be saved by selecting "Save Data to File" from the main menu
- AI tools were used as a learning resource; all code was reviewed and understood by the group
- Developed in VS Code as required by Module 1 setup instructions

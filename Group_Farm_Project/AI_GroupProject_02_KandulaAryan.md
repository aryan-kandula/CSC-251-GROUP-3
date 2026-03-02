# AI Prompts Used – Farm Management System
## Group 2
## Modules 2 & 3 Group Project

---

These are the prompts our group used when consulting AI tools (e.g., ChatGPT / Claude) as a resource during development.
AI was used to help understand syntax, debug logic, and get examples — our group designed the structure, data model, and program flow ourselves.

---

### Prompt 1 – Getting Started with Program Structure
**What we asked:**
> "I'm building a Java program for a small farm business. It needs to track store inventory, animal sales, and veterinary services. I want to use JOptionPane for the GUI. What classes should I create to organize this, and how should the main menu work?"

**What we used it for:**
Helped us confirm our initial class design (StoreItem, Animal, ServiceRecord) and understand how a `while` loop with `showOptionDialog` creates a persistent menu.

---

### Prompt 2 – JOptionPane showOptionDialog Syntax
**What we asked:**
> "How do I use JOptionPane.showOptionDialog in Java to display a menu where the user picks one of several buttons? Show me a simple example with 3 options."

**What we used it for:**
We had only seen `showMessageDialog` and `showInputDialog` before. This helped us learn the syntax for displaying button-based menus for all 4 modules.

---

### Prompt 3 – Using ArrayList with Custom Objects
**What we asked:**
> "I have a class called StoreItem with fields name, price, and quantity. I want to store a list of StoreItems and loop through them to display each one. How do I do this in Java using ArrayList?"

**What we used it for:**
Confirmed the syntax for `ArrayList<StoreItem>`, adding items with `.add()`, and iterating with enhanced for-loops to build the inventory display.

---

### Prompt 4 – Converting ArrayList to String Array for JOptionPane
**What we asked:**
> "I have an ArrayList of Animal objects. I want to show their names in a JOptionPane.showOptionDialog dropdown. How do I convert the list to a String array?"

**What we used it for:**
Learned how to use `.stream().map().toArray()` to dynamically build the option labels for animal and service selection dialogs.

---

### Prompt 5 – Input Validation and Try-Catch
**What we asked:**
> "In Java, when I use JOptionPane.showInputDialog to get a price from the user, how do I handle the case where they type letters instead of a number? Show me a try-catch example."

**What we used it for:**
Added `try { Double.parseDouble(...) } catch (NumberFormatException e)` blocks around all numeric inputs to prevent crashes.

---

### Prompt 6 – Formatting Output with printf / String.format
**What we asked:**
> "How do I use String.format in Java to display a table-like list of items with aligned columns inside a JOptionPane message box?"

**What we used it for:**
Used `String.format("%-25s $%-8.2f Qty: %d", ...)` to make inventory and service record displays look clean and readable.

---

### Prompt 7 – Filtering a List (e.g., showing only available animals)
**What we asked:**
> "I have an ArrayList of Animal objects, each with a boolean field `sold`. How do I create a new list containing only the animals where sold is false?"

**What we used it for:**
Used this to filter available animals for the sell screen and unpaid services for the payment screen.

---

### Prompt 8 – Debugging a Logic Error
**What we asked:**
> "My JOptionPane.showOptionDialog returns -1 when the user closes the window. My program crashes after that because I try to use -1 as an array index. How do I fix this?"

**What we used it for:**
Added `if (idx < 0) return;` guards after every `showOptionDialog` call so the program handles window-close gracefully without crashing.

---

### Prompt 9 – Switch Expressions (Java 14+)
**What we asked:**
> "I've seen `switch` used with `->` arrows in Java. Is this different from the old switch statement? Can I use it in VS Code with Java 17?"

**What we used it for:**
Confirmed we could use enhanced `switch` with arrow syntax (`case 0 -> methodCall()`) for cleaner, more readable menu handling.

---

### Prompt 10 – Running Java in VS Code
**What we asked:**
> "How do I compile and run a Java file in Visual Studio Code? Do I need any extensions installed?"

**What we used it for:**
Set up our development environment – installed the 'Extension Pack for Java' from Microsoft and learned to use the Run button and the integrated terminal with `javac` and `java` commands.

---

*Note: All AI-generated code suggestions were reviewed, understood, and modified by our group to fit our specific program design. We did not copy-paste blindly — we made sure every team member understood each section.*

# Automata Translator

A graphical editor for finite automata developed in Java Swing. This project allows you to visually design automata, manage complex transitions, and handle special symbols such as empty (Epsilon) transitions.

## ✨ Features

### 🏗️ Graphical Editing
- **State Creation**: Double-click anywhere on the canvas to add a state.
- **Organization**: Drag states to rearrange your diagram.
- **Context Menu (Right-Click)**: 
  - Set a state as **Initial** or **Accepting**.
  - Delete a specific state or transition.

### 🔄 Transition Management
- **Creation**: Click on a source state and then on a target state to create a transition.
- **Symbol Normalization**: Inputs like `a b` are automatically transformed into `a, b`.
- **Multiple Transitions**: Supports multiple symbols on the same arrow.
- **Curved Transitions**: Automatically manages curved arrows for back-and-forth transitions between two states.

### ⚡ Epsilon (ε) & Special Symbols Support
- **Dedicated Button**: An "ε" button is available in input windows for easy insertion of the empty transition symbol.
- **Compatibility**: Supports the keywords `eps` and ellipsis `...`.
- **Validation**: The system prevents entering invalid strings (more than one character), except for allowed exceptions.

## 📂 Project Structure

```text
AUTOMATA-TRANSLATOR/
├── Affichage/         # Classes related to the graphical interface (GUI)
├── Automaton/         # Business logic (States, Transitions, Automaton)
├── Main.java          # Application entry point
└── bin/               # Compiled files (.class) - [Generated]
```

## 🚀 Installation and Running

### Prerequisites
- Java JDK 8 or higher installed (check with `javac -version`).

### Compilation (Windows PowerShell)
```powershell
# Create the bin folder and compile the entire project
if (!(Test-Path bin)) { mkdir bin }
javac -d bin (Get-ChildItem -Recurse *.java)
```
### Compilation (Linux / macOS)
```
# Create the bin folder if it doesn't exist and compile all Java files in the project
mkdir -p bin
javac -d bin $(find . -name "*.java")
```

### Running
```powershell
java -cp bin Main
```

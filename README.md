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
├── bin/               # Compiled files (.class) - [Generated]
├── release/           # Release JARs
│   └── AutomataTranslator-v1.0.jar
├── build-release.ps1  # Build & release script
├── LICENSE
├── Main.java          # Application entry point
└── README.md
```

## ⚙️ Installation and Running

### Prerequisites
- Java JDK 8 or higher installed (check with `javac -version`).

### Run the released JAR (recommended)

Download the latest release from the GitHub Releases page, then:
```
java -jar release\AutomataTranslator-v1.0.jar
```

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
# 📌 Automata Translator – Roadmap

This roadmap shows the planned features and improvements for **Automata Translator**, with a quick visual status.

Legend:  
![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey)  
![In Progress](https://img.shields.io/badge/In%20Progress-⏳-orange)  
![Done](https://img.shields.io/badge/Done-✅-green)

---

## Version 1.0 – Initial Release ✅
- Graphical editor for finite automata
- Support for epsilon (ε) transitions
- Context menu: initial/accepting states, delete states/transitions
- Save JAR ready for use (release/AutomataTranslator-v1.0.jar)

## Version 1.1 – Automaton Analysis & Transformation
- ![Done](https://img.shields.io/badge/Done-✅-green) Check if a word belongs to an automaton (word simulation)  
- ![In Progress](https://img.shields.io/badge/In%20Progress-⏳-orange) Convert a **regular expression** into a finite automaton  
- ![In Progress](https://img.shields.io/badge/In%20Progress-⏳-orange) Determinize (transform a non-deterministic automaton into a deterministic one)  
- ![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey) Minimize a finite automaton

## Version 1.2 – Export & Visualization
- ![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey) Download an **image of the automaton** created (PNG or SVG)  
- ![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey) Export and import automata in JSON or XML format  
- ![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey) Undo/Redo for editor actions

## Version 1.3 – Languages & Advanced Automata
- ![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey) Support **infinite-word automata** (ω-automata)  
- ![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey) Convert between different **acceptance conditions** (e.g., final states, Büchi conditions)  
- ![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey) Determine whether an automaton represents a **finite or infinite language**  
- ![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey) Display the **language represented** when manually creating an automaton

## Future Ideas / Possible Improvements
- ![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey) Animate transitions when testing a word  
- ![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey) Advanced support for non-deterministic automata with ε-transitions  
- ![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey) Automatic validation of automaton completeness and consistency  
- ![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey) Suggestion system to fix invalid automata  
- ![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey) “Teacher mode” to show step-by-step why a word is accepted or rejected  
- ![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey) Support for **pushdown automata (PDA)** for more complex languages  
- ![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey) Customizable graphical themes for the canvas  
- ![To Do](https://img.shields.io/badge/To%20Do-🔲-lightgrey) Version history for automata to compare modifications

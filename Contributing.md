# Development

If you are looking to get involved improving this project, this guide can help you get started quickly.

## Structure
Major folders in the project and their purpose:

**Note**: you should be familiar with _Maven_ directory structure, because this project is managed by Maven for Java.

[main/java/](src/main/java)

    core: Includes classes (and groups of classes - packages) that are fundamental to building, and running the project.

    proto: Meant to mean "prototype". Classes in this package extends components of the standard JDK (mainly javax.swing) purposely to initialize them with project-specific values.

    utg: Includes the main class, and its extensions. These set of classes determines how Dashboard should load.

[main/resources/](src/main/resources)

    icons: All image icons used by the project are placed in this folder for uniform access.

    META-INF: Contains the MANIFEST file used by Maven in generating the Jar artefact.

[test/](src/test)

    Resources under this folder are for testing only. They do not count in building, or running the actual program.

## Skills
- Core Java: OOP Concepts, Serialization, etc.
- Maven
- HTML

`Knowledge about UTG, its programs, and courses is important.`

## Logic
Please make sure to read the [logic statement](Logic.md) as well.

## Conventions
- This project is strictly **platform-independent**. In case it became absolutely necessary to implement
  some platform specific components, developer must clearly state it out.
- All features (parts of the project) should be **easily tested, and independent** of each other.
- Developer is encouraged to **document** as much as possible.
- Developer must use "**clean-code**" approach.

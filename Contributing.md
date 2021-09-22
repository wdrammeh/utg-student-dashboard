# Development

If you are looking to get involved improving this project, this guide can help you get started quickly.

## Structure

Major folders in the project and their purpose:

[main/java/](src/main/java)

- [core/](src/main/java/core)
  
  `Includes classes (and groups of classes - packages) that are fundamental to building, and running the project.`

- [proto/](src/main/java/proto)
  
  `Meant to mean "prototype". Classes in this package extends components of the standard JDK (mainly javax.swing) purposely to initialize them with project-specific values.`

- [utg/](src/main/java/utg)
  
  `Includes the main class, and its extensions. These set of classes determines how Dashboard should load.`

[main/resources/](src/main/resources)

- [icons/](src/main/resources/icons)
    
  `All image icons used by the project are placed in this folder for uniform access.`
  
- [META-INF/](src/main/resources/META-INF)
  
  `Contains the MANIFEST file used by Maven in generating the Jar artefact.`

[test/](src/test)

  `Resources under this folder are for testing only. They should not count in building, or running the actual program.`

## Skills

- Core Java: Object-Oriented Programming, Serialization
- Maven
- HTML

_Knowledge about UTG, its programs, and courses is important._

## Furthermore

Please make sure to read the [Logic statement](Logic.md),
the [Convention](Convention.md) as well as the [Glossary](Glossary.md).

## Serialization

Dashboard uses serialization/deserialization to save/retrieve user's data as s/he uses the program.
The serialization approach ("what" & "how") is very tentative, and forms a key part in making releases backward-compatibile. Thus, developer must take great care regarding this component of the project.
More information about serialization per-implementation is discussed in the [serialDir.txt](serialDir.txt) and [serialInfo.txt](serialInfo.txt) files.

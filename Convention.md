# Conventions

- This project is strictly **platform-independent**.
  In case it became absolutely necessary to implement some platform-specific components,
  developer must clearly state it out.
- All features (parts of the project) should be **easily tested**, and **independent** of each other.
- Developer is encouraged to **document** as much as possible.
- Developer must use "**clean-code**" approach.
- **Note** that Running the main branch (i.e. from sources) is technically equivalent
  to running an installed version of the software.
  Hence, developer is advised to modify the launch-path of the program during development and testing (by either supplying a command-line argument, or simply running [Tester](src/test/java/core/Tester.java)) so as to prevent conflict with the normal installation.
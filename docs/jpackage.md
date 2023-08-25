# jpackage

This guide helps you through the process of building a stanadalone Dashboard installer using jpackage. Jpackage is a handy tool for packaging self-contained Java applications.

> The jpackage tool will take as input a Java application and a Java run-time image, and produce a Java application image that includes all the necessary dependencies. It will be able to produce a native package in a platform-specific format, such as an exe on Windows or a dmg on macOS. Each format must be built on the platform it runs on, there is no cross-platform support. The tool will have options that allow packaged applications to be customized in various ways.

# What you'll need

1. [Java SE](https://www.oracle.com/java/technologies/downloads/) / Any JDK build that includes **jpackage**
2. [Maven](https://maven.apache.org/download.cgi)

## Platform requirements

Jpackage relies on some platform-specific components to do its work

**MS Windows**

- [WiX Toolset](https://github.com/wixtoolset/wix)
- [MSIX Packaging Tool](https://learn.microsoft.com/en-us/windows/msix/packaging-tool/tool-overview)

# 1. Build the jars

Firstly, use the project manager - i.e. Apache Maven - to build the required jar with its dependencies. Open a terminal and **cd** into the project root, if you're not there already. Run the following command:

```bash
mvn clean package
```

After maven is done packaging, you'll notice two jar files under the **target** directory:

1. utg-student-dashboard-%version%.jar
2. utg-student-dashboard-%version%-standalone.jar

For this tutorial, we'll focus on the second jar file: "utg-student-dashboard-%version%-standalone.jar". This jar is, as the names suggests, standalone. Meaning that it has all the required dependencies bundled within. So it can be run from anywhere where JRE 11+ is present.

# 2. Prepare grounds

In this step, we'll prepare the necessary grounds required by jpackage to build our installer.

Create a folder named _mypackage_. Do not put this folder in the project root. Preferably, place it in your home directory. E.g: _/home/&lt;username&gt;/mypackage_.

Locate and copy the following files into the _mypackage_ folder:

1. A [dashboard icon](../src/main/resources/icons/dashboard.png)
    > On a Windows system, you need a .ico file instead; .png is not supported.
2. The [LICENSE file](../LICENSE)
3. Create a subdirectory called _target_ and copy the standalone jar file you built in [step 1](#1-build-the-jars) into it.

Finally, the _mypackage_ directory should look like the following:

```
    /home/<username>/mypackage/
    |    target/
    |    |    utg-student-dashbard-%version%-standalone.jar
    |    dashboard.png
    |    LICENSE
```

# 3. Build the package

At this point, we're ready to invoke jpackage. Open a terminal in the _mypackage_ dir you created in [step 2](#2-prepare-grounds), and run the following command:

**Linux**:

```bash
jpackage --name "UTG Student Dashboard" --input target --main-jar utg-student-dashboard-%version%-standalone.jar  --icon dashboard.png --type deb --app-version %version% --description "A Flexible and Elegant student management system for the University of The Gambia" --copyright "(c) 2021 Muhammed W. Drammeh. All rights reserved." --linux-deb-maintainer "Muhammed W. Drammeh <md21712494@utg.edu.gm>" --license-file LICENSE --verbose
```

**Windows**:

```bash
jpackage --name "UTG Student Dashboard" --input target --main-jar utg-student-dashboard-%version%-standalone.jar  --icon dashboard.ico --type exe --app-version %version% --description "A Flexible and Elegant student management system for the University of The Gambia" --copyright "(c) 2021 Muhammed W. Drammeh. All rights reserved." --license-file LICENSE --vendor "Muhammed W. Drammeh" --win-menu --win-menu-group "." --win-shortcut --verbose
```

Once the build is completed successfully, a platform-dependent installer file should be available at the current directory.

Voila!

For more information, see [jpackage command](https://docs.oracle.com/en/java/javase/14/docs/specs/man/jpackage.html#description), or simply run:

```bash
jpackage --help
```

# jpackage

This guide helps you through the process of building a stanadlone Dashboard installer using jpackage. Jpackage is a handy tool for packaging self-contained Java applications.

> The jpackage tool will take as input a Java application and a Java run-time image, and produce a Java application image that includes all the necessary dependencies. It will be able to produce a native package in a platform-specific format, such as an exe on Windows or a dmg on macOS. Each format must be built on the platform it runs on, there is no cross-platform support. The tool will have options that allow packaged applications to be customized in various ways.

## What you'll need

1. Latest [Java SE](https://www.oracle.com/java/technologies/downloads/), or any JDK that includes jpackage
2. [Maven](https://maven.apache.org/download.cgi)

> This guide assumes that you're on a Linux system. If you're on a non-unix based environment, kindly note that the steps described below may not work.

## 1. Build the jars

Firstly, use the project manager - apache maven - to build the required jar with its dependencies. Open a terminal and cd into the project root, if you're not there already. Run the following command:

```
mvn clean package
```

You'll notice two jar files under the target directory:

1. utg-student-dashbard-*.jar
2. utg-student-dashbard-*-standalone.jar

For this tutorial, we'll focus on the second jar: utg-student-dashbard-*-standalone.jar. This jar is, as the names suggests, standalone. Meaning that it has all the required dependencies bundled within. So it can be run from anywhere where JRE 11+ is present.

## 2. Prepare grounds

In this step, we'll prepare grounds for jpackage.

Create a folder named _mypackage_ and move all the files required by jpackage into it for building your package. Do not put this folder in the project root. Idealy, place it in your home directory. E.g: _/home/&lt;username&gt;/mypackage_.

Ok, the folder is created, so what are the files to move into it? Locate and copy the following files into the _mypackage_ folder:

1. [dashboard icon](../../src/main/resources/icons/dashboard.png)
2. The [LICENSE file](../../LICENSE)
3. Create a subdirectory called _target_ and copy the [standalone jar file](../../target/utg-student-dashboard-*-standalone.jar) you built in [step 1](#build-the-jars)

Finally, the _mypackage_ dir should look like the following:

```
    /home/username/mypackage
    |    target
    |    |    utg-student-dashbard-*-standalone.jar
    |    dashboard.png
    |    LICENSE
```

## 3. Build the package

At this point, we're ready to invoke jpackage. Open a terminal in the _mypackage_ (/home/&lt;username&gt;/mypackage) dir you created in [step 2](prepare-grounds), and run the following the command:

```
jpackage --name "UTG Student Dashboard" --input target --main-jar utg-student-dashboard-*-standalone.jar  --icon dashboard.png --type deb --app-version <version> --description "A Flexible and Elegant student management system for the University of The Gambia" --copyright "(c) 2021 Muhammed W. Drammeh. All rights reserved." --linux-deb-maintainer "Muhammed W. Drammeh <md21712494@utg.edu.gm>" --license-file LICENSE --verbose
```

Once the build is done successfully, a file named utg-student-dashboard_*.deb will be created in the dir you initialized the command.

Voila!

For more information, see the [jpackage command](https://docs.oracle.com/en/java/javase/14/docs/specs/man/jpackage.html#description), or simply run:

```
jpackage --help
```

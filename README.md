# Service-Oriented Modifiability Experiment

To provide empirical support for the modifiability of service-based systems this repo contains two functionally equivalent WebShop systems. The system is implemented in very basic fashion and provides CRUD operations for e.g. `customers`, `products`, or `orders`. For simplicity, no persistence of data is implemented, i.e. after restarting a service all changes to data will be reset.

Several tasks have to be performed on the systems within a certain timeframe. Both effectiveness and efficiency should be measured for each version.

To build and start services and components, several scripts are available (see `_scripts` folder in each workspace). The `.sh` scripts should work for both Linux and Mac and should also work with GitBash or Cygwin on Windows. Alternatively, there is also a folder with `.bat` scripts for the Windows command line (`_scripts/win`).

Prerequisites for the Experiment:

- Make sure a [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) >=1.8 is installed and that the `JAVA_HOME` variable is set accordingly.
- Make sure [Maven](https://maven.apache.org/download.cgi) >=3.5.0 is installed and `mvn` is available from the command line.
- Make sure [Node.js](https://nodejs.org/en/download) >=8.0.0 is installed and that `npm` is available from the command line.
- For Version 2 of the system: make sure [Apache Kafka](https://kafka.apache.org/downloads) >=1.1.0 is installed. The Windows build scripts for Kafka and Zookeeper currently expect all related files to reside in `C:\dev\apache-kafka`. If you installed Kafka somewhere else, be sure to adjust `_scripts\win\1_start-zookeeper.bat` and `_scripts\win\2_start-kafka.bat` with your custom path. In the provided Ubuntu VM, both Zookeeper and Kafka are already installed and run as services. So you won't need start scripts.
- Install a Java IDE (recommended: [Eclipse](https://www.eclipse.org/downloads))
- Install a Web IDE (recommended: [Visual Studio Code](https://code.visualstudio.com/download))
- Install a modern web browser (recommended: [Mozilla Firefox](https://www.mozilla.org/en-US/firefox))

Please choose a version and refer to the README in the respective workspace folder ([workspace-version1](workspace-version1/README.md) or [workspace-version2](workspace-version2/README.md)). The evaluation web UI that checks if a task has been successfully finished is the same for both versions (see [exercise-validation](exercise-validation/README.md)).

We also uploaded all artifacts related to the first conducted experiment with 69 Bachelor students as well as the results of the structural metric analysis (see [_results](_results/README.md)). The data in CSV format, the R script for the analysis, the participant survey questions, as well as a spreadsheet with all metric values are provided.

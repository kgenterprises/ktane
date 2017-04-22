# Keep Talking and Nobody Explodes

## Prerequisites

### Point to the credentials file

The credentials file are in this repo under src>resources. The project is not yet configured to find this file dynamically
so to run the project you must point to the file in the Listener.java class. An absolute path is required.
The string for the filepath is a constant called CREDENTIALS_FILE

### Download and install Java and Maven

Install [Java7 or
higher](http://www.oracle.com/technetwork/java/javase/downloads/jre7-downloads-1880261.html).

This project uses the [Apache Maven][maven] build system. Before getting started, be
sure to [download][maven-download] and [install][maven-install] it. When you use
Maven as described here, it will automatically download the needed client
libraries.

[maven]: https://maven.apache.org
[maven-download]: https://maven.apache.org/download.cgi
[maven-install]: https://maven.apache.org/install.html

###Download Lombok

This project uses Lombok to generate boilerplate code. While running the project will work fine, automatic compilation in an IDE 
will require you to download the corresponding Lombok plugin for Intellij or Eclipse

## Running the project

Set your maven preferences to download and install dependencies automatically. After that, you can run
the main method in KtaneManager.java at any time to play.

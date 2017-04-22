# Keep Talking and Nobody Explodes

## Prerequisites

### Download and install Java and Maven

Install Java 8. Ensure your JAVA_HOME variable is set to a 1.8 JDK

```sh
# check if set
$ printenv JAVA_HOME
# if not set
$ export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
```
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

```sh
$ mvn install -DskipTests
```
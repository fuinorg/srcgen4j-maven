# srcgen4j-maven
Source code generation for Java (Maven Plugin)

[![Build Status](https://github.com/fuinorg/srcgen4j-maven/actions/workflows/maven.yml/badge.svg)](https://github.com/fuinorg/srcgen4j-maven/actions/workflows/maven.yml)
[![Coverage Status](https://sonarcloud.io/api/project_badges/measure?project=org.fuin.srcgen4j%3Asrcgen4j-maven-parent&metric=coverage)](https://sonarcloud.io/dashboard?id=org.fuin.srcgen4j%3Asrcgen4j-maven-parent)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.fuin.srcgen4j/srcgen4j-maven-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.fuin.srcgen4j/srcgen4j-maven-parent/)
[![LGPLv3 License](http://img.shields.io/badge/license-LGPLv3-blue.svg)](https://www.gnu.org/licenses/lgpl.html)
[![Java Development Kit 11](https://img.shields.io/badge/JDK-11-green.svg)](https://openjdk.java.net/projects/jdk/11/)

## Versions
- 0.4.3 (or later) = **Java 11** before namespace change from 'javax' to 'jakarta'
- 0.4.2 (or previous) = **Java 8**


## What is this?
The project provides a Maven plugin that executes a parse/generate workflow based on the [srcgen4j-common](https://github.com/fuinorg/srcgen4j-common/)/[srcgen4j-core](https://github.com/fuinorg/srcgen4j-core/) projects. 

## Usage
Simply add the plugin to your project's Maven POM and add configuration an dependencies.

```xml
<plugin>
    <groupId>org.fuin.srcgen4j</groupId>
    <artifactId>srcgen4j-maven-plugin</artifactId>
    <version>0.4.2</version>
    <configuration>
        <!-- Default XML config file name can be changed by adding the following:
        <configFile>srcgen4j-config.xml<configFile>
        -->
        <jaxbClassesToBeBound>
          <!-- JAX enabled configuration classes used in "srcgen4j-config.xml" -->
          <param>org.fuin.srcgen4j.core.velocity.VelocityGeneratorConfig</param>
          <param>org.fuin.srcgen4j.core.velocity.ParameterizedTemplateParserConfig</param>
          <param>org.fuin.srcgen4j.core.velocity.ParameterizedTemplateGeneratorConfig</param>
        </jaxbClassesToBeBound>
    </configuration>
    <dependencies>
        <!-- Libraries used for parsing or code generation that are 
             referenced in "srcgen4j-config.xml"-->    
        <dependency>
            <groupId>org.apache.velocity</groupId>
            <artifactId>velocity</artifactId>
            <version>1.7</version>
        </dependency>
    </dependencies>
</plugin>
```

## Example
You can also find an example project here: [test-project](https://github.com/fuinorg/srcgen4j-maven/tree/master/srcgen4j-maven-test/src/test/resources/test-project).

## Debugging
To start the generation process in debug mode you can checkout the [srcgen4j-maven-app](srcgen4j-maven-app) project.

## Snapshots

Snapshots can be found on the [OSS Sonatype Snapshots Repository](http://oss.sonatype.org/content/repositories/snapshots/org/fuin "Snapshot Repository"). 

Add the following to your .m2/settings.xml (section "repositories") to enable snapshots in your Maven build:
```xml
<repository>
    <id>sonatype.oss.snapshots</id>
    <name>Sonatype OSS Snapshot Repository</name>
    <url>http://oss.sonatype.org/content/repositories/snapshots</url>
    <releases>
        <enabled>false</enabled>
    </releases>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
```
An additional entry to the "pluginRepositories" section is also required:
```xml
<pluginRepository>
    <id>sonatype.oss.snapshots</id>
    <name>Sonatype OSS Snapshot Repository</name>
    <url>http://oss.sonatype.org/content/repositories/snapshots</url>
    <releases>
        <enabled>false</enabled>
    </releases>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</pluginRepository>
```

# srcgen4j-maven
Source code generation for Java (Maven Plugin)

[![Build Status](https://github.com/fuinorg/srcgen4j-maven/actions/workflows/maven.yml/badge.svg)](https://github.com/fuinorg/srcgen4j-maven/actions/workflows/maven.yml)
[![Coverage Status](https://sonarcloud.io/api/project_badges/measure?project=org.fuin.srcgen4j%3Asrcgen4j-maven-plugin&metric=coverage)](https://sonarcloud.io/dashboard?id=org.fuin.srcgen4j%3Asrcgen4j-maven-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.fuin.srcgen4j/srcgen4j-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.fuin.srcgen4j/srcgen4j-maven-plugin/)
[![LGPLv3 License](http://img.shields.io/badge/license-LGPLv3-blue.svg)](https://www.gnu.org/licenses/lgpl.html)
[![Java Development Kit 21](https://img.shields.io/badge/JDK-21-green.svg)](https://openjdk.java.net/projects/jdk/21/)

## Versions
See [Change Log](CHANGELOG.md) for version history.

## What is this?
The project provides a Maven plugin that executes a parse/generate workflow based on the [srcgen4j-common](https://github.com/fuinorg/srcgen4j-common/)/[srcgen4j-core](https://github.com/fuinorg/srcgen4j-core/) projects. 

## Usage
Simply add the plugin to your project's Maven POM and add configuration an dependencies.

```xml
<plugin>
    <groupId>org.fuin.srcgen4j</groupId>
    <artifactId>srcgen4j-maven-plugin</artifactId>
    <version>0.5.0-SNAPSHOT</version>
    <configuration>
        <jaxbClasses>
            <jaxbClass>org.fuin.srcgen4j.core.velocity.VelocityGeneratorConfig</jaxbClass>
            <jaxbClass>org.fuin.srcgen4j.core.velocity.ParameterizedTemplateParserConfig</jaxbClass>
            <jaxbClass>org.fuin.srcgen4j.core.velocity.ParameterizedTemplateGeneratorConfig</jaxbClass>
        </jaxbClasses>
    </configuration>
    <executions>
        <execution>
            <id>srcgen4j</id>
            <phase>process-sources</phase>
            <goals>
                <goal>process-template</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
:warning: If you use a snapshot version, be sure to include the snapshot repositoy to your settings (See below).

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

srcgen4j-maven
==============

Source code generation for Java (Maven Plugin)

__CAUTION: *Project is in early stage (Work in progress)*__

[![Build Status](https://fuin-org.ci.cloudbees.com/job/srcgen4j-maven/badge/icon)](https://fuin-org.ci.cloudbees.com/job/srcgen4j-maven/)

<a href="https://fuin-org.ci.cloudbees.com/job/srcgen4j-maven"><img src="http://www.fuin.org/images/Button-Built-on-CB-1.png" width="213" height="72" border="0" alt="Built on CloudBees"/></a>

What is this?
-------------
The project provides a Maven plugin that executes a parse/generate workflow based on the ([srcgen4j-common](https://github.com/fuinorg/srcgen4j-common/))/([srcgen4j-core](https://github.com/fuinorg/srcgen4j-core/)) projects. 

Usage
-----
Simply add the plugin to your project's Maven POM and add configuration an dependencies.
```xml
<plugin>
    <groupId>org.fuin.srcgen4j</groupId>
    <artifactId>srcgen4j-maven-plugin</artifactId>
    <version>0.4.0</version>
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

You can also find an example project here: [test-project](https://github.com/fuinorg/srcgen4j-maven/tree/master/srcgen4j-maven-test/src/test/resources/test-project).


- - - - - - - - -

Snapshots
=========
Snapshots can be found on the [OSS Sonatype Snapshots Repository](http://oss.sonatype.org/content/repositories/snapshots/org/fuin "Snapshot Repository"). 

Add the following to your .m2/settings.xml to enable snapshots in your Maven build:

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

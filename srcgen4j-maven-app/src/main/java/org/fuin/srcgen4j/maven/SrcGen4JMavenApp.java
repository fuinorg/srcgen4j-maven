/**
 * Copyright (C) 2013 Future Invent Informationsmanagement GmbH. All rights
 * reserved. <http://www.fuin.org/>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.fuin.srcgen4j.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.fuin.srcgen4j.commons.DefaultContext;
import org.fuin.srcgen4j.commons.GenerateException;
import org.fuin.srcgen4j.commons.JaxbHelper;
import org.fuin.srcgen4j.commons.ParseException;
import org.fuin.srcgen4j.commons.SrcGen4J;
import org.fuin.srcgen4j.commons.SrcGen4JConfig;
import org.fuin.srcgen4j.commons.SrcGen4JContext;
import org.fuin.srcgen4j.commons.UnmarshalObjectException;
import org.fuin.utils4j.Utils4J;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Application that takes a Maven POM as input and executes the source code generation. This allows easier debugging of problems.
 */
public final class SrcGen4JMavenApp {

    /**
     * Creates an XML document from the Maven POM file.
     * 
     * @param pomFile
     *            Maven 'pom.xml' to parse.
     * 
     * @return DOM
     */
    private static Document parseMavenPom(final File pomFile) {
        try (final FileInputStream fileIS = new FileInputStream(pomFile)) {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            return builder.parse(fileIS);
        } catch (final IOException | ParserConfigurationException | SAXException ex) {
            throw new RuntimeException("Error parsing Maven pom: " + pomFile, ex);
        }
    }

    /**
     * Loads a list of JAX-B classes.
     * 
     * @param classLoader
     *            Class loader to use.
     * @param jaxbClassesToBeBound
     *            List of class names to load.
     * 
     * @return List of classes to add to JAX-B context.
     */
    private static Class<?>[] getJaxbContextClasses(final ClassLoader classLoader, final List<String> jaxbClassesToBeBound) {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(SrcGen4JConfig.class);
        if (jaxbClassesToBeBound != null) {
            for (final String name : jaxbClassesToBeBound) {
                try {
                    final Class<?> clasz = classLoader.loadClass(name);
                    classes.add(clasz);
                } catch (final ClassNotFoundException ex) {
                    throw new RuntimeException("Class to add to JAXB context not found: " + name, ex);
                }
            }
        }
        return classes.toArray(new Class<?>[classes.size()]);
    }

    /**
     * Loads a configuration file.
     * 
     * @param context
     *            Context to use.
     * @param configFile
     *            XML configuration file to load.
     * @param jaxbClassesToBeBound
     *            List of additional JAXB-classes.
     * 
     * @return Already initialized configuration.
     */
    public static SrcGen4JConfig createAndInit(final SrcGen4JContext context, final File configFile,
            final List<String> jaxbClassesToBeBound) {
        try {
            final JaxbHelper helper = new JaxbHelper();
            final Class<?>[] classes = getJaxbContextClasses(context.getClassLoader(), jaxbClassesToBeBound);
            final SrcGen4JConfig config = helper.create(configFile, JAXBContext.newInstance(classes));
            config.init(context, Utils4J.getCanonicalFile(configFile.getParentFile()));
            return config;
        } catch (final JAXBException ex) {
            throw new RuntimeException("Error creating the JAXB context", ex);
        } catch (final UnmarshalObjectException ex) {
            throw new RuntimeException("Error reading the configuration: " + configFile, ex);
        }
    }

    /**
     * Uses XPath to extract a list of strings from a set of nodes. 
     * 
     * @param xPath XPath instance to use.
     * @param xpathExpression Expression to use for locating the strings to extract.
     * @param doc Document to locate the nodes within.
     * 
     * @return List of strings extracted from the document.
     */
    private static List<String> findStrings(final XPath xPath, final String xpathExpression, final Document doc) {
        final List<String> list = new ArrayList<>();
        try {
            final NodeList nodeList = (NodeList) xPath.compile(xpathExpression).evaluate(doc, XPathConstants.NODESET);
            if (nodeList != null) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    final Node node = nodeList.item(i);
                    list.add(node.getTextContent());
                }
            }
        } catch (final XPathExpressionException ex) {
            throw new RuntimeException("Failed to find '" + xpathExpression + "'", ex);
        }
        return list;
    }

    /**
     * Returns the configuration file from the POM or a default based on the pom directory.
     * 
     * @param xPath XPath instance to use.
     * @param xpathExpression Expression to use for locating the strings to extract.
     * @param doc Document to locate the nodes within.
     * @param dir Directory where the 'pom.xml' is located.
     * 
     * @return XML configuration file.
     */
    private static File extractConfigFile(final XPath xPath, final Document doc, final File dir) {
        final List<String> names = findStrings(xPath, "//configuration/configFile", doc);
        if (names.size() == 0) {
            return new File(dir, "srcgen4j-config.xml");
        }        
        return new File(names.get(0));
    }

    
    /**
     * Main application.
     * 
     * @param args Only argument is the directory where the Maven pom.xml is located. 
     */
    public static void main(final String[] args) {
        
        if (args == null || args.length == 0) {
            System.err.println("Please provide the directory where the Maven pom.xml is located as only command line argument");
            System.exit(1);
        }
        final File dir = new File(args[0]);
        
        /// Read POM and create a configuration  using the configuration
        final File pomFile = new File(dir, "pom.xml");
        final Document pomDoc = parseMavenPom(pomFile);
        final XPath xPath = XPathFactory.newInstance().newXPath();
        final File configFile = extractConfigFile(xPath, pomDoc, dir); 
        final List<String> jaxbClassesToBeBound = findStrings(xPath, "//configuration/jaxbClassesToBeBound/param/text()", pomDoc);
        
        // Generate based on the configuration
        final DefaultContext context = new DefaultContext(SrcGen4JMavenApp.class.getClassLoader(), Collections.emptyList());
        final SrcGen4JConfig config = createAndInit(context, configFile, jaxbClassesToBeBound);
        final SrcGen4J srcGen4J = new SrcGen4J(config, context);
        try {
            srcGen4J.execute();
            System.exit(0);
        } catch (final ParseException | GenerateException ex) {
            throw new RuntimeException("Failed to execute", ex);
        }

    }

}

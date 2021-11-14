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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.fuin.srcgen4j.commons.DefaultContext;
import org.fuin.srcgen4j.commons.GenerateException;
import org.fuin.srcgen4j.commons.ParseException;
import org.fuin.srcgen4j.commons.SrcGen4J;
import org.fuin.srcgen4j.commons.SrcGen4JConfig;
import org.fuin.srcgen4j.commons.SrcGen4JContext;
import org.fuin.utils4j.Utils4J;
import org.fuin.utils4j.jaxb.JaxbUtils;
import org.fuin.utils4j.jaxb.UnmarshallerBuilder;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * A customizable source code generator plugin for maven.
 * 
 * @requiresDependencyResolution compile
 * @requiresProject true
 * @goal process-template
 * @phase generate-sources
 */
public final class SrcGen4JMojo extends AbstractMojo {

    /**
     * The current maven project.
     * 
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * The configuration file.
     * 
     * @parameter default-value="${basedir}/srcgen4j-config.xml"
     * @required
     * @readonly
     */
    private File configFile;

    /**
     * A list of JAXB classes to be bound to the JAXB context.
     * 
     * @parameter
     * @readonly
     */
    private String[] jaxbClassesToBeBound;

    /**
     * Checks if a variable is not <code>null</code> and throws an <code>IllegalNullArgumentException</code> if this rule is violated.
     * 
     * @param name
     *            Name of the variable to be displayed in an error message.
     * @param value
     *            Value to check for <code>null</code>.
     * 
     * @throws MojoExecutionException
     *             Checked value was NULL.
     */
    private void checkNotNull(final String name, final Object value) throws MojoExecutionException {
        if (value == null) {
            throw new MojoExecutionException(name + " cannot be null!");
        }
    }

    @SuppressWarnings("unchecked")
    private List<File> createCp() throws MojoExecutionException {
        try {
            final List<File> entries = new ArrayList<File>();
            final List<String> cpElements = (List<String>) project.getCompileClasspathElements();
            for (final String cpElement : cpElements) {
                final File file = new File(cpElement);
                entries.add(file);
            }
            return entries;
        } catch (final DependencyResolutionRequiredException ex) {
            throw new MojoExecutionException("Error resolving classpath entries", ex);
        }
    }

    private Class<?>[] getJaxbContextClasses(final ClassLoader classLoader) throws MojoExecutionException {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(SrcGen4JConfig.class);
        if ((jaxbClassesToBeBound != null) && (jaxbClassesToBeBound.length > 0)) {
            for (final String name : jaxbClassesToBeBound) {
                try {
                    final Class<?> clasz = classLoader.loadClass(name);
                    classes.add(clasz);
                } catch (final ClassNotFoundException ex) {
                    throw new MojoExecutionException("Class to add to JAXB context not found: " + name, ex);
                }
            }
        }
        return classes.toArray(new Class<?>[classes.size()]);
    }

    /**
     * Creates and initializes a SrcGen4J configuration from a configuration file and adds the necessary configurations.
     * 
     * @param context
     *            Current context - Cannot be NULL.
     * @param configFile
     *            XML configuration file to read - Cannot be NULL.
     * 
     * @return New configuration instance.
     * 
     * @throws MojoExecutionException
     *             Error reading the configuration.
     */
    public SrcGen4JConfig createAndInit(final SrcGen4JContext context, final File configFile) throws MojoExecutionException {
        try {
            final Class<?>[] classes = getJaxbContextClasses(context.getClassLoader());
            final SrcGen4JConfig config = JaxbUtils
                    .unmarshal(new UnmarshallerBuilder().withContext(JAXBContext.newInstance(classes)).build(), configFile);
            config.init(context, Utils4J.getCanonicalFile(configFile.getParentFile()));
            return config;
        } catch (final JAXBException ex) {
            throw new MojoExecutionException("Error creating the JAXB context", ex);
        }
    }

    private void checkConfigFile() throws MojoExecutionException {
        checkNotNull("configFile", configFile);
        if (!configFile.exists()) {
            throw new MojoExecutionException("The configuration file does not exist: " + configFile);
        }
        if (!configFile.isFile()) {
            throw new MojoExecutionException("The configuration file is not a file: " + configFile);
        }
    }

    @Override
    public void execute() throws MojoExecutionException {

        StaticLoggerBinder.getSingleton().setMavenLog(getLog());

        checkConfigFile();

        final DefaultContext context = new DefaultContext(this.getClass().getClassLoader(), createCp());
        final SrcGen4JConfig config = createAndInit(context, configFile);
        final SrcGen4J srcGen4J = new SrcGen4J(config, context);
        try {
            srcGen4J.execute();
        } catch (final ParseException ex) {
            throw new MojoExecutionException("Parsing error", ex);
        } catch (final GenerateException ex) {
            throw new MojoExecutionException("Generation error", ex);
        }

    }

}

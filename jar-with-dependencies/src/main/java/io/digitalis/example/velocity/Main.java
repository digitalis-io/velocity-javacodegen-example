package io.digitalis.example.velocity;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final String OUTPUTDIR="generated-code/src/main/java/io/digitalis/example/velocity";
    private static final String OUTPUTFILE="Example.java";



    public static void main(String[] args) {
        try {

            Main m = new Main();
            m.generateJava();

        } catch (Throwable t) {
            log.error("Problem encountered: "+t.getMessage(), t);
            throw t;
        }
    }


    private void generateJava() {
        try {

            log.debug("Create output directory if does not exist");
            File directory = new File(OUTPUTDIR);
            if (!directory.exists()) {
                log.debug("Creating directory "+OUTPUTDIR);
                Files.createDirectories(Paths.get(OUTPUTDIR));
            }

            log.debug("Delete target generated file if exists");
            File outputFile = new File(OUTPUTDIR+"/"+OUTPUTFILE);
            if (outputFile.exists()) {
                log.debug("Deleting previously generataed file  "+outputFile.getAbsolutePath());
                outputFile.delete();
            } else {
                log.debug("Output file does not exist "+outputFile.getAbsolutePath());
            }
            log.info("Initialising VelocityEngine...");
            VelocityEngine velocityEngine = new VelocityEngine();

            velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

            log.debug("Added classloader for resources");

            velocityEngine.init();

            log.debug("VelocityEngine initialised");

            final String templatePath = "templates/" + OUTPUTFILE + ".vm";
            InputStream input = this.getClass().getClassLoader().getResourceAsStream(templatePath);
            if (input == null) {
                throw new IOException("Template file doesn't exist");
            } else {
                log.info("Template file exists @ "+templatePath);
            }

            InputStreamReader reader = new InputStreamReader(input);

            VelocityContext velocityContext = new VelocityContext();

            velocityContext.put("mymessage", "I have been generated @ "+new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));

            log.debug("Context values added for templating out");

            Template template = velocityEngine.getTemplate(templatePath, "UTF-8");

            log.debug("Got templated from Velocity Engine");

            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            template.merge(velocityContext, writer);

            log.debug("Template Merged");


            writer.flush();
            writer.close();

            log.info("Done!! Should be a new file generated @ - "+outputFile.getAbsolutePath());

            String content = FileUtils.readFileToString(outputFile, "UTF-8");

            log.info("Generated File Contents:\n"+content+"\n\n");


        } catch (Throwable t) {
            log.error("Problem encountered: "+t.getMessage(), t);
            throw new RuntimeException("Problem encountered generating file: " + t.getMessage(), t);
        }
    }
}

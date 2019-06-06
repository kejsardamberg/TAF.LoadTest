package se.claremont.taf.performance.gui;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import se.claremont.taf.performance.LoadResource;
import se.claremont.taf.performance.gui.guiProfiles.TafButton;
import se.claremont.taf.performance.gui.guiProfiles.TafFileChooser;
import se.claremont.taf.performance.gui.guiProfiles.TafLabel;
import se.claremont.taf.performance.gui.guiProfiles.TafPanel;
import se.claremont.taf.performance.threadscenariosteps.LoadTestFeature;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static se.claremont.taf.performance.gui.MainGui.loadedUsableTestMethodsList;

public class JarLoader extends TafPanel {

    private TafFileChooser jarFileChooser;
    private TafLabel statusMessage;

    public JarLoader(){
        statusMessage = new TafLabel("");
        TafButton jarFileSelectorButton = new TafButton("Browse for test methods jar file..");
        JarFileActionListener jarFileActionListener = new JarFileActionListener(this);
        jarFileSelectorButton.addActionListener(jarFileActionListener);
        this.add(jarFileSelectorButton);
        this.add(statusMessage);
    }

    private List<Class<?>> getClassesFromJar(File file){
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(file.getPath());
        } catch (IOException e1) {
            statusMessage.setText(e1.toString());
            return null;
        }
        Enumeration<JarEntry> entry = jarFile.entries();

        URL[] urls = new URL[0];
        try {
            urls = new URL[]{ new URL("jar:file:" + file.getPath() +"!/") };
        } catch (MalformedURLException e1) {
            statusMessage.setText(e1.toString());
            return null;
        }
        URLClassLoader cl = URLClassLoader.newInstance(urls);
        List<Class<?>> classesInJar = new ArrayList<Class<?>>();

        StringBuilder message = new StringBuilder();
        message.append("<html><body");
        while (entry.hasMoreElements()) {
            JarEntry je = entry.nextElement();
            if(je.isDirectory() || !je.getName().endsWith(".class")){
                continue;
            }
            // -6 because of .class
            String className = je.getName().substring(0,je.getName().length()-6);
            className = className.replace('/', '.');
            try {
                Class c = cl.loadClass(className);
                classesInJar.add(c);
            } catch (Throwable e1) {
                message.append("Error processing class '").append(className).append("':").append(e1.getMessage()).append("<br>");
            }
        }
        message.append("</body></html>");
        statusMessage.setText(message.toString());
        return classesInJar;
    }

    private File getJar(){
        jarFileChooser = new TafFileChooser();
        int returnVal = jarFileChooser.showOpenDialog(this);

        if (returnVal == TafFileChooser.APPROVE_OPTION) {
            File file = jarFileChooser.getSelectedFile();
            if(file != null)return file;
        }
        return null;
    }

    private class JarFileActionListener implements ActionListener {
        public File file;
        private  JPanel parentPanel;
        public JarFileActionListener(JPanel parentPanel){
            this.parentPanel = parentPanel;
        }

        public void actionPerformed(ActionEvent e) {
            file = getJar();
            List<Class<?>> classesInJar = getClassesFromJar(file);
            if(classesInJar == null){
                statusMessage.setText("No classes found in jar '" + file.getPath() + "'.");
                return;
            }
            StringBuilder message = new StringBuilder();
            message.append("<html><body>");
            for(Class c : classesInJar){
                Method[] methods = null;
                try{
                    methods = c.getMethods();
                }catch (Throwable t){
                    message.append("Warning: Could not get methods from class '" + c.getName() + "'.<br>");
                    continue;
                }
                if(methods == null || methods.length == 0) continue;
                for(Method m : methods){
                    for(Annotation a : m.getDeclaredAnnotations()){
                        if(
                                a.annotationType().isAssignableFrom(Test.class) ||
                                a.annotationType().isAssignableFrom(Before.class) ||
                                a.annotationType().isAssignableFrom(After.class) ||
                                a.annotationType().isAssignableFrom(LoadTestFeature.class)
                        ){
                            loadedUsableTestMethodsList.add(new LoadResource(file, c, m));
                        }
                    }
                }
            }
            message.append("</body></html>");
            statusMessage.setText(message.toString());
            if(loadedUsableTestMethodsList.getLoadResourceList().size() == 0){
                statusMessage.setText("No valid JUnit test methods found in any class of jar file '" + file.getPath() + "'.");
                return;
            }
            statusMessage.setText("");
        }
    }


}

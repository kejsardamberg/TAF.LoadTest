package se.claremont.taf.performance.gui;

import java.net.URL;
import java.net.URLClassLoader;

public class JarClassLoader extends URLClassLoader {
    URL url;

    public JarClassLoader(URL url) {
        super(new URL[] {url});
    }
}

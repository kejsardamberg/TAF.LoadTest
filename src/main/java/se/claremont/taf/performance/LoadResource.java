package se.claremont.taf.performance;

import java.io.File;
import java.lang.reflect.Method;

public class LoadResource {

    public File file;
    public Class clazz;
    public Method method;

    public LoadResource(File file, Class c, Method m) {
        this.file = file;
        this.clazz = c;
        this.method = m;
    }
}

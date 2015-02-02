package eu.arthepsy.groovy;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class ResourceUtils {
    public static File getFile(String filePath) {
        Class clazz = ResourceUtils.class;
        String prefix = '/' + clazz.getPackage().getName().replace('.', '/') + '/';
        URL url = clazz.getResource(prefix + filePath);
        assertNotNull(url);
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            return new File(url.getPath());
        }
    }
}

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PropertiesGetter {
    java.util.Properties prop;

    PropertiesGetter(String configFilePath){
        prop = new java.util.Properties();

        try {
            FileInputStream propsInput = new FileInputStream(configFilePath);
            prop.load(propsInput);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPropData(String element) {
        return prop.getProperty(element);
    }
}

package thebob.xmlModels;

import org.jvnet.jaxb2_commons.lang.JAXBToStringStrategy;

public class CustomJAXBToStringStrategy extends JAXBToStringStrategy {

    @Override
    public boolean isUseIdentityHashCode() {
        return false;
    }

    @Override
    protected void appendFieldSeparator(StringBuilder buffer) {
        buffer.append(", \n");
    }

    public static final CustomJAXBToStringStrategy INSTANCE = new CustomJAXBToStringStrategy();

}
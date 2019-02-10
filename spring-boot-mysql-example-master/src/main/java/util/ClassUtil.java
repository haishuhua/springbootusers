package util;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ClassUtil {

    private List<String> fieldNames;
    private List<String> fieldDataTypes;
    private List<Method> fieldGetters;
    private List<Method> fieldSetters;

    public ClassUtil(Class<?> classType) {
        fieldNames = new ArrayList<String>();
        fieldDataTypes = new ArrayList<String>();
        fieldGetters = new ArrayList<Method>();
        fieldSetters = new ArrayList<Method>();

        BeanInfo info = null;;
        PropertyDescriptor[] props = null;
        try {
            info = Introspector.getBeanInfo(classType, Object.class);
            props = info.getPropertyDescriptors();
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }

        if(props != null) {
            for (PropertyDescriptor pd : props) {
                if (pd.getPropertyType() != null){
                    fieldNames.add(pd.getName());
                    fieldDataTypes.add(pd.getPropertyType().getName());
                    fieldGetters.add(pd.getReadMethod());
                    fieldSetters.add(pd.getWriteMethod());
                }
            }
        }
    }

    public List<String> getFieldNames() {
        return this.fieldNames;
    }

    public List<String> getFieldDataTypes() {
        return this.fieldDataTypes;
    }

    public List<Method> getFieldGetters() {
        return this.fieldGetters;
    }

    public List<Method> getFieldSetters() {
        return this.fieldSetters;
    }

    public String getFieldDataType(String fieldName) {
        if (fieldNames == null || fieldDataTypes == null)
            return null;

        for (int i = 0; i < fieldNames.size(); i++)
            if(fieldNames.get(i).equals(fieldName))
                return fieldDataTypes.get(i);

        return null;
    }

    public Boolean hasField(String fieldName) {
        if(fieldNames == null)
            return false;
        return this.fieldNames.contains(fieldName);
    }

}

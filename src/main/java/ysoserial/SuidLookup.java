package ysoserial;

import java.io.ObjectStreamClass;
import java.util.HashMap;
import java.util.Map;

public class SuidLookup {

    static Map<Class, String> classes = new HashMap<>();

    static {
        classes.put(bsh.Interpreter.class, "org.beanshell:bsh");

        classes.put(com.mchange.v2.c3p0.PoolBackedDataSource.class, "com.mchange:c3p0");

        classes.put(org.apache.commons.beanutils.BeanComparator.class, "commons-beanutils:commons-beanutils");

        classes.put(org.apache.commons.collections.functors.ChainedTransformer.class, "commons-collections:commons-collections");
        classes.put(org.apache.commons.collections.map.LazyMap.class, "commons-collections:commons-collections");
        classes.put(org.apache.commons.collections.keyvalue.TiedMapEntry.class, "commons-collections:commons-collections");

        classes.put(org.apache.commons.collections4.comparators.TransformingComparator.class, "org.apache.commons:commons-collections");
        classes.put(org.apache.commons.collections4.functors.InstantiateTransformer.class, "org.apache.commons:commons-collections");
        classes.put(org.apache.commons.collections4.bag.TreeBag.class, "org.apache.commons:commons-collections");

        classes.put(org.apache.commons.fileupload.disk.DiskFileItem.class, "commons-fileupload:commons-fileupload");

        classes.put(org.codehaus.groovy.runtime.ConvertedClosure.class, "org.codehaus.groovy:groovy");

        classes.put(org.hibernate.property.Getter.class, "org.hibernate:hibernate-core");

        classes.put(org.jboss.weld.interceptor.spi.model.InterceptionModel.class, "org.jboss.weld:weld-core");
        classes.put(org.jboss.interceptor.reader.ReflectiveClassMetadata.class, "org.jboss.interceptor:jboss-interceptor-core");

        classes.put(net.sf.json.JSONObject.class, "net.sf.json-lib:json-lib:jdk15");

        classes.put(org.python.core.PyBytecode.class, "org.python:jython-standalone");

        classes.put(org.mozilla.javascript.NativeJavaObject.class, "rhino:js");

        classes.put(org.apache.myfaces.view.facelets.el.ValueExpressionMethodExpression.class, "org.apache.myfaces.core:myfaces-impl");

        classes.put(com.sun.syndication.feed.impl.ObjectBean.class, "rome:rome");

        classes.put(com.vaadin.data.util.NestedMethodProperty.class, "com.vaadin:vaadin-server");
    }

    public static void main(String[] args) {
        StringBuilder stringBuilder = new StringBuilder("[");
        boolean flag = false;
        for (Map.Entry<Class, String> entry : classes.entrySet()) {
            Class klass = entry.getKey();
            String desc = entry.getValue();
            if (flag) {
                stringBuilder.append(",");
            }
            long suid = ObjectStreamClass.lookupAny(klass).getSerialVersionUID();
            stringBuilder
                .append("{")
                .append("\"class\":").append("\"").append(klass.getName()).append("\",")
                .append("\"suid\":").append(suid).append(",")
                .append("\"desc\":").append("\"").append(desc).append("\"")
                .append("}");
            flag = true;
        }
        stringBuilder.append("]");
        System.out.println(stringBuilder);
    }
}

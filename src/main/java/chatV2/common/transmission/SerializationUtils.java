package chatV2.common.transmission;

import chatV2.common.utils.StreamUtilities;

import java.io.*;
import java.util.Optional;

public final class SerializationUtils {

    public byte[] getBytes(Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        byte[] resultBytes = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            resultBytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtilities.tryCloseStream(byteArrayOutputStream, objectOutputStream);
        }
        return resultBytes;
    }

    private Object getRawObject(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        Object resultObject = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            resultObject = objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtilities.tryCloseStream(byteArrayInputStream, objectInputStream);
        }
        return resultObject;
    }

    public Object getObject(byte[] bytes, Optional<Class<?>> objectClass) {
        Object rawObject = getRawObject(bytes);
        if(objectClass.isPresent()) {
            return rawObject != null && objectClass.get().isAssignableFrom(rawObject.getClass()) ? rawObject : null;
        } else {
            return rawObject;
        }
    }
}

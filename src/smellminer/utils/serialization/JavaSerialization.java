package smellminer.utils.serialization;

import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import org.apache.commons.lang.SerializationUtils;
import java.util.logging.Logger;

public class JavaSerialization implements ISerializationStrategy {
    private static final Logger LOGGER = Logger.getLogger(JavaSerialization.class.getName());

    @Override
    public Object deserializeFrom(final byte[] data) throws ISerializationStrategy.SerializationException {
	return SerializationUtils.deserialize(data);
    }

    @Override
    public Object deserializeFrom(final String filename) throws ISerializationStrategy.SerializationException {
	JavaSerialization.LOGGER.info("Deserializing object from " + filename);
	try {
	    final FileInputStream fisM = new FileInputStream(filename);
	    final ObjectInputStream oisM = new ObjectInputStream(fisM);
	    final Object obj = oisM.readObject();
	    oisM.close();
	    return obj;
	} catch (ClassNotFoundException e) {
	    throw new ISerializationStrategy.SerializationException(e);
	} catch (IOException e2) {
	    throw new ISerializationStrategy.SerializationException(e2);
	}
    }

    @Override
    public byte[] serialize(final Object obj)
	    throws ISerializationStrategy.SerializationException {
	return SerializationUtils.serialize((Serializable) obj);
    }

    @Override
    public void serialize(final Object obj, final String filename)
	    throws ISerializationStrategy.SerializationException {
	JavaSerialization.LOGGER.info("Serializing object of type "
		+ obj.getClass().getName() + " to " + filename);
	try {
	    final FileOutputStream fos = new FileOutputStream(filename);
	    final ObjectOutputStream oos = new ObjectOutputStream(fos);
	    oos.writeObject(obj);
	    oos.close();
	} catch (IOException e) {
	    throw new ISerializationStrategy.SerializationException(e);
	}
    }
}
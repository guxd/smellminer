package smellminer.utils.serialization;

public interface ISerializationStrategy {
    Object deserializeFrom(byte[] p0) throws SerializationException;

    Object deserializeFrom(String p0) throws SerializationException;

    byte[] serialize(Object p0) throws SerializationException;

    void serialize(Object p0, String p1) throws SerializationException;

    public static class SerializationException extends Exception {
	private static final long serialVersionUID = 7492466587431989538L;

	public SerializationException(Throwable e) {
	    super(e);
	}
    }
}
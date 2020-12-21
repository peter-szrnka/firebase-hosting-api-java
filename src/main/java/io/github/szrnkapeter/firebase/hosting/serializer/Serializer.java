package io.github.szrnkapeter.firebase.hosting.serializer;

/**
 * @author Peter Szrnka
 * @since 0.1
 */
public interface Serializer {

	/**
	 * JSON to Object conversion.
	 * 
	 * @param clazz Result class.
	 * @param responseBody The JSON response body
	 * @return a T class
	 * @throws Exception The unexpected exception
	 */
	<T> T getObject(Class<T> clazz, String responseBody) throws Exception;
}
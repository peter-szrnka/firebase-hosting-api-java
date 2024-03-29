package io.github.szrnkapeter.firebase.hosting.serializer;

/**
 * @author Peter Szrnka
 * @since 0.1
 */
public interface Serializer {

	/**
	 * JSON to Object conversion.
	 * 
	 * @param <T> The T Type
	 * @param clazz Result class.
	 * @param json The JSON response body
	 * @return a T class
	 * 
	 * @since 0.1
	 */
	<T> T getObject(Class<T> clazz, String json);

	/**
	 * Object to JSON conversion.
	 * 
	 * @param <T> The T Type
	 * @param clazz Input class.
	 * @param obj The input object
	 * @return The JSON representation of the given object
	 * 
	 * @since 0.2
	 */
	<T> String toJson(Class<T> clazz, T obj);
}
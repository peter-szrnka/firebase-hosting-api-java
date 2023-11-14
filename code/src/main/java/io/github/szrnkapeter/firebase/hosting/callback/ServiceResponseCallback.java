package io.github.szrnkapeter.firebase.hosting.callback;

/**
 * A simple (functional) interface for returning the service responses of the deployment subprocesses.
 *
 * @author Peter Szrnka
 * @since 0.4
 */
public interface ServiceResponseCallback {

	/**
	 * Returns with the subservice response object.
	 * 
	 * @param <T> the response type.
	 * @param function The called function's name
	 * @param response The concrete response object
	 */
	<T> void getResponse(String function, T response);
}
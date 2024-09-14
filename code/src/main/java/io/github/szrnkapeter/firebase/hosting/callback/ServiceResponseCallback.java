package io.github.szrnkapeter.firebase.hosting.callback;

/**
 * A simple (functional) interface for returning the service responses of the deployment subprocesses.
 *
 * @author Peter Szrnka
 * @since 0.4
 */
public interface ServiceResponseCallback {

	/**
	 * Returns with the sub service response object.
	 *
	 * @param function The called function's name
	 * @param response The concrete response object
	 */
	void getResponse(String function, Object response);
}
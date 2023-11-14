package io.github.szrnkapeter.firebase.hosting.callback;

/**
 * A simple (functional) interface for returning with HTTP responses.
 *
 * @author Peter Szrnka
 * @since 0.4
 */
public interface HttpResponseCallback {

	/**
	 * Returns with some basic response information.
	 * 
	 * @param function The called functions name 
	 * @param code HTTP response code
	 * @param responseMessage HTTP response message
	 */
	void getResponseInfo(String function, int code, String responseMessage);
}
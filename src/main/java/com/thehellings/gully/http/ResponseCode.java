package com.thehellings.gully.http;

/**
 * A listing of common HTTP response codes, with some friendly names, and their response codes.
 *
 * Only those which are needed have been added for now, more will be added as they are used by the application or
 * any of its supporting applications.
 */
public enum ResponseCode {
	REDIRECT_PERMANENT(302),
	ERROR_NOT_FOUND(404),
	SERVER_ERROR(500);

	private int code;
	ResponseCode(int code) {
		this.code = code;
	}

	/**
	 * Gives back the numeric response code for this response message
	 *
	 * @return numeric HTTP code
	 */
	public int getCode() {
		return this.code;
	}
}

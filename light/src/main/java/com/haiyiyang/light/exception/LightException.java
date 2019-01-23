package com.haiyiyang.light.exception;

public class LightException extends RuntimeException {

	private static final long serialVersionUID = -1L;

	public static enum Code {
		UNDEFINED(), SETTINGS_ERROR(), PERMISSION_ERROR(), ZK_ERROR(), SERVICE_ERROR(), INVOKE_ERROR();
	}

	private final Code code;
	private static final String MESSAGE_PREFIX = " | message: ";
	private static final String CODE_PREFIX = "LightException > code: ";

	public LightException(Throwable cause) {
		super(cause);
		this.code = Code.UNDEFINED;
	}

	public LightException(Code code, Throwable cause) {
		super(cause);
		this.code = code;
	}

	public LightException(String message) {
		super(message);
		this.code = Code.UNDEFINED;
	}

	public LightException(Code code, String message) {
		super(message);
		this.code = code;
	}

	public LightException(Code code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	@Override
	public String toString() {
		return new StringBuilder(CODE_PREFIX).append(code).append(MESSAGE_PREFIX).append(getMessage()).toString();
	}

	public static final String FILE_NOT_FOUND = "File does not exist.";
	public static final String SERVICE_NOT_FOUND = "No service was found.";
	public static final String LOADING_FILE_FAILED = "Loading file failed.";
	public static final String SERVER_STARTUP_FAILED = "Server startup failed.";
	public static final String NO_NETWORK_PERMISSION = "This IP No network permission.";
	public static final String FILE_NOT_FOUND_OR_EMPTY = "File does not exist, or is empty.";
	public static final String INVALID_CONFIG_SERVER_URL = "Light config server URL is invalid.";
}

package stash.exceptions;

public class StashRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public StashRuntimeException() {
		super();
	}

	public StashRuntimeException(String message) {
		super(message);
	}
	
    public StashRuntimeException(Throwable cause) {
        super(cause);
    }
}

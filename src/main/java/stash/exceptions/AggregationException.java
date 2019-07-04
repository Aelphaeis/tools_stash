package stash.exceptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * The class {@code AggregationException} represents that multiple exceptions have occurred.
 * @author Aelphaeis
 *
 */
public class AggregationException extends StashException {
	private static final long serialVersionUID = 1L;
	private final Collection<Throwable> exceptions;

	/**
	 * Takes a message and a collection of exceptions. The exceptions can be retrieved with 
	 * {@link AggregationException#getExceptions()} If a null collection is passed, it's replaced with an empty 
	 * collection.
	 * @param message
	 * @param exceptions
	 */
	public AggregationException(String message, Collection<? extends Throwable> exceptions) {
		super(message);
		if(exceptions == null) {
			this.exceptions = Collections.unmodifiableCollection(new ArrayList<Throwable>(0));
		}
		else {
			this.exceptions = Collections.unmodifiableCollection(exceptions);
		}
	}


	/**
	 * Returns an unmodifiable collection that contains the exceptions passed into this exception. This cannot return
	 * null. If no exceptions were passed into this an empty collection will be returned.
	 * @return
	 */
	public Collection<Throwable> getExceptions() {
		return exceptions;
	}
	

	/**
	 * Returns an unmodifiable collection that contains the exceptions passed into this exception. This cannot return
	 * null. If no exceptions were passed into this an empty collection will be returned.
	 * @return
	 */
	public <T> Collection<T> getExceptions(Class<T> type) {
		return Collections.unmodifiableList(exceptions.stream()
				.map(type::cast).collect(Collectors.toList()));
	}
}

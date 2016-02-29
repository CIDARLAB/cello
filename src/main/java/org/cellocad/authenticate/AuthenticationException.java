package org.cellocad.authenticate;

/**
 *
 * @author evanappleton
 */
public class AuthenticationException 
	extends Exception {
	
	private static final long serialVersionUID = 7476470055381392172L;

	public AuthenticationException(String exp) {
		super(exp);
	}

}

package org.cellocad.authenticate;

/**
 *
 * @author evanappleton
 */
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The UserInformation class defines objects as triplets
 * <Username, Salt, Password>
 * 
 * Since we use the Java Persistence API, we 
 * define the UserInformation class as an 
 * Entity.
 * 
 * @author Ernst Oberortner
 */

@Entity
public class UserInformation {

	@Id
	private String username;

	private byte[] salt;
	private byte[] encrypted_password;
	
	protected UserInformation() {
		this.username = null;
		this.salt = null;
		this.encrypted_password = null;
	}
	
	public UserInformation(String username, byte[] salt, byte[] encrypted_password) {
		this.username = username;
		this.salt = salt;
		this.encrypted_password = encrypted_password;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public byte[] getSalt() {
		return this.salt;
	}
	
	public byte[] getEncryptedPassword() {
		return this.encrypted_password;
	}
}

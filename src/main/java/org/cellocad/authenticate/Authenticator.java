package org.cellocad.authenticate;

/**
 *
 * @author evanappleton
 */

import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Authenticator class represents the interface between the
 * AuthenticationServlet and the persisted objects.
 *
 * @author Ernst Oberortner
 *
 */
public class Authenticator {

    private EntityManager entityManager;

    /**
     * Authenticator Constructor
     *
     * @param db ... the name of the DB which contains username and password
     * information
     */
    public Authenticator(String db) {

        try {
            EntityManagerFactory emf =
                    Persistence.createEntityManagerFactory(db);

            this.entityManager = emf.createEntityManager();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The login/2 method evaluates if a given user and password exists.
     *
     * @param user ... username
     * @param password ... password
     * @return true ... the user exists false ... the user does not exist
     * @throws AuthenticationException
     */

    public String getUsername(String basic_auth) {

        String authString = basic_auth.split(" ")[1];
        byte[] bytesEncoded = authString.getBytes();
        byte[] valueDecoded = Base64.decodeBase64(bytesEncoded);
        String authPlain = new String(valueDecoded);
        String username = authPlain.split(":")[0];
        return username;
    }

    public boolean login(String basic_auth) {
        String authString = basic_auth.split(" ")[1];
        byte[] bytesEncoded = authString.getBytes();
        byte[] valueDecoded = Base64.decodeBase64(bytesEncoded);
        String authPlain = new String(valueDecoded);

        String username = authPlain.split(":")[0];
        String password = authPlain.split(":")[1];

        try {
            return login(username, password);
        } catch (AuthenticationException e) {
            return false;
        }
    }

//    public boolean isAuthorized(String basic_auth) throws AuthenticationException {
//
//        String authString = basic_auth.split(" ")[1];
//        byte[] bytesEncoded = authString.getBytes();
//        byte[] valueDecoded = Base64.decodeBase64(bytesEncoded);
//        String authPlain = new String(valueDecoded);
//        String username = authPlain.split(":")[0];
//        String password = authPlain.split(":")[1];
//
//        return login(username, password);
//    }

    public boolean login(String user, String password)
            throws AuthenticationException {

        UserInformation ui = this.entityManager.find(UserInformation.class, user);
        if (null == ui) {
            // if the user does not exist, we throw an exception
//            throw new AuthenticationException("Invalid Login!");
            return false;
        }

        /*
         * hash & salt the received password
         */
        byte[] received_password = getEncryptedPassword(
                password, ui.getSalt());

        /*
         * now, we compare the given password and 
         * the user's password stored in the DB
         */
        return Arrays.equals(
                received_password,
                ui.getEncryptedPassword());
    }

    /**
     * The register/2 method stores the user including its password
     *
     * @param user ... username
     * @param password ... password
     *
     * @throws AuthenticationException
     */
    public void register(String user, String password, Boolean initialize)
            throws AuthenticationException {

        UserInformation ui = this.entityManager.find(UserInformation.class, user);
        if (null != ui) {
           
            if (initialize == false) {
                // if the user does exist already, then we throw an exception
                throw new AuthenticationException("The user exists already!");
            } else {
                return;
            }
        }

        /*
         * hash & salt the password
         */
        byte[] salt = generateSalt();
        byte[] encrypted_password = getEncryptedPassword(password, salt);

        //BCryptPasswordEncoder bc = new BCryptPasswordEncoder(10);
        //bc.encode(password);

        /*
         * then, we store username and password into 
         * out database
         */
        UserInformation userInfo = new UserInformation(user, salt, encrypted_password);
        this.persist(userInfo);
    }

    /**
     * transaction-based persistence of a UserInformation object
     *
     * @param ui ... the UserInformation object
     */
    private void persist(UserInformation ui) {
        this.entityManager.getTransaction().begin();
        this.entityManager.persist(ui);
        this.entityManager.getTransaction().commit();
    }

    /**
     * Generates a hash from the supplied password and salt (see
     * {@link PasswordEncryptionService#generateSalt()}) using the PBKDF2 with
     * SHA-1 algorithm
     *
     *
     * @param password - Password to hash
     * @param salt - Salt to use for the hashing
     * @return - Password hash
     */
    private byte[] getEncryptedPassword(String password, byte[] salt) {
        try {
            // PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
            // specifically names SHA-1 as an acceptable hashing algorithm for
            // PBKDF2
            String algorithm = "PBKDF2WithHmacSHA1";
            // SHA-1 generates 160 bit hashes, so that's what makes sense here
            int derivedKeyLength = 160;
            // Pick an iteration count that works for you. The NIST recommends
            // at
            // least 1,000 iterations:
            // http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
            // iOS 4.x reportedly uses 10,000:
            // http://blog.crackpassword.com/2010/09/smartphone-forensics-cracking-blackberry-backup-passwords/
            int iterations = 20000;

            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt,
                    iterations, derivedKeyLength);

            SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

            return f.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
//        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
//            /*
//             * This should only happen when running this code on a new
//             * environment. It will never happen unpredictably and therefore is
//             * caught here so that utilizing code doesn't require a try/catch
//             * block.
//             */
//            e.printStackTrace();
//       	}
        return null;
    }

    /**
     * SALT generator
     */
    private byte[] generateSalt() {
        try {
            // we're using SecureRandom instead of just Random
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

            // Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
            byte[] salt = new byte[8];
            random.nextBytes(salt);

            return salt;
        } catch (NoSuchAlgorithmException e) {
            /*
             * This should only happen when running this code on a new
             * environment. It will never happen unpredictably and therefore is
             * caught here so that utilizing code doesn't require a try/catch
             * block.
             */
            e.printStackTrace();
        }
        return null;
    }
}

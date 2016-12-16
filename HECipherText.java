import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.Vector;

import edu.biu.scapi.midLayer.asymmetricCrypto.keys.DamgardJurikPrivateKey;
import edu.biu.scapi.midLayer.asymmetricCrypto.keys.DamgardJurikPublicKey;
import edu.biu.scapi.midLayer.asymmetricCrypto.keys.KeySendableData;
import edu.biu.scapi.midLayer.asymmetricCrypto.keys.ScDamgardJurikPrivateKey;
import edu.biu.scapi.midLayer.asymmetricCrypto.keys.ScDamgardJurikPublicKey;
import edu.biu.scapi.midLayer.asymmetricCrypto.encryption.DamgardJurikEnc;
import edu.biu.scapi.midLayer.ciphertext.AsymmetricCiphertext;
import edu.biu.scapi.midLayer.ciphertext.AsymmetricCiphertextSendableData;
import edu.biu.scapi.midLayer.ciphertext.BigIntegerCiphertext;
import edu.biu.scapi.midLayer.plaintext.BigIntegerPlainText;
import edu.biu.scapi.midLayer.plaintext.Plaintext;

class HECipherText {
    private AsymmetricCiphertext ct;
    private HEKey key;
    
    public HECipherText(HEKey key) {
	this.ct = new BigIntegerCiphertext(BigInteger.ONE);
	this.key = key;
    }

    public HECipherText(AsymmetricCiphertext ct, HEKey key) {
	this.ct = ct;
	this.key = key;
    }

    public HEKey key() {
	return this.key;
    }

    public HECipherText addCT(HECipherText other) {
	AsymmetricCiphertext act = key.dj.add(ct, other.ct, BigInteger.ONE);
	return new HECipherText(act, key);
    }

    public HECipherText prodCT(HEPlainText pt) {
	AsymmetricCiphertext act = key.dj.multByConst(ct, pt.getN(), BigInteger.ONE);
	return new HECipherText(act, key);
    }
    
    public static HECipherText encryptCT(HEPlainText pt, HEKey key) {
	HECipherText hct = new HECipherText(key);
	hct.encrypt(pt);
	return hct;
    }

    public void encrypt(HEPlainText pt) {
	ct = key.dj.encrypt(pt.getValue());
    }

    public HEPlainText decrypt() {
	try{
	    return new HEPlainText(key.dj.decrypt(ct));
	} catch (KeyException e) {
	    System.err.println(e.getMessage());
	    System.exit(1);
	    return new HEPlainText(0);
	}
    }
    
    public static void main(String[] args) {
	HEKey key = new HEKey();
	HEPlainText pt = new HEPlainText(1);
	String text = "Bob, Regarding Patti Sullivan's contributions to the west desk this year, her \n"
	    + "efforts deserve recognition and a PBR award.  Patti stepped up to fill the\n"
	    + "gap left by Randy Gay's personal leave.  Patti held together the scheduling\n"
	    + "traders came to depend on the information Patti provided.   This information\n"
	    + "has been extremely critical this year due to the pipeline explosion and size\n"
	    + "\n"
	    + "Phillip";
	BigInteger textint = new BigInteger(text.getBytes());
	HECipherText ct = encryptCT(pt, key);
	System.out.println("pt value is " + pt.getN());
	System.out.println("text length is " + text.getBytes().length);
	for (int i = 0; i < 20; i++) {
	    ct.decrypt();
	}
    }
}

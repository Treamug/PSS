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

import edu.biu.scapi.exceptions.NoMaxException;
import edu.biu.scapi.midLayer.asymmetricCrypto.keys.DamgardJurikPrivateKey;
import edu.biu.scapi.midLayer.asymmetricCrypto.keys.DamgardJurikPublicKey;
import edu.biu.scapi.midLayer.asymmetricCrypto.keys.KeySendableData;
import edu.biu.scapi.midLayer.asymmetricCrypto.keys.ScDamgardJurikPrivateKey;
import edu.biu.scapi.midLayer.asymmetricCrypto.keys.ScDamgardJurikPublicKey;
import edu.biu.scapi.midLayer.ciphertext.AsymmetricCiphertext;
import edu.biu.scapi.midLayer.ciphertext.AsymmetricCiphertextSendableData;
import edu.biu.scapi.midLayer.ciphertext.BigIntegerCiphertext;
import edu.biu.scapi.midLayer.plaintext.BigIntegerPlainText;
import edu.biu.scapi.midLayer.plaintext.Plaintext;

class HEPlainText {
    private Plaintext pt;
    public static HEPlainText ZERO = new HEPlainText(0);
    public static HEPlainText ONE = new HEPlainText(1);
    public HEPlainText(int plaintext) {
	this.pt = new BigIntegerPlainText(BigInteger.valueOf(plaintext));
    }

    public HEPlainText(Plaintext plaintext) {
	this.pt = plaintext; 
    }

    public HEPlainText(BigInteger plaintext) {
	this.pt = new BigIntegerPlainText(plaintext);
    }

    public Plaintext getValue() {
	return pt;
    }

    public BigInteger getN() {
	return ((BigIntegerPlainText)pt).getX();
    }
    
    public boolean equals(HEPlainText other) {
	BigInteger v1 = ((BigIntegerPlainText)pt).getX();
	BigInteger v2 = ((BigIntegerPlainText)other.pt).getX();
	return v1 == v2;
    }
    
    public HEPlainText dividedBy(HEPlainText other) {
	BigInteger v1 = ((BigIntegerPlainText)pt).getX();
	BigInteger v2 = ((BigIntegerPlainText)other.pt).getX();
	return new HEPlainText(v1.divide(v2));
    }

    public static void main(String[] args) {
	HEPlainText p = new HEPlainText(0);
	System.out.println(p.equals(HEPlainText.ZERO));
	System.out.println(p.equals(HEPlainText.ONE));
    }
}

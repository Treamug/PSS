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

import org.bouncycastle.util.BigIntegers;

import edu.biu.scapi.midLayer.asymmetricCrypto.encryption.DJKeyGenParameterSpec;
import edu.biu.scapi.midLayer.asymmetricCrypto.encryption.DamgardJurikEnc;
import edu.biu.scapi.midLayer.asymmetricCrypto.encryption.ScDamgardJurikEnc;
import edu.biu.scapi.midLayer.ciphertext.AsymmetricCiphertext;

class HEKey {
    public DamgardJurikEnc dj;
    public HEKey() {
	try {
	    dj = new ScDamgardJurikEnc();
	    DJKeyGenParameterSpec spec = new DJKeyGenParameterSpec(2048, 40);
	    KeyPair keys = dj.generateKey(spec);
	    dj.setKey(keys.getPublic(), keys.getPrivate());
	    dj.setLengthParameter(2);
	} catch (InvalidParameterSpecException|InvalidKeyException e) {
	    System.err.println(e.getMessage());
	    System.exit(1);
	}
    }

    public static void main(String[] args) {
	HEKey key = new HEKey();
    }
}

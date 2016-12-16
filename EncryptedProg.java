import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;

/*

  At this layer of abstraction, all plaintext are HEPlainText, keyword is an array of 
  0s and 1s, all ciphertext are HECipherText. So the conversion from document to 
  plaintext should be out side this class and vise-versa.

  The PlainText will be implemented using BigInteger.

 */

class EncryptedProg {
    HECipherText[] buffer;
    HECipherText[] counts;
    HECipherText[] kws;
    HEKey key;
    int gamma;

    public EncryptedProg(int len, int g, HEKey key, int[] keywords) {
	this.key = key;
	this.gamma = g;
	buffer = new HECipherText[len*gamma];
	counts = new HECipherText[len*gamma];
	reset();
	kws = new HECipherText[keywords.length];
	for (int i = 0; i < keywords.length; i++) {
	    kws[i] = HECipherText.encryptCT(new HEPlainText(keywords[i]), key);
	}
    }

    public void reset() {
	for (int i = 0; i < buffer.length; i++) {
	    buffer[i] = new HECipherText(key);
	    counts[i] = new HECipherText(key);
	}
    }
    
    public void insert(HEPlainText document, Set<Integer> keywords) {
	HECipherText count = new HECipherText(key);
	Random rnd = new Random();
	for (int i : keywords) {
	    count = count.addCT(kws[i]);
	}
	HECipherText cdocument = count.prodCT(document);
	Set<Integer> positions = new HashSet<Integer>();
	while (positions.size() < gamma) {
	    positions.add(rnd.nextInt(buffer.length));
	}
	for (int i : positions) {
	    counts[i] = counts[i].addCT(count);
	    buffer[i] = buffer[i].addCT(cdocument);
	}
    }

    public Set<HEPlainText> extract() {
	Set<HEPlainText> documents = new HashSet<HEPlainText>();
	for (int i = 0; i < buffer.length; i++) {
	    HEPlainText c = counts[i].decrypt();
	    //System.err.println("extracting " + c.getN());
	    if (!c.equals(HEPlainText.ZERO)) {
		documents.add(buffer[i].decrypt().dividedBy(c));
	    }
	}
	return documents;
    }
    
    public static void main(String[] args) {
	int[] a = {0, 1, 0, 0, 1};
	EncryptedProg ep = new EncryptedProg(10, 5,  new HEKey(), a);
	HEPlainText[] documents = {
	    new HEPlainText(15),
	    new HEPlainText(25),
	    new HEPlainText(10)
	};
	ArrayList<Set<Integer>> keywords = new ArrayList<Set<Integer>>();
	keywords.add(new HashSet<Integer>() {{ add(1); add(2); }});
	keywords.add(new HashSet<Integer>() {{ add(0); add(4); }});
	keywords.add(new HashSet<Integer>() {{ add(0); add(3); }});
	for (int i = 0; i < documents.length; i++) {
	    ep.insert(documents[i], keywords.get(i));
	}
	Set<HEPlainText> out = ep.extract();
	for (HEPlainText pt : out) {
	    System.out.println(pt.getN());
	}
    }
}

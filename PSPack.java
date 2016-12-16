import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Arrays;
import java.security.MessageDigest;
import java.nio.ByteBuffer;

class PSPack {
    public byte[] buffer;
    public byte[] content;
    public int id;
    public int count;
    public PSPack(byte[] buffer) {
	this.buffer = Arrays.copyOf(buffer, buffer.length);
    }
    public PSPack(int id, int count, int size, byte[] content) {
	this.id = id;
	this.count = count;
	this.content = Arrays.copyOf(content, content.length);
	this.buffer = new byte[size-8];
	ByteBuffer bb = ByteBuffer.allocate(size-8);
	bb.putInt(id);
	bb.putInt(count);
	bb.put(content);
	buffer = bb.array();
	MessageDigest md;
	try{
	    md = MessageDigest.getInstance("SHA");
	    md.update(buffer);
	    byte[] hash = md.digest();
	    byte[] newbuffer = new byte[size];
	    System.arraycopy(buffer, 0, newbuffer, 0, size-8);
	    System.arraycopy(hash, 0, newbuffer, size-8, 8);
	    this.buffer = newbuffer;
	} catch(NoSuchAlgorithmException e) {
	    System.err.println(e.getMessage());
	    System.exit(1);
	}
    }
    public byte[] dump() {
	return Arrays.copyOf(buffer, buffer.length);
    }
    public boolean load() {
	MessageDigest md;
	try{
	    md = MessageDigest.getInstance("SHA");
	    byte[] payload = Arrays.copyOfRange(buffer, 0, buffer.length - 8);
	    byte[] hash = Arrays.copyOfRange(buffer, buffer.length - 8, buffer.length);
	    byte[] hashfull = md.digest(payload);
	    for (int i = 0; i < 8; i++) {
		if (hash[i] != hashfull[i]) return false;
	    }
	    ByteBuffer bb = ByteBuffer.wrap(payload);
	    id = bb.getInt();
	    count = bb.getInt();
	    content = new byte[buffer.length-16];
	    bb.get(content);
	    return true;
	} catch(NoSuchAlgorithmException e) {
	    System.err.println(e.getMessage());
	    System.exit(1);
	    return false;
	}
    }
    public static void main(String[] args) {
	PSPack pack = new PSPack(1, 10, 512, "lskjdlfjs".getBytes());
	byte[] bs = pack.dump();
	PSPack new_pack = new PSPack(bs);
	if (new_pack.load()) {
	    System.out.println("ID: " + new_pack.id);
	    System.out.println("Count: " + new_pack.id);
	    System.out.println("Content with length " + new_pack.content.length);
	    System.out.println(new String(new_pack.content));
	} else {
	    System.out.println("Error on decoding");
	}
    }
}

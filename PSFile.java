import java.lang.Math;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Paths;

class PSFile {
    public byte[] content;
    public int id;

    public PSFile(String path) {
	try {
	    content = Files.readAllBytes(Paths.get(path));
	} catch (IOException e) {
	    System.err.println(e.getMessage());
	}
	Random random = new Random();
	id = random.nextInt();
	while (id < 0x01000000) {
	    id = random.nextInt();
	}
    }

    public PSFile(byte[] con) {
	content = Arrays.copyOf(con, con.length);
	Random random = new Random();
	id = random.nextInt();
	while (id < 0x01000000) {
	    id = random.nextInt();
	}
    }
    
    public ArrayList<PSPack> getPacks(int size) {
	int cursor;
	int pack_size = size - 16;
	ArrayList<PSPack> packs = new ArrayList<PSPack>();
	for (cursor = 0; cursor < content.length; cursor += pack_size) {
	    packs.add(new PSPack(id, cursor, size, Arrays.copyOfRange(content, cursor,
								      cursor+pack_size)));
	}
	return packs;
    }
    
    public static void main(String[] args) {
	PSFile f = new PSFile("maildir/allen-p/all_documents/1.");
	for (PSPack ps: f.getPacks(512)) {
	    System.out.println("---- new block ----");
	    System.out.println(new String(ps.content));
	}
    }
}

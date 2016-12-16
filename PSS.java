import java.math.BigInteger;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

class PSS {
    private Map<String, Integer> kmap;
    private ArrayList<String> klist;
    private EncryptedProg ep;

    static CharsetEncoder asciiEncoder =
	Charset.forName("US-ASCII").newEncoder();
    
    private void readKUniv(String path) throws IOException {
	File f = new File(path);
	BufferedReader br = new BufferedReader(new FileReader(f));
	kmap = new HashMap<String, Integer>();
	klist = new ArrayList<String>();
	String line = null;
	int i = 0;
	while ((line = br.readLine()) != null) {
	    kmap.put(line, new Integer(i));
	    klist.add(line);
	    i += 1;
	}
	br.close();
    }

    private int[] readKeywords(String path) throws IOException {
	File f = new File(path);
	BufferedReader br = new BufferedReader(new FileReader(f));
	int[] v_keywords = new int[kmap.size()];
	String line = null;
	while ((line = br.readLine()) != null) {
	    Integer index = kmap.get(line);
	    if (index == null) {
		System.err.println("Keyword " + line + " not in the universe");
	    } else {
		v_keywords[index] = 1;
	    }
	}
	br.close();
	for (int i = 0; i < v_keywords.length; i++) {
	    // System.out.println(v_keywords[i]);
	}
	return v_keywords;
    }

    
    public void setUp(int m, int l) throws IOException {
	readKUniv("data/keywords/word_list");
	//String[] karray = {"Don't know how to do this",};
	//karray = klist.toArray(karray);
	int[] filter = readKeywords("data/keywords/target");
	//filter[0] = 1;
	//filter[5] = 1;
	ep = new EncryptedProg(m, l, new HEKey(), filter);
    }

    private String readEmail(File f) throws IOException {
	BufferedReader br = new BufferedReader(new FileReader(f));
	String line = null;
	String paragraph = "";
	boolean beginRead = false;
	while ((line = br.readLine()) != null) {
	    if (beginRead) {
		paragraph = paragraph + (line + "\n");
	    }
	    if (line.isEmpty()) {
		beginRead = true;
	    }
	}
	br.close();
	if (!asciiEncoder.canEncode(paragraph)) {
	    throw new IOException();
	}
	return paragraph;
    }

    private Set<Integer> kSet(String para) {
	String[] words = para.split("\\W+");
	Set<Integer> indices = new HashSet<Integer>();
	for (int i = 0; i < words.length; i++) {
	    Integer x = kmap.get(words[i].toLowerCase());
	    if (x == null) continue;
	    indices.add(x);
	}
	return indices;
    }

    private void insertDoc(String para, Set<Integer> kset) {
	byte[] b = para.getBytes();
	PSFile psf = new PSFile(b);
	ArrayList<PSPack> psps = psf.getPacks(250);
	for (PSPack pack : psps) {
	    byte[] content = pack.dump();
	    //System.err.println(new BigInteger(content));
	    HEPlainText hpt = new HEPlainText(new BigInteger(content));
	    ep.insert(hpt, kset);
	}
    }

    public void filterAll(File f) throws IOException {
	if (f.isFile()){
	    try {
		String para = readEmail(f);
		Set<Integer> kset = kSet(para);
		insertDoc(para, kset);
	    } catch (IOException e) {
		return;
	    }
	} else if (f.isDirectory()) {
	    File[] fileList = f.listFiles();
	    for (File child : fileList) {
		filterAll(child);
	    }
	}
	
    }

    public void recoverAll() {
	Set<HEPlainText> out = ep.extract();
	for (HEPlainText pt : out) {
	    BigInteger num = pt.getN();
	    PSPack psp = new PSPack(num.toByteArray());
	    if (psp.load()) {
		System.err.println("------------ pack ---------------");
		System.err.println(new String(psp.content));
		System.err.println("<<<<<<<<<<<< end <<<<<<<<<<<<<<<");
	    }
	}
    }
    
    public static void main(String[] args) throws IOException{
	PSS p = new PSS();
	long startTime, endTime;
	startTime = System.currentTimeMillis();
	p.setUp(10, 5);
	endTime = System.currentTimeMillis();
	System.out.println("setup took " + (endTime - startTime)/1000 + " seconds");
	File f = new File("data/emails");
	p.filterAll(f);
	startTime = endTime;
	endTime = System.currentTimeMillis();
	System.out.println("filter took " + (endTime - startTime)/1000 + " seconds");
	p.recoverAll();
	startTime = endTime;
	endTime = System.currentTimeMillis();
	System.out.println("decrypt took " + (endTime - startTime)/1000 + " seconds");
    }
}

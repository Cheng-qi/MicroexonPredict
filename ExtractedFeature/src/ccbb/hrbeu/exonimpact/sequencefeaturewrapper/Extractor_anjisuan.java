package ccbb.hrbeu.exonimpact.sequencefeaturewrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;

public class Extractor_anjisuan {
	
	private static Logger log=Logger.getLogger(Extractor_anjisuan.class);
	
	private static Extractor_anjisuan instance=null;
	
	public static Extractor_anjisuan get_instance(){
		if(instance==null){
			instance=new Extractor_anjisuan();
		}
		
		return instance;
	}
	
	public Extractor_anjisuan() {
		super();
	}
	
	private HashMap<String, IndexedFastaSequenceFile> faiGenome = new HashMap<String,IndexedFastaSequenceFile>();
	
	public String getSequence(String chr, int start, int end) {
		log.trace("chr: "+chr+" start: "+start+" end: "+end);
		
		byte[] seq = faiGenome.get(chr).getSubsequenceAt(chr, start, end).getBases();
		
		String str = new String(seq);
		return str;

	}
	
	public void init(String fastaPath) {
		
		log.trace("fasta_path: "+fastaPath);
		
		String rootPath = fastaPath;
		File file = new File(rootPath);
		String[] pathList = file.list();
		
		log.trace("Number of fasta files: "+pathList.length);
		
		try {
			
			for (int i = 0; i < pathList.length; ++i) {
				String fasta = rootPath + pathList[i];
				

				File fastaFile = new File(fasta);
				
				IndexedFastaSequenceFile testFai;
				
				
					testFai = new IndexedFastaSequenceFile(fastaFile);
				
				// String chr = pathList[i].split("\\.", 3)[0];
				String chr = pathList[i].substring(0, pathList[i].length() - 3);
				log.trace("genome contain the fasta is: "+chr);
				faiGenome.put(chr, testFai);

			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
			System.out.println(e.getMessage());
		} 

	}

	public void testTbi() {

	}

	public void testFai() throws FileNotFoundException {
		// File fastaFile = new
		// File("F:\\AS-pipeline\\data\\mm9\\fasta\\chr1.fa");
		// IndexedFastaSequenceFile testFai = new
		// IndexedFastaSequenceFile(fastaFile);
		// ReferenceSequence x = testFai.getSubsequenceAt("chr1", 0, 100);
		// System.out.println(x.getBases()[3]);
		// String.format();

	}

	// IndexedGenome index= IndexedGenome.readIndex(indexFile);
	public static void main(String[] args) throws FileNotFoundException {
		Extractor_anjisuan name = new Extractor_anjisuan();
		// name.testFai();
		name.init("D:\\360°²È«ä¯ÀÀÆ÷ÏÂÔØ\\ensembl_seq\\");		
		System.out.println(name.getSequence("ENST00000000412", 1, 10));

	}

}

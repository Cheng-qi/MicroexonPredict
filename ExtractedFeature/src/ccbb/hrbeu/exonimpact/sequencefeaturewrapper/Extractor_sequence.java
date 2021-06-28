package ccbb.hrbeu.exonimpact.sequencefeaturewrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import htsjdk.samtools.reference.FastaSequenceIndex;
import htsjdk.samtools.reference.IndexedFastaSequenceFile;

public class Extractor_sequence {
	
	private static Logger log=Logger.getLogger(Extractor_sequence.class);
	
	private static Extractor_sequence instance=null;
	
	public static Extractor_sequence get_instance(){
		if(instance==null){
			instance=new Extractor_sequence();
		}
		
		return instance;
	}
	
	public Extractor_sequence() {
		super();
	}
	
	private HashMap<String, IndexedFastaSequenceFile> faiGenome = new HashMap<String, IndexedFastaSequenceFile>();
	
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
				if (!fasta.endsWith(".fa"))
					continue;

				File fastaFile = new File(fasta);
				File indexFile = new File(fasta + ".fai");
				IndexedFastaSequenceFile testFai;
				
				if (indexFile.exists())
					testFai = new IndexedFastaSequenceFile(fastaFile,
							new FastaSequenceIndex(indexFile));
				else {
					log.trace("index file : "+indexFile);
					// testFai = new IndexedFastaSequenceFile(fastaFile);
					Process p = Runtime.getRuntime().exec(
							"/usr/bin/samtools faidx " + fasta);
					Thread.sleep(2 * 1000);
					System.out.println(fasta);
					p.waitFor();
					testFai = new IndexedFastaSequenceFile(fastaFile,
							new FastaSequenceIndex(indexFile));

				}

				// String chr = pathList[i].split("\\.", 3)[0];
				String chr = pathList[i].substring(0, pathList[i].length() - 3);
				log.trace("genome contain the fasta is: "+chr);
				faiGenome.put(chr, testFai);

			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		Extractor_sequence name = new Extractor_sequence();
		// name.testFai();
		name.init("F:\\4\\chrom\\");		
		System.out.println(name.getSequence("chr1", 1, 1000));

	}

}

package ccbb.hrbeu.exonimpact.sequencefeaturewrapper;

import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import ccbb.hrbeu.exonimpact.genestructure.Exon;
import ccbb.hrbeu.exonimpact.genestructure.Transcript;
import ccbb.hrbeu.exonimpact.genestructure.Transcript.ASTYPE;
import htsjdk.tribble.annotation.Strand;

public class Miso_decoder {
	
	private static Logger log=Logger.getLogger(Miso_decoder.class); 
	
	private static Miso_decoder instance = null;

	public static Miso_decoder get_instance() {
		if (instance == null) {
			instance = new Miso_decoder();
		}

		return instance;
	}

	public Miso_decoder() {

	}

	public Miso_decoder(String one_event) {
		oneEvent = one_event;
		this.buildGeneFrag();
	}

	public Transcript get_transcript(String input) {

		oneEvent = input;
		fragment = new Transcript();
		buildGeneFrag();

		return fragment;
	}

	private String oneEvent;
	private ASTYPE asType;

	Transcript fragment = new Transcript();
	private String chr;

	private Strand strand;

	public static String regexCasseteExon = "chr\\w+:(\\d+):(\\d+):(\\+|-)@chr\\w+:(\\d+):(\\d+):(\\+|-)@chr\\w+:(\\d+):(\\d+):(\\+|-)";

	public static String regexA5SS = "chr\\w+:(\\d+):(\\d+)\\|(\\d+):(\\+|-)@chr(\\w+):(\\d+):(\\d+):(\\+|-)";

	public static String regexA3SS = "chr(\\w+):(\\d+):(\\d+):(\\+|-)@chr\\w+:(\\d+)\\|(\\d+):(\\d+):(\\+|-)";

	public static String regexRetainedIntro = "chr(\\w+):(\\d+)-(\\d+):(\\+|-)@chr(\\w+):(\\d+)-(\\d+):(\\+|-)";

	private void buildGeneFragPostive() {
		String[] args = oneEvent.split("\\@", 20);
		if (asType.equals(ASTYPE.SE)) {

			for (int i = 0; i < args.length; ++i) {
				String[] quer = args[i].split("\\:", 20);
				int beg = Integer.parseInt(quer[1]);
				int end = Integer.parseInt(quer[2]);

				// fragment.add(new FullBEDFeature.Exon(organism, quer[0], beg,
				// end, strand);
				chr = quer[0];

				fragment.addExon(new Exon(chr, beg, end));
				
				if (i == 1) {
					fragment.setTarget_start(beg);
					fragment.setTarget_end(end);
				}else{
					fragment.flank_exons.add(new Exon(chr,beg,end) );
				}

			}
			fragment.setChr(chr);

		}

		if (asType.equals(ASTYPE.RI)) {

			String[] quert = args[0].split("\\:", 20);
			chr = quert[0];

			String[] quer = quert[1].split("-", 20);
			int beg1 = Integer.parseInt(quer[0]);
			int end1 = Integer.parseInt(quer[1]);

			fragment.addExon( new Exon(chr, beg1, end1) );

			quert = args[1].split("\\:", 20);
			quer = quert[1].split("-", 20);

			int beg2 = Integer.parseInt(quer[0]);
			int end2 = Integer.parseInt(quer[1]);
			fragment.addExon(new Exon(chr,end1 + 1, beg2 - 1) );
			fragment.addExon(new Exon(chr, beg2, end2));

			fragment.setChr(chr);
			fragment.setTarget_start(beg1);
			fragment.setTarget_end(end2);
		}

		if (asType.equals(ASTYPE.A5SS)) {

			String[] quer = args[0].split("\\:", 20);
			String[] begs = quer[2].split("\\|");
			int beg = Integer.parseInt(quer[1]);

			int end = Integer.parseInt(begs[1]);
			int endAS = Integer.parseInt(begs[0]);
			
			chr = quer[0];

			fragment.addExon(new Exon(chr ,beg, endAS));
			fragment.addExon(new Exon(chr, endAS + 1, end));

			quer = args[1].split("\\:", 20);
			int beg2 = Integer.parseInt(quer[1]);
			int end2 = Integer.parseInt(quer[2]);
			fragment.addExon(new Exon(chr,beg2, end2));
			fragment.setChr(chr);

			fragment.setTarget_start(beg);
			fragment.setTarget_end(end);
			
			fragment.flank_exons.add(new Exon(chr,beg2,end2));
		}

		if (asType.equals(ASTYPE.A3SS)) {

			String[] quer = args[0].split("\\:", 20);
			int beg = Integer.parseInt(quer[1]);
			int end = Integer.parseInt(quer[2]);
			
			chr = quer[0];

			fragment.addExon(new Exon(chr,beg, end));
			fragment.flank_exons.add(new Exon(chr,beg,end));

			quer = args[1].split("\\:", 20);
			String[] ends = quer[1].split("\\|");

			end = Integer.parseInt(quer[2]);
			beg = Integer.parseInt(ends[0]);
			int begAS = Integer.parseInt(ends[1]);

			fragment.addExon(new Exon(chr,beg, begAS - 1));
			fragment.addExon(new Exon(chr,begAS, end));
			fragment.setChr(chr);

			fragment.setTarget_start(beg);
			fragment.setTarget_end(end);
		}

	}

	private void buildGeneFragNegative() {
		String[] args = oneEvent.split("\\@", 20);
		if (asType.equals(ASTYPE.SE)) {

			for (int i = args.length - 1; i >= 0; --i) {
				String[] quer = args[i].split("\\:", 20);
				int beg = Integer.parseInt(quer[1]);
				int end = Integer.parseInt(quer[2]);
				chr = quer[0];

				fragment.addExon(new Exon(chr,beg, end));
				fragment.setChr(chr);

				if (i == 1) {
					fragment.setTarget_start(beg);
					fragment.setTarget_end(end);
				}else{
					fragment.flank_exons.add(new Exon(chr,beg,end));
				}
			}

		}

		if (asType.equals(ASTYPE.RI)) {

			String[] quert = args[1].split("\\:", 20);
			chr = quert[0];
			String[] quer = quert[1].split("-", 20);

			int beg1 = Integer.parseInt(quer[1]);
			int end1 = Integer.parseInt(quer[0]);

			fragment.addExon(new Exon(chr, beg1, end1));

			quert = args[0].split("\\:", 20);
			quer = quert[1].split("-", 20);

			int beg2 = Integer.parseInt(quer[1]);
			int end2 = Integer.parseInt(quer[0]);
			fragment.addExon(new Exon(chr, end1 + 1, beg2 - 1));
			fragment.addExon(new Exon(chr, beg2, end2));

			fragment.setTarget_start(end1 + 1);
			fragment.setTarget_end(end2);
		}

		if (asType.equals(ASTYPE.A5SS)) {

			String[] quer = args[1].split("\\:", 20);
			int beg = Integer.parseInt(quer[1]);
			int end = Integer.parseInt(quer[2]);
			
			chr = quer[0];

			fragment.addExon(new Exon(chr,beg, end));
			fragment.flank_exons.add(new Exon(chr,beg,end));
			
			quer = args[0].split("\\:", 20);
			String[] begs = quer[2].split("\\|");
			end = Integer.parseInt(quer[1]);

			beg = Integer.parseInt(begs[1]);
			int begAS = Integer.parseInt(begs[0]);

			fragment.addExon(new Exon(chr,begAS, beg - 1));
			fragment.addExon(new Exon(chr,beg, end));

			fragment.setTarget_start(begAS);
			fragment.setTarget_end(end);
		}

		if (asType.equals(ASTYPE.A3SS)) {
			String[] quer = args[1].split("\\:", 20);
			String[] ends = quer[1].split("\\|");

			int beg = Integer.parseInt(quer[2]);
			int endAS = Integer.parseInt(ends[0]);
			int end = Integer.parseInt(ends[1]);
			
			chr = quer[0];

			fragment.addExon(new Exon(chr,beg, endAS));
			fragment.addExon(new Exon(chr,endAS + 1, end));
			quer = args[0].split("\\:", 20);
			int beg2 = Integer.parseInt(quer[1]);
			int end2 = Integer.parseInt(quer[2]);

			fragment.addExon(new Exon(chr,beg2, end2));

			fragment.setTarget_start(beg);
			fragment.setTarget_end(end);
			
			fragment.flank_exons.add(new Exon(chr,beg2,end2));
			
		}
	}

	protected void buildGeneFrag() {
		
		asType = tellASType(oneEvent);
		fragment.set_as_type(asType);
		log.trace("The event is: "+oneEvent);
		log.trace("The event's as type is: "+asType.toString() );

		if (oneEvent.endsWith("-")) {
			strand = Strand.NEGATIVE;
			buildGeneFragNegative();
		} else {
			strand = Strand.POSITIVE;
			buildGeneFragPostive();
		}

		fragment.setStrand(strand);
		fragment.getExons().get(1).setAlternative(true);
		
	}

	public static ASTYPE tellASType(String pos) {
		if (Pattern.matches(regexCasseteExon, pos.subSequence(0, pos.length())))
			return ASTYPE.SE;
		else if (Pattern.matches(regexA5SS, pos.subSequence(0, pos.length())))
			return ASTYPE.A5SS;
		else if (Pattern.matches(regexA3SS, pos.subSequence(0, pos.length())))
			return ASTYPE.A3SS;
		else if (Pattern.matches(regexRetainedIntro, pos.subSequence(0, pos.length())))
			return ASTYPE.RI;

		return ASTYPE.UNKNOWN;

	}

	public static void main(String[] args) {
		// Miso_decoder new_decoder=new Miso_decoder();
		Transcript t = Miso_decoder.get_instance()
				.get_transcript("chr2:9624561:9624679:+@chr2:9627585:9627676:+@chr2:9628276:9628591:+");

		for (Exon iter_exon : t.getExons()) {
			System.out.println(iter_exon.getExonBegCoorPos() );
		}

	}

}

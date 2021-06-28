package ccbb.hrbeu.exonimpact.genestructure;

import java.util.regex.Pattern;

import htsjdk.tribble.AsciiFeatureCodec;
import htsjdk.tribble.annotation.Strand;
import htsjdk.tribble.readers.LineIterator;
import htsjdk.tribble.util.ParsingUtils;

public class ExternBEDCodec extends AsciiFeatureCodec<ExtendBEDFeature> {

	public ExternBEDCodec() {
		super(ExtendBEDFeature.class);
		// TODO Auto-generated constructor stub
	}

	static int startOffsetValue = 1;
	private static final Pattern SPLIT_PATTERN = Pattern.compile("\\t|( +)");

	protected boolean readHeaderLine(String line) {
		// We don't parse BED header
		return false;
	}

	public ExtendBEDFeature decode(String line) {

		if (line.trim().length() == 0) {
			return null;
		}

		if (line.startsWith("#") || line.startsWith("track") || line.startsWith("browser")) {
			this.readHeaderLine(line);
			return null;
		}

		String[] tokens = SPLIT_PATTERN.split(line, -1);
		return decode(tokens);

	}

	public ExtendBEDFeature decode(String[] tokens) {
		int tokenCount = tokens.length;

		// The first 3 columns are non optional for BED. We will relax this
		// and only require 2.

		if (tokenCount < 2) {
			return null;
		}

		String chr = tokens[0];

		// The BED format uses a first-base-is-zero convention, Tribble features
		// use 1 => add 1.
		int start = Integer.parseInt(tokens[1]) + startOffsetValue;

		int end = start;
		if (tokenCount > 2) {
			end = Integer.parseInt(tokens[2]);
		}

		ExtendBEDFeature feature = new ExtendBEDFeature(chr, start, end);

		// The rest of the columns are optional. Stop parsing upon encountering
		// a non-expected value

		// Name
		if (tokenCount > 3) {
			String name = tokens[3].replaceAll("\"", "");
			feature.setName(name);
		}

		// Score
		if (tokenCount > 4) {
			try {
				float score = Float.parseFloat(tokens[4]);
				feature.setScore(score);
			} catch (NumberFormatException numberFormatException) {

				// Unexpected, but does not invalidate the previous values.
				// Stop parsing the line here but keep the feature
				// Don't log, would just slow parsing down.
				return feature;
			}
		}

		// Strand
		if (tokenCount > 5) {
			String strandString = tokens[5].trim();
			char strand = (strandString.length() == 0) ? ' ' : strandString.charAt(0);

			if (strand == '-') {
				feature.setStrand(Strand.NEGATIVE);
			} else if (strand == '+') {
				feature.setStrand(Strand.POSITIVE);
			} else {
				feature.setStrand(Strand.NONE);
			}
		}

		// Color
		if (tokenCount > 8) {
			String colorString = tokens[8];
			feature.setColor(ParsingUtils.parseColor(colorString));
		}

		// Coding information is optional
		if (tokenCount > 11) {
			createExons(start, tokens, feature, feature.getStrand());
		}
		
		if(tokenCount>12){
			feature.gene_id=tokens[12];
		}
		
		if (tokenCount > 13) {

			if (tokens[13].equals("")){
				feature.is_protein_coding = false;
				feature.protein_id="NA";
			}else{
				feature.is_protein_coding = true;
				feature.protein_id=tokens[13];				
			}
		}

		return feature;
	}

	private void createExons(int start, String[] tokens, ExtendBEDFeature gene, Strand strand)
			throws NumberFormatException {

		int cdStart = Integer.parseInt(tokens[6]) + startOffsetValue;
		int cdEnd = Integer.parseInt(tokens[7]);
		
		gene.transcript.setCds_start(cdStart);
		gene.transcript.setCds_end(cdEnd);
		
		int exonCount = Integer.parseInt(tokens[9]);
		String[] exonSizes = new String[exonCount];
		String[] startsBuffer = new String[exonCount];
		ParsingUtils.split(tokens[10], exonSizes, ',');
		ParsingUtils.split(tokens[11], startsBuffer, ',');

		int exonNumber = (strand == Strand.NEGATIVE ? exonCount : 1);
		
		if (startsBuffer.length == exonSizes.length) {
			for (int i = 0; i < startsBuffer.length; i++) {
				int exonStart = start + Integer.parseInt(startsBuffer[i]);
				int exonEnd = exonStart + Integer.parseInt(exonSizes[i]) - 1;
				//gene.addExon(exonStart, exonEnd, cdStart, cdEnd, exonNumber);
				Exon t_exon=new Exon(gene.getChr(),exonStart,exonEnd);

				if(cdStart<exonEnd&&cdStart>exonStart){
					t_exon.setCds_start(cdStart);
				}else if(exonStart>cdStart){
					t_exon.setCds_start(exonStart);
				}
				
				if(cdEnd>exonStart&&cdEnd<exonEnd){
					t_exon.setCds_end(cdEnd);
				}else if(exonEnd<cdEnd){
					t_exon.setCds_end(exonEnd);					
				}
				
				gene.transcript.addExon(t_exon  );

				if (strand == Strand.NEGATIVE) {
					exonNumber--;
				} else {
					exonNumber++;
				}
			}
		}
	}

	@Override
	public boolean canDecode(String arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object readActualHeader(LineIterator arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}

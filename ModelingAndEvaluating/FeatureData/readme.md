# Microexons and Microindels Features Datasets

## Data Source

- Microindels: The positive data from HGMD, we obtained 2036 NFS-microindels which insert/delete shorter than 30 and multiples of three nucleotides, of those, 1694 and 342 were microdeletions and microinsertions, respectively; The negative data from the 1000 genome project, similarly, we obtained a total of 2,546 neutral microindels, including 1806 microdeletions and 740 microinsertions. 
- Microexons: We extracted almost all microexons with length less than 30 and integer multiples of 3 from hg19 in Ensemble database. After excluded the frame-shift ones and those locating in intron regions or containing stop codons, we obtained 3941 microexons. 

## Features Explanation

- mean_phylop, min_phylop, max_phylop: The mean, min and max value of DNA conservation scores, were from UCSC;
- mean_ASA, min_ASA, max_ASA: The mean, min and max value of accessible surface areas were predicted by SPINE-X;
- mean_disorder, min_disorder, max_disorder: The mean, min and max value of disorder scores of amino acid sequences encoded by microexons or microindels, were prodicted by SPINE-D;
- ss_E, ss_C, ss_H: The secondary structure of amino acid sequence encoded by microexons were the probabilities of α-helix(H), β-fold(E) and random coil(C), respectively;
- mean_(E, C, H), min_(E, C, H), max_(E, C, H): The mean, min and max probabilities of α-helix(H), β-fold(E) and random coil(C), respectively;
- protein_length: The lengths of amino acid sequences encoded by microexons or microindels;
- indel_exon_length: The lengths of microexons or microindels;
- start_length, end_lenth: Distances to terminals.

**Note**: The all features of secondary structures and accessible surface areas were predicted by SPINE-X, and disorder scores were achieved by SPINE-D. Please refer to our paper for more details.





 
# Prediction of Functional Microexons with Transferring Learning

## Introduction
Microexons are a particular kind of exons with length of less than 30 nucleotides. More than 60% of annotated human microexons were found to have high levels of sequence conservation, indicating of potential functions. The tool was developed to predict functional microexons using TCA(transfer component analysis) and  KNN(K-Nearest Neighbor) with *k*=5. Please refer to our paper *Prediction of Functional Microexons with Transferring Learning* for more details.

## Online service
If you only use this tool to predict Functional Microexons, we also provided a long-term [online service](http://MicroExonsPredict.chengqi.site//onlineSevice). 

## Requirement
1. python 3.7.1
2. java 14.0
3. numpy 1.16.0
4. pandas 0.23.4

## Steps of Usage
1. Download databases from http://MicroexonsPredict.chengqi.site/databases.zip;
2. Assign correct databases path in ./configuration.txt;
3. Input your variants (one position based on 0 of microexon per row, example: chr11:233910:233928 that is a microexon with 18bps) in ./input.txt.
4. You can get prediction results in ./results.txt using following command
 ```bash
python Predict.py
``` 

## Note That
1. This program applies to microexons with length less than 30bps and being an integer of 3;
2. Thispositions of inputed microexons are  based on the GRCH37/hg19；
3. This positions of inputed microexons are based on 0.


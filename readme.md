# Prediction of Functional Microexons with Transferring Learning

## Introduce

## Online service
If you only use this tool to predict Functional microexon, we also provided a long-term online service platform at http://chengqi.site/MicroExonsPredict/sevice

## Requirement:
1. python 3.7.1
2. java 14.0
3. numpy 1.16.0
4. pandas 0.23.4

## Steps of Usage:
1. Download databases from http:chengqi.site/MicroexonPredict/databases.zip;
2. Assign correct databases path in ./configuration.txt;
3. Input your variants (one position based on 0 of microexon per row, example: chr11:233910:233928 that is a microexon with 18bps) in ./input.txt.
4. You can get prediction results in ./results.txt using following command
 ```bash
python predict.py
``` 

## Note That:
1. This program applies to microexons with length less than 30bps and being an integer of 3;
2. Thispositions of inputed microexons are  based on the GRCH37/hg19；
3. This positions of inputed microexons are based on 0.


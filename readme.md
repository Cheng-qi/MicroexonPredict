# Prediction of Functional Microexons with Transferring Learning

## Introduce

## Online service
为了方便使用，我们提供了长期的在线服务平台，该平台集成了该


## Requirement:
1.python 3.7.1
2.java 14.0
3.numpy 1.16.0
4.pandas 0.23.4


## Steps of operation:
1. Download four databases from https://pan.baidu.com/s/1ydhlCKcdY5YIUFmnZQR40A (Fetch code: n28b);
2. Assign correct databases path in ./configuration.txt;
3. Input your variants (one position based on 0 of microexon per row, example: chr11:233910:233928  is a microexon with 18bps) in ./input.txt.
4. Double-click on the ./run.exe to get results in ./results.txt.


## Note that:
(1) This program runs based on Windows environment;
(2) This program applies to microexons with length less than 30bps and being an integer of 3;
(3) Thispositions of inputed microexons are  based on the GRCH37/hg19；
(4) This positions of inputed microexons are based on 0.


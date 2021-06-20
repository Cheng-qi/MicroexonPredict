#!/usr/bin/env python
# encoding: utf-8

"""
@version: 3.7
@author: Qi Cheng
@contact: chengqi@hrbeu.edu.cn
@site: https://github.com/Cheng-qi
@software: PyCharm
@file: run.py
@time: 2021/4/30 15:15
"""

import os
import re
import sys

import time

import abstractindelfeature
import numpy as np

def Recognition(cases):
    csv_data = np.loadtxt("./templates", dtype = np.str, delimiter = ",")
    templates_index = csv_data.astype(np.double)
    templates = templates_index[:,:-1]
    indexs = templates_index[:,-1]
    dists=[]
    for template in templates:
        feature_np = np.array(cases)
        dist = np.linalg.norm(feature_np-template)
        dists.append(dist)
    dists_np = np.array(dists)
    sort_np = np.argsort(dists_np)

    k = 5
    # print(indexs[sort_np[:10]])
    if indexs[sort_np[0]] <1e5:
        result = indexs[sort_np[0]]
    else:
        result = sum(indexs[sort_np[:k]])/k
    return result

#归一化
def Standardize(feature):
    feature_std = feature[:]
    feature_std[0] = (feature_std[0]+1.2185)/1.8685
    feature_std[1] = (feature_std[1]+5.895)/6.545
    feature_std[2] = (feature_std[2]+1.156)/1.811

    feature_std[3] = (feature_std[3]-2.0045)/66.914
    feature_std[4] = (feature_std[4]-0.434)/68.484
    feature_std[5] = (feature_std[5]-3.1667)/89.7667

    feature_std[6] = (feature_std[6]-0.0785)/0.9157
    feature_std[7] = (feature_std[7]-0.0759)/0.9181
    feature_std[8] = (feature_std[8]-0.0805)/0.9146

    feature_std[12] = (feature_std[12]-0.0170)/0.9033
    feature_std[13] = (feature_std[13]-0.0149)/0.8945
    feature_std[14] = (feature_std[14]-0.0176)/0.9154

    feature_std[15] = (feature_std[15]-0.0267)/0.8851
    feature_std[16] = (feature_std[16]-0.0238)/0.8876
    feature_std[17] = (feature_std[17]-0.0266)/0.9090


    feature_std[18] = (feature_std[18]-0.0192)/0.9362
    feature_std[19] = (feature_std[19]-0.0139)/0.9415
    feature_std[20] = (feature_std[20]-0.0214)/0.934

    feature_std[21] = (feature_std[21]-5)/34345
    feature_std[22] = (feature_std[22]-3)/24
    feature_std[23] = (feature_std[23]-0)/13351
    feature_std[24] = (feature_std[24]-0)/24487

    return feature_std



if __name__=="__main__":

    basedir = os.path.abspath(os.path.dirname(__file__))
    # print(basedir)
    os.chdir(basedir)
    os.putenv('CLASSPATH', '.;' +
              basedir + '/feature_abstract/jar/commons-beanutils-1.9.3.jar;' +
              basedir + '/feature_abstract/jar/commons-configuration2-2.1.jar;' +
              basedir + '/feature_abstract/jar/commons-io-2.5.jar;' +
              basedir + '/feature_abstract/jar/commons-lang3-3.4.jar;' +
              basedir + '/feature_abstract/jar/igv.jar;' +
              basedir + '/feature_abstract/jar/log4j-1.2.17.jar;' +
              basedir + '/feature_abstract/jar/picard-1.119.jar;' +
              basedir + '/feature_abstract/jar/sqlite-jdbc-3.8.11.2.jar;')

    if len(sys.argv)==3:
        microexons_str = sys.argv[2]
    else:
        # print(os.environ['CLASSPATH'])
        input_file_name = "input.txt"
        # 1.read input
        with open(input_file_name, "r") as in_file:
            microexons_str = in_file.read()
    # print(microexons_str)
    microexons = microexons_str.strip().split("\n")
    microexons_pos_list = []
    raw_input_content = ""
    for microexon_i in microexons:
        try:
            [chr, start, end] = re.split(':|-| |\t', microexon_i.strip())

        except:
            print("invalid input")
            time.sleep(2)
            sys.exit(-1)
        microexons_pos_list.append(":".join([chr, start, end]))
        raw_input_content += "\t".join([chr, start, end])+"\n"

    # os.chdir("./feature_abstract")
    with open("./feature_abstract/usr_input/case.txt", "w") as f:
        f.write(raw_input_content)

    # 2. abstract features
    result = abstractindelfeature.feature_abstract2("input.txt", "feature.csv")
    if type(result)==bool and result == False:
        # print("Failed!")
        sys.exit(-1)
    os.chdir(basedir)


    # 3. predict
    predict_result = "position\tfunctional_probability\n"
    predict_result_dict = {}
    with open("./feature_abstract/feature.csv", "r") as f:
        microexons = f.readlines()
    for microexon_i in microexons:
        feature_list = microexon_i.strip().split(",")
        feature_std = Standardize(list(map(float,feature_list[1:])))
        probability = Recognition(feature_std)
        predict_result_dict[feature_list[0]] = probability
    for microexon_i in microexons_pos_list:
        [chr, start, end] = re.split(':|-| |\t', microexon_i)
        if re.match("chr[0-9XY]{1,2}",chr) and (int(end)-int(start))<30 and (int(end)-int(start))%3==0:
            if microexon_i in predict_result_dict.keys():
                predict_result += microexon_i + "\t" + str(predict_result_dict[microexon_i]) + "\n"
            else:
                predict_result += microexon_i + "\t untranslated region\n"
        else:
            predict_result += microexon_i + "\t invalid input\n"


    with open("result.txt", "w") as f:
        f.write(predict_result)

    # print("Predict finished. Please check \"result.txt\"")
    print("FINISHED")


    # os.chdir(basedir)
    # print(os.getenv("CLASSPATH"))
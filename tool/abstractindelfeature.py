#!/usr/bin/env python
# encoding: utf-8

"""
@version: 3.7
@author: Qi Cheng
@contact: chengqi@hrbeu.edu.cn
@site: https://github.com/Cheng-qi
@software: PyCharm
@file: abstractfeature.py
@time: 2020/7/29 10:41
"""

import os
# import tempfile
# import path
import pandas as pd


def readcmd(cmd):
    # ftmp = tempfile.NamedTemporaryFile(suffix='.out', prefix='tmp', delete=False)
    # fpath = ftmp.name
    # if os.name=="nt":
    #     fpath = fpath.replace("/","\\") # forwin
    # ftmp.close()

    #os.system(cmd + " >/dev/null  2>" + fpath)
    data = os.system(cmd + ">/dev/null   2>&1")

    # with open(fpath, 'r') as file:
    #     data = file.read()
    #     file.close()
    # os.remove(fpath)
    return data

def feature_abstract2(input_raw_file_name, features_file_name):

    # print("Extracting feature...")

    os.chdir("./feature_abstract")
    # print(os.getcwd())
    # result = readcmd("java ccbb.hrbeu.exonimpact.test.Test_all_1")
    result = readcmd("java -jar ExtractedFeature.jar")
    # print(result)
    if not os.path.isfile("./usr_input/case.txt"):
        return False
    # if result[-10:]!="success!!!":
    #     return False

    pre_features_file_name = "./usr_input/pre_feature.csv"
    with open(pre_features_file_name, "r") as f:
        all_lines = f.readlines()

    with_feature_raw_input = []
    without_feature_raw_input = []
    all_feature_raw_input = []

    all_features = {
        'position': [],
        'mean_phylop': [],
        'min_phylop': [],
        'max_phylop': [],
        'mean_ASA': [],
        'min_ASA': [],
        'max_ASA': [],
        'mean_disorder': [],
        'min_disorder': [],
        'max_disorder': [],
        'ss_E': [],
        'ss_C': [],
        'ss_H': [],
        'mean_E': [],
        'min_E': [],
        'max_E': [],
        'mean_C': [],
        'min_C': [],
        'max_C': [],
        'mean_H': [],
        'min_H': [],
        'max_H': [],
        'protein_length': [],
        'indel_exon_length': [],
        'start_length': [],
        'end_length': [],
        # 'label':[]
    }

    for i, line in enumerate(all_lines):
        if i == 0:
            continue
        features_list = line.strip().split(',')

        all_feature_raw_input.append(features_list[0])

        if (len(features_list) < 31):
            if (without_feature_raw_input.count(features_list[0]) == 0):
                without_feature_raw_input.append(features_list[0])
            continue
        if (with_feature_raw_input.count(features_list[0]) != 0):
            continue
        # feature = np.zeros((1,27), dtype=np.float)

        raw_input_split_list = features_list[0].split(':')

        bed_input = raw_input_split_list[0] + ':' + str(int(raw_input_split_list[1])) + ':' + str(
            int(raw_input_split_list[2]))

        all_features['position'].append(bed_input)
        all_features['min_phylop'].append(features_list[9])
        all_features['max_phylop'].append(features_list[10])
        all_features['mean_phylop'].append(features_list[11])

        all_features['ss_E'].append(features_list[12])
        all_features['ss_C'].append(features_list[13])
        all_features['ss_H'].append(features_list[14])

        all_features['max_E'].append(features_list[15])
        all_features['min_E'].append(features_list[16])
        all_features['mean_E'].append(features_list[17])

        all_features['max_C'].append(features_list[18])
        all_features['min_C'].append(features_list[19])
        all_features['mean_C'].append(features_list[20])

        all_features['max_H'].append(features_list[21])
        all_features['min_H'].append(features_list[22])
        all_features['mean_H'].append(features_list[23])

        all_features['mean_ASA'].append(features_list[24])
        all_features['min_ASA'].append(features_list[25])
        all_features['max_ASA'].append(features_list[26])

        all_features['min_disorder'].append(features_list[27])
        all_features['max_disorder'].append(features_list[28])
        all_features['mean_disorder'].append(features_list[29])

        all_features['protein_length'].append(features_list[30])

        all_features['indel_exon_length'].append(int(raw_input_split_list[2])-int(raw_input_split_list[1]))

        target_region = features_list[8].split(':')[1]
        target_region_start = int(target_region.split('-')[0])
        target_region_end = int(target_region.split('-')[1])
        all_features['start_length'].append(target_region_start - 1)
        all_features['end_length'].append(int(float(features_list[30])) - target_region_end)

        with_feature_raw_input.append(features_list[0])

    all_features_pd = pd.DataFrame(all_features)
    all_features_pd.to_csv(features_file_name, index=False, header=False)
    return all_features_pd


if __name__=="__main__":
    feature_abstract2("","feature2.csv")


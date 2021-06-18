
import os
import numpy as np
from getAllValues import *
from urllib import request
import time
#忽视警告
import warnings
warnings.filterwarnings("ignore")

def Recognition(cases):
    csv_data = np.loadtxt("\\".join(os.getcwd().split("\\")[:-1])+"/db/templates3.0", dtype = np.str, delimiter = ",")
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
    result = sum(indexs[sort_np[:k]])/k
    return result

#归一化
def Standardize(feature):
    feature_std = feature[:]
    feature_std[0] = (feature_std[0]+1.3909)/20231
    feature_std[1] = (feature_std[1]+5.895)/6.4303
    feature_std[2] = (feature_std[2]+1.1766)/1.8316

    feature_std[3] = (feature_std[3]-3)/131.05
    feature_std[4] = (feature_std[4]-0.7)/130.1
    feature_std[5] = (feature_std[5]-3)/167.8

    feature_std[6] = (feature_std[6]-0.0818)/0.9124
    feature_std[7] = (feature_std[7]-0.0811)/0.9129
    feature_std[8] = (feature_std[8]-0.0819)/0.9126

    feature_std[12] = (feature_std[12]-0.0169)/0.9133
    feature_std[13] = (feature_std[13]-0.0156)/0.9146
    feature_std[14] = (feature_std[14]-0.0169)/0.9133

    feature_std[15] = (feature_std[15]-0.0273)/0.8842
    feature_std[16] = (feature_std[16]-0.0262)/0.8853
    feature_std[17] = (feature_std[17]-0.0275)/0.9006


    feature_std[18] = (feature_std[18]-0.0167)/0.938
    feature_std[19] = (feature_std[19]-0.0162)/0.9384
    feature_std[20] = (feature_std[20]-0.0167)/0.9385

    feature_std[21] = (feature_std[21]-5)/34345
    feature_std[22] = (feature_std[22]-3)/24
    feature_std[23] = (feature_std[23]-0)/9853
    feature_std[24] = (feature_std[24]-0)/24488

    return feature_std

#检查输入的合法性
def Check(Pos):
    try:
        chr_list = [Pos.split(':')[0], eval(Pos.split(':')[1].split('-')[0]), eval(Pos.split(':')[1].split('-')[1])]
        lenth = chr_list[2]-chr_list[1]+1
    except:
        return "Your input does not meet the requirements!"
    else:
        if lenth <= 0:
            return "Your input does not meet the requirements!"
        if lenth > 30:
            return "Please ensure that the length of the input microexon is less than 30!"
        if lenth%3 != 0:
            return "Please ensure that the length of the input microexon is an integer of 3!"
        return 1

#检查网络是否连接
def IsNetwork():
    try:
        request.urlopen("http://genome.ucsc.edu/cgi-bin/hgTables")
    except:
        print("Network connection error.\nPlease run it again after connecting network!")
        time.sleep(1)
        return -1
    else:
        return 1

#主函数
def Predict(Pos):
    if Check(Pos)!=1: return "Invalid position"
    feature = getAllValues(Pos)
    if feature != -1:
        feature_std = Standardize(feature)
        return str(round(Recognition(feature_std),3))
    else:
        return "untranslated region"

if __name__ == '__main__':
    print("Please wait...")
    user_input =""
    if IsNetwork() == 1:
        print("S:Single microexon predicting;")
        print("P:Predict microexons in ./user_input.txt;")
        print("Other:Exit.")
        user_input = input("Please enter the function initial:")
        if user_input == "S" or user_input =="s":
            user_input = "C"
            while user_input == "C" or user_input == "c":
                Pos = input("Please input microexon's position:(eg: chr11:247277-247300)\n")
                print("Please wait...")
                result = Predict(Pos)
                if result == "Invalid position":
                    print(Check(user_input))
                elif result == "untranslated region":
                    print("There are untranscribed regions at this position in hg19!")
                else:
                    print("The probability that this microexon is functional is %s!"%result)
                user_input = input("C:Continue;\nOther:Exit.\nPlease enter the function initial:")
        elif user_input == "P" or user_input =="p":
            print("Please wait...")
            with open(os.getcwd()+"/user_input.txt") as in_file:
                microexons = in_file.readlines()
            if microexons[-1][-1] != "\n":
                microexons[-1] = microexons[-1]+"\n"
            results = ["Microexon\tProbability"]
            for microexon in microexons:
                results.append(microexon[:-1]+"\t"+Predict(microexon[:-1]))
            with open(os.getcwd()+"/results.txt", "w") as out_file:
                out_file.truncate()
                out_file.write("\n".join(results))
                print("Success!")
        else: 
            time.sleep(1)



# def main():
#     try:
#         request.urlopen("http://genome.ucsc.edu/cgi-bin/hgTables")
#     except:
#         print("Network connection error.\nPlease run it again after connecting network!!!")
#     else:
#         user_input = ""
#         while not user_input:
#             user_input = input("Please input microexon's position:(eg: chr11:247277-247300)\n")
#             try:
#                 chr_list = [user_input.split(':')[0], eval(user_input.split(':')[1].split('-')[0]), eval(user_input.split(':')[1].split('-')[1])]
#                 lenth = chr_list[2]-chr_list[1]+1
#             except:
#                 print("Your input does not meet the requirements!")
#                 user_input = ""
#                 continue
#             else:
#                 if lenth <= 0:
#                     print("Your input does not meet the requirements!")
#                     user_input = ""
#                     continue
#                 if lenth > 30:
#                     print("Please ensure that the length of the input microexon is less than 30!")
#                     user_input = ""
#                     continue
#                 if lenth%3 != 0:
#                     print("Please ensure that the length of the input microexon is an integer of 3!")
#                     user_input = ""
#                     continue
#         print("Please wait...")
#         feature = getAllValues(user_input)
#         if feature != -1:
#             feature_std = Standardize(feature)
#             print("The probability that this microexon is functional is ", end="")
#             print(round(Recognition(feature_std),3))
#         else:
#             print("There are untranscribed regions at this position in hg19")
#         return 0

# if __name__ == '__main__':
#     while 1:
#         main()
#         print("C:Continue;\nE:Exit.")
#         user_input = input("Please enter the function initial:")
#         while user_input!="E" and user_input!="C":
#             print("Your input does not meet the requirements, please re-enter:")
#             user_input = input()
#         if user_input=="E":
#             break


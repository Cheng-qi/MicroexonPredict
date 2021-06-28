# setwd("E:\\cheng\\Desktop\\microexon\\exonimpact_li\\tool\\0github\\MicroexonPredict\\TCACode")
############Functions##############
#1. RBF kernel
rbf_dot=function(patterns1,patterns2,deg){
  patterns1=as.matrix(patterns1);
  patterns2=as.matrix(patterns2);
  
  patterns1_length=nrow(patterns1);
  patterns2_length=nrow(patterns2);
  G=rowSums(patterns1*patterns1);
  H=rowSums(patterns2*patterns2);
  G = as.data.frame(G);
  H = as.data.frame(H);
  G_over = rep(G,patterns2_length)
  Q = as.data.frame(G_over)
  H_over = rep(H,patterns1_length)
  R = as.data.frame(H_over)
  R=as.data.frame(t(R))
  H=Q+R-2*(patterns1%*%t(patterns2));
  H=exp(-H/2/deg^2);
}

#2.TCA
tca=function(sourcefeatures,targetfeatures,m,kwidth){

  X = rbind(sourcefeatures,targetfeatures)
  K = rbf_dot(X,X,kwidth)
  K=as.matrix(K);
  n1=nrow(sourcefeatures);
  n2=nrow(targetfeatures);
  L=matrix(0,n1+n2,n1+n2);
  L[c(1:n1),c(1:n1)]=1/(n1^2);
  L[c((n1+1):(n1+n2)),c((n1+1):(n1+n2))]=1/(n2^2);
  L[c((n1+1):(n1+n2)),c(1:n1)]=-1/(n1*n2);
  L[c(1:n1),c((n1+1):(n1+n2))]=-1/(n1*n2);
  u=0.1;
  H=diag(n1+n2)-matrix(1,n1+n2,n1+n2)/(n1+n2);
  I=diag(n1+n2);
  M1=I+u*K%*%L%*%K;
  M=solve(M1)%*%K%*%H%*%K;
  xx=eigen(M);
  V=xx$vectors;
  D=xx$values;
  Dreal=Re(D);
  # Dreal=sort(Dreal,decreasing = T);
  # indice=order(Dreal,decreasing = T);
  W=V[,c(1:m)];
  Kr=t(W)%*%K;
  return(Kr)
}

# 3.evaluate function
evaluate  <- function(results) {
  for(thre_i in c(100:0))
  {
    results[which(results[,2] >thre_i*0.01), 'predict_label'] = 1 
    results[which(results[,2] <=thre_i*0.01), 'predict_label'] = 0  
    confusion = c(0,0,0,0)
    for(ntest_i in 1:nrow(all_test_result))
    {
      if(results[ntest_i,'label']==0 && results[ntest_i,'predict_label']==0)  # TN
        confusion[1] = confusion[1]+1
      if(results[ntest_i,'label']==0 && results[ntest_i,'predict_label']==1)  # FP
        confusion[2] = confusion[2]+1
      if(results[ntest_i,'label']==1 && results[ntest_i,'predict_label']==0)  # FN
        confusion[3] = confusion[3]+1
      if(results[ntest_i,'label']==1 && results[ntest_i,'predict_label']==1)  # TP
        confusion[4] = confusion[4]+1
    }
    
    fpr_i=confusion[2]/(confusion[1]+confusion[2]);            #fpr;          
    tpr_i=confusion[4]/(confusion[3]+confusion[4]);            #tpr;            
    precision_i=confusion[4]/(confusion[2]+confusion[4]);      #precision; 
    accuracy_i=(confusion[1]+confusion[4])/(sum(confusion));   #print(accuracy);
    MCC_i=(confusion[1]*confusion[4]-confusion[2]*confusion[3])/sqrt((confusion[1]+confusion[2])*(confusion[1]+confusion[3]))/sqrt((confusion[2]+confusion[4])*(confusion[4]+confusion[3]));
    
    if(thre_i==100)
    {
      fpr = fpr_i
      tpr = tpr_i
      precision = precision_i
      accuracy = accuracy_i
      MCC = MCC_i
      
    } else
    {
      fpr = rbind(fpr, fpr_i)
      tpr = rbind(tpr, tpr_i)
      precision = rbind(precision, precision_i)     
      accuracy = rbind(accuracy, accuracy_i)
      MCC = rbind(MCC, MCC_i)
    }
  } 
  row.names(fpr)<-c(100:0)*0.01
  row.names(tpr)<-c(100:0)*0.01
  row.names(precision)<-c(100:0)*0.01
  row.names(accuracy)<-c(100:0)*0.01
  row.names(MCC)<-c(100:0)*0.01
  return(data.frame(fpr,tpr,precision,accuracy,MCC))
}
# 4.Plot Density
showDensity <- function(myplot){
  myplot_show = myplot+  geom_line(stat = "density",size=1)+
    scale_linetype_manual(values = c("solid","longdash"))+
    scale_colour_manual(values= c('black', 'black'))+
    theme(
      panel.grid.major = element_line(colour = "black",linetype = "twodash"), 
      panel.grid.minor = element_line(colour = NA,linetype = "solid"), panel.background = element_rect(fill = NA), 
      legend.background = element_rect(fill = NA),
      legend.position = c(0.8, 0.9),
      panel.border = element_rect(fill=NA,color="black", size=1, linetype="solid"),
      plot.title = element_text(size = 25, vjust = 0.5, hjust = 0.5),
      legend.title = element_blank(),
      legend.text = element_text(size = 25),
      legend.key = element_rect(fill = NA),
      legend.key.width =unit(1.5,'cm'),
      legend.key.height =unit(0.8,'cm'),
      axis.ticks.x=element_blank(),
      axis.text.x=element_text(size = 25, vjust = 0.5, hjust = 0.5,color = "black"),
      axis.text.y=element_text(size = 25, vjust = 0.5, hjust = 0.5,color = "black"),
      axis.title.x = element_text(size = 25, vjust = 0.5, hjust = 0.5,color = "black"),
      axis.title.y = element_text(size = 25, vjust = 0.5, hjust = 0.5,color = "black"),
    )
  show(myplot_show)
}



#### 1.Import data ####

# hgmd  //del:1694 ins:342 indel:2036
hgmd_del_data = read.csv("FeatureData/microindels/hgmd_micro_del_features2.csv",header = T, row.names = 'position')
hgmd_del_label = rep.int(1, nrow(hgmd_del_data))

hgmd_ins_data = read.csv("FeatureData/microindels/hgmd_micro_ins_features2.csv",header = T, row.names = 'position')
hgmd_ins_label = rep.int(1, nrow(hgmd_ins_data))

# gene1000  // del:1806 ins:740 indel:2546
gene1000_del_data = read.csv("FeatureData/microindels/1000gene_micro_del_features2.csv",header = T, row.names = 'position')
gene1000_del_label = rep.int(0, nrow(gene1000_del_data))

gene1000_ins_data = read.csv("FeatureData/microindels/1000gene_micro_ins_features2.csv",header = T, row.names = 'position')
gene1000_ins_label = rep.int(0, nrow(gene1000_ins_data))

# microexon 
microexon_data = read.csv("FeatureData/microexons/microexon_features2.csv",header = T, row.names = 'position')
microexon_data_with_ss = read.csv("FeatureData/microexons/microexon_features_with_ss_structure2.csv",header = T, row.names = 'position')


# microindel 
hgmd_gene1000_data = rbind(hgmd_del_data,hgmd_ins_data,gene1000_del_data,gene1000_ins_data)
hgmd_gene1000_label = c(hgmd_del_label,hgmd_ins_label, gene1000_del_label, gene1000_ins_label)
indel_features = as.matrix(hgmd_gene1000_data)
indel_labels = hgmd_gene1000_label
num_label1_microindel = nrow(hgmd_del_data)+nrow(hgmd_ins_data)
num_label0_microindel = nrow(gene1000_del_data)+nrow(gene1000_ins_data)


# microexon
microexon_features = as.matrix(microexon_data)

# statistics
indel_num = nrow(indel_features)
microexon_num = nrow(microexon_features)

###### 1.1 Normalization####
indel_features_std = indel_features 
microexon_features_std = microexon_features

for(col_i in 1:ncol(indel_features))
{
  indel_features_std[,col_i] = (indel_features[,col_i]-min(indel_features[,col_i]))/(max(indel_features[,col_i])-min(indel_features[,col_i]))
  microexon_features_std[,col_i] = (microexon_features[,col_i]-min(microexon_features[,col_i]))/(max(microexon_features[,col_i])-min(microexon_features[,col_i]))
}



#### 2.micronindel with original features ####

indel_features_matrix = as.matrix(indel_features) 
n=nrow(indel_features);    
n1 = num_label1_microindel;   #label1 
n0 = num_label0_microindel;   #label0 

library(kernlab);
set.seed(12);
n1_rand=sample(n1,n1);          # shuffle
n0_rand=sample(n0,n0);          # shuffle

train_data_1 = indel_features_matrix[c(1:n1),]
train_data_0 = indel_features_matrix[c((n1+1):n),]
train_data_1 = train_data_1[n1_rand,]
train_data_0 = train_data_0[n0_rand,]
cross_num = 10

cross_batch0 = as.integer(nrow(train_data_0)/cross_num)
cross_batch1 = as.integer(nrow(train_data_1)/cross_num)

###### 2.1Modeling####

#### 10-fold cross validation
for( cross_i in 0:(cross_num-1))
{ 
  model_data = rbind(train_data_1[-c((cross_i*cross_batch1+1):((cross_i+1)*cross_batch1)),], train_data_0[-c((cross_i*cross_batch0+1):((cross_i+1)*cross_batch0)),]);
  model_label = as.data.frame(c(rep(1,(n1-cross_batch1)),rep(0,(n0-cross_batch0))))
  test_data = rbind(train_data_1[c((cross_i*cross_batch1+1):((cross_i+1)*cross_batch1)),], train_data_0[c((cross_i*cross_batch0+1):((cross_i+1)*cross_batch0)),]);
  test_label = as.data.frame(c(rep(1,cross_batch1),rep(0,cross_batch0)))
  model = ksvm(model_data,model_label,prob.model=T,type="C-svc",kernel="rbfdot", kpar=list(sigma=0.15));#sigma
  
  test_result = try(predict(model,test_data,type = "probabilities"), silent = T)
  while(typeof(test_result)=="character")
  {
    model = ksvm(model_data,model_label,prob.model=T,type="C-svc",kernel="rbfdot", kpar=list(sigma=0.15))
    test_result = try(predict(model,test_data,type = "probabilities"), silent = T)
  }

  model_results = as.data.frame(test_result, row.names = row.names(test_data))
  model_results[row.names(test_data), 'label'] = test_label
  if(cross_i==0){
    all_test_result = model_results
  }else{
    all_test_result = rbind(all_test_result, model_results)
  }
}

###### 2.2 Evalutation in original features####  

performance_before_tca = evaluate(all_test_result);

###### 2.3 Drawing ROC  ####
tpr_before_tca = performance_before_tca$tpr
fpr_before_tca = performance_before_tca$fpr

AUC_before_tca = 0
for(fpr_mean_i in c(2:length(fpr_before_tca)))
{
  dfpr_area = tpr_before_tca[fpr_mean_i]*(fpr_before_tca[fpr_mean_i]-fpr_before_tca[fpr_mean_i-1])
  AUC_before_tca = AUC_before_tca+dfpr_area
}

library(ggplot2)
ggplot(as.data.frame(cbind(fpr_before_tca, tpr_before_tca)),aes(x = fpr_before_tca,y = tpr_before_tca))+
  geom_ribbon(aes(fpr_before_tca,ymin = 0,ymax = tpr_before_tca),fill =NA)+ 
  geom_line(color='black',size=2)+
  xlim(0,1)+ylim(0,1)+ 
  labs(x='FPR',y='TPR')+  
  theme(plot.title = element_text(hjust = 0.5))+  
  annotate(geom="text", x=0.5, y=0.5, label=paste("AUC=",as.character(round(AUC_before_tca,3))), size = 10)+ 
  theme(
    panel.grid.major = element_line(colour = "black",linetype = "twodash"),
    panel.background = element_rect(fill = NA), 
    legend.background = element_rect(fill = NA),
    legend.position = c(0.8, 0.9),
    panel.border = element_rect(fill=NA,color="black", size=1, linetype="solid"),
    plot.title = element_text(size = 25, vjust = 0.5, hjust = 0.5),
    legend.title = element_blank(),
    legend.text = element_text(size = 25),
    legend.key = element_rect(fill = NA),
    legend.key.width =unit(1.5,'cm'),
    legend.key.height =unit(0.8,'cm'),
    axis.ticks.x=element_blank(),
    axis.text.x=element_text(size = 25, vjust = 0.5, hjust = 0.5,color = "black"),
    axis.text.y=element_text(size = 25, vjust = 0.5, hjust = 0.5,color = "black"),
    axis.title.x = element_text(size = 25, vjust = 0.5, hjust = 0.5,color = "black"),
    axis.title.y = element_text(size = 25, vjust = 0.5, hjust = 0.5,color = "black"),
  )

#### 3.micronindel with TCA features ####
###### 3.1Import TCA data####
RBF_WIDTH = 0.5
RDIM = 7
feature_data=read.csv(paste("RBFWidth",as.character(RBF_WIDTH),"TCAFeature.csv",sep =""),header=T,row.names = 'X'); #Import TCA Feature
features = as.matrix(feature_data[,c(1:RDIM)])
sourcefeatures=features[c(1:indel_num),]; # Microindel with TCA Features
targetfeatures=features[c((n+1):(length(features[,1]))),]; # Microexons with TCA Features
n1 = num_label1_microindel
n0 = num_label0_microindel


library(kernlab);
set.seed(12);
n1_rand=sample(n1,n1);          
n0_rand=sample(n0,n0);        
train_data_1 = sourcefeatures[c(1:n1),]
train_data_0 = sourcefeatures[c((n1+1):n),]
train_data_1 = train_data_1[n1_rand,]
train_data_0 = train_data_0[n0_rand,]
cross_num = 10
cross_batch0 = as.integer(nrow(train_data_0)/cross_num)
cross_batch1 = as.integer(nrow(train_data_1)/cross_num)
###### 3.2 10-flod Cross Validation ####
for( cross_i in 0:(cross_num-1))
{ 
  model_data = rbind(train_data_1[-c((cross_i*cross_batch1+1):((cross_i+1)*cross_batch1)),], train_data_0[-c((cross_i*cross_batch0+1):((cross_i+1)*cross_batch0)),]);
  model_label = as.data.frame(c(rep(1,(n1-cross_batch1)),rep(0,(n0-cross_batch0))))
  test_data = rbind(train_data_1[c((cross_i*cross_batch1+1):((cross_i+1)*cross_batch1)),], train_data_0[c((cross_i*cross_batch0+1):((cross_i+1)*cross_batch0)),]);
  test_label = as.data.frame(c(rep(1,cross_batch1),rep(0,cross_batch0)))
  model = ksvm(model_data,model_label,prob.model=T,type="C-svc",kernel="rbfdot", kpar=list(sigma=0.15)); 
  
  test_result = try(predict(model,test_data,type = "probabilities"), silent = T)
  while(typeof(test_result)=="character")
  {
    model = ksvm(model_data,model_label,prob.model=T,type="C-svc",kernel="rbfdot", kpar=list(sigma=0.15))
    test_result = try(predict(model,test_data,type = "probabilities"), silent = T)
  }
  
  model_results = as.data.frame(test_result, row.names = row.names(test_data))
  model_results[row.names(test_data), 'label'] = test_label
  if(cross_i==0){
    all_test_result = model_results
  }else{
    all_test_result = rbind(all_test_result, model_results)
  }
}

###### 3.3 Evluation in TCA Features####  

performance_after_tca = evaluate(all_test_result)

###### 3.4 Drow ROC ####
fpr_after_tca = performance_after_tca$fpr
tpr_after_tca = performance_after_tca$tpr

AUC_after_tca = 0
for(fpr_mean_i in c(2:length(fpr_after_tca)))
{
  dfpr_area = tpr_after_tca[fpr_mean_i]*(fpr_after_tca[fpr_mean_i]-fpr_after_tca[fpr_mean_i-1])
  AUC_after_tca = AUC_after_tca+dfpr_area
}

library(ggplot2)
ggplot(as.data.frame(cbind(fpr_after_tca, tpr_after_tca)),aes(x = fpr_after_tca,y = tpr_after_tca))+
  geom_ribbon(aes(fpr_after_tca,ymin = 0,ymax = tpr_after_tca),fill = NA)+ 
  geom_line(color='black',size=2)+
  xlim(0,1)+ylim(0,1)+ 
  labs(x='FPR',y='TPR')+  
  theme(plot.title = element_text(hjust = 0.5))+ 
  annotate(geom="text", x=0.5, y=0.5, label=paste("AUC=",as.character(round(AUC_after_tca,3))), size = 10)+ 
  theme(
    panel.grid.major = element_line(colour = "black",linetype = "twodash"),
    panel.background = element_rect(fill = NA), 
    legend.background = element_rect(fill = NA),
    legend.position = c(0.8, 0.9),
    panel.border = element_rect(fill=NA,color="black", size=1, linetype="solid"),
    plot.title = element_text(size = 25, vjust = 0.5, hjust = 0.5),
    legend.title = element_blank(),
    legend.text = element_text(size = 25),
    legend.key = element_rect(fill = NA),
    legend.key.width =unit(1.5,'cm'),
    legend.key.height =unit(0.8,'cm'),
    axis.ticks.x=element_blank(), 
    axis.text.x=element_text(size = 25, vjust = 0.5, hjust = 0.5,color = "black"),
    axis.text.y=element_text(size = 25, vjust = 0.5, hjust = 0.5,color = "black"),
    axis.title.x = element_text(size = 25, vjust = 0.5, hjust = 0.5,color = "black"),
    axis.title.y = element_text(size = 25, vjust = 0.5, hjust = 0.5,color = "black"),
  )
  
#### 4.Density Plot of Features ####
#### 4.1 TCA ####
library(ggplot2)
RBF_WIDTH = 0.5
RDIM = 7
feature_data=read.csv(paste("RBFWidth",as.character(RBF_WIDTH),"TCAFeature.csv",sep =""),header=T,row.names = 'X'); #Import TCA Feature
indel_tca = as.data.frame(features[c(1:indel_num),]);
microexon_tca = features[c((indel_num+1):(indel_num+microexon_num)),];

tca_data = rbind(indel_tca,microexon_tca)
tca_data["label"] = c(rep("microindels", indel_num),rep("microexons", microexon_num))

tca_plot<-ggplot(tca_data) +
  aes(x = V1, linetype=label,colour=label) +
  xlab("Feature")+
  ylab("Density")
showDensity(tca_plot)


####4.2 PCA ####
pca_matr=as.matrix(rbind(indel_features_std,microexon_features_std))
W = t(pca_matr)%*%pca_matr
W2 = eigen(W)
W_vector=W2$vectors
W_vector_6 = W_vector[,c(1:RDIM)]
pca_data = pca_matr%*%W_vector_6
indel_pca = as.matrix(indel_features_std)%*%W_vector_6
microexon_pca = as.matrix(microexon_features_std)%*%W_vector_6

pca_data = rbind(as.data.frame(indel_pca),as.data.frame(microexon_pca))
pca_data["label"] = c(rep("microindels", indel_num),rep("microexons", microexon_num))

pca_plot<-ggplot(pca_data) +
  aes(x = V1, linetype=label,colour=label) +
  xlab("Feature")+
  ylab("Density")
showDensity(pca_plot)


##### 6.Prediction of Functional Microexon ####
###### 6.1 Train ####
train_features = sourcefeatures
train_labels = indel_labels
microexon_pred = targetfeatures

model_microexon = ksvm(train_features,train_labels,prob.model=T,type="C-svc",kernel="rbfdot", kpar=list(sigma=0.15));#sigma表示高斯核宽度

microexon_result = try(predict(model_microexon, microexon_pred,type = "probabilities"), silent = T)
while(typeof(microexon_result)=="character")
{
  model_microexon = ksvm(train_features, train_labels,prob.model=T,type="C-svc",kernel="rbfdot", kpar=list(sigma=0.15))
  microexon_result = try(predict(model_microexon,microexon_pred,type = "probabilities"), silent = T)
}


microexon_result = predict(model_microexon,microexon_pred,type = "probabilities")

microexon_data['probability'] = microexon_result[,2]
microexon_probability = microexon_data[,"probability"]
thre = 0.5 
microexon_label = microexon_probability
microexon_label[which(microexon_data$probability >=thre)] = "Functional"
microexon_label[which(microexon_data$probability < thre)] = "Neutral"

microexon_data['labels'] = microexon_label

###### 6.2PCA-Biplot ####
library("factoextra");
target_pca=princomp(microexon_features,cor = TRUE);
picture_of_target=fviz_pca_biplot(target_pca,axes=c(1,2),label="none",habillage = microexon_label,
                                  col.var = c(rep("DNA conservation",3),rep("ASA",3),rep("Disorder probability",3),
                                              rep("Secondary structure",12),rep("Protein length",1),
                                              rep("Microexon length",1),rep("Distances to terminals",2)),
                                              labelsize=0.01,pointsize=1)+

  theme(

    panel.grid.major = element_line(colour = "black",linetype = "twodash"), 
    panel.grid.minor = element_line(colour = NA,linetype = "solid"), panel.background = element_rect(fill = NA), 
    legend.position = "right",
    panel.border = element_rect(fill=NA,color="black", size=1, linetype="solid"),
    
    plot.title = element_text(size = 15, vjust = 0.5, hjust = 0),
    legend.title = element_blank(),
    legend.text = element_text(size = 15),
    legend.key.size=unit(0.5,'cm'),
    axis.ticks.x=element_blank(),
    axis.text.x=element_text(size = 15, vjust = 0.5, hjust = 0.5,color = "black"),
    axis.text.y=element_text(size = 15, vjust = 0.5, hjust = 0.5,color = "black"),
    axis.title.x = element_text(size = 15, vjust = 0.5, hjust = 0.5,color = "black"),
    axis.title.y = element_text(size = 15, vjust = 0.5, hjust = 0.5,color = "black"),
  );
print(picture_of_target);

### 6.3Disorder ####
disorder_density<-ggplot(microexon_data) +
  aes(x = mean_disorder, linetype=labels,colour=labels) +
  xlab("Disorder score")+
  ylab("Density")
showDensity(disorder_density)


### 6.4Second Structure ####
microexon_data["mean_ss"] = microexon_data_with_ss$ss_mean
microexon_data["max_ss"] = microexon_data_with_ss$ss_max
microexon_data["min_ss"] = microexon_data_with_ss$ss_min

second_structure_density<-ggplot(microexon_data) +
  aes(x = mean_ss, linetype=labels,colour=labels) +
  xlab("Disorder score")+
  ylab("Density")
showDensity(second_structure_density)

### 6.5ASA ####

ASA_density<-ggplot(microexon_data) +
  aes(x = mean_ASA, linetype=labels,colour=labels) +
  xlab("ASA")+
  ylab("Density")
showDensity(ASA_density)



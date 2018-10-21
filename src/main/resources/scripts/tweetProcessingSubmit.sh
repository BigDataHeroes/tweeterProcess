#!/bin/bash


hadoophome=/home/pablo/EntornosTrabajo/KeepCodingFinalProject/spark-2.3.2-bin-hadoop2.7
sparkSubmit=$hadoophome/bin/spark-submit


source properties.sh

outputBayesModel=hdfs:/user/raj_ops/tweets/model/bayesmodel
outputCountvectorizedModel=hdfs:/user/raj_ops/tweets/model/countvectmodel
originPath=hdfs:/user/raj_ops/tweets/output
outputPath=hdfs:/user/raj_ops/tweets/outputSparkProcess
outputProcessFiles=hdfs:/user/raj_ops/tweets/processOk

# Local
#$sparkSubmit --class com.keepcoding.bigdataheroes.main.predict --master local[2] --deploy-mode client tweetprocessing-0.0.1-SNAPSHOT.jar $outputBayesModel $outputCountvectorizedModel $originPath $outputPath $outputProcessFiles  

# Hadoop
spark-submit --class com.keepcoding.bigdataheroes.main.predict --master yarn --deploy-mode cluster --num-executors 1 --executor-memory 512M tweetprocessing-0.0.1-SNAPSHOT.jar $outputBayesModel $outputCountvectorizedModel $originPath $outputPath $outputProcessFiles
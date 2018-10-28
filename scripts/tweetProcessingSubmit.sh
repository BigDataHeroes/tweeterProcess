#!/bin/bash


hadoophome=/home/pablo/EntornosTrabajo/KeepCodingFinalProject/spark-2.3.2-bin-hadoop2.7
sparkSubmit=$hadoophome/bin/spark-submit


source properties.sh

outputBayesModel=$scriptmodels/bayesmodel
outputCountvectorizedModel=$scriptmodels/countvectmodel
originPath=$scriptdata/output
outputPath=$scriptdata/outputSparkProcess
outputProcessFiles=$scriptdata/processOk

# Local
$sparkSubmit --class com.keepcoding.bigdataheroes.main.predict --master local[2] --deploy-mode client tweetprocessing-0.0.1-SNAPSHOT.jar $outputBayesModel $outputCountvectorizedModel $originPath $outputPath $outputProcessFiles  

# Hadoop
#spark-submit --class com.keepcoding.bigdataheroes.main.predict --master local[2] --deploy-mode client tweetprocessing-0.0.1-SNAPSHOT.jar $outputBayesModel $outputCountvectorizedModel $originPath $outputPath $outputProcessFiles
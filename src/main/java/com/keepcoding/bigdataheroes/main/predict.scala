package com.keepcoding.bigdataheroes.main


import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.ml.feature.{CountVectorizer, CountVectorizerModel,StopWordsRemover, Tokenizer}
import org.apache.spark.mllib.linalg.SparseVector
import org.apache.spark.sql.SparkSession



object predict {
  
  val outputBayesModel="/home/pablo/EntornosTrabajo/KeepCodingFinalProject/model2"
  val outputCountvectorizedModel="/home/pablo/EntornosTrabajo/KeepCodingFinalProject/countVectModel/model2"
  
  val fileToProcess = "file:/home/pablo/EntornosTrabajo/KeepCodingFinalProject/output/tweetscentroProcess.txt"
  
  def main(args: Array[String]) {
    
    val mySpark = SparkSession
                  .builder()
                  .master("local[2]")
                  .appName("TweetProcess")
                  .getOrCreate()
    
    import mySpark.implicits._
    
    val sc = mySpark.sparkContext
    
    val model1Gram = NaiveBayesModel.load(sc, outputBayesModel)
    val cvModel = CountVectorizerModel.load(outputCountvectorizedModel)

    val tweets = sc.textFile(fileToProcess).toDF()
    
    val tokenizerTweets = new Tokenizer().setInputCol("value").setOutputCol("words")
    
    val spanishStopWords = StopWordsRemover.loadDefaultStopWords("spanish")
    val remover1 = new StopWordsRemover().setInputCol("words").setOutputCol("wordFiltered")
    val dfToPredict = cvModel.transform(remover1.transform(tokenizerTweets.transform(tweets)))
    
    
    val predict = dfToPredict.select("features1Gram").map(row => Seq(model1Gram.predict(SparseVector.fromML(row(0).asInstanceOf[org.apache.spark.ml.linalg.SparseVector]))))
    
    predict.filter(p => p(0)==1).count
    predict.filter(p => p(0)==0).count
  }
}
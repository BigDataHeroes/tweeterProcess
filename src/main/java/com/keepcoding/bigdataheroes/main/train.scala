package com.keepcoding.bigdataheroes.main

import org.apache.spark.ml.feature.{RegexTokenizer, Tokenizer}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.ml.feature.StopWordsRemover
import org.apache.spark.ml.feature.NGram
import org.apache.spark.ml.feature.{CountVectorizer, CountVectorizerModel}
import scala.collection.mutable.WrappedArray
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.SparseVector
import org.apache.spark.{SparkConf, SparkContext}


object train {
  
  val inputNeg = "file:/home/pablo/EntornosTrabajo/KeepCodingFinalProject/trainData/neg.txt"
  val inputPos = "file:/home/pablo/EntornosTrabajo/KeepCodingFinalProject/trainData/pos.txt"
  val outputBayesModel="/home/pablo/EntornosTrabajo/KeepCodingFinalProject/model2"
  val outputCountvectorizedModel="/home/pablo/EntornosTrabajo/KeepCodingFinalProject/countVectModel/model2"
  
  def main(args: Array[String]) {
    
    //Load Spark 
    val mySpark = SparkSession
    .builder()
    .master("local[2]")
    .appName("TweetProcess")
    .getOrCreate()
    
    import mySpark.implicits._
    
    val sc = mySpark.sparkContext
    
    //Process input files
    val DFTextPos = sc.textFile(inputPos).map(x => (x,1)).toDF()
    val DFTextNeg = sc.textFile(inputNeg).map(x => (x,0)).toDF()
    val textDF = DFTextPos.union(DFTextNeg).toDF("text", "tag")
    
    //Pipeline proccesing preparation
    val tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
    val spanishStopWords = StopWordsRemover.loadDefaultStopWords("spanish")
    val remover = new StopWordsRemover().setInputCol("words").setOutputCol("wordFiltered")
    val bigram = new NGram().setN(2).setInputCol("wordFiltered").setOutputCol("bigrams")
    val trigram = new NGram().setN(3).setInputCol("wordFiltered").setOutputCol("trigrams")
    
    //Pipeline procession execution
    val tokenized = tokenizer.transform(textDF)
    val filtDF = remover.transform(tokenized)
    val bigramDF = bigram.transform(filtDF)
    val triGramDF = trigram.transform(bigramDF)
    
    //Vocabula size
    val vocsize = triGramDF.select("wordFiltered").rdd.map(r => r(0)).flatMap(x => x.asInstanceOf[WrappedArray[String]]).toDF().distinct().count
    val vocsizeBi = triGramDF.select("bigrams").rdd.map(r => r(0)).flatMap(x => x.asInstanceOf[WrappedArray[String]]).toDF().distinct().count
    val vocsizetri = triGramDF.select("trigrams").rdd.map(r => r(0)).flatMap(x => x.asInstanceOf[WrappedArray[String]]).toDF().distinct().count
    
    // Count vectrized model preparation
    val cvModel: CountVectorizerModel = new CountVectorizer().setInputCol("wordFiltered").setOutputCol("features1Gram").setVocabSize(vocsize.toInt).fit(triGramDF)
    val cvModelBi: CountVectorizerModel = new CountVectorizer().setInputCol("bigrams").setOutputCol("features2Gram").setVocabSize(vocsizeBi.toInt).fit(triGramDF)
    val cvModeltri: CountVectorizerModel = new CountVectorizer().setInputCol("trigrams").setOutputCol("features3Gram").setVocabSize(vocsizetri.toInt).fit(triGramDF)
    
    val DFforTraining = cvModeltri.transform(cvModelBi.transform(cvModel.transform(triGramDF)))
    
    // Split data in train and test
    val Array(training, test) = DFforTraining.randomSplit(Array(0.7, 0.3))
    
    val train1Gram = training.select("tag","features1Gram").map(row => LabeledPoint(row.getInt(0), SparseVector.fromML(row(1).asInstanceOf[org.apache.spark.ml.linalg.SparseVector])))
    val test1Gram = test.select("tag","features1Gram").map(row => LabeledPoint(row.getInt(0), SparseVector.fromML(row(1).asInstanceOf[org.apache.spark.ml.linalg.SparseVector])))
    
    val train2Gram = training.select("tag","features2Gram").map(row => LabeledPoint(row.getInt(0), SparseVector.fromML(row(1).asInstanceOf[org.apache.spark.ml.linalg.SparseVector])))
    val test2Gram = test.select("tag","features2Gram").map(row => LabeledPoint(row.getInt(0), SparseVector.fromML(row(1).asInstanceOf[org.apache.spark.ml.linalg.SparseVector])))
    
    val train3Gram = training.select("tag","features3Gram").map(row => LabeledPoint(row.getInt(0), SparseVector.fromML(row(1).asInstanceOf[org.apache.spark.ml.linalg.SparseVector])))
    val test3Gram = test.select("tag","features3Gram").map(row => LabeledPoint(row.getInt(0), SparseVector.fromML(row(1).asInstanceOf[org.apache.spark.ml.linalg.SparseVector])))
    
    //Train Naive bayes model
    val model1Gram = NaiveBayes.train(train1Gram.rdd, lambda = 1.0, modelType = "multinomial")
    val model2Gram = NaiveBayes.train(train2Gram.rdd, lambda = 1.0, modelType = "multinomial")
    val model3Gram = NaiveBayes.train(train3Gram.rdd, lambda = 1.0, modelType = "multinomial")
    
    // Save models
    model1Gram.save(sc, outputBayesModel)
    cvModel.save(outputCountvectorizedModel)
    
    
    
    val predictionAndLabel1Gram = test1Gram.map(p => (model1Gram.predict(p.features), p.label))
    val accuracy1Gram = 1.0 * predictionAndLabel1Gram.filter(x => x._1 == x._2).count() / test1Gram.count()
    
    val predictionAndLabel2Gram = test2Gram.map(p => (model2Gram.predict(p.features), p.label))
    val accuracy2Gram = 1.0 * predictionAndLabel2Gram.filter(x => x._1 == x._2).count() / test2Gram.count()
    
    val predictionAndLabel3Gram = test3Gram.map(p => (model3Gram.predict(p.features), p.label))
    val accuracy3Gram = 1.0 * predictionAndLabel3Gram.filter(x => x._1 == x._2).count() / test3Gram.count()
    
    print(s"Accuracy 1 gram: ${accuracy1Gram}")
    print(s"Accuracy 2 gram: ${accuracy2Gram}")
    print(s"Accuracy 3 gram: ${accuracy3Gram}")
    
    
    mySpark.stop()
  }
}
package com.keepcoding.bigdataheroes.main


import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import java.io.File
import org.apache.spark.ml.feature.{CountVectorizer, CountVectorizerModel,StopWordsRemover, Tokenizer}
import org.apache.spark.mllib.linalg.SparseVector
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.Column
import java.text.SimpleDateFormat
import java.util.Calendar
import org.apache.commons.io.FileUtils
import org.apache.spark.SparkContext
import org.apache.hadoop.fs.{FileSystem,Path}
import org.apache.hadoop.conf.Configuration
import scala.collection.mutable.ListBuffer



object predict {
  
//  val outputBayesModel="/home/pablo/EntornosTrabajo/KeepCodingFinalProject/model2"
//  val outputCountvectorizedModel="/home/pablo/EntornosTrabajo/KeepCodingFinalProject/countVectModel/model2"
//  
//  val originPath = "/home/pablo/EntornosTrabajo/KeepCodingFinalProject/batchDownloadTweets/output"
//  val outputPath = "/home/pablo/EntornosTrabajo/KeepCodingFinalProject/batchDownloadTweets/outputSparkProcess"
//  val outputProcessFiles = "/home/pablo/EntornosTrabajo/KeepCodingFinalProject/batchDownloadTweets/processOk"
  
  def main(args: Array[String]) {
    
    val outputBayesModel= args(0)
    val outputCountvectorizedModel=args(1)
    val originPath = args(2)
    val outputPath = args(3)
    val outputProcessFiles = args(4)
    
    PrintUtiltity.print(s"Variables de entrada: ${outputBayesModel}, ${outputCountvectorizedModel}, ${originPath}, ${outputPath}, ${outputProcessFiles} ")
    
     val mySpark = SparkSession
                  .builder()
                  .appName("TweetProcess")
                  .getOrCreate()
    
      import mySpark.implicits._
    
      val sc = mySpark.sparkContext
     
    val dirs = getListOfDirs(originPath)
     PrintUtiltity.print(s"Lista de directorios: ${dirs}")
    //Load all files in spark
    val rddAllFiles = dirs.map(dir => {
          sc.textFile(dir).toDF("text").withColumn("fecha", lit(dateFromFileName(dir)))
                                        .withColumn("distrito", lit(districtFromFileName(dir)))
      }).reduce(_ union _)
    
       
    //Load models  
    val model1Gram = NaiveBayesModel.load(sc, outputBayesModel)
    val cvModel = CountVectorizerModel.load(outputCountvectorizedModel)
    val spanishStopWords = StopWordsRemover.loadDefaultStopWords("spanish")
    val tokenizerTweets = new Tokenizer().setInputCol("text").setOutputCol("words")
    val remover1 = new StopWordsRemover().setStopWords(spanishStopWords).setInputCol("words").setOutputCol("wordFiltered")
  
    
    //Transform de input data
    val dfToPredict = cvModel.transform(remover1.transform(tokenizerTweets.transform(rddAllFiles)))

    //UDF to predict
    val predictUDF = udf((x:org.apache.spark.ml.linalg.SparseVector) => model1Gram.predict(SparseVector.fromML(x)).toInt)
    
    //DF to group the results of the predictions
    val dfToGroup = dfToPredict.withColumn("predict", predictUDF(col("features1Gram"))).select("fecha", "distrito","predict")
        
    val finalDF = dfToGroup.groupBy("distrito","fecha").agg(aggPositivePredict,aggNegativePredict)
    
    //Write DF to a File
    val today = new SimpleDateFormat("d-M-y").format(Calendar.getInstance().getTime())
    finalDF.write.format("csv").save(outputPath+today)
      
    
    mySpark.stop()
  }

   
    def dateFromFileName(file:String) : String = {
      file.split("/").last.split("\\.").last
    }
    def districtFromFileName(file:String) : String = {
      file.split("/").takeRight(2).head
    }
    
    def aggPositivePredict(): Column = {
      sum(when(col("predict") === 1, 1).otherwise(0)).alias("NumPos")
    }
    def aggNegativePredict(): Column = {
      sum(when(col("predict") === 0, 1).otherwise(0)).alias("NumNeg")
    }
  
  def getListOfDirs(dir: String):List[String] = {
    val fs = FileSystem.get(new Configuration())
    val p = new Path(dir)  
    PrintUtiltity.print(s"Initial dir ${dir}")
    if (fs.exists(p) && fs.isDirectory(p)) {
        val files = fs.listFiles(p, true)
        val filenames = ListBuffer[ String ]( )
        while ( files.hasNext ) {
          
          val file = files.next()
          PrintUtiltity.print(s"File ${file.getPath().toString()}")
          if(file.isFile()){
            filenames += file.getPath().toString()
          } 
        }
        filenames.toList
        
    } else {
        List[String]()
    }
  }
  

}

object PrintUtiltity {
    def print(data:String) = {
      println(data)
    }
}
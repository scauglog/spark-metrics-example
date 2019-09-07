package fr.xebia.xke

import org.apache.spark.sql.types.{StructField, StructType, LongType}
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.ml.feature.VectorAssembler

import scala.util.Random

object SparkMetricsExample {
  val APP_NAME = "spark-metrics-example"
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName(APP_NAME)
      .config("spark.metrics.namespace", APP_NAME)
      .config("spark.metrics.properties", "metrics.properties")
      .getOrCreate()

    val numPartition = 1000
    val rowByPartition = 1000000L
    val lowerBound = -1000
    val upperBound = 1000
    val a = 14000
    val b = 42 * 32 * numPartition
    lazy val rnd = new Random()

    val trainSet = spark.sparkContext.parallelize(1.to(numPartition))
      .repartition(numPartition)
      .flatMap(i => (i * rowByPartition).to((i + 1) * rowByPartition))
      .map{ x =>
        val epsilon = lowerBound + rnd.nextInt( (upperBound - lowerBound) + 1 )
        val y = a * x + b + epsilon
        Row(x, y)
      }

    val schema = StructType(Seq(
      StructField("x", LongType, false),
      StructField("label", LongType, false)
    ))

    val training = new VectorAssembler()
      .setInputCols(Array("x"))
      .setOutputCol("features")
      .transform(spark.createDataFrame(trainSet, schema))


    val lr = new LinearRegression()
      .setMaxIter(100)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)

    val lrModel = lr.fit(training)

    spark.stop()
    // Print the coefficients and intercept for linear regression
    println("######################################################################################################")
    println(s"Coefficients: ${lrModel.coefficients} Intercept: ${lrModel.intercept}")

    // Summarize the model over the training set and print out some metrics
    val trainingSummary = lrModel.summary
    println(s"numIterations: ${trainingSummary.totalIterations}")
    println(s"objectiveHistory: [${trainingSummary.objectiveHistory.mkString(",")}]")
    trainingSummary.residuals.show()
    println(s"RMSE: ${trainingSummary.rootMeanSquaredError}")
    println(s"r2: ${trainingSummary.r2}")


    println("__--__--'' done ''--__--__")

  }
}
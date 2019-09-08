package fr.xebia.xke

import org.apache.spark.groupon.metrics.{SparkCounter, UserMetricsSystem}
import org.apache.spark.scheduler.{SparkListener, SparkListenerTaskEnd, SparkListenerTaskStart}
import org.apache.spark.sql.SparkSession

object Metrics {
  def add(implicit spark: SparkSession): Unit ={
    UserMetricsSystem.initialize(spark.sparkContext, "CustomMetrics")

    lazy val activeTaskCounter: SparkCounter = UserMetricsSystem.counter("ActiveTaskCounter")
    lazy val bytesReadCounter: SparkCounter = UserMetricsSystem.counter("BytesReadCounter")

    spark.sparkContext.addSparkListener( new SparkListener {
      override def onTaskStart(taskStart: SparkListenerTaskStart): Unit = {
        activeTaskCounter.inc(1L)
        println("###############################################")
      }

      override def onTaskEnd(taskEnd: SparkListenerTaskEnd): Unit = {
        activeTaskCounter.dec(1L)
        bytesReadCounter.inc(taskEnd.taskMetrics.inputMetrics.bytesRead)
      }
    })
  }
}

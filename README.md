# Spark Metrics Example
Use Spark EventListener and groupon spark-metrics to send metrics in graphite and recreate spark UI in grafana
## Build
```shell script
mvn clean package
```
## Run
```shell script
spark-submit --file metrics.properties --class fr.xebia.xke.SparkMetricsExample target/spark_metrics-1.0-SNAPSHOT-shaded.jar
```
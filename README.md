# Spark Metrics Example
Use Spark EventListener and groupon spark-metrics to send metrics in graphite and recreate spark UI in grafana
## Build
```shell script
mvn clean package
```
## Run

```shell script
docker run --name grafana -v /var/lib/gmonitor/graphite:/var/lib/graphite/storage/whisper \
           -v /var/lib/gmonitor/grafana/data:/usr/share/grafana/data \
           -p 2003:2003 -p 3000:3000 -p 80:80\
           -d alexmercer/graphite-grafana
spark-submit --files metrics.properties --class fr.xebia.xke.SparkMetricsExample target/spark_metrics-1.0-SNAPSHOT-shaded.jar
```
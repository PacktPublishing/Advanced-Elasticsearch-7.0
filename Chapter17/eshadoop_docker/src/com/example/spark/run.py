from pyspark.sql import SparkSession
import pyspark.sql.functions as f
from pyspark.sql.types import *
from pyspark.sql.functions import expr
from pyspark.ml.feature import VectorAssembler
from com.example.spark_ml.kmeans import create_anomaly_detection_model, find_anomalies
import pandas


# Create Spark Session
def create_spark_session():
    spark = SparkSession.builder.master("local").appName("anomalyDetection").getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    return spark


# Extract data from elasticsearch and select the fields for the anomaly detection
def extract_es_data(spark):
    reader = spark.read.format("org.elasticsearch.spark.sql") \
                .option("es.read.metadata", "true") \
                .option("es.nodes.wan.only", "true").option("es.port", "9200") \
                .option("es.net.ssl", "false").option("es.nodes", "http://elasticsearch")
    df = reader.load("cf_rfem_hist_price")
    df.createTempView("view1")
    df2 = spark_session.sql("Select volume, changePercent, changeOverTime from view1")
    vec_assembler = VectorAssembler(inputCols=["changeOverTime", "changePercent", "volume"], outputCol="features")
    df_features = vec_assembler.transform(df2).select('features')
    return df, df_features


def convert_data_to_features(spark, data):
    schema = StructType([StructField("changeOverTime", FloatType()), StructField("changePercent", FloatType()),
                        StructField("volume", FloatType())])
    df_temp_data = spark.createDataFrame(pandas.DataFrame(data), schema)
    vec_assembler = VectorAssembler(inputCols=["changeOverTime", "changePercent", "volume"], outputCol="features")
    df_features = vec_assembler.transform(df_temp_data).select('features')
    return df_features


def write_es_data(df_es, df_labels_add):
    df_id = df_es.select(expr("_metadata._id as id"))
    df_id_row_index = df_id.withColumn("row_index", f.monotonically_increasing_id())
    df_labels_row_index = df_labels_add.withColumn("row_index", f.monotonically_increasing_id())
    df_update = df_id_row_index.join(df_labels_row_index, on=["row_index"]).drop("row_index")
    df_update.write.format("org.elasticsearch.spark.sql").option("es.write.operation", "update") \
        .option("es.nodes.wan.only", "true").option("es.port", "9200") \
        .option("es.net.ssl", "false").option("es.nodes", "http://elasticsearch") \
        .option("es.mapping.id", "id").option("es.mapping.exclude", "id")\
        .mode("append").save('cf_rfem_hist_price')


if __name__ == '__main__':
    spark_session = create_spark_session()
    df_data, es_data = extract_es_data(spark_session)
    df_labels, centers = create_anomaly_detection_model(es_data)
    write_es_data(df_data, df_labels)
    df_centers_features = convert_data_to_features(spark_session, centers)
    center_labels = find_anomalies(df_centers_features)
    print(center_labels)

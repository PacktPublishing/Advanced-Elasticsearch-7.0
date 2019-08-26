package com.example.esanalytics.spark.mllib;

import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.example.esanalytics.service.impl.EsHadoopSparkServiceImpl;
import java.io.IOException;
import java.nio.file.Paths;


@Component
public class AnomalyDetection {
	public static final Logger logger = LoggerFactory.getLogger(EsHadoopSparkServiceImpl.class);
	public static final String APPLICATION_ROOT_PATH = 
			AnomalyDetection.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	
	public Dataset<Row> buildKmeansModel(Dataset<Row> dataset, String[] fieldNames, int numOfClass) {
		
		VectorAssembler assembler = new VectorAssembler().setInputCols(fieldNames).setOutputCol("features");
		Dataset<Row> features = assembler.transform(dataset);
		KMeansModel model = null;

		try {
			KMeans kmeans = new KMeans().setFeaturesCol("features").setK(numOfClass).setSeed(1L);
			model = kmeans.fit(features);
			String kmeansModelpath = APPLICATION_ROOT_PATH + "kmeansModel";
			model.write().overwrite().save(kmeansModelpath);
		} catch (IOException ex) {
			logger.error(ex.getMessage());
		}

		Dataset<Row> prediction = null;
		if (model != null)
			prediction = model.transform(features).select("prediction");

		return prediction;
	}

	public Dataset<Row> predict(Dataset<Row> dataSet, String[] fieldNames) {
		KMeansModel model = null;
		Dataset<Row> prediction = null;
		VectorAssembler assembler = new VectorAssembler().setInputCols(fieldNames).setOutputCol("features");
		Dataset<Row> features = assembler.transform(dataSet);
	
		String kmeansModelPath = APPLICATION_ROOT_PATH + "kmeansModel";
		boolean exists = Paths.get(kmeansModelPath).toFile().exists();
		if (exists) {
			model = KMeansModel.load(kmeansModelPath);
			prediction = model.transform(features).select("prediction");
		} else {
			logger.error("Kmeans model not found");
		}

		return prediction;
	}

}

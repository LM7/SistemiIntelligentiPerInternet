package wekaLibLinear;



import java.io.File;

import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class WekaMain {

	@SuppressWarnings("static-access")
	public static void main(String[] args)  {
		
		Train train = new Train();
		String modelFilename = "wekaLibLinear/model.txt";

		Test test = new Test();
		Instances testingSet;
		final File TEST_RESOURCES = new File("wekaLibLinear");
		String testingFilename = "Iris-novirginicaTest.arff";
		

		try{
			train.createModel(modelFilename);

			File file = new File(TEST_RESOURCES, testingFilename);

			ArffLoader loader = new ArffLoader();
			loader.setFile(file);
			testingSet = loader.getDataSet();
			testingSet.setClassIndex(testingSet.numAttributes() - 1);

			test.makeClassification(testingSet, modelFilename);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
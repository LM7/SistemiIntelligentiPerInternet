package wekaLibLinear;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;


import java.util.Random;


import de.bwaldvogel.liblinear.SolverType;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.evaluation.output.prediction.CSV;


/**
 * 
 * A Java class that implements a simple text learner, based on WEKA.
 * To be used with MyFilteredClassifier.java.
 * WEKA is available at: http://www.cs.waikato.ac.nz/ml/weka/
 * Copyright (C) 2013 Jose Maria Gomez Hidalgo - http://www.esp.uem.es/jmgomez
 *
 * This program is free software: you can redistribute it and/or modify
 * it for any purpose.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

 * @author Claudia Raponi (revisiting of Jose Maria Gomez Hidalgo's job)
 * see on GitHub: https://github.com/jmgomezh/tmweka
 */

public class Train {

	private final static File TEST_RESOURCES = new File("wekaLibLinear");
	
	/**
	 * Object that stores training data.
	 */
	static Instances trainingSet;
	static LibLINEAR liblinear = new LibLINEAR();
	
	
	 private static void loadInstancesFromARFF(String filename, String className) throws IOException {
	        File file = new File(TEST_RESOURCES, filename);

	        ArffLoader loader = new ArffLoader();
	        loader.setFile(file);
	        trainingSet = loader.getDataSet();
	        //Attribute classAttribute = trainingSet.attribute(className);
	        //trainingSet.setClass(classAttribute);
	    }

	public static void evaluate() {
		try {
			trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
			
			Evaluation eval = new Evaluation(trainingSet);
			
	        liblinear.setSolverType(SolverType.L2R_LR);
	        liblinear.setProbabilityEstimates(true);
	        liblinear.buildClassifier(trainingSet);
			
			/*Necessario per stampare i dettagli di predizione del valutatore*/
			StringBuffer output = new StringBuffer();
			AbstractOutput printout = new CSV(); 
		    printout.setBuffer(output); 
		    printout.setAttributes("1");
			
			eval.crossValidateModel(liblinear, trainingSet, 10, new Random(1),printout, null, true);
			
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toClassDetailsString());
			
			/*stampa valori di predizione*/
			//System.out.println("OutPut: "+output); 
		
			System.out.println("===== Evaluating on filtered (training) dataset done =====");
		}
		catch (Exception e) {
			System.out.println("Problem found when evaluating");
			System.out.println(e);
		}
	}

	/* 
	
	/**
	 * This method trains the classifier on the loaded dataset.
	 */
	public static void learn() {
		try {
			trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
			
	        liblinear.setSolverType(SolverType.L2R_LR);
	        liblinear.setProbabilityEstimates(true);
	        liblinear.buildClassifier(trainingSet);
	        
	        for (Instance instance : trainingSet) {
	            liblinear.classifyInstance(instance);
	            liblinear.distributionForInstance(instance);
	        }

			System.out.println("===== Training on filtered (training) dataset done =====");
		}
		catch (Exception e) {
			System.out.println("Problem found when training");
			System.out.println(e);
		}
	}

	/**
	 * This method saves the trained model into a file. This is done by
	 * simple serialization of the classifier object.
	 * @param fileName The name of the file that will store the trained model.
	 */
	public static void saveModel(String fileName) {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
			out.writeObject(liblinear);
			out.close();
			System.out.println("===== Saved model: " + fileName + " =====");
		} 
		catch (IOException e) {
			System.out.println("Problem found when writing: " + fileName);
		}
	}

	/**
	 * This method create the trained model into a file.
	 * @param arff The name of the arff file that will allow to create the trained model.
	 * 		  model The name of the file that will store the trained model.
	 * @throws Exception 
	 */
	public static void createModel(String model) throws Exception {
		//loadDataset(arff);
		loadInstancesFromARFF("iris-novirginica.arff", "class");
		//loadInstancesFromARFF("weka_testing2.arff", "class");
		//loadInstancesFromARFF("weather.arff", "class");
		
		evaluate();
		learn();
		saveModel(model);
		System.out.println();
	}
	
	public static void main(String args[]) throws Exception {
		createModel("wekaLibLinear/model.txt");
	}
}	
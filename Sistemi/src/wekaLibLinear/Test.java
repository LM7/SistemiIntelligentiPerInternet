package wekaLibLinear;


/**
 * A Java class that implements a simple text classifier, based on WEKA.
 * To be used with MyFilteredLearner.java.
 * WEKA is available at: http://www.cs.waikato.ac.nz/ml/weka/
 * Copyright (C) 2013 Jose Maria Gomez Hidalgo - http://www.esp.uem.es/jmgomez
 *
 * This program is free software: you can redistribute it and/or modify
 * it for any purpose.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * @author Claudia Raponi (revisiting of Jose Maria Gomez Hidalgo's job)
 * see on GitHub: https://github.com/jmgomezh/tmweka
 */

import weka.core.*;
import weka.classifiers.meta.FilteredClassifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.*;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * This class implements a simple text classifier in Java using WEKA.
 * It loads a file with the text to classify, and the model that has been
 * learnt with MyFilteredLearner.java.
 * @author Jose Maria Gomez Hidalgo - http://www.esp.uem.es/jmgomez
 * @see MyFilteredLearner
 */
public class Test {

	/**
	 * String that stores the text to classify
	 */
	String text;
	/**
	 * Object that stores the instance.
	 */
	Instances instances;
	/**
	 * Object that stores the classifier.
	 */
	static LibLINEAR liblinear = new LibLINEAR();

	/**
	 * This method loads the model to be used as classifier.
	 * @param fileName The name of the file that stores the text.
	 */
	public void loadModel(String fileName) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
			Object tmp = in.readObject();
			liblinear = (LibLINEAR) tmp;
			in.close();
			System.out.println("===== Loaded model: " + fileName + " =====");
		} 
		catch (Exception e) {
			// Given the cast, a ClassNotFoundException must be caught along with the IOException
			System.out.println("Problem found when reading: " + fileName);
		}
	}

	/**
	 * This method performs the classification of the instance.
	 * Output is done at the command-line.
	 * @throws IOException 
	 */
	public void classify(Instances i) throws IOException {
		Map<String, String> result = new HashMap<String,String>();
		instances = i;
		String prediction,istance;
		try {
			System.out.println("\n Number of valid instances analyzed: "+instances.numInstances()+"\n");
			
			System.out.println("===== Classified instance =====");
			for(int k=0;k<instances.numInstances();k++) {

				double pred = liblinear.classifyInstance(instances.instance(k));
				double[] distributions = liblinear.distributionForInstance(instances.instance(k));

				prediction = instances.classAttribute().value((int) pred);
		
				double predictionValue = distributions[(int) pred];

				System.out.println(instances.instance(k)+","+prediction+","+predictionValue);
				istance = instances.instance(k).toString();

				result.put(istance,prediction);
				System.out.println();
			}

		}
		catch (Exception e) {
			System.out.println("Problem found when classifying the text");
			System.out.println(e);
		}
		createResult(result,"wekaLibLinear/result.xls");
	}

	/*
	 * Metodo per la creazione del file result.xls:
	 * questo file contiene la classificazione predetta dal sistema
	 */
	public static void createResult(Map<String, String> result, String nameFile) throws IOException {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Result");
		int rownum = 0;
		Set<String> keys = result.keySet();
		for (String key : keys) {
			Row row = sheet.createRow(rownum++);
			int cellnum = 0;
			Cell cell = row.createCell(cellnum++);
			cell.setCellValue(key);
			Cell cellSent = row.createCell(cellnum++);
			cellSent.setCellValue(result.get(key));
		}
		try {
			FileOutputStream out = new FileOutputStream(new File(nameFile));
			workbook.write(out);
			out.close();
			System.out.println("Excel written successfully \n");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		workbook.close();
	}

	/**
	 * This method classify test dataset.
	 * @param arff The name of the arff file that will allow to classify the test dataset.
	 * 		  model The name of the file of model created by the training.
	 * @throws IOException 
	 */
	public void makeClassification (Instances arff, String model) throws IOException {
		loadModel(model);
		classify(arff);
	}
}	

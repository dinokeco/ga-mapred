package edu.ibu;

import java.io.FileInputStream;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.Remove;

public class Classifier {

	public static void main(String[] args) throws Exception {

		ArffLoader trainLoader = new ArffLoader();
		trainLoader.setSource(new FileInputStream("/home/dino/Desktop/phd/lung-cancer-train.arff"));
		Instances trainInstances = trainLoader.getDataSet();

		ArffLoader testLoader = new ArffLoader();
		testLoader.setSource(new FileInputStream("/home/dino/Desktop/phd/lung-cancer-test.arff"));
		Instances testInstances = testLoader.getDataSet();

		Remove r = new Remove();
		r.setInputFormat(trainInstances); //
		r.setInvertSelection(false);
		r.setAttributeIndicesArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });

		String[] options = new String[2];
		options[0] = "-R"; // "range"
		options[1] = "1,2-90,100-12601";

		r.setOptions(options); // r.batchFinished(); // Instances trainSubset =
		
		NumericToNominal converter = new NumericToNominal();
		String[] options2 = new String[2];
		options2[0] = "-R"; // "range"
		options2[1] = "1";
		converter.setOptions(options2);
		
		Instances xxx = Filter.useFilter(trainInstances, r);
		converter.setInputFormat(xxx);
		Instances newdata = Filter.useFilter(xxx, converter);
		newdata.setClassIndex(0);

		Instances testSubset = Filter.useFilter(Filter.useFilter(testInstances, r), converter);
		testSubset.setClassIndex(0);

		long ts = System.currentTimeMillis();
		MultilayerPerceptron mlp = new MultilayerPerceptron();
		mlp.buildClassifier(newdata);
		System.out.println("build time " + (System.currentTimeMillis() - ts));
		ts = System.currentTimeMillis();
		Evaluation e = new Evaluation(newdata);
		e.evaluateModel(mlp, testSubset);
		System.out.println("evaulation time " + (System.currentTimeMillis() - ts));
		System.out.println("Done " + e.toMatrixString());
	}

	// public static void main(String[] args) throws JsonGenerationException,
	// JsonMappingException, IOException {
	// int[][] array = new int[2][2];
	//
	// array[0][0]=1;
	// array[0][1]=2;
	// array[1][0]=3;
	// array[1][1]=4;
	//
	// for (int[] arr : array) {
	// System.out.println(Arrays.toString(arr));
	// }
	// }
}

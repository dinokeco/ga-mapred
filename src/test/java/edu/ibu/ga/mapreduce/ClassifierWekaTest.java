package edu.ibu.ga.mapreduce;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class ClassifierWekaTest {
	
	private Instances train;
	
	private Instances test;
	
	@Before
	public void setup() throws MalformedURLException, IOException{
		CSVLoader trainLoader = new CSVLoader();
		trainLoader.setSource(new URL("file:///home/dino/Dropbox/phd/nsl-kdd-dataset/KDDTrain+.csv").openStream());
		train = trainLoader.getDataSet();
		
		CSVLoader testLoader = new CSVLoader();
		testLoader.setSource(new URL("file:///home/dino/Dropbox/phd/nsl-kdd-dataset/KDDTest+.csv").openStream());
		test = testLoader.getDataSet();
	}
	
	@Test
	public void randomTreeKDDFull() throws Exception{
		RandomForest tree = new RandomForest();
		
		Remove remove = new Remove();
		remove.setAttributeIndicesArray(new int[] {0, 1, 6, 9, 11, 14, 15, 16, 19, 21, 22});
		remove.setInvertSelection(true);
		
		remove.setInputFormat(train);
		Instances trainSubset = Filter.useFilter(train, remove);
		Instances testSubset = Filter.useFilter(test, remove);
		trainSubset.setClassIndex(0);
		testSubset.setClassIndex(0);
		
		tree.buildClassifier(trainSubset);
		
		Evaluation e = new Evaluation(trainSubset);
		e.evaluateModel(tree, testSubset);
		
		// accuracy
		double accuracy = e.incorrect() / (e.correct() + e.incorrect() + e.unclassified());
		
		System.out.println("ACCURACY: "+ accuracy);
		System.out.println(e.toSummaryString("Random Tree IDS Full", true));
		System.out.println("TEST DONE");
		
	}
	
	@Ignore
	@Test
	public void randomTreeKDD21() throws Exception{
		RandomTree tree = new RandomTree();
		
		Remove remove = new Remove();
		remove.setAttributeIndicesArray(new int[] {0, 1, 5, 8, 10, 12, 14, 15, 18, 19, 20, 21});
		remove.setInvertSelection(true);
		
		remove.setInputFormat(train);
		Instances trainSubset = Filter.useFilter(train, remove);
		Instances testSubset = Filter.useFilter(test, remove);
		trainSubset.setClassIndex(0);
		testSubset.setClassIndex(0);
		
		tree.buildClassifier(trainSubset);
		
		Evaluation e = new Evaluation(testSubset);
		e.evaluateModel(tree, testSubset);
		// accuracy
		//double accuracy = e.correct() / (e.correct() + e.incorrect() + e.unclassified());
		System.out.println(e.toSummaryString());
		System.out.println("dino");
		
	}
}

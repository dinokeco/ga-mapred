package edu.ibu.ga.mapreduce.logic;

import java.io.IOException;
import java.net.URL;

import org.apache.hadoop.conf.Configuration;

import edu.ibu.ga.FitnessFunction;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class RandomForestCSVFitnessFunction implements FitnessFunction {

	private Instances trainInstances;

	private Instances testInstances;

	private RandomForest tree;

	@Override
	public void init(Configuration conf) {
		try {
			CSVLoader trainLoader = new CSVLoader();
			trainLoader.setSource(new URL(conf.get("ga.mapreduce.microarray.train.dataset")).openStream());
			trainInstances = trainLoader.getDataSet();
			
			CSVLoader testLoader = new CSVLoader();
			testLoader.setSource(new URL(conf.get("ga.mapreduce.microarray.test.dataset")).openStream());
			testInstances = testLoader.getDataSet();
			
			tree = new RandomForest();
			System.out.println("instances loaded from HDFS");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load train and test dataset", e);
		}
	}

	@Override
	public void evaluate(Chromosome c) {
		try {
			int onesCount = countOnes(c);
			
			Remove remove = new Remove();
			remove.setAttributeIndicesArray(getOneIndices(c, onesCount));
			remove.setInvertSelection(true);
			
			remove.setInputFormat(trainInstances);
			Instances trainSubset = Filter.useFilter(trainInstances, remove);
			
			remove.setInputFormat(testInstances);
			Instances testSubset = Filter.useFilter(testInstances, remove);

			trainSubset.setClassIndex(0);
			testSubset.setClassIndex(0);

			tree.buildClassifier(trainSubset);
			Evaluation e = new Evaluation(trainSubset);
			e.evaluateModel(tree, testSubset);
			// accuracy
			c.setFintess(e.correct() / (e.correct() + e.incorrect() + e.unclassified()));

			System.out.println(c.getBits().toString());
			System.out.println(c.getFintess());
			/*System.out.println("confussion matrix:");
			System.out.println(edu.ibu.ga.util.Util.toString(e.confusionMatrix()));
			System.out.println("Fitness (accuracy): " + c.getFintess());*/
		} catch (Exception e) {
			System.err.println(c.toString());
			e.printStackTrace();
			throw new RuntimeException("Failed to evaluate fitness", e);
		}
	}

	private int countOnes(Chromosome c) {
		int counter = 0;
		for (int i = 0; i < c.getLength(); i++) {
			if (c.getBits().get(i)) {
				counter++;
			}
		}
		return counter;
	}
	
	private int[] getOneIndices(Chromosome c, int totalOnes){
		int[] indices = new int[totalOnes+1];
		int counter = 1;
		indices[0] = 0;
		for (int i = 0; i < c.getLength(); i++) {
			if (c.getBits().get(i)) {
				indices[counter] = i+1;
				counter++;
			}
		}
		return indices;
	}
}

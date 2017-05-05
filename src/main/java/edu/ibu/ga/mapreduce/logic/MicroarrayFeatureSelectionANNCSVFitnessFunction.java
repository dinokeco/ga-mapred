package edu.ibu.ga.mapreduce.logic;

import java.io.IOException;
import java.net.URL;

import org.apache.hadoop.conf.Configuration;

import edu.ibu.ga.FitnessFunction;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class MicroarrayFeatureSelectionANNCSVFitnessFunction implements FitnessFunction {

	private Instances trainInstances;

	private Instances testInstances;

	private MultilayerPerceptron ann;

	@Override
	public void init(Configuration conf) {
		try {
			CSVLoader trainLoader = new CSVLoader();
			trainLoader.setSource(new URL(conf.get("ga.mapreduce.microarray.train.dataset")).openStream());
			trainInstances = trainLoader.getDataSet();
			
			CSVLoader testLoader = new CSVLoader();
			testLoader.setSource(new URL(conf.get("ga.mapreduce.microarray.test.dataset")).openStream());
			testInstances = testLoader.getDataSet();
			
			ann = new MultilayerPerceptron();
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

			ann.buildClassifier(trainSubset);
			Evaluation e = new Evaluation(trainSubset);
			e.evaluateModel(ann, testSubset);
			// accuracy
			c.setFintess(e.correct() / (e.correct() + e.incorrect() + e.unclassified()));
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

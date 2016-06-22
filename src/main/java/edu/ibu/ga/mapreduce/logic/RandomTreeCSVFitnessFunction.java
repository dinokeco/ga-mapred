package edu.ibu.ga.mapreduce.logic;

import java.io.IOException;
import java.net.URL;

import org.apache.hadoop.conf.Configuration;

import edu.ibu.ga.FitnessFunction;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomTree;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class RandomTreeCSVFitnessFunction implements FitnessFunction {

	private Instances trainInstances;

	private Instances testInstances;

	@Override
	public void init(Configuration conf) {
		try {
			CSVLoader trainLoader = new CSVLoader();
			trainLoader.setSource(new URL(conf.get("ga.mapreduce.microarray.train.dataset")).openStream());
			trainInstances = trainLoader.getDataSet();
			
			CSVLoader testLoader = new CSVLoader();
			testLoader.setSource(new URL(conf.get("ga.mapreduce.microarray.test.dataset")).openStream());
			testInstances = testLoader.getDataSet();
			
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

			
			RandomTree treeloc = new RandomTree();
			treeloc.buildClassifier(trainSubset);
			
			Evaluation e = new Evaluation(testSubset);
			e.evaluateModel(treeloc, testSubset);
			// accuracy
			c.setFintess(e.incorrect() / (e.correct() + e.incorrect() + e.unclassified()));
			System.out.println("   "+c.getFintess()+" f#"+c.getBits().toString());
			
			/*System.out.println(trainSubset.toSummaryString());
			System.out.println(edu.ibu.ga.util.Util.toString(e.confusionMatrix()));
			System.out.println(e.toSummaryString());*/
			/*System.out.println("confussion matrix:");
			System.out.println(edu.ibu.ga.util.Util.toString(e.confusionMatrix()));
			System.out.println("Fitness (accuracy): " + c.getFintess());*/
			// 0.8116455696202531 F#{0, 1, 2, 5, 22, 24, 28, 30, 39} 
			//0.7930801687763713 F#{0, 1, 2, 4, 5, 8, 10, 19, 21, 27, 29, 38, 40}


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

package edu.ibu.ga.mapreduce.logic;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.Remove;
import edu.ibu.ga.FitnessFunction;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import edu.ibu.ga.util.Util;

public class MicroarrayFeatureSelectionANNFitnessFunction implements FitnessFunction {

	private Instances trainInstances;
	private Instances testInstances;
	private MultilayerPerceptron ann;

	@Override
	public void init(Configuration conf) {
		try {
			Path trainPath = new Path(conf.get("ga.mapreduce.microarray.train.dataset"));
			FSDataInputStream trainInput = FileSystem.get(conf).open(trainPath);
			ArffLoader trainLoader = new ArffLoader();
			trainLoader.setSource(trainInput);
			trainInstances = trainLoader.getDataSet();
			Path testPath = new Path(conf.get("ga.mapreduce.microarray.test.dataset"));
			FSDataInputStream testInput = FileSystem.get(conf).open(testPath);
			ArffLoader testLoader = new ArffLoader();
			testLoader.setSource(testInput);
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
			int onesCount = Util.countOnes(c);
			Remove r = buildAttributeSelectionFilter(onesCount, c);
			NumericToNominal converter = new NumericToNominal();
			String[] options = new String[2];
			options[0] = "-R";
			options[1] = "1";
			converter.setOptions(options);
			r.setInputFormat(trainInstances);
			Instances trainSubset = Filter.useFilter(trainInstances, r);
			r.setInputFormat(testInstances);
			Instances testSubset = Filter.useFilter(testInstances, r);
			converter.setInputFormat(trainSubset);
			trainSubset = Filter.useFilter(trainSubset, converter);
			testSubset = Filter.useFilter(testSubset, converter);
			trainSubset.setClassIndex(0);
			testSubset.setClassIndex(0);

			ann.buildClassifier(trainSubset);
			Evaluation e = new Evaluation(trainSubset);
			e.evaluateModel(ann, testSubset);
			// accuracy
			c.setFintess(e.correct() / (e.correct() + e.incorrect() + e.unclassified()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to evaluate fitness", e);
		}
	}

	

	private Remove buildAttributeSelectionFilter(int onesCount, Chromosome c) throws Exception {
		Remove filter = new Remove();

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < c.getLength(); i++) {
			if (!c.getBits().get(i)) {
				builder.append(i + 2).append(",");
			}
		}
		String[] options = new String[2];
		options[0] = "-R";
		if (builder.length() > 1) {
			options[1] = builder.substring(0, builder.length() - 1).toString();
		}

		filter.setOptions(options);
		return filter;
	}
}

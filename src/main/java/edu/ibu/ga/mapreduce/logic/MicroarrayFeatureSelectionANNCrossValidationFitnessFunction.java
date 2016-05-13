package edu.ibu.ga.mapreduce.logic;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

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

public class MicroarrayFeatureSelectionANNCrossValidationFitnessFunction implements FitnessFunction {

	private Map<Integer, Instances> data;
	private MultilayerPerceptron ann;
	private int folds;

	@Override
	public void init(Configuration conf) {
		data = new TreeMap<Integer, Instances>();

		try {
			folds = conf.getInt("ga.mapreduce.microarray.fold.size", 0);
			String path = conf.get("ga.mapreduce.microarray.dataset");
			String label = conf.get("ga.mapreduce.label");

			for (int i = 1; i <= folds; i++) {
				Path p = new Path(path + label + "_fold_" + i + ".arrf");
				FSDataInputStream in = FileSystem.get(conf).open(p);
				ArffLoader trainLoader = new ArffLoader();
				trainLoader.setSource(in);
				data.put(i, trainLoader.getDataSet());
			}

			ann = new MultilayerPerceptron();

			System.out.println("instances loaded from HDFS " + data.keySet().toString());
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

			r.setInputFormat(data.values().iterator().next());

			for (int i = 1; i <= folds; i++) {

				Instances test = null;
				Instances train = null;

				for (int j = 1; j <= folds; j++) {
					if (i == j) {
						// train subset
						test = Filter.useFilter(data.get(j), r);
					} else {
						// test subset
						if (train == null) {
							train = Filter.useFilter(data.get(j), r);
						} else {
							Instances flt = Filter.useFilter(data.get(j), r);
							for (int k = 0; k < flt.numInstances(); k++) {
								train.add(flt.instance(k));
							}
						}
					}
				}

				converter.setInputFormat(test);
				
				test = Filter.useFilter(test, converter);
				train = Filter.useFilter(train, converter);

				test.setClassIndex(0);
				train.setClassIndex(0);

				ann.buildClassifier(train);
				Evaluation e = new Evaluation(train);
				e.evaluateModel(ann, test);

				double accuracy = e.correct() / (e.correct() + e.incorrect() + e.unclassified());
				if (c.getFintess() < accuracy) {
					c.setFintess(accuracy);
				}

				System.out.println(i + " Confussion Matrix:");
				System.out.println(edu.ibu.ga.util.Util.toString(e.confusionMatrix()));
				System.out.println(i + " Accuracy: " + accuracy);
				System.out.println(i + " Fitness: " + c.getFintess());

			}

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

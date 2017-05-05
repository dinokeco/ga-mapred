package edu.ibu.ga.mapreduce.logic;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;

import edu.ibu.ga.FitnessFunction;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class MicroarrayFeatureSelectionSvmCsvCvFitnessFunction implements FitnessFunction {

	private Map<Integer, Instances> data;
	private SMO svm;
	private int folds;

	@Override
	public void init(Configuration conf) {
		data = new TreeMap<Integer, Instances>();

		try {
			folds = conf.getInt("ga.mapreduce.microarray.fold.size", 0);
			String path = conf.get("ga.mapreduce.microarray.dataset");
			String label = conf.get("ga.mapreduce.label");

			for (int i = 1; i <= folds; i++) {
				CSVLoader loader = new CSVLoader();
				loader.setSource(new URL(path + label + "_fold_" + folds + "_" + i + ".csv").openStream());
				data.put(i, loader.getDataSet());
			}

			svm = new SMO();

			System.out.println("instances loaded from HDFS " + data.keySet().toString());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load train and test dataset", e);
		}
	}

	@Override
	public void evaluate(Chromosome c) {
		try {
			Remove remove = new Remove();
			remove.setAttributeIndicesArray(c.getOneIndices());
			remove.setInvertSelection(true);

			double[] accuracies = new double[folds];
			for (int f = 1; f <= folds; f++) {
				// test data set is same as fold
				Instances test = this.data.get(f);
				
				// all rest are train instances
				Instances train = null;
				for(int i = 1; i <= folds; i++){
					if (i != f){
						if (train == null){ // if train dataset is empty use first fold
							train = this.data.get(i);
						}else{
							train.addAll(this.data.get(i)); // append fold to train instances
						}
					}
				}

				remove.setInputFormat(train);
				Instances trainSubset = Filter.useFilter(train, remove);

				remove.setInputFormat(test);
				Instances testSubset = Filter.useFilter(test, remove);

				trainSubset.setClassIndex(0);
				testSubset.setClassIndex(0);

				svm.buildClassifier(trainSubset);
				Evaluation e = new Evaluation(trainSubset);
				e.evaluateModel(svm, testSubset);
				
				accuracies[f-1] = e.correct() / (e.correct() + e.incorrect() + e.unclassified());
				
				//System.out.println("Fold Fitness (accuracy): " + accuracies[f-1]);
			}
			
			// accuracy is average accuracy of all others
			double sum = 0;
			for(int i = 0 ; i < folds; i++){
				sum += accuracies[i];
			}

			c.setFintess(sum/folds);

			System.out.println("AVG Fitness (accuracy): " + c.getFintess());
		} catch (Exception e) {
			System.err.println(c.toString());
			e.printStackTrace();
			throw new RuntimeException("Failed to evaluate fitness", e);
		}
	}
}

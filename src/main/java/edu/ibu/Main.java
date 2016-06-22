package edu.ibu;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.unsupervised.attribute.Remove;

public class Main {

	public static void main(String[] args) throws Exception {
		CSVLoader l = new CSVLoader();
		l.setSource(new File("/home/dino/Dropbox/phd/IDS dataset/std-filtered-kdd-train.csv"));
		//l.setSource(new URL("https://s3-us-west-1.amazonaws.com/dino.ga/datasets/leukemia2_train.csv").openStream());
				//new File("data/leukemia2_train.csv"));
		
		Instances train = l.getDataSet();
		
		l.setSource(new File("/home/dino/Dropbox/phd/IDS dataset/std-filtered-kdd-test.csv"));
		//l.setSource(new URL("https://s3-us-west-1.amazonaws.com/dino.ga/datasets/leukemia2_test.csv").openStream());
				//new File("data/leukemia2_test.csv"));
		Instances test = l.getDataSet();
		
		int numberOfAttributes = Collections.list(train.enumerateAttributes()).size();
		
		System.out.println(numberOfAttributes);
		Random r = new Random();
		for (int iter = 0; iter < 50; iter++) {
			// how may features to use:
			int featuresToUse = r.nextInt(30)+5;
			
			Set<Integer> featuresSet = new HashSet<Integer>();
			for (int i = 0; i < featuresToUse; i++) {
				featuresSet.add(r.nextInt(numberOfAttributes-1)+1);
			}
			
			int[] features = new int[featuresSet.size() + 1];
			int i = 1;
			for (Integer ft : featuresSet) {
				features[i] = ft;
				i++;
			}
			features[0] = 0;
			
			Remove remove = new Remove();
			remove.setAttributeIndicesArray(features);
			remove.setInvertSelection(true);
			remove.setInputFormat(train);
			
			Instances subTrain = weka.filters.Filter.useFilter(train, remove);
			subTrain.setClassIndex(0);
			Instances subTest = weka.filters.Filter.useFilter(test, remove);
			subTest.setClassIndex(0);
			
			SMO ann = new SMO();
			ann.buildClassifier(subTrain);
			Evaluation e = new Evaluation(subTrain);
			e.evaluateModel(ann, subTest);
			
			System.out.println(featuresToUse+" features "+Arrays.toString(features)+" : accuracy ="+ (e.correct() / (e.correct() + e.incorrect() + e.unclassified())));
		}
	}
}

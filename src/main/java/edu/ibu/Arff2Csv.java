package edu.ibu;

import java.io.File;
import java.io.IOException;

import weka.core.converters.ArffLoader;
import weka.core.converters.CSVSaver;

public class Arff2Csv {

	public static void main(String[] args) throws IOException {
		
		ArffLoader loader = new ArffLoader();
		loader.setSource(new File("/home/dino/Dropbox/phd/IDS dataset/KDDTrain+.arff"));
		
		//System.out.println(loader.getDataSet());
		
		CSVSaver saver = new CSVSaver();
		saver.setFile(new File("/home/dino/Dropbox/phd/IDS dataset/full-kdd-train.csv"));
		saver.setInstances(loader.getDataSet());
		saver.writeBatch();
	}
}

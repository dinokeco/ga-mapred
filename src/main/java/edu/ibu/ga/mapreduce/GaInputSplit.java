package edu.ibu.ga.mapreduce;

import java.io.IOException;
import java.io.Serializable;

import org.apache.hadoop.mapreduce.InputSplit;

public class GaInputSplit extends InputSplit implements Serializable{

	private static final long serialVersionUID = -8944408665332565208L;

	@Override
	public long getLength() throws IOException, InterruptedException {
		return 1;
	}

	@Override
	public String[] getLocations() throws IOException, InterruptedException {
		return new String[] {};
	}

}

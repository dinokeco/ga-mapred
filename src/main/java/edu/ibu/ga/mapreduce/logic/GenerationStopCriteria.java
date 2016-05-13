package edu.ibu.ga.mapreduce.logic;

import org.apache.hadoop.conf.Configuration;

import edu.ibu.ga.StopCriteria;

public class GenerationStopCriteria implements StopCriteria{

	private int numberOfGenerations;
	
	@Override
	public void init(Configuration conf) {
		this.numberOfGenerations = conf.getInt("ga.mapreduce.number.of.generations", 0);
	}

	@Override
	public boolean terminate(int generation, double maxFitnes, double averageFitnes, double maxFitnesDelta, double averageFitnesDelta) {
		return !(generation < numberOfGenerations);
	}

}

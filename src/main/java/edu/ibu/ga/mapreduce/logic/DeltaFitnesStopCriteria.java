package edu.ibu.ga.mapreduce.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;

import edu.ibu.ga.StopCriteria;
import edu.ibu.ga.mapreduce.domain.Population;

public class DeltaFitnesStopCriteria implements StopCriteria{

	private int numberOfGenerations;
	
	private int generationsWindow;
	
	private List<Double> bestFitnesHistory;
	
	@Override
	public void init(Configuration conf) {
		this.numberOfGenerations = conf.getInt("ga.mapreduce.number.of.generations", 0);
		
		this.generationsWindow = conf.getInt("ga.mapreduce.generations.window", 0);
		
		this.bestFitnesHistory = new ArrayList<Double>();
	}

	@Override
	@Deprecated
	public boolean terminate(int generation, double maxFitnes, double averageFitnes, double maxFitnesDelta, double averageFitnesDelta) {
		return !(generation < numberOfGenerations);
	}

	public boolean terminate(Population population) {
		this.bestFitnesHistory.add(population.getMaxFitness());
		Set<Double> windowFitness = new HashSet<Double>();
		
		int counter = 0;
		for (int i = bestFitnesHistory.size()-1; i >=0 ; i--) {
			windowFitness.add(bestFitnesHistory.get(i));
			if (counter > generationsWindow){
				break;
			}
			counter++;
		}
		if (windowFitness.size() == 1 && population.getGeneration() > generationsWindow){
			return true;
		}
		
		return !(population.getGeneration() < numberOfGenerations);
	}
}

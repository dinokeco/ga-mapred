package edu.ibu.ga.mapreduce.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Population implements Serializable {

	private static final long serialVersionUID = 8417948678909211938L;

	private List<Chromosome> chromosomes;

	private double averageFitness;

	private double maxFitness;

	private double averageFitnessDelta;

	private double maxFitnessDelta;
	
	private int generation;

	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

	public Population(int size) {
		setChromosomes(new ArrayList<Chromosome>(size));
	}

	public List<Chromosome> getChromosomes() {
		return chromosomes;
	}

	public void setChromosomes(List<Chromosome> chromosomes) {
		this.chromosomes = chromosomes;
	}

	public double getAverageFitness() {
		return averageFitness;
	}

	public void setAverageFitness(double averageFitness) {
		this.averageFitness = averageFitness;
	}

	public double getMaxFitness() {
		return maxFitness;
	}

	public void setMaxFitness(double maxFitness) {
		this.maxFitness = maxFitness;
	}

	public double getAverageFitnessDelta() {
		return averageFitnessDelta;
	}

	public void setAverageFitnessDelta(double averageFitnessDelta) {
		this.averageFitnessDelta = averageFitnessDelta;
	}

	public double getMaxFitnessDelta() {
		return maxFitnessDelta;
	}

	public void setMaxFitnessDelta(double maxFitnessDelta) {
		this.maxFitnessDelta = maxFitnessDelta;
	}

	public void addChromosome(Chromosome c) {
		this.chromosomes.add(c);
	}

	public void setChromosome(int index, Chromosome c) {
		this.chromosomes.set(index, c);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("chromosomes=").append(chromosomes.toString()).append("\n");
		builder.append("averageFitness=").append(averageFitness).append("\n");

		return builder.toString();
	}

}

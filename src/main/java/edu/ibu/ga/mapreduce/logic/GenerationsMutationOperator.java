package edu.ibu.ga.mapreduce.logic;

import org.apache.hadoop.conf.Configuration;

import acm.util.RandomGenerator;

import edu.ibu.ga.MutationOpeator;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import edu.ibu.ga.mapreduce.domain.Population;

/**
 * Generations mutation operator increases mutation probability by 1% every 100 generations
 * @author dino
 *
 */
public class GenerationsMutationOperator implements MutationOpeator{

	private RandomGenerator gen;
	
	private float mutationProbability;
	
	private int lastChangedGeneration;
	
	private int mutationGenerations;
	
	@Override
	public void init(Configuration conf) {
		gen = new RandomGenerator();
		gen.setSeed(System.nanoTime());
		this.mutationProbability = conf.getFloat("ga.mapreduce.mutation.probability", 0);
		this.mutationGenerations = conf.getInt("ga.mapreduce.mutation.generations", 100);
	}

	@Deprecated
	@Override
	public void mutate(Chromosome c, float mutationProbability) {
	}

	@Override
	public void mutate(Chromosome c, Population population) {
		if (population.getGeneration() % mutationGenerations == 0 && population.getGeneration() != 0 && population.getGeneration() != lastChangedGeneration){
			this.mutationProbability += 0.01*this.mutationProbability;
			lastChangedGeneration = population.getGeneration();
		}
		
		if (gen.nextDouble() < mutationProbability){
			int index = gen.nextInt(c.getLength());
			c.getBits().set(index, !c.getBits().get(index));
		}
	}

}

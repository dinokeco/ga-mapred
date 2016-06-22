package edu.ibu.ga.mapreduce.logic;

import org.apache.hadoop.conf.Configuration;

import acm.util.RandomGenerator;

import edu.ibu.ga.MutationOpeator;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import edu.ibu.ga.mapreduce.domain.Population;

public class StandardMutationOperator implements MutationOpeator{

	private RandomGenerator gen;
	
	private float mutationProbability;
	
	@Override
	public void init(Configuration conf) {
		gen = new RandomGenerator();
		gen.setSeed(System.nanoTime());
		this.mutationProbability = conf.getFloat("ga.mapreduce.mutation.probability", 0);
	}

	@Deprecated
	@Override
	public void mutate(Chromosome c, float mutationProbability) {
		if (gen.nextDouble() < mutationProbability){
			int index = gen.nextInt(c.getLength());
			c.getBits().set(index, !c.getBits().get(index));
		}
	}

	@Override
	public void mutate(Chromosome c, Population population) {
		if (gen.nextDouble() < mutationProbability){
			int index = gen.nextInt(c.getLength());
			c.getBits().set(index, !c.getBits().get(index));
		}
	}

}

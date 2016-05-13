package edu.ibu.ga.mapreduce.logic;

import org.apache.hadoop.conf.Configuration;

import acm.util.RandomGenerator;

import edu.ibu.ga.MutationOpeator;
import edu.ibu.ga.mapreduce.domain.Chromosome;

public class StandardMutationOperator implements MutationOpeator{

	private RandomGenerator gen;
	
	@Override
	public void init(Configuration conf) {
		gen = new RandomGenerator();
		gen.setSeed(System.nanoTime());
	}

	@Override
	public void mutate(Chromosome c, float mutationProbability) {
		if (gen.nextDouble() < mutationProbability){
			int index = gen.nextInt(c.getLength());
			c.getBits().set(index, !c.getBits().get(index));
		}
	}

}

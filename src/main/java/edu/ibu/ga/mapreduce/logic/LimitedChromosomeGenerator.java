package edu.ibu.ga.mapreduce.logic;

import org.apache.hadoop.conf.Configuration;

import acm.util.RandomGenerator;
import edu.ibu.ga.ChromosomeGenerator;
import edu.ibu.ga.mapreduce.domain.Chromosome;

public class LimitedChromosomeGenerator implements ChromosomeGenerator {

	private RandomGenerator randomGenerator;

	private int chromosomeLength;

	private int limit;

	@Override
	public Chromosome generateNextChromosome() {
		Chromosome c = new Chromosome(chromosomeLength);
		for (int i = 0; i < limit; i++) {
			int randomIndex = randomGenerator.nextInt(0, chromosomeLength);
			c.getBits().set(randomIndex);
		}
		return c;
	}

	@Override
	public void init(Configuration conf) {
		chromosomeLength = conf.getInt("ga.mapreduce.chromosome.length", 0);
		limit = conf.getInt("ga.mapreduce.chromosome.limit", 10);
		randomGenerator = new RandomGenerator();
		randomGenerator.setSeed(System.nanoTime());

		System.out.println("Chromosome length " + chromosomeLength);
	}

}

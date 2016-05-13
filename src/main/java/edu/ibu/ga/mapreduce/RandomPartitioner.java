package edu.ibu.ga.mapreduce;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Partitioner;

import acm.util.RandomGenerator;

import edu.ibu.ga.mapreduce.domain.Chromosome;

public class RandomPartitioner extends Partitioner<NullWritable, Chromosome> {

	private RandomGenerator random;

	public RandomPartitioner() {
		random = new RandomGenerator();
		random.setSeed(System.nanoTime());
	}

	@Override
	public int getPartition(NullWritable key, Chromosome value, int numPartitions) {
		return random.nextInt(0, numPartitions - 1);
	}

}

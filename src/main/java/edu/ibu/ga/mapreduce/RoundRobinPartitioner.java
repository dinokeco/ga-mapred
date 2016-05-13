package edu.ibu.ga.mapreduce;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Partitioner;

import edu.ibu.ga.mapreduce.domain.Chromosome;

public class RoundRobinPartitioner extends Partitioner<NullWritable, Chromosome> {

	int counter = 0;

	@Override
	public int getPartition(NullWritable key, Chromosome value, int numPartitions) {
		return (counter++ % numPartitions);
	}

}

package edu.ibu.ga.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import edu.ibu.ga.mapreduce.domain.Chromosome;

public class DgaReducer extends Reducer<NullWritable, Chromosome, NullWritable, NullWritable> {

	private double maxFitness = 0;

	private Chromosome bestOfBest;

	@Override
	protected void reduce(NullWritable key, Iterable<Chromosome> values, Context context) throws IOException, InterruptedException {
		
		while (values.iterator().hasNext()) {
			Chromosome c = values.iterator().next();
			if (maxFitness < c.getFintess()) {
				maxFitness = c.getFintess();
				bestOfBest = c.deepCopy();
			}
		}
		context.getCounter("best", bestOfBest.toString()).increment(0);
		//context.write(NullWritable.get(), bestOfBest);
	}
}

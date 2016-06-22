package edu.ibu.ga.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import edu.ibu.ga.mapreduce.domain.Chromosome;

public class DgaReducer extends Reducer<NullWritable, Chromosome, NullWritable, Text> {

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
		/*for (int i = 0; i < bestOfBest.getBits().length(); i++) {
			if (bestOfBest.getBits().get(i)){
				context.getCounter("best-features", String.valueOf(i)).increment(0);
			}
		}
		*/
		//TODO write output to S3 file
		context.getCounter("best-fitness", String.valueOf(bestOfBest.getFintess())).increment(0);
		context.write(NullWritable.get(), new Text(bestOfBest.getBits().toString()+"$"+bestOfBest.toString()));
	}
}

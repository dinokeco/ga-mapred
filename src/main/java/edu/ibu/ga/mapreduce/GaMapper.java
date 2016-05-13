package edu.ibu.ga.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;

import edu.ibu.ga.ChromosomeComparator;
import edu.ibu.ga.FitnessFunction;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import edu.ibu.ga.util.Util;

public class GaMapper extends Mapper<NullWritable, Chromosome, NullWritable, Chromosome> {

	private FitnessFunction fitnessFunction;
	private ChromosomeComparator comparator;
	private Chromosome best;
	private String bestRecordLocation;
	private boolean skiped = true;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		bestRecordLocation = context.getConfiguration().get("ga.mapreduce.best.record.location");
		best = Util.readBestChromosome(bestRecordLocation, context.getConfiguration());
		fitnessFunction = (FitnessFunction) Util.initializeClass(context.getConfiguration(), context.getConfiguration().get("ga.mapreduce.fitness.function"));
		comparator = (ChromosomeComparator) Util.initializeClass(context.getConfiguration(), context.getConfiguration().get("ga.mapreduce.chromosome.comparator"));
		// elitism
		if (best != null && context.getTaskAttemptID().getTaskID().getId() == 0) {
			skiped = false;
			context.write(NullWritable.get(), best);
		}
	}

	@Override
	protected void map(NullWritable key, Chromosome value, Context context) throws IOException, InterruptedException {
		fitnessFunction.evaluate(value);
		
		// first mapper skip one record because elitism
		if (best != null && best.getFintess() > value.getFintess() && context.getTaskAttemptID().getTaskID().getId() == 0 && !skiped) {
			skiped = true;
			return;
		}
		
		// capture best
		if (comparator.isBetter(best, value)) {
			best = new Chromosome(value.getLength());
			best.fromString(value.toString());
		}
		context.setStatus("working");
		context.progress();
		
		context.write(NullWritable.get(), value);
	}

	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		if (best != null) {
			context.getCounter("ga" + context.getTaskAttemptID().toString(), "best").increment((long) (best.getFintess()*100));
			writeBestChromosome(bestRecordLocation + context.getConfiguration().get("ga.mapreduce.generation") + "_" + context.getTaskAttemptID().toString(), context.getConfiguration(), best);
		}
	}

	private void writeBestChromosome(String location, Configuration conf, Chromosome best) throws IOException {
		FileSystem fs = FileSystem.get(conf);
		FSDataOutputStream out = fs.create(new Path(location));
		out.writeUTF(best.toString());
		out.close();
	}

	
}

package edu.ibu.ga.mapreduce.job;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import edu.ibu.ga.StopCriteria;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import edu.ibu.ga.util.Util;

public class GaJob extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		ToolRunner.run(new GaJob(), args);
		System.out.println("Done in: " + (System.currentTimeMillis() - start) + " ms");
	}

	@Override
	public int run(String[] args) throws Exception {

		StopCriteria stopCriteria = (StopCriteria) Util.initializeClass(getConf(), getConf().get("ga.mapreduce.stop.criteria"));

		int generation = 1;
		do {
			getConf().setInt("ga.mapreduce.generation", generation);
			getConf().set("mapred.output.dir", getConf().get("ga.mapreduce.output.location") + generation + "/");
			String name = "GA (" + getConf().get("ga.mapreduce.label", "") + ") " + generation;
			Job job = Job.getInstance(getConf(), name);
			
			job.setJarByClass(GaJob.class);
			job.waitForCompletion(true);
			getConf().set("ga.mapreduce.generate.initial.population", "false");
			getConf().set("mapred.input.dir", getConf().get("ga.mapreduce.output.location") + generation + "/");
			generation++;
		} while (!stopCriteria.terminate(generation, 0, 0, 0, 0));

		Chromosome best = Util.readBestChromosome(getConf().get("ga.mapreduce.best.record.location"), getConf());
		System.out.println("Best = " + best.getFintess());
		System.out.println(best.toString());
		System.out.println("1: " + Util.countOnes(best));
		System.out.println("0: " + (best.getLength() - Util.countOnes(best)));
		return 0;
	}
}

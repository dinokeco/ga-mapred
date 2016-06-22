package edu.ibu.ga.mapreduce.job;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class IdentityJob extends Configured implements Tool {
	
	public static void main(String[] args) throws Exception {
		ToolRunner.run(new DgaJob(), args);
	}

	@Override
	public int run(String[] args) throws Exception {
		Job job = Job.getInstance(getConf(), "identity job");
		job.setJarByClass(IdentityJob.class);
		return (job.waitForCompletion(true) ? 0 : 1);
	}
}

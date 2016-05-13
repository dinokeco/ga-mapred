package edu.ibu.ga.mapreduce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import edu.ibu.ga.mapreduce.domain.Chromosome;

public class GaInputFormat extends FileInputFormat<NullWritable, Chromosome> implements Configurable {

	private Configuration conf;

	@Override
	public RecordReader<NullWritable, Chromosome> createRecordReader(InputSplit inputSplit, TaskAttemptContext context) throws IOException, InterruptedException {
		return new GaRecordReader();
	}

	@Override
	public List<InputSplit> getSplits(JobContext job) throws IOException {
		Configuration conf = job.getConfiguration();
		if (conf.getBoolean("ga.mapreduce.generate.initial.population", true)) {
			// create dummy splits because of FileInputFormat inheritance
			super.getSplits(job);
			int numberOfMappers = conf.getInt("ga.mapreduce.number.of.mappers", 0);
			List<InputSplit> inputSplits = new ArrayList<InputSplit>();
			for (int i = 0; i < numberOfMappers; i++) {
				inputSplits.add(new GaInputSplit());
			}
			return inputSplits;
		} else {
			return super.getSplits(job);
		}
	}

	@Override
	public Configuration getConf() {
		return conf;
	}

	@Override
	public void setConf(Configuration conf) {
		this.conf = conf;
	}
}

package edu.ibu.ga.mapreduce;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import edu.ibu.ga.mapreduce.domain.Population;

public class DgaInputFormat extends InputFormat<NullWritable, Population> implements Configurable {

	private Configuration conf;

	@Override
	public RecordReader<NullWritable, Population> createRecordReader(InputSplit inputSplit, TaskAttemptContext context) throws IOException, InterruptedException {
		return new DgaRecordReader();
	}

	@Override
	public List<InputSplit> getSplits(JobContext context) throws IOException, InterruptedException {
		Configuration conf = context.getConfiguration();
		int numberOfMappers = conf.getInt("ga.mapreduce.number.of.mappers", 0);
		List<InputSplit> inputSplits = new ArrayList<InputSplit>();
		for (int i = 0; i < numberOfMappers; i++) {
			inputSplits.add(new DgaInputSplit());
		}
		return inputSplits;
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

package edu.ibu.ga.mapreduce;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class IdentityMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

	private final static IntWritable one = new IntWritable(1);
	private Text word = new Text();

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		System.out.println("init mapper done");
	}

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString();
		StringTokenizer tokenizer = new StringTokenizer(line);
		while (tokenizer.hasMoreTokens()) {
			word.set(tokenizer.nextToken());
			context.write(word, one);
		}
	}

	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		System.out.println("cleanup done");
	}

}

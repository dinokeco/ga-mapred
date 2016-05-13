package edu.ibu.ga.mapreduce;

import java.io.IOException;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import edu.ibu.ga.mapreduce.domain.Chromosome;

public class GaOutputFormat extends TextOutputFormat<NullWritable, Chromosome> {

	public RecordWriter<NullWritable, Chromosome> getRecordWriter(TaskAttemptContext context) throws java.io.IOException, InterruptedException {
		FileSystem fs = FileSystem.get(context.getConfiguration());
		String folder = context.getConfiguration().get("mapred.output.dir");
		FSDataOutputStream out = fs.create(new Path(folder + getUniqueFile(context, "part", "")));
		return new GaRecordWritter(out);
	}

	public static class GaRecordWritter extends RecordWriter<NullWritable, Chromosome> {
		private LineRecordWriter<NullWritable, Text> writter;
		private FSDataOutputStream out;
		
		public GaRecordWritter(FSDataOutputStream out) {
			this.out = out;
			this.writter = new LineRecordWriter<NullWritable, Text>(out);
		}

		@Override
		public void close(TaskAttemptContext context) throws IOException, InterruptedException {
			out.close();
		}

		@Override
		public void write(NullWritable key, Chromosome value) throws IOException, InterruptedException {
			writter.write(NullWritable.get(), new Text(value.toString()));
		}

	}
}

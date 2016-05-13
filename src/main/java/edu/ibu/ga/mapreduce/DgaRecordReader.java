package edu.ibu.ga.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import edu.ibu.ga.ChromosomeGenerator;
import edu.ibu.ga.mapreduce.domain.Population;
import edu.ibu.ga.util.Util;

public class DgaRecordReader extends RecordReader<NullWritable, Population> {

	private boolean triggered = false;

	private ChromosomeGenerator chromosomeGenerator;
	
	private Population population;

	@Override
	public void close() throws IOException {
		// all in memory nothing to close
	}

	@Override
	public NullWritable getCurrentKey() throws IOException, InterruptedException {
		return NullWritable.get();
	}

	@Override
	public Population getCurrentValue() throws IOException, InterruptedException {
		return population;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		return 0;
	}

	@Override
	public void initialize(InputSplit inputSplit, TaskAttemptContext context) throws IOException, InterruptedException {
		int nodePopulationSize = context.getConfiguration().getInt("ga.mapreduce.population.size.per.node", 0);
		String cromosomeGeneratorClass = context.getConfiguration().get("ga.mapreduce.chromosome.generator");
		// inject chromosome generator
		chromosomeGenerator = (ChromosomeGenerator) Util.initializeClass(context.getConfiguration(), cromosomeGeneratorClass);
		
		population = new Population(nodePopulationSize);
		for (int i=0; i<nodePopulationSize; i++){
			population.addChromosome(chromosomeGenerator.generateNextChromosome());
		}
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		// allow just one trigger of map method
		if (!triggered){
			triggered = true;
			return true;
		}
		return !triggered;
	}


}

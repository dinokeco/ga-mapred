package edu.ibu.ga.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;

import edu.ibu.ga.ChromosomeGenerator;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import edu.ibu.ga.util.Util;

public class GaRecordReader extends RecordReader<NullWritable, Chromosome> {

	private ChromosomeGenerator chromosomeGenerator;

	private Chromosome chromosome;

	private int chromosomesPerNode = 0;

	private int counter = 0;

	private boolean generateInitialPopulation;

	private int chromosomeLength = 0;

	private LineRecordReader reader;

	@Override
	public void close() throws IOException {
		if (!generateInitialPopulation) {
			reader.close();
		}
	}

	@Override
	public NullWritable getCurrentKey() throws IOException, InterruptedException {
		return NullWritable.get();
	}

	@Override
	public Chromosome getCurrentValue() throws IOException, InterruptedException {
		if (generateInitialPopulation) {
			chromosome = chromosomeGenerator.generateNextChromosome();
			counter++;
		} else {
			Text string = reader.getCurrentValue();
			chromosome = new Chromosome(chromosomeLength);
			chromosome.fromString(string.toString());
		}
		return chromosome;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		if (generateInitialPopulation) {
			return counter / ((float) chromosomesPerNode);
		} else {
			return reader.getProgress();
		}
	}

	@Override
	public void initialize(InputSplit inputSplit, TaskAttemptContext context) throws IOException, InterruptedException {
		generateInitialPopulation = context.getConfiguration().getBoolean("ga.mapreduce.generate.initial.population", true);
		chromosomeLength = context.getConfiguration().getInt("ga.mapreduce.chromosome.length", 0);

		if (generateInitialPopulation) {
			int populationSize = context.getConfiguration().getInt("ga.mapreduce.population.size", 0);
			int numberOfMappers = context.getConfiguration().getInt("ga.mapreduce.number.of.mappers", 0);
			chromosomesPerNode = populationSize / numberOfMappers;

			// inject chromosome generator
			String cromosomeGeneratorClass = context.getConfiguration().get("ga.mapreduce.chromosome.generator");
			chromosomeGenerator = (ChromosomeGenerator) Util.initializeClass(context.getConfiguration(), cromosomeGeneratorClass);
		} else {
			reader = new LineRecordReader();
			reader.initialize(inputSplit, context);
		}
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if (generateInitialPopulation) {
			return counter < chromosomesPerNode;
		} else {
			return reader.nextKeyValue();
		}
	}

}

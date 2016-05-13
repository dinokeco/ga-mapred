package edu.ibu.ga.mapreduce;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Reducer;

import edu.ibu.ga.CrossoverOperator;
import edu.ibu.ga.MutationOpeator;
import edu.ibu.ga.SelectionOperator;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import edu.ibu.ga.mapreduce.domain.Population;
import edu.ibu.ga.util.Util;

public class GaReducer extends Reducer<NullWritable, Chromosome, NullWritable, Chromosome> {

	private CrossoverOperator crossoverOperator;
	private MutationOpeator mutationOpeator;
	private SelectionOperator selectionOperator;

	private float mutationProbability;
	private float crossoverProbability;
	private int windowSize;

	protected void setup(Context context) throws IOException, InterruptedException {
		crossoverOperator = (CrossoverOperator) Util.initializeClass(context.getConfiguration(), context.getConfiguration().get("ga.mapreduce.crossover.operator"));
		mutationOpeator = (MutationOpeator) Util.initializeClass(context.getConfiguration(), context.getConfiguration().get("ga.mapreduce.mutation.operator"));
		selectionOperator = (SelectionOperator) Util.initializeClass(context.getConfiguration(), context.getConfiguration().get("ga.mapreduce.selection.operator"));

		mutationProbability = context.getConfiguration().getFloat("ga.mapreduce.mutation.probability", 0);
		crossoverProbability = context.getConfiguration().getFloat("ga.mapreduce.crossover.probability", 0);
		windowSize = context.getConfiguration().getInt("ga.mapreduce.window.size", 0);
	}

	@Override
	protected void reduce(NullWritable key, Iterable<Chromosome> values, Context context) throws IOException, InterruptedException {
		Population subPopulation = new Population(windowSize);
		Population newGenerationSubPopulation = new Population(windowSize);

		while (values.iterator().hasNext()) {
			Chromosome c = new Chromosome(0);
			// deep copy because underlying iterator implementation
			c.fromString(values.iterator().next().toString());

			if (subPopulation.getChromosomes().size() < windowSize && values.iterator().hasNext()) {
				subPopulation.addChromosome(c);
			} else {
				if (!values.iterator().hasNext()) {
					subPopulation.addChromosome(c);
				}
				// generate new sub-population
				while (newGenerationSubPopulation.getChromosomes().size() < subPopulation.getChromosomes().size()) {
					List<Chromosome> parents = selectionOperator.selectForMating(subPopulation);
					List<Chromosome> childs = crossoverOperator.crossover(parents, crossoverProbability);
					// mutation
					for (Chromosome ch : childs) {
						mutationOpeator.mutate(ch, mutationProbability);
					}

					for (Chromosome ch : childs) {
						newGenerationSubPopulation.addChromosome(ch);
					}
				}

				System.out.println("emit " + newGenerationSubPopulation.getChromosomes().size() + " chromosomes");

				for (int i = 0; i < subPopulation.getChromosomes().size(); i++) {
					context.write(NullWritable.get(), newGenerationSubPopulation.getChromosomes().get(i));
				}
				newGenerationSubPopulation.getChromosomes().clear();
				subPopulation.getChromosomes().clear();
				subPopulation.getChromosomes().add(c);
				System.out.println(newGenerationSubPopulation.getChromosomes().size() + "/" + subPopulation.getChromosomes().size());
			}
		}
	}
}

package edu.ibu.ga.mapreduce;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;

import edu.ibu.ga.CrossoverOperator;
import edu.ibu.ga.FitnessFunction;
import edu.ibu.ga.MutationOpeator;
import edu.ibu.ga.SelectionOperator;
import edu.ibu.ga.StopCriteria;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import edu.ibu.ga.mapreduce.domain.Population;
import edu.ibu.ga.util.Util;

public class DgaMapper extends Mapper<NullWritable, Population, NullWritable, Chromosome> {

	private CrossoverOperator crossoverOperator;

	private MutationOpeator mutationOpeator;

	private SelectionOperator selectionOperator;

	private StopCriteria stopCriteria;

	private FitnessFunction fitnessFunction;

	private float crossoverProbability;

	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		crossoverOperator = (CrossoverOperator) Util.initializeClass(context.getConfiguration(), context.getConfiguration().get("ga.mapreduce.crossover.operator"));
		mutationOpeator = (MutationOpeator) Util.initializeClass(context.getConfiguration(), context.getConfiguration().get("ga.mapreduce.mutation.operator"));
		selectionOperator = (SelectionOperator) Util.initializeClass(context.getConfiguration(), context.getConfiguration().get("ga.mapreduce.selection.operator"));
		stopCriteria = (StopCriteria) Util.initializeClass(context.getConfiguration(), context.getConfiguration().get("ga.mapreduce.stop.criteria"));
		fitnessFunction = (FitnessFunction) Util.initializeClass(context.getConfiguration(), context.getConfiguration().get("ga.mapreduce.fitness.function"));

		crossoverProbability = context.getConfiguration().getFloat("ga.mapreduce.crossover.probability", 0);
	}
	
	@Override
	protected void map(NullWritable key, Population value, Context context) throws IOException, InterruptedException {
		int generation = 0;
		double maxFitness = 0;
		double avgFitness = 0;
		double prevAvgFitness = 0;
		double prevMaxFitness = 0;

		Population iteratorPopulation = value;
		Chromosome best = null;
		
		while (!stopCriteria.terminate(iteratorPopulation)) {
			prevMaxFitness = maxFitness;
			prevAvgFitness = avgFitness;
			avgFitness = 0;
			maxFitness = 0;

			// evaluate fitness function
			for (Chromosome c : iteratorPopulation.getChromosomes()) {
				fitnessFunction.evaluate(c);
				avgFitness += c.getFintess();
				if (maxFitness < c.getFintess()) {
					maxFitness = c.getFintess();
					if (best == null){
						best = c.deepCopy();
					}else if(best.getFintess() < c.getFintess()){
						best = c.deepCopy();
					}
				}
			}

			// keep track of max and average fitness variables
			avgFitness = avgFitness / iteratorPopulation.getChromosomes().size();
			iteratorPopulation.setMaxFitness(maxFitness);
			iteratorPopulation.setAverageFitness(avgFitness);
			iteratorPopulation.setAverageFitnessDelta(avgFitness - prevAvgFitness);
			iteratorPopulation.setMaxFitnessDelta(Math.abs(maxFitness - prevMaxFitness));
			iteratorPopulation.setGeneration(generation);
			
			//System.out.println("pop avg" + iteratorPopulation.getAverageFitness());
			System.out.println(generation+ ". gen \t"+ best.getFintess() + " F#"+ best.getBits().toString());
			
			//context.getCounter("current-best-fitness", generation+"-"+String.valueOf(best.getFintess())).increment(0);
			//context.getCounter("current-avg-fitness", generation+"-"+String.valueOf(iteratorPopulation.getAverageFitness())).increment(0);
			
			// creation of new population
			Population nextGeneration = new Population(iteratorPopulation.getChromosomes().size());

			// elitism
			nextGeneration.addChromosome(best);
			Chromosome b = best.deepCopy();
			
			while (nextGeneration.getChromosomes().size() < iteratorPopulation.getChromosomes().size()) {
				// selection of parents for mating
				List<Chromosome> parents = selectionOperator.selectForMating(iteratorPopulation);

				// crossover
				List<Chromosome> childs = crossoverOperator.crossover(parents, crossoverProbability);

				// mutation
				for (Chromosome c : childs) {
					mutationOpeator.mutate(c, iteratorPopulation);
				}

				for (Chromosome c : childs) {
					// adding offsprings one by one to keep populations same
					// accross generations
					if (nextGeneration.getChromosomes().size() < iteratorPopulation.getChromosomes().size()) {
						nextGeneration.addChromosome(c);
					}
				}
			}
			iteratorPopulation.setChromosomes(nextGeneration.getChromosomes());
			iteratorPopulation.addChromosome(b);
			// report progress to JT with current generation processed
			context.setStatus("gen " + generation);
			context.progress();
			generation++;
		}

		context.write(NullWritable.get(), best);
	}

	@Override
	protected void cleanup(Context context) throws IOException, InterruptedException {
		super.cleanup(context);
		System.out.println("cleanup done");
	}
}

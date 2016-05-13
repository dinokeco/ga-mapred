package edu.ibu.ga.mapreduce.logic;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;

import acm.util.RandomGenerator;

import edu.ibu.ga.SelectionOperator;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import edu.ibu.ga.mapreduce.domain.Population;

public class RouleteWheelSelection implements SelectionOperator {

	private RandomGenerator gen;

	@Override
	public void init(Configuration conf) {
		gen = new RandomGenerator();
		gen.setSeed(System.nanoTime());
	}

	@Override
	public List<Chromosome> selectForMating(Population population) {
		double sumFitness = 0;
		for (int i = 0; i < population.getChromosomes().size(); i++) {
			sumFitness += population.getChromosomes().get(i).getFintess();
		}

		double randomFitnessFirstParent = gen.nextDouble(0, sumFitness);
		double randomFitnessSecondParent = gen.nextDouble(0, sumFitness);

		List<Chromosome> parents = new ArrayList<Chromosome>();
		System.out.println(population.getChromosomes().size());
		System.out.println("#1" + randomFitnessFirstParent + " #2" + randomFitnessSecondParent + " ##" + sumFitness);
		double fitnessSumIterator = 0;
		for (int i = 0; i < population.getChromosomes().size(); i++) {
			if (parents.size() == 2) {
				// parents have been selected
				break;
			}

			if (randomFitnessFirstParent >= fitnessSumIterator && randomFitnessFirstParent < fitnessSumIterator + population.getChromosomes().get(i).getFintess()) {
				parents.add(population.getChromosomes().get(i));
			}

			if (randomFitnessSecondParent >= fitnessSumIterator && randomFitnessSecondParent < fitnessSumIterator + population.getChromosomes().get(i).getFintess()) {
				parents.add(population.getChromosomes().get(i));
			}
			fitnessSumIterator += population.getChromosomes().get(i).getFintess();
		}

		return parents;
	}
}

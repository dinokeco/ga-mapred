package edu.ibu.ga.mapreduce.logic;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;

import acm.util.RandomGenerator;
import edu.ibu.ga.CrossoverOperator;
import edu.ibu.ga.mapreduce.domain.Chromosome;

public class SinglePointCrossover implements CrossoverOperator {

	private RandomGenerator gen;

	@Override
	public void init(Configuration conf) {
		gen = new RandomGenerator();
		gen.setSeed(System.nanoTime());
	}

	@Override
	public List<Chromosome> crossover(List<Chromosome> parents, float crossoverProbability) {
		if (gen.nextDouble() < crossoverProbability) {
			Chromosome parent1 = parents.get(0);
			Chromosome parent2 = parents.get(1);

			int length = parent1.getLength();

			int crossoverIndex = gen.nextInt(length);

			Chromosome child1 = new Chromosome(length);
			Chromosome child2 = new Chromosome(length);

			for (int i = 0; i < length; i++) {
				if (i < crossoverIndex) {
					child1.getBits().set(i, parent1.getBits().get(i));
					child2.getBits().set(i, parent2.getBits().get(i));
				} else {
					child1.getBits().set(i, parent2.getBits().get(i));
					child2.getBits().set(i, parent1.getBits().get(i));
				}
			}
			List<Chromosome> childs = new ArrayList<Chromosome>();
			childs.add(child1);
			childs.add(child2);

			return childs;
		} else {
			return parents;
		}
	}

}

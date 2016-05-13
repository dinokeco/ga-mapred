package edu.ibu.ga;

import java.util.List;

import edu.ibu.ga.mapreduce.domain.Chromosome;

public interface CrossoverOperator extends Initializable {

	public List<Chromosome> crossover(List<Chromosome> parents, float crossoverProbability);
}

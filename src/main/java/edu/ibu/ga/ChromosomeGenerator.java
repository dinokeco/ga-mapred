package edu.ibu.ga;

import edu.ibu.ga.mapreduce.domain.Chromosome;

public interface ChromosomeGenerator extends Initializable{

	public Chromosome generateNextChromosome();
}

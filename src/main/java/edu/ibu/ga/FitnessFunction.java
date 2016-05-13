package edu.ibu.ga;

import edu.ibu.ga.mapreduce.domain.Chromosome;

public interface FitnessFunction extends Initializable{

	public void evaluate(Chromosome c);
	
}

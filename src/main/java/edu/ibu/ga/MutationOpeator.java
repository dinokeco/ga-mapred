package edu.ibu.ga;

import edu.ibu.ga.mapreduce.domain.Chromosome;

public interface MutationOpeator extends Initializable{

	public void mutate(Chromosome c, float mutationProbability);
}

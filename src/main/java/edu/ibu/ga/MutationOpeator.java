package edu.ibu.ga;

import edu.ibu.ga.mapreduce.domain.Chromosome;
import edu.ibu.ga.mapreduce.domain.Population;

public interface MutationOpeator extends Initializable{

	@Deprecated
	public void mutate(Chromosome c, float mutationProbability);
	
	public void mutate(Chromosome c, Population population);
}

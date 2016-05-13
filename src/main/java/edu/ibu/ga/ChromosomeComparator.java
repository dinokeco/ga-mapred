package edu.ibu.ga;

import edu.ibu.ga.mapreduce.domain.Chromosome;

public interface ChromosomeComparator extends Initializable {

	public boolean isBetter(Chromosome current, Chromosome new_);
}

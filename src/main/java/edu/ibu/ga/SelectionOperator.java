package edu.ibu.ga;

import java.util.List;

import edu.ibu.ga.mapreduce.domain.Chromosome;
import edu.ibu.ga.mapreduce.domain.Population;

public interface SelectionOperator extends Initializable {

	public List<Chromosome> selectForMating(Population population);
	
}

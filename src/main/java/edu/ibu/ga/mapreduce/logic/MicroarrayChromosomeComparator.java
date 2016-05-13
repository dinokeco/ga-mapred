package edu.ibu.ga.mapreduce.logic;

import org.apache.hadoop.conf.Configuration;

import edu.ibu.ga.ChromosomeComparator;
import edu.ibu.ga.mapreduce.domain.Chromosome;

public class MicroarrayChromosomeComparator implements ChromosomeComparator {

	@Override
	public boolean isBetter(Chromosome current, Chromosome new_) {
		return current == null || current.getFintess() < new_.getFintess();
	}

	@Override
	public void init(Configuration conf) {
	}

}

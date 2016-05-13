package edu.ibu.ga.mapreduce.logic;

import org.apache.hadoop.conf.Configuration;

import edu.ibu.ga.ChromosomeComparator;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import edu.ibu.ga.util.Util;

public class DefaultChromosomeComparator implements ChromosomeComparator {

	@Override
	public boolean isBetter(Chromosome current, Chromosome new_) {
		if (current == null || current.getFintess() < new_.getFintess()) {
			return true;
		} else if (current.getFintess() == new_.getFintess()) {
			return Util.countOnes(new_) < Util.countOnes(current);
		}
		return false;
	}

	@Override
	public void init(Configuration conf) {
	}

}

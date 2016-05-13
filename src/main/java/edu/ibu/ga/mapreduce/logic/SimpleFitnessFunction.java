package edu.ibu.ga.mapreduce.logic;

import org.apache.hadoop.conf.Configuration;

import edu.ibu.ga.FitnessFunction;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import edu.ibu.ga.util.Util;

/**
 * Fitness function for One Max problem
 * 
 * @author dino
 *
 */
public class SimpleFitnessFunction implements FitnessFunction{

	
	@Override
	public void init(Configuration conf) {
		// nothing to init
	}

	@Override
	public void evaluate(Chromosome c) {
		c.setFintess(Util.convert(c.getBits()));
	}

}

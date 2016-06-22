package edu.ibu.ga.mapreduce.logic;

import org.apache.hadoop.conf.Configuration;
import org.junit.Before;
import org.junit.Test;

import edu.ibu.ga.FitnessFunction;
import edu.ibu.ga.mapreduce.domain.Chromosome;

public class MicroarrayFeatureSelectionSVMCSVFitnessFunctionTest {

	private FitnessFunction ff = new MicroarrayFeatureSelectionSVMCSVFitnessFunction();
	
	@Before
	public void setup(){
		Configuration conf = new Configuration();
		conf.set("ga.mapreduce.microarray.train.dataset", "https://s3-us-west-1.amazonaws.com/dino.ga/datasets/leukemia2_train.csv");
		conf.set("ga.mapreduce.microarray.test.dataset", "https://s3-us-west-1.amazonaws.com/dino.ga/datasets/leukemia2_test.csv");
	
		ff.init(conf);
	}
	@Test
	public void checkSVM(){
		Chromosome c = new Chromosome(11224);
		c.getBits().set(0);
		c.getBits().set(20);
		c.getBits().set(200);
		c.getBits().set(199);
		c.getBits().set(11223);
		
		ff.evaluate(c);
		
		Chromosome c1 = new Chromosome(11224);
		
		c1.getBits().set(1);
		c1.getBits().set(2);
		c1.getBits().set(3);
		c1.getBits().set(4);
		c1.getBits().set(5);
		ff.evaluate(c1);
		
		Chromosome c2 = new Chromosome(11224);
		c2.getBits().set(7);
		c2.getBits().set(11);
		
		ff.evaluate(c2);
	}
}

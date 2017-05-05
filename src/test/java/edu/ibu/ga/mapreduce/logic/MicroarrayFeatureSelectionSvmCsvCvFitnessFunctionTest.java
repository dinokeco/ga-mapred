package edu.ibu.ga.mapreduce.logic;

import org.apache.hadoop.conf.Configuration;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.ibu.ga.FitnessFunction;
import edu.ibu.ga.mapreduce.domain.Chromosome;

// 12600
public class MicroarrayFeatureSelectionSvmCsvCvFitnessFunctionTest {

	private FitnessFunction ff = new MicroarrayFeatureSelectionSvmCsvCvFitnessFunction();
	
	@Before
	public void setup(){
		Configuration conf = new Configuration();
		
		conf.set("ga.mapreduce.microarray.fold.size", "3");
		conf.set("ga.mapreduce.microarray.dataset","file:///home/dino/workspace-jee/ga-mapred/data/cv_3_fold/");
		conf.set("ga.mapreduce.label","Lung_Cancer");
	
		ff.init(conf);
	}
	@Test
	public void checkSVM(){
		Chromosome c = new Chromosome(12600);
		c.getBits().set(0);
		c.getBits().set(20);
		c.getBits().set(200);
		c.getBits().set(199);
		c.getBits().set(11223);
		
		for(int i = 0 ; i<5; i++)
		ff.evaluate(c);
		/*
		Chromosome c1 = new Chromosome(12600);
		
		c1.getBits().set(1);
		c1.getBits().set(2);
		c1.getBits().set(3);
		c1.getBits().set(4);
		c1.getBits().set(5);
		ff.evaluate(c1);
		
		Chromosome c2 = new Chromosome(12600);
		c2.getBits().set(7);
		c2.getBits().set(11);
		
		ff.evaluate(c2); */
	}
}

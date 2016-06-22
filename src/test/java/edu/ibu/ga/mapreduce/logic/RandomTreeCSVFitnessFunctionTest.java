package edu.ibu.ga.mapreduce.logic;

import org.apache.hadoop.conf.Configuration;
import org.junit.Before;
import org.junit.Test;

import edu.ibu.ga.FitnessFunction;
import edu.ibu.ga.mapreduce.domain.Chromosome;

public class RandomTreeCSVFitnessFunctionTest {

	private FitnessFunction ff = new RandomTreeCSVFitnessFunction();
	
	@Before
	public void setup(){
		Configuration conf = new Configuration();
		conf.set("ga.mapreduce.microarray.train.dataset", "file:///home/dino/Dropbox/phd/workspace/ga-mapred/data/std-filtered-kdd-train.csv");
		conf.set("ga.mapreduce.microarray.test.dataset", "file:///home/dino/Dropbox/phd/workspace/ga-mapred/data/std-filtered-kdd-test.csv");
	
		ff.init(conf);
	}
	@Test
	public void check(){
		Chromosome best = new Chromosome(41);
		best.getBits().set(2);
		best.getBits().set(4);
		best.getBits().set(7);
		best.getBits().set(11);
		best.getBits().set(13);
		best.getBits().set(18);
		best.getBits().set(19);
		
		best.getBits().set(23);
		best.getBits().set(27);
		best.getBits().set(35);
		//0.770464135021097 F#{0, 5, 6, 8, 9, 17, 21}
		//0.8074261603375528 F#{2, 4, 7, 11, 13, 18, 19, 23, 27, 35}
		//0.8245569620253165 f#{2, 4, 9, 11, 13, 18, 19, 23, 31, 40}
		ff.evaluate(best);
		/*Random r = new Random();
		for (int j = 0 ; j < 2000; j++){
			Chromosome c = new Chromosome(41);
			
			for (int i = 0; i<41; i++){
				if (r.nextBoolean()){
					c.getBits().set(i);
				}
			}

			ff.evaluate(c);
			
			if (c.getFintess() > best.getFintess()){
				best.setFintess(c.getFintess());
				best.setBits(c.getBits());
			}
		}*/
		
		System.out.println("BEST: "+ best.getBits().toString());
		System.out.println("BEST: "+ best.getFintess());
	}
}

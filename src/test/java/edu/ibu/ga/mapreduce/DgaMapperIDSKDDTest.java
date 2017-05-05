package edu.ibu.ga.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import edu.ibu.ga.ChromosomeGenerator;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import edu.ibu.ga.mapreduce.domain.Population;
import edu.ibu.ga.util.Util;


@SuppressWarnings({"rawtypes", "unchecked"})
public class DgaMapperIDSKDDTest {

	private DgaMapper mapper;

	private Context context;
	
	private Population population;
	
	@Before
	public void setup() throws IOException, InterruptedException{
					
		Configuration conf = new Configuration();
		conf.set("ga.mapreduce.population.size.per.node", "50");
		conf.set("ga.mapreduce.chromosome.length", "41");
		conf.set("ga.mapreduce.chromosome.generator", "edu.ibu.ga.mapreduce.logic.LimitedChromosomeGenerator");
		conf.set("ga.mapreduce.chromosome.comparator", "edu.ibu.ga.mapreduce.logic.DefaultChromosomeComparator");
		conf.set("ga.mapreduce.crossover.operator", "edu.ibu.ga.mapreduce.logic.SinglePointCrossover");
		conf.set("ga.mapreduce.mutation.operator", "edu.ibu.ga.mapreduce.logic.GenerationsMutationOperator");
		conf.set("ga.mapreduce.selection.operator", "edu.ibu.ga.mapreduce.logic.RouleteWheelSelection");
		conf.set("ga.mapreduce.fitness.function", "edu.ibu.ga.mapreduce.logic.RandomTreeCSVFitnessFunction");
		conf.set("ga.mapreduce.stop.criteria", "edu.ibu.ga.mapreduce.logic.GenerationStopCriteria");
		conf.set("ga.mapreduce.generations.window", "50");
		conf.set("ga.mapreduce.number.of.generations", "1000");
		conf.set("ga.mapreduce.mutation.probability", "0.3");
		conf.set("ga.mapreduce.crossover.probability", "0.95");
		
		conf.set("ga.mapreduce.microarray.train.dataset", "file:///home/dino/Dropbox/phd/nsl-kdd-dataset/KDDTrain+_20Percent.csv");
		//conf.set("ga.mapreduce.microarray.test.dataset", "file:///home/dino/Dropbox/phd/nsl-kdd-dataset/KDDTest+.csv");
		conf.set("ga.mapreduce.microarray.test.dataset", "file:///home/dino/Dropbox/phd/nsl-kdd-dataset/KDDTest+.csv");
		
		context = Mockito.mock(Context.class);
		Mockito.when(context.getConfiguration()).thenReturn(conf);
		Mockito.doNothing().when(context).progress();
		Mockito.doNothing().when(context).setStatus(Mockito.anyString());
		Mockito.doNothing().when(context).write(Mockito.anyObject(), Mockito.anyObject());
		
		mapper = new DgaMapper();
		mapper.setup(context);
		
		ChromosomeGenerator chromosomeGenerator = (ChromosomeGenerator) Util.initializeClass(conf, conf.get("ga.mapreduce.chromosome.generator"));
		
		int nodePopulationSize = conf.getInt("ga.mapreduce.population.size.per.node", 0);
		population = new Population(nodePopulationSize);
	
		
		//population.addChromosome(new Chromosome(41, "0, 1, 5, 8, 9, 10, 13, 14, 15, 18, 20, 21"));
		population.addChromosome(new Chromosome(41, "0, 1, 5, 8, 10, 12, 14, 15, 18, 19, 20, 21"));
		//0.8827182398864443 F#{0, 5, 8, 10, 16, 17, 18, 25, 30, 31, 39}
		//0.8827182398864443 F#{0, 5, 8, 10, 16, 17, 18, 25, 30, 31, 39}
		//0.8913679914833215 F#{0, 5, 10, 13, 17, 18, 21, 24}
		//0.8755766501064585 F#{0, 5, 7, 10, 11, 17, 21}
		//0.8710965223562811 F#{0, 5, 10, 11, 18, 26, 33, 37}
		//0.8645315826827538 F#{0, 5, 6, 7, 10, 12, 13, 14, 15, 17, 22, 24, 28, 39}
		//0.8602288857345636 F#{4, 5, 10, 11, 13, 17, 20, 26}
		//0.8543736692689851 F#{0, 5, 6, 7, 15, 20, 21, 24, 26}
		//0.8629790631653655 F#{0, 5, 7, 10, 19, 23}
		//0.8485628105039035 F#{0, 5, 9, 10, 13, 19, 23}

		// random tree - Train/Test
		//1000. gen 	0.8986870120652946 F#{0, 1, 5, 8, 9, 10, 13, 14, 15, 18, 20, 21}
		//106. gen 	0.9044978708303761 F#{0, 5, 8, 10, 13, 14, 15, 18, 20, 21}
		//352. gen 	0.9044978708303761 F#{0, 5, 8, 10, 13, 14, 15, 18, 20, 21}

		
	    // random tree - Train/Test21
		// 151. gen 	0.8661603375527426 F#{0, 1, 5, 8, 10, 12, 14, 15, 18, 19, 20, 21}
		// 209. gen 	0.8661603375527426 F#{0, 1, 5, 8, 10, 12, 14, 15, 18, 19, 20, 21}

		// random tree - Train 20% / Test 21
		// 746. gen 	0.8583122362869199 F#{0, 3, 5, 6, 9, 10, 11, 14, 15, 17, 19, 20, 21, 24, 26, 38}

		// random tree - Train 20% / Test 
		// 44. gen 	0.903388928317956 F#{0, 5, 8, 10, 14, 19, 20, 21, 26, 38}
		// 175. gen 0.9058729595457772 F#{0, 5, 7, 10, 11, 12, 17, 19, 20, 21, 24, 26, 27}
		// 261. gen 	0.9061834634492548 F#{0, 5, 6, 10, 13, 14, 19, 20, 21, 24, 26, 27, 38}

		for (int i=population.getChromosomes().size(); i<nodePopulationSize; i++){
			population.addChromosome(chromosomeGenerator.generateNextChromosome());
		}
		
	}
	@Test
	public void test() throws IOException, InterruptedException{
		mapper.map(NullWritable.get(), population, context);
		System.out.println("Dino");
	}
}

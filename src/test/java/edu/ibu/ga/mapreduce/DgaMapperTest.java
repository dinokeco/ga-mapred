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
public class DgaMapperTest {

	private DgaMapper mapper;

	private Context context;
	
	private Population population;
	
	@Before
	public void setup() throws IOException, InterruptedException{
					
		Configuration conf = new Configuration();
		conf.set("ga.mapreduce.population.size.per.node", "2");
		conf.set("ga.mapreduce.chromosome.length", "41");
		conf.set("ga.mapreduce.chromosome.generator", "edu.ibu.ga.mapreduce.logic.LimitedChromosomeGenerator");
		conf.set("ga.mapreduce.chromosome.comparator", "edu.ibu.ga.mapreduce.logic.DefaultChromosomeComparator");
		conf.set("ga.mapreduce.crossover.operator", "edu.ibu.ga.mapreduce.logic.SinglePointCrossover");
		conf.set("ga.mapreduce.mutation.operator", "edu.ibu.ga.mapreduce.logic.GenerationsMutationOperator");
		conf.set("ga.mapreduce.selection.operator", "edu.ibu.ga.mapreduce.logic.RouleteWheelSelection");
		conf.set("ga.mapreduce.fitness.function", "edu.ibu.ga.mapreduce.logic.RandomTreeCSVFitnessFunction");
		conf.set("ga.mapreduce.stop.criteria", "edu.ibu.ga.mapreduce.logic.DeltaFitnesStopCriteria");
		conf.set("ga.mapreduce.generations.window", "4");
		conf.set("ga.mapreduce.number.of.generations", "50");
		conf.set("ga.mapreduce.mutation.probability", "0.2");
		conf.set("ga.mapreduce.crossover.probability", "0.7");
		
		//conf.set("ga.mapreduce.microarray.train.dataset", "https://s3-us-west-1.amazonaws.com/dino.ga/datasets/leukemia2_train.csv");
		//conf.set("ga.mapreduce.microarray.test.dataset", "https://s3-us-west-1.amazonaws.com/dino.ga/datasets/leukemia2_test.csv");
		
		conf.set("ga.mapreduce.microarray.train.dataset", "file:///home/dino/Dropbox/phd/IDS dataset/full-kdd-train.csv");
		conf.set("ga.mapreduce.microarray.test.dataset", "file:///home/dino/Dropbox/phd/IDS dataset/full-kdd-test.csv");
		
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
	/*	
		Chromosome c1  = new Chromosome(41);
		c1.getBits().set(7);
		c1.getBits().set(17);
		c1.getBits().set(21);
		
		
		Chromosome c2  = new Chromosome(41);
		c2.getBits().set(6);
		c2.getBits().set(7);
		c2.getBits().set(17);
		
		population.addChromosome(c1);
		population.addChromosome(c2);
		*/
		
//		0.8827182398864443 F#{0, 5, 8, 10, 16, 17, 18, 25, 30, 31, 39}
//0.8827182398864443 F#{0, 5, 8, 10, 16, 17, 18, 25, 30, 31, 39}

		for (int i=0; i<nodePopulationSize; i++){
			population.addChromosome(chromosomeGenerator.generateNextChromosome());
		}
		
	}
	@Test
	public void test() throws IOException, InterruptedException{
		mapper.map(NullWritable.get(), population, context);
		System.out.println("Dino");
	}
}

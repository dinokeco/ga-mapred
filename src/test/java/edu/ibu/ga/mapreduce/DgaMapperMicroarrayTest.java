package edu.ibu.ga.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import edu.ibu.ga.ChromosomeGenerator;
import edu.ibu.ga.mapreduce.domain.Chromosome;
import edu.ibu.ga.mapreduce.domain.Population;
import edu.ibu.ga.util.Util;


// 9 tumors 0.9545454545454546 F#{0, 51, 99, 158, 192, 268, 285, 347, 507, 553, 579, 738, 774, 1270, 1286, 1364, 1725, 1951, 1984, 2162, 2182, 2200, 2459, 2647, 2773, 2799, 2950, 3068, 3116, 3181, 3450, 3466, 3504, 3560, 3730, 3846, 4135, 4140, 4321, 4750, 4793, 4872, 5182, 5359}
// 11 tumors 0.9180327868852459 F#{33, 246, 550, 731, 827, 967, 983, 1072, 1122, 1401, 1474, 1492, 1669, 2282, 2362, 2503, 2504, 3135, 3701, 3741, 3760, 4411, 4448, 4450, 4973, 5481, 5650, 5785, 5829, 5897, 6215, 6344, 6692, 6767, 6862, 6975, 6989, 7121, 7166, 7571, 7944, 8031, 8571, 8888, 9420, 9744, 10749, 10768, 11065, 11171, 11257, 11642, 11753, 11963, 12109, 12194, 12207}
// 14 tumors 0.45045045045045046 F#{218, 265, 400, 786, 867, 986, 1442, 1457, 2461, 2826, 4014, 4753, 5225, 5511, 6177, 6240, 6637, 7868, 8932, 9067, 9093, 9373, 10484, 10569, 12023, 12122, 12559, 13390, 13449, 13666, 14066, 14890}

@SuppressWarnings({"rawtypes", "unchecked"})
public class DgaMapperMicroarrayTest {

	private DgaMapper mapper;

	private Context context;
	
	private Population population;
	
	private String bestSoFar = "218, 265, 400, 786, 867, 986, 1442, 1457, 2461, 2826, 4014, 4753, 5225, 5511, 6177, 6240, 6637, 7868, 8932, 9067, 9093, 9373, 10484, 10569, 12023, 12122, 12559, 13390, 13449, 13666, 14066, 14890";
	
	@Before
	public void setup() throws IOException, InterruptedException{
					
		Configuration conf = new Configuration();
		conf.set("ga.mapreduce.population.size.per.node", "100");
		//conf.set("ga.mapreduce.chromosome.length", "5726"); // 9 tumors
		//conf.set("ga.mapreduce.chromosome.length", "12533"); // 11 tumors
		conf.set("ga.mapreduce.chromosome.length", "15009"); // 14 tumors
		
		conf.set("ga.mapreduce.chromosome.generator", "edu.ibu.ga.mapreduce.logic.LimitedChromosomeGenerator");
		conf.set("ga.mapreduce.chromosome.limit", "5");
		
		conf.set("ga.mapreduce.chromosome.comparator", "edu.ibu.ga.mapreduce.logic.DefaultChromosomeComparator");
		
		conf.set("ga.mapreduce.crossover.operator", "edu.ibu.ga.mapreduce.logic.SinglePointCrossover");
		conf.set("ga.mapreduce.crossover.probability", "0.7");
		
		conf.set("ga.mapreduce.mutation.operator", "edu.ibu.ga.mapreduce.logic.StandardMutationOperator");
		conf.set("ga.mapreduce.mutation.probability", "0.05");
		
		conf.set("ga.mapreduce.selection.operator", "edu.ibu.ga.mapreduce.logic.RouleteWheelSelection");
		
		conf.set("ga.mapreduce.fitness.function", "edu.ibu.ga.mapreduce.logic.MicroarrayFeatureSelectionSVMCSVFitnessFunction");
		
		conf.set("ga.mapreduce.stop.criteria", "edu.ibu.ga.mapreduce.logic.GenerationStopCriteria");
		conf.set("ga.mapreduce.number.of.generations", "1000");
		
		
		//conf.set("ga.mapreduce.microarray.train.dataset", "https://s3-us-west-1.amazonaws.com/dino.ga/datasets/leukemia2_train.csv");
		//conf.set("ga.mapreduce.microarray.test.dataset", "https://s3-us-west-1.amazonaws.com/dino.ga/datasets/leukemia2_test.csv");
		
		conf.set("ga.mapreduce.microarray.train.dataset", "file:///home/dino/workspace-jee/ga-mapred/data/14-tumors_train.csv");
		conf.set("ga.mapreduce.microarray.test.dataset", "file:///home/dino/workspace-jee/ga-mapred/data/14-tumors_test.csv");
		
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
		
		//population.addChromosome(new Chromosome(conf.getInt("ga.mapreduce.chromosome.length", 0), bestSoFar));
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

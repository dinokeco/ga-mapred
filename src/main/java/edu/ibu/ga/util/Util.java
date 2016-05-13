package edu.ibu.ga.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.BitSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import edu.ibu.ga.mapreduce.domain.Chromosome;

public class Util {

	public static int countOnes(Chromosome c) {
		int counter = 0;
		for (int i = 0; i < c.getLength(); i++) {
			if (c.getBits().get(i)) {
				counter++;
			}
		}
		return counter;
	}
	
	public static String toString(double[][] matrix) {
		StringBuilder builder = new StringBuilder();
		for (double[] row : matrix) {
			builder.append(Arrays.toString(row)).append("\n");
		}
		return builder.toString();
	}

	public static long convert(BitSet bits) {
		long value = 0L;
		for (int i = 0; i < bits.size(); ++i) {
			value += bits.get(i) ? (1L << i) : 0L;
		}
		return value;
	}

	public static Object initializeClass(Configuration conf, String className) {
		try {
			Class<?> c = Class.forName(className);
			Method initMethod = c.getMethod("init", Configuration.class);
			Object instance = c.newInstance();
			initMethod.invoke(instance, conf);
			return instance;
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize class " + className, e);
		}
	}

	public static Chromosome readBestChromosome(String location, Configuration conf) throws IOException {
		FileSystem fs = FileSystem.get(conf);
		Path p = new Path(location);
		
		if (!fs.exists(p)){
			fs.mkdirs(p);
		}
		
		FileStatus[] files = fs.listStatus(p);
		Chromosome best = null;
		if (files != null) {
			for (FileStatus f : files) {
				if (!f.isDirectory()) {
					FSDataInputStream in = fs.open(f.getPath());
					Chromosome c = new Chromosome(0);
					c.fromString(in.readUTF());
					if (best == null || c.getFintess() > best.getFintess()) {
						best = c;
					}
					in.close();
				}
			}
		}
		return best;
	}
}

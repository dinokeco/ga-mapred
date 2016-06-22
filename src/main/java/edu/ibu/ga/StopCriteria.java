package edu.ibu.ga;

import edu.ibu.ga.mapreduce.domain.Population;

public interface StopCriteria extends Initializable{

	public boolean terminate(int generation, double maxFitnes, double averageFitnes, double maxFitnesDelta, double averageFitnesDelta);
	
	public boolean terminate(Population population);
}

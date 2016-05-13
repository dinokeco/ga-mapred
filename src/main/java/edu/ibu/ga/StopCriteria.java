package edu.ibu.ga;

public interface StopCriteria extends Initializable{

	public boolean terminate(int generation, double maxFitnes, double averageFitnes, double maxFitnesDelta, double averageFitnesDelta);
}

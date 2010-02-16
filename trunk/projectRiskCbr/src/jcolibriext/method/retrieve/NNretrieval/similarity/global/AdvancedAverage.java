package jcolibriext.method.retrieve.NNretrieval.similarity.global;

import jcolibriext.method.retrieve.NNretrieval.similarity.AdvancedGlobalSimilarityFunction;


/**
 * This function computes the average of the similarites of its subattributes.
 * @author Alessio Rocchi
 * @version 1.0
 */
public class AdvancedAverage extends AdvancedGlobalSimilarityFunction {


	public double computeSimilarity(double[] values, double[] weigths, int ivalue)
	{
		double acum = 0;
		double weigthsAcum = 0;
		for(int i=0; i<ivalue; i++)
		{
			acum += values[i] * weigths[i];
			weigthsAcum += weigths[i];
		}
		if(weigthsAcum == 0)
			return 0;
		return acum/weigthsAcum;
	}


}

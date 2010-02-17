package jcolibriext.method.retrieve.NNretrieval.similarity;

import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.cbrcore.CaseComponent;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.similarity.GlobalSimilarityFunction;
import jcolibri.method.retrieve.NNretrieval.similarity.InContextLocalSimilarityFunction;
import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

/**
 * Utility class to compute global similarities, based on StandardGlobalSimilarity. 
 * The implemented compute(...) method computes the similarity of the sub-attributes 
 * of this compound attribute and then calls the computeSimilarity() abstract method to
 * obtain the similarity value.<br>
 * That way, the computeSimilarity() method is a hook to easly compute global similarities.
 * @author Alessio Rocchi
 * @version 1.0
 *
 */
public abstract class AdvancedGlobalSimilarityFunction implements GlobalSimilarityFunction {

	public NNConfig localSimConfig = new NNConfig();
    /**
     * Computes the similarities of the sub-attributes of this CaseComponent and calls the computeSimilarity() method with those values.
     * @param componentOfCase compound attribute of the case
     * @param componentOfQuery compound attribute of the query
     * @param _case case being compared
     * @param _query query being compared
     * @param numSimConfig Similarity functions configuration
     * @return a value between [0..1]
     * @see jcolibri.method.retrieve.NNretrieval.similarity.GlobalSimilarityFunction#compute(jcolibri.cbrcore.CaseComponent, jcolibri.cbrcore.CaseComponent, jcolibri.cbrcore.CBRCase, jcolibri.cbrcore.CBRQuery, jcolibri.method.retrieve.NNretrieval.NNConfig)
     */
    public double compute(CaseComponent componentOfCase, CaseComponent componentOfQuery, CBRCase _case,
	    CBRQuery _query, NNConfig numSimConfig)
    {
	GlobalSimilarityFunction gsf = null;
	LocalSimilarityFunction lsf = null;
	
	
	Attribute[] attributes = jcolibri.util.AttributeUtils.getAttributes(componentOfCase.getClass());
	
	double[] values = new double[attributes.length];
	double[] weights = new double[attributes.length];
	
	int ivalue = 0;

	for(int i=0; i<attributes.length; i++)
	{
		Attribute at1 = attributes[i];
		Attribute at2 = new Attribute(at1.getName(), componentOfQuery.getClass());
		
		try{
//			java.beans.PropertyDescriptor pd = new java.beans.PropertyDescriptor(at1.getName(),at1.getDeclaringClass());
//			pd.getReadMethod().invoke(componentOfCase, (Object[])null);
			
			if(	(	((gsf = numSimConfig.getGlobalSimilFunction(at1)) != null) || 
					((lsf = numSimConfig.getLocalSimilFunction(at1))  != null)) &&
				(	at1.getValue(componentOfCase) == null ||
					at2.getValue(componentOfQuery) == null)) 
			{
				values[ivalue] = 0;
				weights[ivalue++] = 0;
			} 
			else if(gsf != null)
			{
				values[ivalue] = gsf.compute((CaseComponent)at1.getValue(componentOfCase), (CaseComponent)at2.getValue(componentOfQuery), _case, _query, numSimConfig);
				weights[ivalue++] = numSimConfig.getWeight(at1) * localSimConfig.getWeight(at1);
			}
			else if(lsf != null)
			{
	       		    	if(lsf instanceof InContextLocalSimilarityFunction)
	       		    	{
	       		    	    InContextLocalSimilarityFunction iclsf = (InContextLocalSimilarityFunction)lsf;
	       		    	    iclsf.setContext(componentOfCase, componentOfQuery, _case, _query, at1.getName());
	       		    	}
				values[ivalue] = lsf.compute(at1.getValue(componentOfCase), at2.getValue(componentOfQuery));
				weights[ivalue++] = numSimConfig.getWeight(at1) * localSimConfig.getWeight(at1);
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	return computeSimilarity(values, weights, ivalue);

    }
    
    /**
     * Hook method that must be implemented by subclasses returned the global similarity value.
     * @param values of the similarity of the sub-attributes
     * @param weigths of the sub-attributes
     * @param numberOfvalues (or sub-attributes) that were obtained (some subattributes may not compute for the similarity).
     * @return a value between [0..1]
     */
    public abstract double computeSimilarity(double[] values, double[] weigths, int numberOfvalues);

}

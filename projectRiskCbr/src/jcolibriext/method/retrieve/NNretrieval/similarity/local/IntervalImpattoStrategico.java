package jcolibriext.method.retrieve.NNretrieval.similarity.local;

import persistentclasses.attributes.ImpattoStrategico;
import jcolibri.exception.NoApplicableSimilarityFunctionException;
import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

public class IntervalImpattoStrategico implements LocalSimilarityFunction {
	/** Interval */
	double _interval;

	/**
	 * Constructor.
	 */
	public IntervalImpattoStrategico(double interval) {
		_interval = interval;
	}

	/**
	 * Applies the similarity function.
	 * 
	 * @param o1
	 *            Number
	 * @param o2
	 *            Number
	 * @return result of apply the similarity function.
	 */
	public double compute(Object o1, Object o2) throws NoApplicableSimilarityFunctionException{
		if ((o1 == null) || (o2 == null))
			return 0;
		if (!(o1 instanceof ImpattoStrategico))
			throw new NoApplicableSimilarityFunctionException(this.getClass(), o1.getClass());
		if (!(o2 instanceof ImpattoStrategico))
			throw new NoApplicableSimilarityFunctionException(this.getClass(), o2.getClass());


		Number i1 = (((ImpattoStrategico) o1).getValue());
		Number i2 = (((ImpattoStrategico) o2).getValue());
		
		double v1 = i1.doubleValue();
		double v2 = i2.doubleValue();
		return 1 - ((double) Math.abs(v1 - v2) / _interval);
	}
	
	/** Applicable to ImpattoStrategico */
	public boolean isApplicable(Object o1, Object o2)
	{
		if((o1==null)&&(o2==null))
			return true;
		else if(o1==null)
			return o2 instanceof ImpattoStrategico;
		else if(o2==null)
			return o1 instanceof ImpattoStrategico;
		else
			return (o1 instanceof ImpattoStrategico)&&(o2 instanceof ImpattoStrategico);
	}

}


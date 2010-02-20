package projectriskcbr.test.projectsriskcbr.config;

import jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import jcolibri.method.retrieve.NNretrieval.similarity.local.Interval;
import jcolibriext.method.retrieve.NNretrieval.similarity.global.AdvancedAverage;
import projectriskcbr.config.SelfNNConfigurator;

public class TravelDescriptionExternalConfigurator implements
		SelfNNConfigurator {

	@Override
	public Object getSimilarityFunction(String attribute) {
		if(attribute.equals("Duration")) {
			return new Interval(31);
		} else if(attribute.equals("Region")) {
			return new AdvancedAverage();
		} else {
			return new Equal();
		}
	}

}

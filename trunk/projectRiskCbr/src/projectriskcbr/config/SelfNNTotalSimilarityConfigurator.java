package projectriskcbr.config;

import jcolibri.method.retrieve.NNretrieval.NNConfig;

/**
 * Classes implementing this interface are able to generate a NNSimConfig representing a configuration
 * for the entirety of its attributes
 * @author Alessio Rocchi
 */
public interface SelfNNTotalSimilarityConfigurator {
	NNConfig getTotalSimilarityConfig(NNConfig simConfig);
}

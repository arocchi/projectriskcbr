package projectriskcbr.config;

/**
 * Classes implementing this interface return trough getSimilarityFunction the similarity function that is applied
 * for each one of its fields
 * @author Alessio Rocchi
 */
public interface SelfNNConfigurator {
	Object getSimilarityFunction(String attribute);
}

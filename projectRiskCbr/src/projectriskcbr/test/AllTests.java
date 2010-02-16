package projectriskcbr.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import projectriskcbr.test.jcolibriext.method.retrieve.NNretrieval.similarity.global.AdvancedAverageTest;
import projectriskcbr.test.persistentclasses.ProgettoTest;
import projectriskcbr.test.projectsriskcbr.config.ConfigurationGroupTest;
import projectriskcbr.test.projectsriskcbr.config.DOMAsJTest;
import projectriskcbr.test.projectsriskcbr.config.NNConfiguratorTest;


@RunWith(Suite.class)
@SuiteClasses(	{
					DOMAsJTest.class,
					ConfigurationGroupTest.class,
					NNConfiguratorTest.class,
					AdvancedAverageTest.class,
					ProgettoTest.class
				})
public class AllTests {
}

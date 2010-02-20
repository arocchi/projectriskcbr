package test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.jcolibriext.method.retrieve.NNretrieval.similarity.global.AdvancedAverageTest;
import test.persistentclasses.ProgettoTest;
import test.persistentclasses.utils.AzioniSuggesterTest;
import test.persistentclasses.utils.RischioSuggesterTest;
import test.projectsriskcbr.config.ConfigurationGroupTest;
import test.projectsriskcbr.config.DOMAsJTest;
import test.projectsriskcbr.config.NNConfiguratorTest;


@RunWith(Suite.class)
@SuiteClasses(	{
					DOMAsJTest.class,
					ConfigurationGroupTest.class,
					NNConfiguratorTest.class,
					AdvancedAverageTest.class,
					ProgettoTest.class,
					RischioSuggesterTest.class,
					AzioniSuggesterTest.class
				})
public class AllTests {
}

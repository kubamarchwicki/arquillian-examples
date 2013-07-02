package pl.marchwicki.feedmanager;

import static org.junit.Assert.*;

import javax.ejb.EJBException;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.xml.sax.SAXParseException;

import pl.marchwicki.feedmanager.model.FeedBuilder;

@RunWith(Arquillian.class)
public class FeedServiceIntegrationTest {

	final String FEED_NAME = "some_feed_name";
	
	@Deployment
	public static WebArchive deployment() {
		return ShrinkWrap.create(WebArchive.class, "test.war")
				.addClasses(FeedsService.class, FeedBuilder.class)
				.addClasses(InMemoryFeedsRepository.class, FeedsRepository.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"));
	}

	@Inject
	FeedsService service;
	
	@Test(expected = EJBException.class)
	public void shouldThrowExceptionOnEmptyFeed() {
		service.addNewItems(FEED_NAME, "");
	}

	@Test
	public void shouldThrowParsingExceptionOnEmptyFeed() {
		try {
			service.addNewItems(FEED_NAME, "");
			fail("Should have got exception");
		} catch (EJBException e) {
			assertEquals(IllegalArgumentException.class, e.getCausedByException().getClass());
		}
	}

	
}

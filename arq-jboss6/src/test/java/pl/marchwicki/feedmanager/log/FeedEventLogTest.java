package pl.marchwicki.feedmanager.log;

import static com.jayway.awaitility.Awaitility.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.marchwicki.feedmanager.FeedsRepository;
import pl.marchwicki.feedmanager.FeedsService;
import pl.marchwicki.feedmanager.InMemoryFeedsRepository;
import pl.marchwicki.feedmanager.model.Feed;
import pl.marchwicki.feedmanager.model.FeedBuilder;
import pl.marchwicki.feedmanager.model.Item;

@RunWith(Arquillian.class)
public class FeedEventLogTest {

	private final String FEED_NAME = "javalobby";

	@Inject
	FeedsService service;
	
	@Inject
	LocalEventListener listener;

	@Deployment
	public static WebArchive createDeployment() throws Exception {
		File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("com.jayway.awaitility:awaitility", "rome:rome:0.9")
				.withTransitivity().asFile();

		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addAsLibraries(libs)
				.addClasses(FeedsService.class, FeedBuilder.class,
						FeedsRepository.class)
				.addClasses(Feed.class, Item.class)
				.addClass(InMemoryFeedsRepository.class)
				.addClass(FeedBodyLoggingInterceptor.class)
				.addClass(FeedEventLog.class)
				.addClass(LocalEventListener.class)
				.addAsResource("feed.xml", "feed.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"));
	}

	@Before
	public void clearLog() {
		LocalEventListener.log = null;
	}
	
	@Test
	public void shouldNotifyObserverTest() throws Exception {
		//given
		final InputStream source = this.getClass().getClassLoader().getResourceAsStream("feed.xml");
		final String rssParameterBody = new Scanner(source, "UTF-8").useDelimiter("\\A").next();

		//when
		service.addNewItems(FEED_NAME, rssParameterBody);
		
		await().until(
				fieldIn(LocalEventListener.class).ofType(FeedEventLog.class),
				notNullValue());
		assertThat(LocalEventListener.log.getFeedname(), equalTo(FEED_NAME));
		assertThat(LocalEventListener.log.getItemsCount(), equalTo(15));
	}

	@Singleton
	@Named
	public static class LocalEventListener {

		static FeedEventLog log;
		
		public void listener(@Observes FeedEventLog log) {
			LocalEventListener.log = log;
		}
		
		
	}

}

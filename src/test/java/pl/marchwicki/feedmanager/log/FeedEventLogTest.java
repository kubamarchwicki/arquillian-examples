package pl.marchwicki.feedmanager.log;

import static com.jayway.awaitility.Awaitility.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*; 

import java.io.File;
import java.net.URL;
import java.util.Scanner;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.marchwicki.feedmanager.InMemoryFeedsRepository;
import pl.marchwicki.feedmanager.FeedsService;
import pl.marchwicki.feedmanager.model.FeedBuilder;
import pl.marchwicki.feedmanager.rs.RestFeedConsumerTest;

@RunWith(Arquillian.class)
public class FeedEventLogTest {

	private final String FEED_NAME = "javalobby";
	
	@Inject
	FeedsService service;
	
	static FeedEventLog log;
	
	@Deployment
	public static WebArchive createDeployment() throws Exception {
		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addClass(FeedBuilder.class)
				.addClass(FeedsService.class)
				.addClass(InMemoryFeedsRepository.class)
				.addClass(FeedBodyLoggingInterceptor.class)
				.addClass(EventListener.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"));
	}
	
	@Before
	public void init() {
		log = null;
	}
	
	@Test
	public void shouldNotifyObserverTest() throws Exception {
		new Thread(new Runnable() {
			public void run() {
				service.addNewItems(FEED_NAME, rssParameterBody);				
			}
		}).start();
		
		await().until( fieldIn(FeedEventLogTest.class).ofType(FeedEventLog.class), notNullValue());
		
		assertThat(log.getFeedname(), equalTo(FEED_NAME));
		assertThat(log.getItemsCount(), equalTo(15));
	}
	
	@Test
	public void shouldNotNotifyOnMalformedFeed() throws Exception {
		new Thread(new Runnable() {
			public void run() {
				service.addNewItems(FEED_NAME, "");				
			}
		}).start();
		
		Thread.sleep(1000);
		
		assertThat(log, nullValue());
	}	

	@Named
	public static class EventListener {
		
		public void listener(@Observes FeedEventLog log) {
			FeedEventLogTest.log = log;
		}
	}

	@BeforeClass
	public static void readXmlContent() throws Exception {
		URL dir_url = RestFeedConsumerTest.class.getResource("/");
		File dir = new File(dir_url.toURI());
		File xml = new File(dir, "feed.xml");
		
		rssParameterBody = new Scanner( xml, "UTF-8" ).useDelimiter("\\A").next(); 
	}
	

	private static String rssParameterBody;

}

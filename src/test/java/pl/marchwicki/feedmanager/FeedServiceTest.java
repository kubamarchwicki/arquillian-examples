package pl.marchwicki.feedmanager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.Scanner;
import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.Test;

import pl.marchwicki.feedmanager.model.FeedBuilder;
import pl.marchwicki.feedmanager.rs.RestFeedConsumerTest;

public class FeedServiceTest {

	private final String FEED_NAME = "javalobby";

	FeedsService service = new FeedsService();
	FeedsRepository repository = new FeedsRepository();
	
	{
		service.repository = this.repository;
		service.builder = new FeedBuilder();
	}
	
	@Test
	public void shouldStoreFeedsInRepositoryTest() {
		service.addNewItems(FEED_NAME, rssParameterBody);
		
		assertThat(repository.getFeed(FEED_NAME).getItems().size(), equalTo(15));
		assertThat(repository.getAllFeeds().size(), equalTo(1));
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

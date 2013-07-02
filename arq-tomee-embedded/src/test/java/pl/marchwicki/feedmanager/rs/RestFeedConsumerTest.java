package pl.marchwicki.feedmanager.rs;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.Scanner;

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.marchwicki.feedmanager.FeedsRepository;
import pl.marchwicki.feedmanager.FeedsService;
import pl.marchwicki.feedmanager.InMemoryFeedsRepository;
import pl.marchwicki.feedmanager.model.FeedBuilder;

@RunWith(Arquillian.class)
public class RestFeedConsumerTest {

	private final String FEED_NAME = "javalobby";
	
	@Inject
	FeedsRepository repository;
	
	@Deployment
	public static WebArchive createDeployment() throws Exception {
		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addClass(RestFeedConsumerEndpoint.class)
				.addClasses(FeedsService.class, FeedBuilder.class)
				.addClass(InMemoryFeedsRepository.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"));
	}

	@Test
	public void shouldParseXmlFeedTest(@ArquillianResource URL baseURL) throws Exception {
		//given
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(baseURL.toURI() + "rs/feed/"+FEED_NAME);
		post.setEntity(new StringEntity(rssParameterBody));  		
		
		//when
		HttpResponse response = httpclient.execute(post);
		
		//then
		assertThat(response.getStatusLine().getStatusCode(), equalTo(201));
		assertThat(repository.getAllFeeds().size(), equalTo(1));
		assertThat(repository.getFeed(FEED_NAME).getItems().size(), equalTo(15));
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
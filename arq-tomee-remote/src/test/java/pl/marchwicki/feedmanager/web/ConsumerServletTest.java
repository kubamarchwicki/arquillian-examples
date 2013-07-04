package pl.marchwicki.feedmanager.web;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.marchwicki.feedmanager.FeedsRepository;
import pl.marchwicki.feedmanager.FeedsService;
import pl.marchwicki.feedmanager.InMemoryFeedsRepository;
import pl.marchwicki.feedmanager.log.FeedBodyLoggingInterceptor;
import pl.marchwicki.feedmanager.log.FeedEventLog;
import pl.marchwicki.feedmanager.model.Feed;
import pl.marchwicki.feedmanager.model.FeedBuilder;
import pl.marchwicki.feedmanager.model.Item;

@RunWith(Arquillian.class)
public class ConsumerServletTest {

	private final String FEED_NAME = "javalobby";

	@Inject
	FeedsRepository repository;

	@Deployment
	public static WebArchive createDeployment() throws Exception {
		File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("org.hibernate:hibernate-entitymanager", 
						"org.apache.httpcomponents:httpclient", 
						"rome:rome:0.9")
				.withTransitivity().asFile();

		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addAsLibraries(libs)
				.addClass(ConsumerServlet.class)
				.addClasses(FeedsService.class, FeedBuilder.class, FeedsRepository.class)
				.addClass(InMemoryFeedsRepository.class)
				.addClasses(Feed.class, Item.class)
				.addClasses(FeedEventLog.class, FeedBodyLoggingInterceptor.class)
				.addAsResource("feed.xml", "feed.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"));
	}

	@Test
	@RunAsClient
	@InSequence(1)
	public void shouldReturn405OnGetTest(@ArquillianResource URL baseURL)
			throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(baseURL.toURI() + "web/consume/");
		HttpResponse response = httpclient.execute(get);
		assertThat(response.getStatusLine().getStatusCode(), equalTo(405));
	}

	@Test
	@RunAsClient
	@InSequence(2)
	public void shouldParseXmlFeedTest(@ArquillianResource URL baseURL)
			throws Exception {
		// given
		final URL dir_url = ConsumerServletTest.class.getResource("/");
		final File dir = new File(dir_url.toURI());
		final File xml = new File(dir, "feed.xml");

		final String rssParameterBody = new Scanner(xml, "UTF-8").useDelimiter("\\A").next();
	
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(baseURL.toURI() + "web/consume/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("rss", rssParameterBody));
		nameValuePairs.add(new BasicNameValuePair("feedname", FEED_NAME));
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		// when
		HttpResponse response = httpclient.execute(post);

		// then
		assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
		assertThat(EntityUtils.toString(response.getEntity()),
				equalTo("There are 15 articles in the feed"));
	}
	
	@Test
	@InSequence(3)
	public void shouldContainParsedFeedTest() {
		assertThat(repository.getAllFeeds().size(), equalTo(1));
		assertThat(repository.getFeed(FEED_NAME).getItems().size(), equalTo(15));
	}


}
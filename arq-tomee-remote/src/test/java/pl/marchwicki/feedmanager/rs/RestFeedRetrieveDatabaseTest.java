package pl.marchwicki.feedmanager.rs;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.marchwicki.feedmanager.DatabaseFeedsRepository;
import pl.marchwicki.feedmanager.FeedsRepository;
import pl.marchwicki.feedmanager.FeedsService;
import pl.marchwicki.feedmanager.log.FeedBodyLoggingInterceptor;
import pl.marchwicki.feedmanager.log.FeedEventLog;
import pl.marchwicki.feedmanager.model.Feed;
import pl.marchwicki.feedmanager.model.FeedBuilder;
import pl.marchwicki.feedmanager.model.Item;
import pl.marchwicki.feedmanager.model.entities.FeedEntity;
import pl.marchwicki.feedmanager.model.entities.ItemEntity;

import com.github.kevinsawicki.http.HttpRequest;

@RunWith(Arquillian.class)
public class RestFeedRetrieveDatabaseTest {

	private final String FEED_NAME = "javalobby";

	@Deployment
	public static WebArchive createDeployment() throws Exception {
		String[] deps = { "org.hibernate:hibernate-entitymanager",
				"com.github.kevinsawicki:http-request:5.4",
				"com.google.collections:google-collections:1.0",
				"rome:rome:0.9"};

		File[] libs = Maven.resolver().loadPomFromFile("pom.xml").resolve(deps)
				.withTransitivity().asFile();

		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addAsLibraries(libs)
				.addClass(RestFeedConsumerEndpoint.class)
				.addClasses(FeedsService.class, FeedBuilder.class, FeedsRepository.class)
				.addClass(DatabaseFeedsRepository.class)
				.addClasses(Feed.class, FeedEntity.class, Item.class, ItemEntity.class)
				.addClasses(FeedEventLog.class, FeedBodyLoggingInterceptor.class)
				.addAsResource("test-persistence.xml",
						"META-INF/persistence.xml")
				.addAsResource("test-beans.xml", "META-INF/beans.xml");
	}

	// TODO: arquillian database extension
	@Test
	@RunAsClient
	@Ignore
	public void shouldReturnNotFoundForNoFeedsTest(
			@ArquillianResource URL baseURL) throws Exception {
		//when
		int statusCode = HttpRequest.get(baseURL.toURI() + "rs/feed/"+FEED_NAME).code();
		
		//then
		assertThat(statusCode, equalTo(200));
	}

}
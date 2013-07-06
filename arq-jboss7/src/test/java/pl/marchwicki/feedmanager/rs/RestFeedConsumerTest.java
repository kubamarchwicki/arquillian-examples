package pl.marchwicki.feedmanager.rs;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
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
public class RestFeedConsumerTest {

	private final String FEED_NAME = "javalobby";
	
	@Inject
	FeedsRepository repository;
	
	@Deployment
	public static WebArchive createDeployment() throws Exception {
		File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("org.apache.httpcomponents:httpclient", "rome:rome:0.9")
				.withTransitivity().asFile();

		WebAppDescriptor web = Descriptors.create(WebAppDescriptor.class)
				.version("3.0")
				.createServletMapping()
				.servletName("javax.ws.rs.core.Application").urlPattern("/*")
				.up();
		
		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addAsLibraries(libs)
				.addClass(RestFeedConsumerEndpoint.class)
				.addClasses(FeedsService.class, FeedBuilder.class, FeedsRepository.class)
				.addClass(InMemoryFeedsRepository.class)
				.addClasses(Feed.class, Item.class)
				.addClasses(FeedEventLog.class, FeedBodyLoggingInterceptor.class)
				.addAsResource("feed.xml", "feed.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"))
				.setWebXML(new StringAsset(web.exportAsString()));
	}

	@Test
	@RunAsClient
	@InSequence(1)
	public void init(@ArquillianResource URL baseURL) throws Exception {
		final InputStream source = this.getClass().getClassLoader().getResourceAsStream("feed.xml");
		final String rssParameterBody = new Scanner(source, "UTF-8" ).useDelimiter("\\A").next();
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(baseURL.toURI() + "rs/feed/"+FEED_NAME);
		post.setEntity(new StringEntity(rssParameterBody));  		
		
		//when
		HttpResponse response = httpclient.execute(post);

		assertThat(response.getStatusLine().getStatusCode(), equalTo(201));
	}
	
	@Test
	@InSequence(2)
	public void shouldParseXmlFeedTest() {
		//then
		assertThat(repository.getAllFeeds().size(), equalTo(1));
		assertThat(repository.getFeed(FEED_NAME).getItems().size(), equalTo(15));
	}
	
}
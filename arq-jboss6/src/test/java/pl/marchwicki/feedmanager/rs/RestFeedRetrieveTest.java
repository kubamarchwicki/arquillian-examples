package pl.marchwicki.feedmanager.rs;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
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

import com.github.kevinsawicki.http.HttpRequest;

@RunWith(Arquillian.class)
public class RestFeedRetrieveTest {

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws Exception {
		File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("rome:rome:0.9").withTransitivity()
				.asFile();

		
		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addAsLibraries(libs)
				.addClass(RestFeedConsumerEndpoint.class)
				.addClasses(FeedsService.class, FeedBuilder.class, FeedsRepository.class)
				.addClasses(Feed.class, Item.class)
				.addClass(InMemoryFeedsRepository.class)
				
				//remove those to see how meaningless exceptions you get upon deployment
				.addClasses(FeedEventLog.class, FeedBodyLoggingInterceptor.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"));
	}

	@Test
	@RunAsClient
	public void shouldReturnEmptyFeedList(@ArquillianResource URL baseURL) throws Exception {
		//given 
		final StringWriter output = new StringWriter();
		
		//when
		HttpRequest request = HttpRequest.get(baseURL.toURI() + "rs/feeds")
				.receive(output);
		
		//then
		assertThat(request.code(), equalTo(200));
		assertThat(output.toString(), equalTo("[]"));
	}
	
}
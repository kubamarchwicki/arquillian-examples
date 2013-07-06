package pl.marchwicki.feedmanager.ws;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsArrayWithSize.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

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
import pl.marchwicki.feedmanager.log.FeedEventLogListener;
import pl.marchwicki.feedmanager.model.Feed;
import pl.marchwicki.feedmanager.model.FeedBuilder;
import pl.marchwicki.feedmanager.model.Item;
import pl.marchwicki.feedmanager.ws.model.LogStats;

@RunWith(Arquillian.class)
public class FeedStatsSoapTest {

	@Deployment
	public static WebArchive createDeployment() throws Exception {
		File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("rome:rome:0.9")
				.withTransitivity().asFile();

		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addAsLibraries(libs)
				.addClass(LoggingStats.class)
				.addClass(LoggingStatsWS.class)
				
				//this is unnecessary if we could've populated the database earlier on
				.addClass(InMemoryFeedsRepository.class)
				.addClasses(Feed.class, Item.class)
				.addClasses(FeedsService.class, FeedBuilder.class, FeedsRepository.class)
				.addClasses(FeedEventLog.class, FeedBodyLoggingInterceptor.class)
				.addClass(FeedEventLogListener.class)
				.addClass(LogStats.class)
				.addAsResource("feed.xml", "feed.xml")
				.addAsWebInfResource("jbossas-ds.xml")
				.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
	}

	final String feedname = "something";

	@Inject
	FeedsService service;

	@Test
	@InSequence(1)
	public void populateData() throws Exception {
		final InputStream source = this.getClass().getClassLoader().getResourceAsStream("feed.xml");
		final String rssParameterBody = new Scanner(source, "UTF-8").useDelimiter(
				"\\A").next();

		service.addNewItems(feedname, rssParameterBody);
	}
	
	@Test
	@RunAsClient
	@InSequence(2)
	public void shouldReturnLogStatsForSingleFeed(
			@ArquillianResource URL baseURL) throws Exception {
		// given endpoint
		URL wsdlDocumentLocation = new URL(baseURL.toExternalForm() + "LoggingStatsWS?wsdl");
		String namespaceURI = "http://ws.feedmanager.marchwicki.pl/";
		String servicePart = "LoggingStatsWSService";
		QName serviceQN = new QName(namespaceURI, servicePart);

		Service service = Service.create(wsdlDocumentLocation, serviceQN);
		LoggingStats stats = service.getPort(LoggingStats.class);

		// when
		LogStats[] statData = stats.getStats();

		// then
		assertThat(statData, arrayWithSize(1));
		assertThat(statData[0], is(new LogStats(feedname, 15l)));
	}

}

package pl.marchwicki.feedmanager.ws;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsArrayWithSize.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.Scanner;

import javax.inject.Inject;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.hibernate.ejb.HibernatePersistence;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceDescriptor;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.marchwicki.feedmanager.FeedsService;
import pl.marchwicki.feedmanager.InMemoryFeedsRepository;
import pl.marchwicki.feedmanager.log.FeedEventLog;
import pl.marchwicki.feedmanager.log.FeedEventLogListener;
import pl.marchwicki.feedmanager.model.FeedBuilder;
import pl.marchwicki.feedmanager.rs.RestFeedConsumerTest;
import pl.marchwicki.feedmanager.ws.model.LogStats;

@RunWith(Arquillian.class)
public class FeedStatsSoapTest {

	@Deployment
	public static WebArchive createDeployment() throws Exception {
        PersistenceDescriptor persistence = Descriptors.create(PersistenceDescriptor.class)
                .createPersistenceUnit()
                    .name("test")
                    .jtaDataSource("jdbc/arquillian")
                    .provider(HibernatePersistence.class.getName())
                    .getOrCreateProperties()
                    	.createProperty().name("hibernate.show_sql").value("true").up()
                    	.createProperty().name("hibernate.format_sql").value("true").up()
                    	.createProperty().name("hibernate.use_sql_comments").value("true").up()
                    	.createProperty().name("hibernate.dialect").value("org.hibernate.dialect.HSQLDialect").up()
                    	.createProperty().name("hibernate.hbm2ddl.auto").value("create-drop").up()
                    .up()
                .up();	
        
		return ShrinkWrap
				.create(WebArchive.class, "inmemory.war")
				.addClass(LoggingStats.class)
				.addClass(LoggingStatsWS.class)
				.addClass(InMemoryFeedsRepository.class)
				.addClasses(FeedsService.class, FeedBuilder.class)
				.addClass(FeedEventLogListener.class)
				.addClass(FeedEventLog.class)
				.addAsResource(new StringAsset(persistence.exportAsString()),
						"META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"));
	}

	@Inject
	FeedsService service;

	@Test
	public void shouldReturnLogStatsForSingleFeed(
			@ArquillianResource URL baseURL) throws Exception {
		// given feed
		final String feedname = "something";
		service.addNewItems(feedname, rssParameterBody);

		// given endpoint
		URL wsdlDocumentLocation = new URL(baseURL.toExternalForm()
				+ "webservices/LoggingStatsWS?wsdl");
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

	@BeforeClass
	public static void readXmlContent() throws Exception {
		URL dir_url = RestFeedConsumerTest.class.getResource("/");
		File dir = new File(dir_url.toURI());
		File xml = new File(dir, "feed.xml");

		rssParameterBody = new Scanner(xml, "UTF-8").useDelimiter("\\A").next();
	}

	private static String rssParameterBody;
}

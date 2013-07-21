package pl.marchwicki.feedmanager.rs;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.URL;

import org.hibernate.ejb.HibernatePersistence;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceDescriptor;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.marchwicki.feedmanager.DatabaseFeedsRepository;
import pl.marchwicki.feedmanager.FeedsService;
import pl.marchwicki.feedmanager.model.FeedBuilder;

import com.github.kevinsawicki.http.HttpRequest;

@RunWith(Arquillian.class)
public class RestFeedRetrieveDatabaseTest {

	private final String FEED_NAME = "javalobby";
	
	@Deployment
	public static WebArchive createDeployment() throws Exception {
		BeansDescriptor beans = Descriptors.create(BeansDescriptor.class)
				.createAlternatives().clazz(DatabaseFeedsRepository.class.getName()).up();
		
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
				.create(WebArchive.class, "test.war")
				.addClass(RestFeedConsumerEndpoint.class)
				.addClasses(FeedsService.class, FeedBuilder.class)
				.addClass(DatabaseFeedsRepository.class)
				.addAsResource(new StringAsset(persistence.exportAsString()), "META-INF/persistence.xml")
				.addAsResource(new StringAsset(beans.exportAsString()), "META-INF/beans.xml");
	}

	//TODO: arquillian database extension
	@Test
	@Ignore
	public void shouldReturnNotFoundForNoFeedsTest(@ArquillianResource URL baseURL) throws Exception {
		//when
		int statusCode = HttpRequest.get(baseURL.toURI() + "rs/feed/"+FEED_NAME).code();
		
		//then
		assertThat(statusCode, equalTo(200));
	}
	
}
package pl.marchwicki.feedmanager.rs;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.sun.jersey.spi.container.servlet.ServletContainer;

import pl.marchwicki.feedmanager.DatabaseFeedsRepository;
import pl.marchwicki.feedmanager.FeedsService;
import pl.marchwicki.feedmanager.model.FeedBuilder;

@RunWith(Arquillian.class)
public class RestFeedRetrieveDatabaseTest {

	private final String FEED_NAME = "javalobby";
	
	@Deployment
	public static WebArchive createDeployment() throws Exception {
		WebAppDescriptor web = Descriptors.create(WebAppDescriptor.class)
				.version("3.0")
				.createServlet()
					.servletName("jersey")
					.servletClass(ServletContainer.class.getName())
					.loadOnStartup(1)
				.up()
				.createServletMapping()
					.servletName("jersey").urlPattern("/*")
				.up();

		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addClass(RestFeedConsumerEndpoint.class)
				.addClasses(FeedsService.class, FeedBuilder.class)
				.addClass(DatabaseFeedsRepository.class)
				.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
				.addAsResource("test-beans.xml", "META-INF/beans.xml")
				.setWebXML(new StringAsset(web.exportAsString()));
	}

	//TODO: arquillian database extension
	@Test
	@Ignore
	public void shouldReturnNotFoundForNoFeedsTest(@ArquillianResource URL baseURL) throws Exception {
		//given
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(baseURL.toURI() + "rs/feed/"+FEED_NAME);
		
		//when
		HttpResponse response = httpclient.execute(get);
		
		//then
		assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
	}
	
}
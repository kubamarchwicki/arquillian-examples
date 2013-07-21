package pl.marchwicki.feedmanager.rs;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.StringWriter;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.marchwicki.feedmanager.FeedsService;
import pl.marchwicki.feedmanager.InMemoryFeedsRepository;
import pl.marchwicki.feedmanager.model.FeedBuilder;

import com.github.kevinsawicki.http.HttpRequest;
import com.sun.jersey.spi.container.servlet.ServletContainer;

@RunWith(Arquillian.class)
public class RestFeedRetrieveTest {

	@Deployment(testable=false)
	public static WebArchive createDeployment() throws Exception {
		WebAppDescriptor web = Descriptors.create(WebAppDescriptor.class)
				.version("3.0")
				.createServlet()
					.servletName("jersey")
					.servletClass(ServletContainer.class.getName())
					.createInitParam()
						.paramName("com.sun.jersey.config.property.packages")
						.paramValue("pl.marchwicki.feedmanager;org.codehaus.jackson.jaxrs")
					.up()
					.loadOnStartup(1)
				.up()
				.createServletMapping()
					.servletName("jersey").urlPattern("/*")
				.up();
				
		
		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addClass(RestFeedConsumerEndpoint.class)
				.addClasses(FeedsService.class, FeedBuilder.class)
				.addClass(InMemoryFeedsRepository.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"))
				.setWebXML(new StringAsset(web.exportAsString()));
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
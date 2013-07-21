package pl.marchwicki.feedmanager.web;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.inject.Inject;

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

import com.github.kevinsawicki.http.HttpRequest;

@RunWith(Arquillian.class)
public class ConsumerServletTest {

	private final String FEED_NAME = "javalobby";

	@Inject
	FeedsRepository repository;

	@Deployment
	public static WebArchive createDeployment() throws Exception {
		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addClass(ConsumerServlet.class)
				.addClasses(FeedsService.class, FeedBuilder.class)
				.addClass(InMemoryFeedsRepository.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"));
	}

	@Test
	public void shouldReturn405OnGetTest(@ArquillianResource URL baseURL)
			throws Exception {
		int statusCode = HttpRequest.get(baseURL.toURI() + "web/consume/").code();
		assertThat(statusCode, equalTo(405));
	}

	@Test
	public void shouldParseXmlFeedTest(@ArquillianResource URL baseURL)
			throws Exception {
		// given
		final Map<String, String> form = new HashMap<String, String>(2);
		form.put("rss", rssParameterBody);
		form.put("feedname", FEED_NAME);

		final String url = baseURL.toURI() + "web/consume/";
		final StringWriter output = new StringWriter();
		
		// when
		HttpRequest request = HttpRequest.post(url)
				.form(form).receive(output);

		// then
		assertThat(request.code(), equalTo(200));
		assertThat(output.toString(),
				equalTo("There are 15 articles in the feed"));

		assertThat(repository.getAllFeeds().size(), equalTo(1));
		assertThat(repository.getFeed(FEED_NAME).getItems().size(), equalTo(15));
	}

	@BeforeClass
	public static void readXmlContent() throws Exception {
		URL dir_url = ConsumerServletTest.class.getResource("/");
		File dir = new File(dir_url.toURI());
		File xml = new File(dir, "feed.xml");

		rssParameterBody = new Scanner(xml, "UTF-8").useDelimiter("\\A").next();
	}

	private static String rssParameterBody;
}
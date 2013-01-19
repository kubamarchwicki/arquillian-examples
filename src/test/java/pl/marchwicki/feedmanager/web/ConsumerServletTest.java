package pl.marchwicki.feedmanager.web;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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

import pl.marchwicki.feedmanager.model.FeedBuilder;

@RunWith(Arquillian.class)
public class ConsumerServletTest {

	private static String rssParameterBody;
	
	@BeforeClass
	public static void readXmlContent() throws Exception {
		URL dir_url = ConsumerServletTest.class.getResource("/");
		File dir = new File(dir_url.toURI());
		File xml = new File(dir, "feed.xml");
		
		rssParameterBody = new Scanner( xml, "UTF-8" ).useDelimiter("\\A").next(); 
	}
	
	
	@Deployment
	public static WebArchive createDeployment() throws Exception {
		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addClass(ConsumerServlet.class)
				.addClass(FeedBuilder.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"));
	}

	@Test
	public void shouldReturn405OnGetTest(@ArquillianResource URL baseURL) throws Exception {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet get = new HttpGet(baseURL.toURI() + "web/consume/");
		HttpResponse response = httpclient.execute(get);
		assertThat(response.getStatusLine().getStatusCode(), equalTo(405));
	}
	
	@Test
	public void shouldParseXmlFeedTest(@ArquillianResource URL baseURL) throws Exception {
		//given
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost post = new HttpPost(baseURL.toURI() + "web/consume/");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);  
		nameValuePairs.add(new BasicNameValuePair("rss", rssParameterBody));  
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));  		
		
		//when
		HttpResponse response = httpclient.execute(post);
		
		//then
		assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
		assertThat(EntityUtils.toString(response.getEntity()), equalTo("There are 15 articles in the feed"));
	}
	
	

}
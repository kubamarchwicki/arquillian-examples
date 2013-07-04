package pl.marchwicki.feedmanager.model;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.Scanner;

import org.junit.Test;

public class FeedBuilderTest {

	FeedBuilder builder = new FeedBuilder();
	
	@Test(expected = IllegalArgumentException.class)
	public void throwExceptionOnParseNull() {
		builder.fromXml(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void throwExceptionOnParseEmpty() {
		builder.fromXml("");
	}
	
	@Test
	public void parseFeedXml() throws Exception {
		//given
		URL dir_url = getClass().getResource("/");
		File dir = new File(dir_url.toURI());
		File xml = new File(dir, "feed.xml");
		String text = new Scanner( xml, "UTF-8" ).useDelimiter("\\A").next(); 

		//when
		Feed f = builder.fromXml(text);
		
		//then
		assertThat(f.getItems().size(), equalTo(15));
		assertThat(f.getTitle(), equalTo("Javalobby - The heart of the Java developer community"));
		assertThat(f.getLink(), equalTo("http://java.dzone.com/"));
		
		assertThat(f.getItems().get(1).getTitle(), equalTo("Inspections are not Optional "));
	}
	
}

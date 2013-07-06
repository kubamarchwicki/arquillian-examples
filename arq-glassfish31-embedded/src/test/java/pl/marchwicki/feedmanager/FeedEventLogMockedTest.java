package pl.marchwicki.feedmanager;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URL;
import java.util.Scanner;

import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Named;
import javax.validation.Validator;

import org.junit.Test;

import pl.marchwicki.feedmanager.log.FeedEventLog;
import pl.marchwicki.feedmanager.model.FeedBuilder;

public class FeedEventLogMockedTest {

	private final String FEED_NAME = "javalobby";

	FeedsService service = new FeedsService();

	{
		service.builder = new FeedBuilder();
		service.repository = new InMemoryFeedsRepository();
		service.validator = mock(Validator.class);
		service.event = mock(Event.class);
	}
	
	@Test
	public void shouldNotifyObserverTest() throws Exception {
		//given
		final URL dir_url = FeedEventLogMockedTest.class.getResource("/");
		final File dir = new File(dir_url.toURI());
		final File xml = new File(dir, "feed.xml");

		final String rssParameterBody = new Scanner(xml, "UTF-8").useDelimiter("\\A").next();

		//when
		service.addNewItems(FEED_NAME, rssParameterBody);
		verify(service.event, times(1)).fire((FeedEventLog) any());
	}

}

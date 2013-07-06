package pl.marchwicki.feedmanager.log;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.Test;

public class FeedEventLogListenerMockedTest {

	EntityManager em = mock(EntityManager.class);
	FeedEventLogListener listener = new FeedEventLogListener();
	{
		listener.em = this.em;
	}
	
	@Test
	public void shouldCreateLogEntityTest() {
		//given
		final String feedname = "FEED_NAME";
		final Integer count = 10;
		final Date date = new Date();

		final FeedEventLog log = FeedEventLog.Builder.forFeed(feedname)
			.withItemsCount(count)
			.withDate(date)
			.build();
		
		assertThat(log.getId(), nullValue());
		
		//when
		listener.processLog(log);
		
		//then
		verify(em, times(1)).persist(log);
		verify(em, times(1)).flush();
	}
}

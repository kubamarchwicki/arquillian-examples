package pl.marchwicki.feedmanager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import pl.marchwicki.feedmanager.model.Feed;
import pl.marchwicki.feedmanager.model.Item;

public class FeedRepositoryTest {

	private final String FEED_NAME = "test-feed-name";
	private FeedsRepository repository = new InMemoryFeedsRepository();
	
	@Test
	public void shouldAddAFeed() {
		//given
		Feed f = new Feed.Builder()
			.withTitle("test")
			.withLink("http://test.com/test")
			.build();
		
		Item i = new Item.Builder()
			.withTitle("Test item")
			.build();
		
		f.addItem(i);
		
		//when
		repository.addItem(FEED_NAME, f);
		
		//then
		assertThat(repository.getAllFeeds().size(), equalTo(1));
		assertThat(repository.getFeed("various name"), equalTo(null));
		assertThat(repository.getFeed(FEED_NAME), equalTo(f));
	}
	
	
}

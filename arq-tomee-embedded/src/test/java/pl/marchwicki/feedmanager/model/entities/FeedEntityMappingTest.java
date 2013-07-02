package pl.marchwicki.feedmanager.model.entities;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pl.marchwicki.feedmanager.model.Feed;
import pl.marchwicki.feedmanager.model.Item;

public class FeedEntityMappingTest {

	Feed feed;

	@Before
	public void init() {
		feed = new Feed.Builder().withTitle("test")
				.withLink("http://test.com/test").build();
		Item i = new Item.Builder().withTitle("Test item").build();
		feed.addItem(i);
	}

	@Test
	public void shouldPopulateFeedItemsOnPostLoad() {
		FeedEntity e = new FeedEntity.Builder().withFeed(feed)
				.withFeedname("samplename").build();
		e.syncItemsMapping();
		e.getItems().clear();
		
		e.setupItemsMapping();

		assertThat(e.getEntityItems().size(), equalTo(1));
		assertThat(e.getItems().size(), equalTo(1));
		assertThat(e.getEntityItems().size(), equalTo(e.getItems().size()));
		for (int i=0; i<e.getItems().size(); i++) {
			assertEquals("Element "+i+" mismatched", e.getItems().get(i), e.getEntityItems().get(i));
		}
	}

	@Test
	public void shouldSyncFeedItemsOnPrePersist() {
		FeedEntity e = new FeedEntity.Builder().withFeed(feed)
				.withFeedname("samplename").build();
		e.syncItemsMapping();
		
		assertThat(feed.getItems().size(), equalTo(1));
		assertThat(e.getEntityItems().size(), equalTo(e.getItems().size()));
		for (int i=0; i<e.getItems().size(); i++) {
			assertEquals("Element "+i+" mismatched", e.getItems().get(i), e.getEntityItems().get(i));
		}
		
		Item i2 = new Item.Builder().withTitle("Test item2").build();
		e.getItems().add(i2);
		e.syncItemsMapping();

		assertThat(feed.getItems().size(), equalTo(2));
		assertThat(e.getEntityItems().size(), equalTo(e.getItems().size()));
		for (int i=0; i<e.getItems().size(); i++) {
			assertEquals("Element "+i+" mismatched", e.getItems().get(i), e.getEntityItems().get(i));
		}

	}

}

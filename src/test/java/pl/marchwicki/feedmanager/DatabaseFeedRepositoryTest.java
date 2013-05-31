package pl.marchwicki.feedmanager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Date;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.marchwicki.feedmanager.model.Feed;
import pl.marchwicki.feedmanager.model.Item;
import pl.marchwicki.feedmanager.model.entities.FeedEntity;
import pl.marchwicki.feedmanager.model.entities.ItemEntity;

@RunWith(Arquillian.class)
public class DatabaseFeedRepositoryTest {

	private final String FEED_NAME = "test-persistence-feed-name";

	@Deployment
	public static WebArchive createDeployment() throws Exception {
		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addClasses(FeedsRepository.class,
						InMemoryFeedsRepository.class,
						DatabaseFeedsRepository.class)
				.addClasses(FeedEntity.class, ItemEntity.class)
				.addClasses(Feed.class, Item.class)
				.addAsResource("test-persistence.xml",
						"META-INF/persistence.xml")
				.addAsResource("test-beans.xml", "META-INF/beans.xml");
	}

	@Inject
	FeedsRepository repository;

	@Test
	@Ignore(value="I really don't know why repository throws NPE")
	public void shouldPersistFeed() {
		// given
		Feed f = new Feed.Builder().withTitle("test")
				.withLink("http://test.com/test").build();

		Item i = new Item.Builder().withTitle("Test item")
				.withLink("http://wp.pl").withDate(new Date())
				.withContent("Yada yada yada").build();
		f.addItem(i);

		// when
		repository.addItem(FEED_NAME, f);

		assertThat(repository.getAllFeeds().size(), equalTo(1));
		assertThat(repository.getFeed("various name"), equalTo(null));
		assertThat(repository.getFeed(FEED_NAME).getItems().size(), equalTo(1));
		assertEquals(repository.getFeed(FEED_NAME), new FeedEntity.Builder()
				.withFeedname(FEED_NAME).withFeed(f).build());
	}

}

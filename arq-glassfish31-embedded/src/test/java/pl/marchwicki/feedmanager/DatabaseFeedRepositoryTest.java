package pl.marchwicki.feedmanager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Date;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
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
		BeansDescriptor descriptor = Descriptors.create(BeansDescriptor.class)
				.createAlternatives()
					.clazz(DatabaseFeedsRepository.class.getName())
					.up();
		
		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addClasses(FeedsRepository.class,
						DatabaseFeedsRepository.class,
						InMemoryFeedsRepository.class)
				.addClasses(FeedEntity.class, ItemEntity.class)
				.addClasses(Feed.class, Item.class)
				.addAsWebInfResource(new StringAsset(descriptor.exportAsString()), "beans.xml")
				.addAsResource("test-persistence.xml",
						"META-INF/persistence.xml");
	}

	@Inject
	FeedsRepository repository;

	@Test
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

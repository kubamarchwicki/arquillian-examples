package pl.marchwicki.feedmanager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.marchwicki.feedmanager.log.FeedBodyLoggingInterceptor;
import pl.marchwicki.feedmanager.log.FeedEventLog;
import pl.marchwicki.feedmanager.model.Feed;
import pl.marchwicki.feedmanager.model.Item;
import pl.marchwicki.feedmanager.model.entities.FeedEntity;
import pl.marchwicki.feedmanager.model.entities.ItemEntity;

@RunWith(Arquillian.class)
public class DatabaseFeedRepositoryTest {

	private final String FEED_NAME = "test-persistence-feed-name";
	
	@PersistenceContext
	EntityManager em;
	
	@Inject
	FeedsRepository repository;
	
	@Deployment
	public static WebArchive createDeployment() throws Exception {
		File[] libs = Maven.resolver().loadPomFromFile("pom.xml")
				.resolve("com.google.collections:google-collections:1.0").withTransitivity()
				.asFile();
		
		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addAsLibraries(libs)
				.addClass(DatabaseFeedsRepository.class)
				.addClass(InMemoryFeedsRepository.class)
				.addClass(FeedsRepository.class)
				.addClasses(FeedEntity.class, Feed.class, ItemEntity.class, Item.class)
				.addClasses(FeedEventLog.class, FeedBodyLoggingInterceptor.class)
				.addAsWebInfResource("jbossas-ds.xml")
				.addAsResource("test-persistence.xml", "META-INF/persistence.xml")
				.addAsResource("test-beans.xml", "META-INF/beans.xml");
	}	
	
	@Test
	public void shouldPersistFeed() {
		//given
		Feed f = new Feed.Builder()
			.withTitle("test")
			.withLink("http://test.com/test")
			.build();
		
		Item i = new Item.Builder()
			.withTitle("Test item")
			.withLink("http://wp.pl")
			.withDate(new Date())
			.withContent("Yada yada yada")
			.build();
		
		f.addItem(i);
		
		//when
		repository.addItem(FEED_NAME, f);

		
		assertThat(repository.getAllFeeds().size(), equalTo(1));
		assertThat(repository.getFeed("various name"), equalTo(null));
		assertThat(repository.getFeed(FEED_NAME).getItems().size(), equalTo(1));
		assertEquals(repository.getFeed(FEED_NAME), new FeedEntity.Builder()
				.withFeedname(FEED_NAME)
				.withFeed(f)
				.build());
	}
	
}

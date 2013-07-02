package pl.marchwicki.feedmanager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.ejb.HibernatePersistence;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;

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
		BeansDescriptor beans = Descriptors.create(BeansDescriptor.class)
				.createAlternatives().clazz(DatabaseFeedsRepository.class.getName()).up();
		
        PersistenceDescriptor persistence = Descriptors.create(PersistenceDescriptor.class)
                .createPersistenceUnit()
                    .name("test")
                    .jtaDataSource("jdbc/arquillian")
                    .provider(HibernatePersistence.class.getName())
                    .getOrCreateProperties()
                    	.createProperty().name("hibernate.show_sql").value("true").up()
                    	.createProperty().name("hibernate.format_sql").value("true").up()
                    	.createProperty().name("hibernate.use_sql_comments").value("true").up()
                    	.createProperty().name("hibernate.dialect").value("org.hibernate.dialect.HSQLDialect").up()
                    	.createProperty().name("hibernate.hbm2ddl.auto").value("create-drop").up()
                    .up()
                .up();	
		
		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addClass(DatabaseFeedsRepository.class)
				.addClasses(FeedEntity.class, ItemEntity.class)
				.addAsResource(new StringAsset(persistence.exportAsString()),
						"META-INF/persistence.xml")
				.addAsResource(new StringAsset(beans.exportAsString()),
						"META-INF/beans.xml");
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

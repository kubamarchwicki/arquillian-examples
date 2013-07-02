package pl.marchwicki.feedmanager.log;

import static com.jayway.awaitility.Awaitility.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.ejb.HibernatePersistence;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class FeedEventLogListenerTest {

	@Deployment
	public static WebArchive createDeployment() throws Exception {
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
				.addClass(FeedEventLogListener.class)
				.addClass(FeedEventLog.class)
				.addAsResource(new StringAsset(persistence.exportAsString()), "META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"));
	}	
	
	@Inject
	FeedEventLogListener listener;
	
    @PersistenceContext
    EntityManager em;
    
	@Test
	public void shouldCreateLogEntityTest() throws Exception {
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
		
		await().until(new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return log.getId() != null; 
            }
        });
		
		//then
		assertThat(log.getId(), notNullValue());
	}
}

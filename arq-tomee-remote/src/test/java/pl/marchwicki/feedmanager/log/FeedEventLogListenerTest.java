package pl.marchwicki.feedmanager.log;

import static com.jayway.awaitility.Awaitility.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class FeedEventLogListenerTest {

	@Deployment
	public static WebArchive createDeployment() throws Exception {
		String[] deps = { "org.hibernate:hibernate-entitymanager",
				"com.jayway.awaitility:awaitility" };

		File[] libs = Maven.resolver().loadPomFromFile("pom.xml").resolve(deps)
				.withTransitivity().asFile();

		return ShrinkWrap
				.create(WebArchive.class, "test.war")
				.addAsLibraries(libs)
				.addClass(FeedEventLogListener.class)
				.addClass(FeedEventLog.class)
				.addAsResource("test-persistence.xml",
						"META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"));
	}

	@Inject
	FeedEventLogListener listener;

	@PersistenceContext
	EntityManager em;

	@Test
	public void shouldCreateLogEntityTest() throws Exception {
		// given
		final String feedname = "FEED_NAME";
		final Integer count = 10;
		final Date date = new Date();

		final FeedEventLog log = FeedEventLog.Builder.forFeed(feedname)
				.withItemsCount(count).withDate(date).build();

		assertThat(log.getId(), nullValue());

		// when
		listener.processLog(log);

		await().until(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return log.getId() != null;
			}
		});

		// then
		assertThat(log.getId(), notNullValue());
	}
}

package pl.marchwicki.feedmanager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import pl.marchwicki.feedmanager.model.Feed;
import pl.marchwicki.feedmanager.model.Item;

@RunWith(Arquillian.class)
public class FeedValidationIntegrationTest {

	@Resource
	Validator validator;

	@Deployment
	public static WebArchive createDeployment() throws Exception {
		return ShrinkWrap.create(WebArchive.class, "test.war");
	}

	@Test
	public void noErrors() {
		Feed f = new Feed.Builder().withTitle("test")
				.withLink("http://test.com/test").build();
		Item i = new Item.Builder().withTitle("Test item").build();
		f.addItem(i);

		Set<ConstraintViolation<Feed>> errors = validator.validate(f);

		assertThat(errors.size(), equalTo(0));
	}

	@Test
	public void shouldReturnNullTitleError() {
		Feed f = new Feed.Builder().withLink("http://test.com/test").build();
		Item i = new Item.Builder().withTitle("Test item").build();
		f.addItem(i);

		Set<ConstraintViolation<Feed>> errors = validator.validate(f);

		assertThat(errors.size(), equalTo(1));
		assertThat(errors.iterator().next().getMessage(),
				equalTo("may not be null"));
	}

	@Test
	public void shouldReturnNoTitleError() {
		Feed f = new Feed.Builder().withTitle("")
				.withLink("http://test.com/test").build();
		Item i = new Item.Builder().withTitle("Test item").build();
		f.addItem(i);

		Set<ConstraintViolation<Feed>> errors = validator.validate(f);

		assertThat(errors.size(), equalTo(1));
		assertThat(errors.iterator().next().getMessage(),
				startsWith("size must be between 1"));
	}
}

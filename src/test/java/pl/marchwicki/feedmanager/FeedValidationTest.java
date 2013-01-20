package pl.marchwicki.feedmanager;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import pl.marchwicki.feedmanager.model.Feed;
import pl.marchwicki.feedmanager.model.Item;

public class FeedValidationTest {

	private static Validator validator;

	@BeforeClass
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
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
		Feed f = new Feed.Builder()
				.withLink("http://test.com/test").build();
		Item i = new Item.Builder().withTitle("Test item").build();
		f.addItem(i);
		
		Set<ConstraintViolation<Feed>> errors = validator.validate(f);

		assertThat(errors.size(), equalTo(1));
        assertThat(errors.iterator().next().getMessage(), equalTo("may not be null"));
	}	
	
	@Test
	public void shouldReturnNoTitleError() {
		Feed f = new Feed.Builder().withTitle("")
				.withLink("http://test.com/test").build();
		Item i = new Item.Builder().withTitle("Test item").build();
		f.addItem(i);
		
		Set<ConstraintViolation<Feed>> errors = validator.validate(f);

		assertThat(errors.size(), equalTo(1));
        assertThat(errors.iterator().next().getMessage(), startsWith("size must be between 1"));
	}	
}

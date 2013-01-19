package pl.marchwicki.feedmanager;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import pl.marchwicki.feedmanager.model.Feed;
import pl.marchwicki.feedmanager.model.FeedBuilder;

@Named
public class FeedsService {

	@Inject
	FeedsRepository repository;
	
	@Inject
	FeedBuilder builder;
	
	@Resource
	Validator validator;
	
	public Feed addNewItems(String feedname, String xml) {
		Feed feed = builder.fromXml(xml);
		
		Set<ConstraintViolation<Feed>> errors = validator.validate(feed);
		if (errors.size() > 0) {
			throw new IllegalArgumentException("Validation errors: " + errors);
		}
		
		repository.addItem(feedname, feed);
		return feed;
	}
}

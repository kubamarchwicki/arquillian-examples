package pl.marchwicki.feedmanager;

import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import pl.marchwicki.feedmanager.log.FeedBodyLoggingInterceptor;
import pl.marchwicki.feedmanager.log.FeedEventLog;
import pl.marchwicki.feedmanager.model.Feed;
import pl.marchwicki.feedmanager.model.FeedBuilder;

@Stateless
public class FeedsService {

	@Inject
	FeedsRepository repository;
	
	@Inject
	FeedBuilder builder;

	@Inject
	Event<FeedEventLog> event;
	
	@Resource
	Validator validator;
	
	@Interceptors(FeedBodyLoggingInterceptor.class)
	public Feed addNewItems(String feedname, String xml) {
		Feed feed = builder.fromXml(xml);
		
		Set<ConstraintViolation<Feed>> errors = validator.validate(feed);
		if (errors.size() > 0) {
			throw new IllegalArgumentException("Validation errors: " + errors);
		}
		
		event.fire(FeedEventLog.Builder.forFeed(feedname)
				.withItemsCount(feed.getItems().size())
				.now().build());

		repository.addItem(feedname, feed);
		return feed;
	}

	public Feed getFeed(String feedname) {
		return repository.getFeed(feedname);
	}
}

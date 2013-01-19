package pl.marchwicki.feedmanager;

import javax.inject.Inject;
import javax.inject.Named;

import pl.marchwicki.feedmanager.model.Feed;
import pl.marchwicki.feedmanager.model.FeedBuilder;

@Named
public class FeedsService {

	@Inject
	FeedsRepository repository;
	
	@Inject
	FeedBuilder builder;
	
	public Feed addNewItems(String feedname, String xml) {
		Feed feed = builder.fromXml(xml);
		repository.addItem(feedname, feed);
		
		return feed;
	}
}

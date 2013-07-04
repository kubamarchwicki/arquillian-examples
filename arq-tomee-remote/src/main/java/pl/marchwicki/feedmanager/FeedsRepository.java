package pl.marchwicki.feedmanager;

import java.util.List;

import pl.marchwicki.feedmanager.model.Feed;

public interface FeedsRepository {

	public abstract void addItem(String feedname, Feed feed);

	public abstract Feed getFeed(String feedname);

	public abstract List<? extends Feed> getAllFeeds();

}
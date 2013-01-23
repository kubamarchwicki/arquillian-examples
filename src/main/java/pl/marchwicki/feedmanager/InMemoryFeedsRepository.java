package pl.marchwicki.feedmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import pl.marchwicki.feedmanager.model.Feed;

@Singleton
@Startup
@Lock(LockType.READ)
//@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class InMemoryFeedsRepository implements FeedsRepository {

	private Map<String, Feed> repository = new HashMap<String, Feed>();

	public void addItem(String feedname, Feed feed) {
		if (repository.containsKey(feedname)) {
			repository.get(feedname).addItems(feed.getItems());
		} else {
			repository.put(feedname, feed);
		}
	}

	public Feed getFeed(String feedname) {
		return repository.get(feedname);
	}
	
	public List<Feed> getAllFeeds() {
		return new ArrayList<Feed>(repository.values());
	}

	
}

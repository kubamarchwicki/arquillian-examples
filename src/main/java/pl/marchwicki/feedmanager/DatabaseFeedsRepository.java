package pl.marchwicki.feedmanager;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pl.marchwicki.feedmanager.model.Feed;
import pl.marchwicki.feedmanager.model.entities.FeedEntity;

import com.google.common.collect.Iterables;

@Stateless
@Database
public class DatabaseFeedsRepository implements FeedsRepository {

	@PersistenceContext
	EntityManager em;
	
	public void addItem(String feedname, Feed feed) {
		FeedEntity entity = new FeedEntity.Builder()
			.withFeedname(feedname)
			.withFeed(feed)
			.build();
		
		em.persist(entity);
	}

	public Feed getFeed(String feedname) {
		List<FeedEntity> list = em.createNamedQuery(FeedEntity.GET_FEED_BY_FEEDNAME, FeedEntity.class)
			.setParameter("feedname", feedname)
			.getResultList();		
		
		return Iterables.getOnlyElement(list, null);
	}

	public List<? extends Feed> getAllFeeds() {
		return em.createNamedQuery(FeedEntity.GET_ALL_FEEDS, FeedEntity.class)
				.getResultList();
	}

}

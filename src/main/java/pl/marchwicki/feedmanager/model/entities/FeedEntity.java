package pl.marchwicki.feedmanager.model.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import pl.marchwicki.feedmanager.model.Feed;
import pl.marchwicki.feedmanager.model.Item;

@Entity
@Table(name = "feeds")
@NamedQueries({
	@NamedQuery(name=FeedEntity.GET_ALL_FEEDS, query="from FeedEntity f"),
	@NamedQuery(name=FeedEntity.GET_FEED_BY_FEEDNAME, query="from FeedEntity f where f.feedname = :feedname")
})
public class FeedEntity extends Feed {

	public final static String GET_FEED_BY_FEEDNAME = "FeedEntity.getFeedByFeedname";
	public final static String GET_ALL_FEEDS = "FeedEntity.getAllFeeds";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long feedId;
	private String feedname;

	@OneToMany(mappedBy="feed", cascade=CascadeType.PERSIST)
	private List<ItemEntity> items = new ArrayList<ItemEntity>();
	
	@PostLoad
	public void setupItemsMapping() {
		if (items == null) return;
		ArrayList<Item> nonPersistableItemsCollection = new ArrayList<Item>();
		for (ItemEntity i : items) {
			Item item = new Item.Builder()
				.withTitle(i.getTitle())
				.withLink(i.getLink())
				.withContent(i.getContent())
				.withDate(i.getDate())
				.build();
			
			nonPersistableItemsCollection.add(item);
		}
		
		setItems(nonPersistableItemsCollection);
	}

	@PrePersist
	public void syncItemsMapping() {
		for (Item i : getItems()) {
			if (items.contains(i)) continue;
			
			ItemEntity entity = new ItemEntity(i);
			entity.setFeed(this);
			items.add(entity);			
		}
	}
	
	public Long getFeedId() {
		return feedId;
	}

	public void setFeedId(Long feedId) {
		this.feedId = feedId;
	}

	public String getFeedname() {
		return feedname;
	}

	public void setFeedname(String feedname) {
		this.feedname = feedname;
	}
	
	@Transient
	public List<ItemEntity> getEntityItems() {
		return items;
	}
	
	public FeedEntity() {
	}

	private FeedEntity(Builder builder) {
		this.feedname = builder.feedname;
		setTitle(builder.feed.getTitle());
		setLink(builder.feed.getLink());
		setItems(builder.feed.getItems());
	}

	public static class Builder {
		private String feedname;
		private Feed feed;

		public Builder withFeedname(String feedname) {
			this.feedname = feedname;
			return this;
		}
		
		public Builder withFeed(Feed feed) {
			this.feed = feed;
			return this;
		}


		public FeedEntity build() {
			return new FeedEntity(this);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeedEntity other = (FeedEntity) obj;
		if (feedname == null) {
			if (other.feedname != null)
				return false;
		} else if (!feedname.equals(other.feedname))
			return false;
		return true;
	}


	


}

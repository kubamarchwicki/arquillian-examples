package pl.marchwicki.feedmanager.model.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;

import pl.marchwicki.feedmanager.model.Item;

@Entity
@Table(name = "items")
public class ItemEntity extends Item {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long itemId;

	@ManyToOne
	@JoinColumn(name="feedId")
	private FeedEntity feed;

	public ItemEntity() {
	}
	
	public ItemEntity(Item i) {
		setContent(i.getContent());
		setDate(i.getDate());
		setLink(i.getLink());
		setTitle(i.getTitle());
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	@JsonIgnore
	public FeedEntity getFeed() {
		return feed;
	}

	public void setFeed(FeedEntity feed) {
		this.feed = feed;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return super.toString();
	}
	
	
}

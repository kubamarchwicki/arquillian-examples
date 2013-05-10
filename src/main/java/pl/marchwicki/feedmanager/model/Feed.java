package pl.marchwicki.feedmanager.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@MappedSuperclass
public class Feed {

	@NotNull
	@Size(min=1)
	private String title;
	
	@NotNull
	@Size(min=1)
	private String link;
	
	@Transient
	@Size(min=1)
	private List<Item> items;

	public Feed() {
		
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public void addItem(Item item) {
		if (this.items == null) {
			this.items = new ArrayList<Item>();
		}
		this.items.add(item);
	}
	
	public void addItems(List<Item> items) {
		if (items == null) {
			items = new ArrayList<Item>();
		}
		this.items.addAll(items);
	}	

	private Feed(Builder builder) {
		this.title = builder.title;
		this.link = builder.link;
		this.items = builder.items;
	}

	public static class Builder {
		private String title;
		private String link;
		private List<Item> items;

		public Builder withTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder withLink(String link) {
			this.link = link;
			return this;
		}

		public Builder withItems(List<Item> items) {
			this.items = items;
			return this;
		}

		public Feed build() {
			return new Feed(this);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!getClass().isAssignableFrom(obj.getClass()))
			return false;
		Feed other = (Feed) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	
	
	
	
}

package pl.marchwicki.feedmanager.model;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

@MappedSuperclass
public class Item {

	@NotNull
	@Size(min=1)
	private String title;
	
	@NotNull
	@Size(min=1)
	private String link;
	
	@NotNull
	@Size(min=1)
	private String content;
	
	@Past
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	public Item() {
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	private Item(Builder builder) {
		this.title = builder.title;
		this.link = builder.link;
		this.content = builder.content;
		this.date = builder.date;
	}

	public static class Builder {
		private String title;
		private String link;
		private String content;
		private Date date;

		public Builder withTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder withLink(String link) {
			this.link = link;
			return this;
		}

		public Builder withContent(String content) {
			this.content = content;
			return this;
		}

		public Builder withDate(Date date) {
			this.date = date;
			return this;
		}

		public Item build() {
			return new Item(this);
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
		Item other = (Item) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (date.getTime() != other.date.getTime())
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

	@Override
	public String toString() {
		return "Item [title=" + title + ", link=" + link + ", content="
				+ content + ", date=" + date + "]";
	}
	
	

}

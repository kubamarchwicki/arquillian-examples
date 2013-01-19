package pl.marchwicki.feedmanager.model;

import java.util.Date;

public class Item {

	private String title;
	private String link;
	private String content;
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
}

package pl.marchwicki.feedmanager.log;

import java.util.Date;

public class FeedEventLog {
	
	private String feedname;
	private Integer itemsCount;
	private Date date;
	
	public String getFeedname() {
		return feedname;
	}

	public void setFeedname(String feedname) {
		this.feedname = feedname;
	}

	public Integer getItemsCount() {
		return itemsCount;
	}

	public void setItemsCount(Integer itemsCount) {
		this.itemsCount = itemsCount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	private FeedEventLog(Builder builder) {
		this.feedname = builder.feedname;
		this.itemsCount = builder.itemsCount;
		this.date = builder.date;
	}

	public static class Builder {
		private String feedname;
		private Integer itemsCount;
		private Date date;

		public static Builder forFeed(String feedname) {
			Builder b = new Builder();
			b.feedname = feedname;
			return b;
		}

		public Builder withItemsCount(Integer itemsCount) {
			this.itemsCount = itemsCount;
			return this;
		}

		public Builder withDate(Date date) {
			this.date = date;
			return this;
		}
		
		public Builder now() {
			this.date = new Date();
			return this;
		}

		public FeedEventLog build() {
			return new FeedEventLog(this);
		}
	}

}

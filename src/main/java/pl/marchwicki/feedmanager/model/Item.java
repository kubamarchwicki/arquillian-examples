package pl.marchwicki.feedmanager.model;

import java.util.Date;

import com.sun.syndication.feed.synd.SyndEntryImpl;

public class Item {

	private long id;
	private String title;
	private String link;
	private String content;
	private Date date;
	private Feed feed;

	public Item(SyndEntryImpl romeEntry) {
		this.title = romeEntry.getTitle();
		this.link = romeEntry.getLink();
		this.content = romeEntry.getDescription().getValue();
		this.date = romeEntry.getPublishedDate();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public Feed getFeed() {
		return feed;
	}

	public void setFeed(Feed feed) {
		this.feed = feed;
	}

}

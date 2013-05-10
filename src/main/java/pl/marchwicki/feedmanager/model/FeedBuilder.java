package pl.marchwicki.feedmanager.model;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Named;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

@Named
public class FeedBuilder {

	public Feed fromXml(String xml) {
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed;
		try {
			feed = input.build(new StringReader(xml));
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return fromRomeSyndFeed(feed);
	}

	public Feed fromRomeSyndFeed(SyndFeed romeFeed) {
		Feed f = new Feed.Builder().withTitle(romeFeed.getTitle())
				.withLink(romeFeed.getLink()).withItems(new ArrayList<Item>())
				.build();

		List<?> romeEntries = romeFeed.getEntries();
		for (Object o : romeEntries) {
			SyndEntryImpl romeEntry = (SyndEntryImpl) o;
			Item item = new ItemBuilder()
				.fromRomeSyndEntry(romeEntry)
				.build();
			f.addItem(item);
		}

		return f;
	}

	public static class ItemBuilder {
		private String title;
		private String link;
		private String content;
		private Date date;

		public ItemBuilder fromRomeSyndEntry(SyndEntryImpl romeEntry) {
			this.title = romeEntry.getTitle();
			this.link = romeEntry.getLink();
			this.content = romeEntry.getDescription().getValue();
			this.date = romeEntry.getPublishedDate();
			
			return this;
		}

		public Item build() {
			return new Item.Builder()
				.withTitle(title)
				.withLink(link)
				.withContent(content)
				.withDate(date)
				.build();
		}

	}
}

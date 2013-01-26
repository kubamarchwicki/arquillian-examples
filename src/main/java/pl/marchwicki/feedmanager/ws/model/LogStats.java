package pl.marchwicki.feedmanager.ws.model;

public class LogStats {

	private String feedname;
	private Long itemsCount;

	public LogStats() {
		
	}
	
	public LogStats(String feedname, Long itemsCount) {
		this.feedname = feedname;
		this.itemsCount = itemsCount;
	}

	public String getFeedname() {
		return feedname;
	}

	public void setFeedname(String feedname) {
		this.feedname = feedname;
	}

	public Long getItemsCount() {
		return itemsCount;
	}

	public void setItemsCount(Long itemsCount) {
		this.itemsCount = itemsCount;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogStats other = (LogStats) obj;
		if (feedname == null) {
			if (other.feedname != null)
				return false;
		} else if (!feedname.equals(other.feedname))
			return false;
		if (itemsCount == null) {
			if (other.itemsCount != null)
				return false;
		} else if (!itemsCount.equals(other.itemsCount))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LogStats [feedname=" + feedname + ", itemsCount=" + itemsCount
				+ "]";
	}
	
}

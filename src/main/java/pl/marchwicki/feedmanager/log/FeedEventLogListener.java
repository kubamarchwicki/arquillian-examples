package pl.marchwicki.feedmanager.log;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Named;

@Stateless
public class FeedEventLogListener {

	//TODO: persistence context
	
	@Asynchronous
	public void processLog(@Observes FeedEventLog log) {
		//TODO: persist
	}
	
}

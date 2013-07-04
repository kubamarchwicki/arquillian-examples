package pl.marchwicki.feedmanager.log;

import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class FeedEventLogListener {

	private final static Logger logger = Logger
			.getLogger(FeedEventLogListener.class.getName());

	@PersistenceContext
	EntityManager em;

	@Asynchronous
	public void processLog(@Observes FeedEventLog log) {
		logger.info("New feed notification: " + log.getFeedname() + " " + "("
				+ log.getItemsCount() + " items)");
		em.persist(log);
		em.flush();
	}

	// @Asynchronous
	// public Future<FeedEventLog> processLog(@Observes FeedEventLog log) {
	// em.persist(log);
	// em.flush();
	// em.refresh(log);
	//
	// return new AsyncResult<FeedEventLog>(log);
	// }

}

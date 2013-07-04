package pl.marchwicki.feedmanager.log;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class FeedEventLogListener {

	private final static Logger logger = LoggerFactory.getLogger(FeedEventLogListener.class);
	
	@PersistenceContext
	EntityManager em;
	
	@Asynchronous
	public void processLog(@Observes FeedEventLog log) {
		logger.info("New feed notification: {} ({} items)", log.getFeedname(), log.getItemsCount());
		em.persist(log);
		em.flush();
	}
	
//	@Asynchronous
//	public Future<FeedEventLog> processLog(@Observes FeedEventLog log) {
//		em.persist(log);
//		em.flush();
//		em.refresh(log);
//		
//		return new AsyncResult<FeedEventLog>(log);
//	}
	
}

package pl.marchwicki.feedmanager.log;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class FeedEventLogListener {

	@PersistenceContext
	EntityManager em;
	
	@Asynchronous
	public void processLog(@Observes FeedEventLog log) {
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

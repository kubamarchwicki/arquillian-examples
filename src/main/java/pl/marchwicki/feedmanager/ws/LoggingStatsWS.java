package pl.marchwicki.feedmanager.ws;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import pl.marchwicki.feedmanager.ws.model.LogStats;

@WebService
@Stateless
public class LoggingStatsWS implements LoggingStats {

	@PersistenceContext
	EntityManager em;
	
	public LogStats[] getStats() {
		List<LogStats> statistics = em.createQuery("select " +
				"new pl.marchwicki.feedmanager.ws.model.LogStats(feedname, sum(itemsCount)) " +
				"from FeedEventLog " +
				"group by feedname", LogStats.class)
				.getResultList();
		
		return statistics.toArray(new LogStats[statistics.size()]);
	}
	
}

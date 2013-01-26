package pl.marchwicki.feedmanager.ws;

import javax.jws.WebService;

import pl.marchwicki.feedmanager.ws.model.LogStats;

@WebService
public interface LoggingStats {
	public LogStats[] getStats();
}

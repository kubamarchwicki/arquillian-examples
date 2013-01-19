package pl.marchwicki.feedmanager.rs;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import pl.marchwicki.feedmanager.FeedsService;

@Path("/rs")
@Stateless
public class RestFeedConsumerEndpoint {

	@Inject
	FeedsService service;
	
	@Path("/consume/{feedname}")
	@POST
	public Response consume(@PathParam("feedname") String feedname, String messageBody) {
		service.addNewItems(feedname, messageBody);
		return Response.status(Status.CREATED).build();
	}
	
}

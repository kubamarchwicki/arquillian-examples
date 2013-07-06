package pl.marchwicki.feedmanager.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.marchwicki.feedmanager.FeedsService;
import pl.marchwicki.feedmanager.model.Feed;

@WebServlet(urlPatterns="/web/consume/*")
public class ConsumerServlet extends HttpServlet {

	private static final long serialVersionUID = -3793876752332268424L;

	@EJB
	FeedsService service;

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String rssbody = req.getParameter("rss");
		String feedname = req.getParameter("feedname");

		Feed feed = service.addNewItems(feedname, rssbody);
		
		PrintWriter writer = resp.getWriter();
		writer.append("There are " + feed.getItems().size()
				+ " articles in the feed");
		writer.flush();
		
	}

	
	
}

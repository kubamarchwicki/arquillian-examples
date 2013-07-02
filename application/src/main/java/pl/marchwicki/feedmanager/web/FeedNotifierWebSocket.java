package pl.marchwicki.feedmanager.web;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

import pl.marchwicki.feedmanager.web.sockets.WebsocketInboundProducer;

@WebServlet(urlPatterns = "/ping")
public class FeedNotifierWebSocket extends WebSocketServlet {

	private static final long serialVersionUID = 2123817779584828179L;

	@Inject
	WebsocketInboundProducer producer;
	
//	protected CharBuffer repositoryInfo(String feedname) {
//		logger.info("Connections available: {}", connections.size());
//		Feed feed = repository.getFeed(feedname);
//		StringBuilder builder = new StringBuilder();
//		
//		if (feed != null) {
//			builder.append(feedname).append(" (").append(feed.getItems().size()).append("items)");
//		} else {
//			builder.append("PONG!");
//		}
//		return CharBuffer.wrap(builder.toString());
//	}
	
	@Override
	protected StreamInbound createWebSocketInbound(String arg0,
			HttpServletRequest arg1) {
		return producer.get();
	}

}

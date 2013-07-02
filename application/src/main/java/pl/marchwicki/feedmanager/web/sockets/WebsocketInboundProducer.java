package pl.marchwicki.feedmanager.web.sockets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WsOutbound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.marchwicki.feedmanager.log.FeedEventLog;

import com.google.gson.Gson;

@Singleton
@Startup
@Lock(LockType.READ)
public class WebsocketInboundProducer {

	private final static Logger logger = LoggerFactory
			.getLogger(WebsocketInboundProducer.class);
	private Set<NotificationInbound> connections = new CopyOnWriteArraySet<NotificationInbound>();

	public StreamInbound get() {
		NotificationInbound inbound = new NotificationInbound();
		connections.add(inbound);
		logger.info("Total connections: {}", connections.size());
		return inbound;
	}

	public void eventLogNotifier(@Observes FeedEventLog log) throws IOException {
		logger.info("New feed notification: {} ({} items)", log.getFeedname(), log.getItemsCount());
		String json = new Gson().toJson(log);	
		broadcast(CharBuffer.wrap(json));
	}
	
	private void broadcast(CharBuffer message) throws IOException {
		logger.info("Connections available: {}", connections.size());
		for (NotificationInbound inbound : connections) {
			try {
				inbound.onTextMessage(message);
			} catch (Exception e) {
				connections.remove(inbound);
				logger.info("Removed stalled connection. Connections available: {}", connections.size());
			}
		}
	}
	
	protected static class NotificationInbound extends MessageInbound {

		private WsOutbound outbound;

		@Override
		protected void onOpen(WsOutbound outbound) {
			this.outbound = outbound;
		}

		@Override
		protected void onBinaryMessage(ByteBuffer message) throws IOException {
			outbound.writeBinaryMessage(message);
		}

		@Override
		protected void onTextMessage(CharBuffer message) throws IOException {
			outbound.writeTextMessage(message);
		}
	}

}

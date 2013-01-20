package pl.marchwicki.feedmanager.log;

import java.util.Arrays;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedBodyLoggingInterceptor {

	private static Logger log = LoggerFactory.getLogger(FeedBodyLoggingInterceptor.class);
	
	@AroundInvoke
	public Object log(InvocationContext ctx) throws Exception {
		
		String methodName = ctx.getMethod().getName();
		
		StringBuilder b = new StringBuilder();
		b.append("Processing [").append(methodName).append("] => ");
		b.append(Arrays.asList(ctx.getParameters()));
		log.info(b.toString());
		
		return ctx.proceed();
	}
	
}

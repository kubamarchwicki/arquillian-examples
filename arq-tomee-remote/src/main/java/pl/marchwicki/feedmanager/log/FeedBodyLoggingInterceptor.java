package pl.marchwicki.feedmanager.log;

import java.util.Arrays;
import java.util.logging.Logger;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class FeedBodyLoggingInterceptor {

	private static Logger log = Logger.getLogger(FeedBodyLoggingInterceptor.class.getName());
	
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

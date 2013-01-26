package pl.marchwicki.feedmanager;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Qualifier
@Target(value={ElementType.TYPE, ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
public @interface Repository 
{
	public enum RepositoryType { IN_MEMORY, DATABASE }
	RepositoryType value();
	
}
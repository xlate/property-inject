package io.xlate.inject;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;

@ApplicationScoped
@Default
public class TestFileProvider implements PropertyFileProvider {

	@Override
	public String getLocation() {
		return "classpath:global.properties";
	}
	
}
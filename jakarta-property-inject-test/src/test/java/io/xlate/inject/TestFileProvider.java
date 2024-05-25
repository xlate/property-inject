package io.xlate.inject;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;

@ApplicationScoped
@Default
public class TestFileProvider implements PropertyFileProvider {

	@Override
	public String getLocation() {
		return "classpath:global.properties";
	}
	
}
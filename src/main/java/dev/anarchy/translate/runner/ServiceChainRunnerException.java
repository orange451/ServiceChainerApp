package dev.anarchy.translate.runner;

import dev.anarchy.common.DRouteElementI;

public class ServiceChainRunnerException extends Exception {
	private static final long serialVersionUID = 1L;
	private final Exception originalException;
	private final DRouteElementI element;
	
	public ServiceChainRunnerException(Exception originalException, DRouteElementI currentElement) {
		this.originalException = originalException;
		this.element = currentElement;
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		
		b.append(element.getName());
		b.append(" failed with exception:\n");
		b.append(originalException.toString());
		
		return b.toString();
	}
	
	public void printStackTrace() {
		originalException.printStackTrace();
	}
	
	public DRouteElementI getElement() {
		return this.element;
	}
}

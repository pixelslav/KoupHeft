package com.bookSuppliment.server.app.dtos;

public class SessionAttribute {
	
	private final String attributeName;
	private final Object atrributeValue;
	
	public SessionAttribute(String attributeName, Object attributeValue) {
		this.attributeName = attributeName;
		this.atrributeValue = attributeValue;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public Object getAtrributeValue() {
		return atrributeValue;
	}
	
}

package de.applejuicenet.client.gui.components.util;

public class Value {
	private String identifier;
	private String value;
	
	public Value(String identifier, String value){
		this(identifier);
		setValue(value);
	}
	
	public Value(String identifier){
		if (identifier == null){
			throw new RuntimeException("Uengueltiger Identifiert");
		}
		this.identifier = identifier;
		value = "";
	}
	
	public String getIdentifier(){
		return identifier;
	}

	public void setValue(String newValue){
		if (newValue == null){
			value = "";
		}
		else{
			value = newValue;
		}
	}
	
	public String getValue(){
		return value;
	}
}

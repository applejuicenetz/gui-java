package de.applejuicenet.client.fassade.exception;

public class CoreLostException extends RuntimeException{
	
	public CoreLostException(Throwable t){
		super(t);
	}

	public CoreLostException(){
		super();
	}
}

package de.applejuicenet.client.fassade.exception;

public class WrongPasswordException extends RuntimeException{
	
	public WrongPasswordException(){
		super("corepassword invalid");
	}

}

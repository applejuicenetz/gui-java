package de.applejuicenet.client.shared.exception;

/**
 * Created by IntelliJ IDEA.
 * User: KrallT
 * Date: 02.07.2003
 * Time: 14:08:20
 * To change this template use Options | File Templates.
 */
public class NodeAlreadyExistsException extends Exception{
    public NodeAlreadyExistsException(String text){
        super(text);
    }
}

package de.applejuicenet.client.shared;

public class LookAFeel {
    private String name;
    private String className;

    public LookAFeel(String name, String className){
        this.name = name;
        this.className = className;
    }

    public String getName(){
        return name;
    }

    public String getClassName(){
        return className;
    }
}
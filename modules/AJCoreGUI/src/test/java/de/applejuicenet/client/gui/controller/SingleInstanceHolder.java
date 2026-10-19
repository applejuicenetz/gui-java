package de.applejuicenet.client.gui.controller;

import java.nio.file.Path;

/** Child process of SingleInstanceTest: holds the instance lock until it is killed. */
public class SingleInstanceHolder {
    public static void main(String[] args) throws Exception {
        InstanceLock lock = InstanceLock.acquire(Path.of(args[0]));
        System.out.println("locked");
        System.out.flush();
        Thread.sleep(60000);
        lock.close();
    }
}

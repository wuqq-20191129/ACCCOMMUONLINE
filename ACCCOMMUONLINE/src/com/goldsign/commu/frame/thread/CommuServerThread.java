package com.goldsign.commu.frame.thread;

import com.goldsign.commu.frame.server.OnlineServer;
import java.io.IOException;
import java.net.BindException;

/**
 *
 * @author hejj
 */
public class CommuServerThread extends Thread {

    public CommuServerThread() {
    }

    public void run() {
        OnlineServer server = new OnlineServer();
        try {
            server.startSocketListener();
        } catch (BindException e) {

        } catch (IOException e) {

        }

    }

}

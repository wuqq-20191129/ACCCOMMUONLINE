/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.server;

import org.apache.log4j.Logger;

/**
 *
 * @author hejj
 */
public class CommuStopHook extends Thread {

    private OnlineServer server;

    public CommuStopHook(OnlineServer server) {
        this.server = server;
    }

    private static Logger logger = Logger.getLogger(CommuStopHook.class
            .getName());

    public void run() {
        logger.info("Catch communication server stop event!");
        server.stop();
    }

}

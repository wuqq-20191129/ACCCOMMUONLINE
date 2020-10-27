/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.vo;

/**
 * 
 * @author hejj
 */
public class SynchronizedControl {

	private boolean isFinished = false;

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

	public boolean getFinished() {
		return this.isFinished;
	}
}

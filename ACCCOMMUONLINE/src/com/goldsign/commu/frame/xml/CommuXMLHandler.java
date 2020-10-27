/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.xml;

import com.goldsign.commu.frame.util.EncryptionUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;
import java.io.CharArrayWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

/**
 *
 * @author hejj
 */
public class CommuXMLHandler extends DefaultHandler {

    protected final String ENCRYPT_KEY = "GOLDSIGN";
    protected final String NOT_ENCRYPT_PREFIX = "@@";
    private boolean needWriteFile = false;
    private boolean encrypt = false;
    private static final String Config_TAG = "Config";
    private static final String Common_TAG = "Common";
    public static final String FtpPaths_TAG = "FtpPaths";
    private static final String FtpPath_TAG = "FtpPath";
    public static final String Messages_TAG = "Messages";
    private static final String Message_TAG = "Message";
    public static final String STConnectionPool_TAG = "STConnectionPool";
    public static final String OLConnectionPool_TAG = "OLConnectionPool";
    public static final String CommunicationThreadPool_TAG = "CommunicationThreadPool";
    public static final String MaxThreadNumber_TAG = "MaxThreadNumber";
    public static final String ThreadSleepTime_TAG = "ThreadSleepTime";
    public static final String ThreadPriorityAdd_TAG = "ThreadPriorityAdd";
    public static final String MaxSearchNum_TAG = "MaxSearchNum";
    public static final String ConsolePrint_TAG = "ConsolePrint";
    public static final String ThreadBufferCapacity_TAG = "ThreadBufferCapacity";
    public static final String ThreadBufferIncrement_TAG = "ThreadBufferIncrement";
    public static final String PriorityThreadBufferCapacity_TAG = "PriorityThreadBufferCapacity";
    public static final String PriorityThreadBufferIncrement_TAG = "PriorityThreadBufferIncrement";
    public static final String GetMessageFrequency_TAG = "GetMessageFrequency";
    public static final String ReadThreadPriorityAdd_TAG = "ReadThreadPriorityAdd";
    public static final String JmsReconnectCount_TAG = "JmsReconnectCount";
    public static final String JmsExceptionThreadSleepTime_TAG = "JmsExceptionThreadSleepTime";
    public static final String JmsMsghandleSleepTime_TAG = "JmsMsghandleSleepTime";
    public static final String JMSExceptionListenerThread_TAG = "JMSExceptionListenerThread";
    public static final String ConnectionPoolListenerThread_TAG = "ConnectionPoolListenerThread";
    public static final String CPListenerThreadSleepTime_TAG = "CPListenerThreadSleepTime";
    public static final String TestSql_TAG = "TestSql";
    public static final String SqlMsghandleSleepTime_TAG = "SqlMsghandleSleepTime";
    public static final String StartJmsListener_TAG = "StartJmsListener";
    public static final String StartSocketListener_TAG = "StartSocketListener";
    public static final String StartJmsExceptionListener_TAG = "StartJmsExceptionListener";
    public static final String StartConnectionPoolListener_TAG = "StartConnectionPoolListener";
    public static final String MessageQueueThreadSleepTime_TAG = "MessageQueueThreadSleepTime";
    public static final String ConsolePrintFrequency_TAG = "ConsolePrintFrequency";
    public static final String ThreadReleaseResourceWaitCount_TAG = "ThreadReleaseResourceWaitCount";
    private static final String encrypt_ATTR = "encrypt";
    private static final String id_ATTR = "id";
    private static final String value_ATTR = "value";
    private Hashtable config = new Hashtable();
    private Hashtable ftpPaths = new Hashtable();
    private Hashtable dataConnectionPool = new Hashtable();
    private Hashtable onlineConnectionPool = new Hashtable();
    private Hashtable connectionPoolListenerThread = new Hashtable();
    private Hashtable jmsExceptionListenerThread = new Hashtable();
    private Hashtable communicationThreadPool = new Hashtable();
    private Hashtable messages = new Hashtable();
    private CharArrayWriter contents = new CharArrayWriter();
    private boolean inCommon = false;

    private boolean inDataConnectionPool = false;
    private boolean inonlineConnectionPool = false;
    private boolean inCommunicationThreadPool = false;
    private boolean inConnectionPoolListenerThread = false;
    private boolean inJmsExceptionListenerThread = false;
    private String messageId;
    private String messageClass;

    /*
	 * 线程池监控使用

     */
    private Hashtable ThreadPoolMonitor = new Hashtable();
    public static final String ThreadPoolMonitor_TAG = "ThreadPoolMonitor";
    public static final String TPMonitorThreadSleepTime_TAG = "TPMonitorThreadSleepTime";
    public static final String ThreadMsgHandUpMaxNumberAllow_TAG = "ThreadMsgHandUpMaxNumberAllow";
    public static final String ThreadMsgAnalyzeClassPrefix_TAG = "ThreadMsgAnalyzeClassPrefix";
    private boolean inThreadPoolMonitor = false;

    @Override
    public void startElement(String namespaceURI, String localName,
            String qName, Attributes attr) throws SAXException {
        contents.reset();

        if (attr.getValue(encrypt_ATTR) != null) {
            if (attr.getValue(encrypt_ATTR).equals("true")) {
                this.encrypt = true;
            }
        }
        if (localName.equals(Common_TAG)) {
            this.inCommon = true;
        }
        if (localName.equals(FtpPath_TAG)) {
            ftpPaths.put(attr.getValue(id_ATTR), attr.getValue(value_ATTR));
        }
        if (localName.equals(Message_TAG)) {
            messages.put(attr.getValue(id_ATTR), attr.getValue(value_ATTR));
        }

        if (localName.equals(CommuXMLHandler.STConnectionPool_TAG)) {
            this.inDataConnectionPool = true;
        }
        if (localName.equals(CommuXMLHandler.OLConnectionPool_TAG)) {
            this.inonlineConnectionPool = true;
        }
        if (localName.equals(CommuXMLHandler.CommunicationThreadPool_TAG)) {
            this.inCommunicationThreadPool = true;
        }
        if (localName.equals(CommuXMLHandler.ConnectionPoolListenerThread_TAG)) {
            this.inConnectionPoolListenerThread = true;
        }

        if (localName.equals(CommuXMLHandler.JMSExceptionListenerThread_TAG)) {
            this.inJmsExceptionListenerThread = true;
        }
        // 线程池监控

        if (localName.equals(CommuXMLHandler.ThreadPoolMonitor_TAG)) {
            this.inThreadPoolMonitor = true;
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        String value;

        value = contents.toString().trim();

        if (inCommon) {
            if (localName.equals(Common_TAG)) {
                inCommon = false;
            } else {
                if (encrypt) {
                    config.put(localName, getUserIdPwd(value));
                    encrypt = false;
                } else {
                    config.put(localName, value);
                }
            }
        }

        if (this.inDataConnectionPool) {
            if (localName.equals(CommuXMLHandler.STConnectionPool_TAG)) {
                this.inDataConnectionPool = false;
            } else {
                if (encrypt) {
                    this.dataConnectionPool.put(localName, getUserIdPwd(value));
                    encrypt = false;
                } else {
                    this.dataConnectionPool.put(localName, value);
                }
            }
        }

        if (this.inonlineConnectionPool) {
            if (localName.equals(CommuXMLHandler.OLConnectionPool_TAG)) {
                this.inonlineConnectionPool = false;
            } else {
                if (encrypt) {
                    this.onlineConnectionPool.put(localName,
                            getUserIdPwd(value));
                    encrypt = false;
                } else {
                    this.onlineConnectionPool.put(localName, value);
                }
            }
        }

        if (this.inCommunicationThreadPool) {
            if (localName.equals(CommuXMLHandler.CommunicationThreadPool_TAG)) {
                this.inCommunicationThreadPool = false;
            } else {
                this.communicationThreadPool.put(localName, value);
            }
        }

        if (this.inConnectionPoolListenerThread) {
            if (localName
                    .equals(CommuXMLHandler.ConnectionPoolListenerThread_TAG)) {
                this.inConnectionPoolListenerThread = false;
            } else {
                this.connectionPoolListenerThread.put(localName, value);
            }
        }

        if (this.inJmsExceptionListenerThread) {
            if (localName
                    .equals(CommuXMLHandler.JMSExceptionListenerThread_TAG)) {
                this.inJmsExceptionListenerThread = false;
            } else {
                this.jmsExceptionListenerThread.put(localName, value);
            }
        }
        // 线程池监控

        if (this.inThreadPoolMonitor) {
            if (localName.equals(CommuXMLHandler.ThreadPoolMonitor_TAG)) {
                this.inThreadPoolMonitor = false;
            } else {
                this.ThreadPoolMonitor.put(localName, value);
            }

        }

        if (localName.equals(Config_TAG)) {
            config.put(FtpPaths_TAG, ftpPaths);
            config.put(Messages_TAG, messages);

            config.put(CommuXMLHandler.STConnectionPool_TAG,
                    this.dataConnectionPool);
            config.put(CommuXMLHandler.OLConnectionPool_TAG,
                    this.onlineConnectionPool);
            config.put(CommuXMLHandler.CommunicationThreadPool_TAG,
                    this.communicationThreadPool);
            config.put(CommuXMLHandler.ConnectionPoolListenerThread_TAG,
                    this.connectionPoolListenerThread);
            config.put(CommuXMLHandler.JMSExceptionListenerThread_TAG,
                    this.jmsExceptionListenerThread);
            // 线程池监控
            config.put(CommuXMLHandler.ThreadPoolMonitor_TAG,
                    this.ThreadPoolMonitor);
        }

    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        contents.write(ch, start, length);
    }

    public Hashtable parseConfigFile(String aFileName) throws SAXException,
            FileNotFoundException, IOException {
        XMLReader xr;

        xr = XMLReaderFactory
                .createXMLReader("org.apache.xerces.parsers.SAXParser");
        xr.setContentHandler(this);
        xr.parse(new InputSource(new FileReader(aFileName)));

        // Save the encrypted pasword to the config file
        if (needWriteFile) {
            encryptUserPwdInFile(aFileName);
        }

        return config;
    }

    protected void encryptUserPwdInFile(String aFileName)
            throws FileNotFoundException, IOException {
        int pos, posEnd, posCom1, posCom2;
        String value;
        StringBuilder fileContent = new StringBuilder();
        EncryptionUtil encryption = new EncryptionUtil();

        // Read the config file
        BufferedReader br = new BufferedReader(new FileReader(aFileName));
        String line = br.readLine();
        while (line != null) {
            // Skip the comment line
            posCom1 = line.indexOf("<!--");
            posCom2 = line.indexOf("-->");

            pos = line.indexOf(NOT_ENCRYPT_PREFIX);
            // Encrypt the userId or password
            if (pos != -1 && posCom1 == -1 && posCom2 == -1) {
                StringBuilder lineBuf = new StringBuilder(line);

                // Search the end position
                posEnd = line.indexOf("<", pos);

                // Encrypt and replace
                value = line.substring(pos + NOT_ENCRYPT_PREFIX.length(),
                        posEnd).trim();
                value = encryption.biEncrypt(ENCRYPT_KEY, value);
                line = lineBuf.replace(pos, posEnd, value).toString();

            }

            fileContent.append("\r\n").append(line);

            line = br.readLine();
        }

        // Write new content to the config file
        FileWriter fw = new FileWriter(aFileName);
        fw.write(fileContent.toString().substring(2));
        fw.flush();
        fw.close();
    }

    protected String getUserIdPwd(String aUserIdPwd) {
        String result;
        EncryptionUtil encryption = new EncryptionUtil();

        if (aUserIdPwd.startsWith(NOT_ENCRYPT_PREFIX)) {
            result = aUserIdPwd.substring(NOT_ENCRYPT_PREFIX.length());
            needWriteFile = true;
        } else {
            result = encryption.biDecrypt(ENCRYPT_KEY, aUserIdPwd);
        }

        return result;
    }
}

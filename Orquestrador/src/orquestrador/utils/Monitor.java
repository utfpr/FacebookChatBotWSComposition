/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orquestrador.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author marcelo
 */
public class Monitor {

    private Timer timer;
    private File file;
    private String md5;
    private String url;
    private Collection<FileListener> fileListeners;
    private Collection<TimerListener> timerListeners;

    public Monitor() {
        timer = new Timer();
        fileListeners = new ArrayList<>();
        timerListeners = new ArrayList<>();
    }

    public Monitor(String url) {
        this();
        this.url = url;
    }

    public Monitor(File file) {
        this();
        this.file = file;
        this.md5 = "";
    }

    public void fireFileMonitor(int miliseconds) {
        timer.schedule(new ReminderTask(), miliseconds, miliseconds);

    }

    public void fireTimer(int miliseconds) {
        timer.schedule(new TempTimerTask(), miliseconds, miliseconds);
    }

    public void addFileListener(FileListener fl) {
        if (!fileListeners.contains(fl)) {
            fileListeners.add(fl);
        }
    }

    public void removeFileListener(FileListener fl) {
        fileListeners.remove(fl);
    }

    public void addTimerListener(TimerListener tl) {
        if (!timerListeners.contains(tl)) {
            timerListeners.add(tl);
        }
    }

    public void removeFileListener(TimerListener tl) {
        timerListeners.remove(tl);
    }

    private void fireChangeFile(File file) {
        for (FileListener fl : fileListeners) {
            fl.fileChanged(file);
        }
    }

    private void fireTimerGo(String url) {
//        if(disponivel(url)){
            for (TimerListener tl : timerListeners) {
                tl.go(url);
            }
//        }
    }

    class TempTimerTask extends TimerTask {

        @Override
        public void run() {
            fireTimerGo(url);
        }
    }

    class ReminderTask extends TimerTask {
        @Override
        public void run() {
            String md5now = getMD5FromFile(file);
            if (!md5.equals(md5now)) {
                B.log(md5.isEmpty()
                        ? "Extraindo hash do arquivo {0}\n{2} "
                        : "Arquivo {0} foi alterado em {1}",
                        file.getName(),
                        new Date(),
                        md5now);
                md5 = md5now;
                fireChangeFile(file);
            }
        }
    }

    public static void main(String[] args) {
        boolean res = new Monitor().disponivel("http://localhost:8080/axis2/services/WebServiceReservaSala?wsdl");
        System.out.println(res);
    }

//    private Object invokeWS(String url, String metodo, Object... args) throws Exception {
//        Service service = new Service();
//        Call call = (Call) service.createCall();
//        call.setTargetEndpointAddress(new URL(url));
//        call.setOperationName(new QName("http://marcelo.tcc.tsi.cm.utfpr.edu.br", metodo));
//
//        return call.invoke(args);
//    }
    private boolean disponivel(String endereco) {
        try {
            URL u = new URL(endereco);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            int cod = conn.getResponseCode();
            return cod == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            return false;
        }
    }

    private String getMD5FromFile(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                byte[] buffer = new byte[8192];
                int read;
                while ((read = is.read(buffer)) > 0) {
                    digest.update(buffer, read, read);
                }

                byte[] md5sum = digest.digest();
                BigInteger bi = new BigInteger(1, md5sum);

                String out = bi.toString(16);
                return out;
            } catch (NoSuchAlgorithmException | IOException e) {
                return "";
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException ex) {
                }
            }
        } catch (FileNotFoundException ex) {
            return ex.getMessage();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    public void cancel() {
        timer.cancel();
    }
}

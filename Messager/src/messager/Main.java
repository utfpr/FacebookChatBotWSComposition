/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package messager;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Scanner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import noNamespace.BotschemaDocument;
import noNamespace.BotschemaType;
import noNamespace.RequisicaoType;
import org.apache.axis.client.*;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

/**
 *
 * @author Marcelo
 */
public class Main {

    public static final String URL_ORQUESTRADOR = "http://localhost:8080/axis2/services/Orquestrador?wsdl";
    private ConnectionConfiguration config;
    private XMPPConnection connection;
    private ExecutorService executor;

    public Main() {
        initThread();
    }

    public static void main(String[] args) {
        new Main().initThread();
    }

    private void initThread() {
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new MMessageListener());
    }

    private Object invokeWS(String url, String metodo, Object... args) throws Exception {
        Service service = new Service();
        Call call = (Call) service.createCall();
        call.setTargetEndpointAddress(new URL(url));
        call.setOperationName(new QName("http://orquestrador", metodo));

        return call.invoke(args);
    }

    private class MMessageListener implements Runnable, MessageListener {

        public static final String FB_XMPP_HOST = "chat.facebook.com";
        public static final int FB_XMPP_PORT = 5222;
//        public static final int FB_XMPP_PORT = 80;

        @Override
        public void run() {
            try {
                config = new ConnectionConfiguration(FB_XMPP_HOST, FB_XMPP_PORT);
                SASLAuthentication.registerSASLMechanism("DIGEST-MD5", CustomSASLDigestMD5Mechanism.class);
                config.setSASLAuthenticationEnabled(true);
                config.setDebuggerEnabled(false);
                connection = new XMPPConnection(config);

                if (connection.isConnected()) {
//                    connection.disconnect();
                }

                connection.connect();

                log("Conexão iniciada com o id: {0}", connection.getConnectionID());

                if (connection.isConnected()) {
//                    String username = "4499198063";
//                    String password = "Escola1979";
                    String username = "bot.utfpr";
                    String password = "mls1234";

                    try {
                        connection.login(username, password);
                        log("Logado com sucesso: {0}", connection.getUser());

                        final Roster roster = connection.getRoster();
                        roster.reload();

                        log("Lista de amigos:");
                        for (RosterEntry re : roster.getEntries()) {
                            log("{0}", re.getName());
                        }

                        ChatManager chatManager = connection.getChatManager();

                        chatManager.addChatListener(new ChatManagerListener() {
                            @Override
                            public void chatCreated(Chat chat, boolean bln) {
                                chat.addMessageListener(new MessageListener() {
                                    @Override
                                    public void processMessage(Chat chat, Message msg) {

                                        if (msg.getBody() == null) {
                                            return;
                                        }

                                        BotschemaDocument doc = BotschemaDocument.Factory.newInstance();
                                        BotschemaType schema = doc.addNewBotschema();
                                        schema.setProcess(true);

                                        String email = msg.getFrom();
                                        String nome = roster.getEntry(email).getName();

                                        RequisicaoType rec = schema.addNewRequisicao();
                                        rec.setEmail(email);
                                        rec.setRa(nome);
                                        rec.setConteudo(msg.getBody());

                                        log("{0} diz: {1}", nome, msg.getBody());

                                        try {
                                            Object res = invokeWS(URL_ORQUESTRADOR, "conversa", doc.xmlText());
                                            doc = BotschemaDocument.Factory.parse(res + "");
                                            schema = doc.getBotschema();

                                            String resp = schema.getResposta();
                                            resp = resp.replace("$nome", nome);
                                            resp = resp.replace("$botname", "Bot UTFPR");

                                            if (resp.contains("\n")) {
                                                for (String string : resp.split("\n")) {
                                                    log("Orquestrador diz: {0}", string);
                                                    chat.sendMessage(string);
                                                }
                                            } else {
                                                log("Orquestrador diz: {0}", resp);
                                                chat.sendMessage(resp);
                                            }
                                        } catch (Exception e) {
                                            log(e.getMessage());
                                        }
                                    }
                                });
                            }
                        });
                    } catch (Exception e) {
//                        connection.disconnect();
//                        executor.shutdown();
                    }
                }

            } catch (Exception e) {
//                connection.disconnect();
//                executor.shutdown();
            }
        }

        @Override
        public void processMessage(Chat chat, Message msg) {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private void log(String text, Object... args) {
            String value = MessageFormat.format(text, args);
//            Logger.getAnonymousLogger().log(Level.INFO, value);
            System.out.println(value);
        }
    }
}

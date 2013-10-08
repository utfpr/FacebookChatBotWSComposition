package orquestrador;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import orquestrador.utils.Monitor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import noNamespace.BotschemaDocument;
import noNamespace.BotschemaType;
import noNamespace.PerguntaType;
import noNamespace.PerguntasType;
import noNamespace.ReferenciasType;
import noNamespace.RequisicaoType;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.xmlbeans.XmlException;
import orquestrador.utils.B;
import orquestrador.utils.FileListener;
import orquestrador.utils.Servico;
import orquestrador.utils.TimerListener;

/**
 *
 * @author marcelo
 */
public class Orquestrador implements FileListener {

    private Map<String, List<String>> mapServices;
    private Map<String, String> mapNomeServices;
    private Monitor monitorConf;

    public Orquestrador() {
        this.mapServices = new HashMap<>();
        this.mapNomeServices = new HashMap<>();
        init();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        new Orquestrador().init();
//        new Orquestrador().ehComandoSaida("");
        new Orquestrador().teste();
    }

    private void teste() {
        try {
            BotschemaDocument doc = BotschemaDocument.Factory.newInstance();
            BotschemaType schema = doc.addNewBotschema();

            schema.setProcess(true);

            RequisicaoType rec = schema.addNewRequisicao();
            rec.setEmail("-1311576900@chat.facebook.com");

            Scanner scanner = new Scanner(System.in);

            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL("http://localhost:8080/axis2/services/Orquestrador?wsdl"));
            call.setOperationName(new QName("http://orquestrador", "conversa"));
            
            String res;
            while (true) {
                
                res = scanner.nextLine();
                if ("sair".equalsIgnoreCase(res)) {
                    break;
                }
                
                rec.setConteudo(res);
                
                String resp = (String) call.invoke(new Object[]{doc.xmlText()});
                
                doc = BotschemaDocument.Factory.parse(resp);
                System.out.println(doc.getBotschema().getResposta());
            }

        } catch (ServiceException | MalformedURLException | RemoteException | XmlException ex) {
            Logger.getLogger(Orquestrador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String conversa(String requisicaoXML) {
        try {
            BotschemaDocument doc = BotschemaDocument.Factory.parse(requisicaoXML);
            BotschemaType schema = doc.getBotschema();
            RequisicaoType req = schema.getRequisicao();

            String email = req.getEmail();
            String conteudo = req.getConteudo();

            B.log("[Conversa] {0} diz: {1}", email, conteudo);
            Conversa conversa = Conversa.getInstance();
            PerguntaType per = conversa.getPerguntaDireta(email);


            if (per != null) {
                try {
                    per.setResposta(conteudo);
                    if (per.getResposta().matches(per.getRegex())) {
                        if (per.getResposta().matches("sim|s|afirmativo")) {
                            conversa.removeMapsFor(email);
                            schema.setResposta("solicitação cancelada, se desejar realizar outra solicitação basta pedir!");
                            return doc.xmlText();
                        }
                        if (per.getResposta().matches("n|nao|não|negativo")) {
                            conversa.removePerguntaDireta(email);
                            per = getPerguntaNaoRespondida(conversa.getPerguntas(email));
                            if (per != null) {
                                schema.setResposta("OK vamos continuar então!\n" + per.getQuestao());
                                return doc.xmlText();
                            }
                        }
                    } else {
                        schema.setResposta(per.getMessageErro());
                        return doc.xmlText();
                    }
                } catch (Exception e) {
                    B.error(e);
                }
            }



            if (conversa.existeConversa(email)) {

                for (PerguntaType pt : conversa.getPerguntas(email)) {
                    System.out.println(pt.getQuestao());
                }

                try {
                    if (ehComandoSaida(conteudo)) {
                        per = PerguntaType.Factory.newInstance();
                        per.setQuestao("deseja realmente parar a solicitação? (sim|não)");
                        per.setRegex("sim|s|n|não|nao|afirmativo|negativo");
                        per.setMessageErro("Por favor responda 'Sim' ou 'Não'");

                        conversa.putPerguntaDireta(email, per);
                        schema.setResposta(per.getQuestao());

                        return doc.xmlText();
                    }
                } catch (Exception e) {
                    B.error(e);
                }

                substituiRespostas(conversa.getPerguntas(email), doc);

                B.log("Já existe uma conversa com {0}.", email);
                PerguntaType pergunta = getPerguntaNaoRespondida(conversa.getPerguntas(email));
                if (pergunta == null) {
                    B.log("Não há mais pergunstas. Retornando resposta!");
                    schema.setResposta(getRespostaWS(doc, conversa));
                    schema.setProcess(false);
                    conversa.removeMapsFor(email);
                    return doc.xmlText();
                } else {
                    if ("#reperguntar".equals(conteudo)) {
                        pergunta = getPerguntaNaoRespondida(conversa.getPerguntas(email));
                        if (pergunta != null) {
                            return pergunta.getQuestao();
                        }
                    }

                    if (validaResposta(pergunta, conteudo)) {
                        pergunta = getPerguntaNaoRespondida(conversa.getPerguntas(email));
                        if (pergunta == null) {
                            B.log("Não há mais pergunstas. Retornando resposta!");
                            schema.setResposta(getRespostaWS(doc, conversa));
                            conversa.removeMapsFor(email);
                            schema.setProcess(false);
                            return doc.xmlText();
                        }
                        B.log("Retornando nova pergunta para {0}", email);

                        schema.setResposta(pergunta.getQuestao());
                        return doc.xmlText();
                    } else {
                        schema.setResposta(pergunta.getMessageErro());
                        return doc.xmlText();
                    }
                }
            } else {
                B.log("Tentando localizar um serviço que atenda a solicitação:\n{0}", conteudo);
                try {
                    String wsdl = searchService(conteudo);

                    B.log("Iniciando uma nova conversa entre {0} e o serviço {1}", email, wsdl);
                    schema.addNewPerguntas();
                    Object res = invokeWS(wsdl, "processar", doc.xmlText());

                    doc = BotschemaDocument.Factory.parse(res.toString());
                    PerguntasType perguntas = doc.getBotschema().getPerguntas();

//                    System.out.println(doc);


                    B.log("Conversa entre {0} e {1} iniciada com sucesso!", email, wsdl);
                    conversa.putDialogoResponse(email, wsdl, perguntas.getPerguntaArray());

                    B.log("Formulando primeira questão se houver!", email);
                    PerguntaType pergunta = getPerguntaNaoRespondida(conversa.getPerguntas(email));

                    if (pergunta != null) {
                        B.log("   [Conversa] : {0}", pergunta.getQuestao());
                        doc.getBotschema().setResposta(pergunta.getQuestao());
                        return doc.xmlText();
                    } else {
                        substituiRespostas(conversa.getPerguntas(email), doc);
                        doc.getBotschema().setResposta(getRespostaWS(doc, conversa));
                        doc.getBotschema().setProcess(false);
                        return doc.xmlText();
                    }
                } catch (Exception e) {

                    doc.getBotschema().setResposta(e.getMessage());
                    doc.getBotschema().setProcess(false);
                    conversa.removeMapsFor(email);
                    return doc.xmlText();
                }
            }

//            return schema.xmlText();
        } catch (Exception e) {
            return B.getString("Não foi possível formular uma resposta devido a um erro interno!");
        }
    }

    public String registrarWS(String wsdl) throws MalformedURLException {
        try {
            B.log("Web service {0} iniciou chamada de registro!", wsdl);
            mapServices.put(wsdl, new ArrayList<String>());

            B.log("Coletando referencias...", wsdl);
            updateReferences(wsdl);
            return B.getString("Web service {0} registrado com sucesso!", wsdl);
        } catch (Exception e) {
            return B.getString("Impossivel registrar o web serice {0} motivo:\n{1}", wsdl, e.getMessage());
        }
    }

    private void init() {
        try {
            File conf = new File("servicos.xml");
            if (!conf.exists()) {
                throw new Exception(B.getString("Arquivo de configuração não exite:\n{0}", conf));
            }

            B.log("Monitorando arquivo de configuração {0}", conf.getName());
            monitorConf = new Monitor(conf);
            monitorConf.addFileListener(this);
            monitorConf.fireFileMonitor(2000);
        } catch (Exception e) {
            B.error(e);
        }
    }

    @Override
    public void fileChanged(File file) {
        try {
            Servico s = Servico.load(file);
            String urlOrquestrador = s.getUrlOrquestrador();
            for (String url : s.getWebServices()) {
                tentarHandSheck(urlOrquestrador, url);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Orquestrador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Object invokeWS(String url, String metodo, Object... args) throws Exception {
        Service service = new Service();
        Call call = (Call) service.createCall();
        call.setTargetEndpointAddress(new URL(url));
        call.setOperationName(new QName("http://marcelo.tcc.tsi.cm.utfpr.edu.br", metodo));

        return call.invoke(args);
    }

    private void alteraVariaveis(RequisicaoType requisicao, PerguntaType[] perguntas) {
        for (PerguntaType per : perguntas) {
            String resposta = per.getResposta();

            if ("$email".equals(resposta)) {
                per.setResposta(requisicao.getEmail());
            }
            if ("#\\w+".matches(per.getResposta())) {
                try {
                    String wsdl = getServiceForName(resposta);

                } catch (Exception ex) {
                    Logger.getLogger(Orquestrador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void reloadConversa(BotschemaType schema, String wsdl) {
        RequisicaoType req = schema.getRequisicao();
        Conversa.getInstance().putDialogoResponse(req.getEmail(), wsdl, null);
    }

    private void updateReferences(String url) {
        if (mapServices.containsKey(url)) {
            B.log("Coletando referencias do web service ", url);
            try {
                BotschemaDocument doc = BotschemaDocument.Factory.newInstance();
                BotschemaType schema = doc.addNewBotschema();
                schema.addNewReferencias();
                schema.setProcess(true);

                String resposta = invokeWS(url, "processar", doc.xmlText()).toString();

                try {
                    doc = BotschemaDocument.Factory.parse(resposta);
                } catch (Exception e) {
                    System.out.println(url);
                    System.out.println(resposta);
                }
                ReferenciasType ref = doc.getBotschema().getReferencias();

                System.out.println(doc);

                try {
                    resposta = invokeWS(url, "getNameServiceRef").toString();
                    mapNomeServices.put(url, resposta);
                } catch (Exception e) {
                }

                mapServices.put(url, Arrays.asList(ref.getStringArray()));
                B.log("Referencias coletadas com sucesso!");
                B.log("Referencias de {0}:\n{1}", url, printService(ref.getStringArray()));
            } catch (Exception e) {
                B.error(e);
            }
        }
    }

    private String printService(String[] values) {
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        for (String s : values) {
            sb.append(s).append("|");
        }

        return sb.toString();
    }

    private PerguntaType getPerguntaNaoRespondida(PerguntaType[] perguntas) {
        for (PerguntaType p : perguntas) {
            if (p.getResposta() == null) {
                return p;
            }
        }
        return null;
    }

    private String getServiceForName(String value) throws Exception {

        B.log("Pesquisando pelo serviço: {0}", value);

        for (Map.Entry<String, String> entry : mapNomeServices.entrySet()) {
            String url = entry.getKey();
            String nome = entry.getValue();

            if (value.equalsIgnoreCase(nome)) {
                return url;
            }
        }

        throw new Exception("Serviço não encontrado");
    }

    private String searchService(String value) throws Exception {

        if (value.isEmpty()) {
            throw new Exception("Não há uma solicitação!");
        }

        int similar = 0;
        String server = null;
        B.log("analizando serviços");


        for (Map.Entry<String, List<String>> entry : mapServices.entrySet()) {
            String ser = entry.getKey();
            List<String> list = entry.getValue();

            int con = soundex.Soundex.comparacaoFonetica(value, list);

            B.log("Verificando o serviço {0} que retornou o indice {1} para {2}.", ser, value, con);
            if (con > similar) {
                similar = con;
                server = ser;
            }
        }

        if (similar == 0) {
            throw new Exception("Nenhum web service corresponde a solicitação, seja mais específico.");
        }

        B.log("Indice de similaridade: {0}: serviço escolhido: {1}", similar, server);
        return server;
    }

    private void tentarHandSheck(final String urlOrquestrador, final String url) {
        B.log("Tentando handsheck com {0}", url);
        final Monitor monitor = new Monitor(url);
        monitor.addTimerListener(new TimerListener() {
            boolean advised = false;

            @Override
            public void go(String address) {
                try {
                    URL url = new URL(address);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    int cod = conn.getResponseCode();
                    if (cod != 200) {
                        return;
                    }
                    Object ret = invokeWS(address, "handSheck", urlOrquestrador);
                    B.log("Resposta do serviço {0}:\n{1}", address, ret);
                    mapServices.put(address, null);
                    updateReferences(address);

                    monitor.cancel();
                } catch (MalformedURLException ex) {
                    if (!advised) {
                        B.log("Endereço {0} não responde, arguardando resposta...", address);
                        advised = true;
                    }
                } catch (IOException ex) {
//                    B.error(ex);
                } catch (Exception ex) {
                    B.error(ex);
                }
            }
        });

        monitor.fireTimer(1000);
    }

    private boolean validaResposta(PerguntaType pergunta, String conteudo) {
        try {
            String regex = pergunta.getRegex();

            if (regex != null) {
                if (!regex.isEmpty()) {
                    if (conteudo.matches(regex)) {
                        pergunta.setResposta(conteudo);
                        return true;
                    } else {
                        return false;
                    }
                }
            }

            pergunta.setResposta(conteudo);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    private String getRespostaWS(BotschemaDocument doc, Conversa conversa) {
        BotschemaType schema = doc.getBotschema();
        RequisicaoType req = schema.getRequisicao();
        String email = req.getEmail();
        String wsdl = conversa.getServico(email);

        try {
            schema.setProcess(false);
            schema.addNewPerguntas().setPerguntaArray(conversa.getPerguntas(email));
            Object resp = invokeWS(wsdl, "processar", doc.xmlText());
            if (resp == null) {
                throw new Exception(B.getString("Web service {0} retornou uma resposta nula!", wsdl));
            }

            doc = BotschemaDocument.Factory.parse(resp.toString());
            conversa.removeMapsFor(email);
            return doc.getBotschema().getResposta();
        } catch (Exception e) {
            return e.getMessage();
        }

    }

    private void substituiRespostas(PerguntaType[] perguntas, BotschemaDocument doc) {

        for (PerguntaType pergunta : perguntas) {
            String resposta = pergunta.getResposta();
            if (resposta == null) {
                continue;
            }

            BotschemaType schema = doc.getBotschema();
            RequisicaoType req = schema.getRequisicao();

            try {

                if (resposta.matches("\\#\\w+")) {
                    // #pendencias_biblioteca:$email
//                    Pattern pattern = Pattern.compile("\\#\\w+");
//                    Matcher matcher = pattern.matcher(resposta);
//                    while (matcher.find()) {
//                        String dependencia = matcher.group();

                    try {
                        String wsdl = getServiceForName(resposta.replace("#", ""));
                        String res = respostaLogica(doc, wsdl);
                        resposta = replace(resposta, resposta, res);

                        String regex = pergunta.getRegex();
                        if (regex != null) {
                            if (resposta.trim().matches(regex)) {
//                                   resposta = pergunta.getMessageErro();
                                schema.setProcess(false);
                                schema.setResposta(pergunta.getMessageErro());
//                                   Conversa.getInstance().removeMapsFor(req.getEmail());
                            }
                        }
//                            pergunta.setResposta( + "");
                    } catch (Exception ex) {
                        Logger.getLogger(Orquestrador.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                    }
                }

                resposta = replace(resposta, "$email", req.getEmail());
                resposta = replace(resposta, "$ra", req.getRa());
                resposta = replace(resposta, "$nome", "Fulano");
                pergunta.setResposta(resposta);
            } catch (Exception e) {
                pergunta.setResposta(e.getMessage());
            }

        }
    }

    private String replace(String value, CharSequence oldChar, CharSequence newChar) {
        try {
            return value.replace(oldChar, newChar);
        } catch (Exception e) {
            return value;
        }
    }

    private boolean ehComandoSaida(String conteudo) {
        String[] palavrasSaida = "sair,parar,cancelar".split(",");
        int comp = soundex.Soundex.comparacaoFonetica(conteudo, Arrays.asList(palavrasSaida));
        return comp > 0;
    }

    private BotschemaDocument checaCadastro(BotschemaDocument doc) {
        try {
            String wsdl = getServiceForName("pesquisa_aluno");
            BotschemaType schema = doc.getBotschema();
            schema.setProcess(true);
            schema.addNewPerguntas();
            String email = schema.addNewRequisicao().getEmail();
            String conteudo = schema.addNewRequisicao().getConteudo();

            Object res = invokeWS(wsdl, "processar", doc.xmlText());
            doc = BotschemaDocument.Factory.parse(res.toString());
            schema = doc.getBotschema();

            alteraVariaveis(schema.getRequisicao(), schema.getPerguntas().getPerguntaArray());


            schema.setProcess(false);

            res = invokeWS(wsdl, "processar", doc.xmlText());
            doc = BotschemaDocument.Factory.parse(res.toString());
            schema = doc.getBotschema();

            Boolean resposta = Boolean.parseBoolean(schema.getResposta());

            if (!resposta) {
                wsdl = getServiceForName("dados_aluno");

                doc = BotschemaDocument.Factory.newInstance();
                schema = doc.addNewBotschema();
                RequisicaoType req = schema.addNewRequisicao();
                req.setConteudo("#reperguntar");
                req.setEmail(email);
                schema.setProcess(true);

                schema.addNewPerguntas();

                res = invokeWS(wsdl, "processar", doc.xmlText());
                doc = BotschemaDocument.Factory.parse(res.toString());
                schema = doc.getBotschema();


                Conversa.getInstance().putDialogoResponse(req.getEmail(), wsdl, schema.getPerguntas().getPerguntaArray());

                return doc;

            }


            return doc;
        } catch (Exception e) {
            return doc;
        }

    }

    public String respostaLogica(BotschemaDocument doc, String wsdl) {
        try {
//            String wsdl = getServiceForName(service);


            String res = invokeWS(wsdl, "respostaLogica", doc.xmlText()) + "";
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
    }
}

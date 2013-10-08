package br.edu.utfpr.cm.tsi.tcc.marcelo;

import java.text.MessageFormat;
import java.util.Calendar;
import noNamespace.BotschemaDocument;
import noNamespace.BotschemaType;
import noNamespace.PerguntaType;
import noNamespace.PerguntasType;
import noNamespace.ReferenciasType;

/**
 *
 * @author marcelo
 */
public class WebService extends AbstractWebService {

    public WebService() {
        super();
        init();
    }

    @Override
    public String getNameServiceRef() {
        return "saudacao";
    }

    public static void main(String[] args) {
        new WebService().test();
    }

    private void test() {
        BotschemaDocument doc = BotschemaDocument.Factory.newInstance();
        BotschemaType schema = doc.addNewBotschema();
        PerguntasType perguntas = schema.addNewPerguntas();
//        PerguntaType per = perguntas.addNewPergunta();
//
//        per.setQuestao("email");
//        per.setResposta("email@email.com");

        processaResposta(schema);
        
        System.out.println(doc);
        
    }

    @Override
    public boolean handSheck(String orquestrador) {
        return super.handSheck(orquestrador); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String processar(String requisicao) {
        return super.processar(requisicao);
    }

    @Override
    protected void processaPerguntas(BotschemaType schema) {
        PerguntasType perguntas = schema.getPerguntas();
        if (perguntas != null) {
            if (perguntas.sizeOfPerguntaArray() == 0) {
                PerguntaType per = perguntas.addNewPergunta();
                per.setQuestao("email");
                per.setResposta("$email");
            }
        }
    }

    @Override
    protected void processaReferencias(BotschemaType sType) {
        if (sType.getReferencias() != null) {
            ReferenciasType ref = sType.getReferencias();
//            ref.addString("#notas_por_aluno");
            ref.addString("olá");
            ref.addString("hello");
            ref.addString("ei");
            ref.addString("tudo");
            ref.addString("bem");
            ref.addString("oi");
            ref.addString("bom?");
            ref.addString("amigo");
        }

    }

    @Override
    protected void processaResposta(BotschemaType schema) {
        try {
            PerguntasType perguntas = schema.getPerguntas();
            
            StringBuilder sb = new StringBuilder();
            Calendar cal = Calendar.getInstance();
            int hora = cal.get(Calendar.HOUR_OF_DAY);

            String saudacao ;

            if (hora >= 0 && hora < 12) {
                saudacao = "Bom dia";
            } else if (hora >= 12 && hora < 18) {
                saudacao = "Boa tarde";
            }else{
                saudacao = "Boa noite";
            }

            sb.append(MessageFormat.format("{0}, $nome, eu sou o $botname, e estou aqui para auxilia-lo no seu dia-a-dia:\n"
                    + "Se tiver alguma dúvida basta me perguntar.", saudacao));

            schema.setResposta(sb.toString());

        } catch (Exception e) {
            schema.setResposta(b("Erro interno. {0}", e.getMessage()));
        }
    }

    private void log(String text, Object... args) {
        System.out.println(MessageFormat.format(text, args));
    }

    private void init() {
//        GerarDadosMocs.gerarBancoDados();
    }
}

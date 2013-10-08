package br.edu.utfpr.cm.tsi.tcc.marcelo;

import br.edu.utfpr.cm.tsi.tcc.bd.daos.DaoUsuario;
import br.edu.utfpr.cm.tsi.tcc.marcelo.utils.B;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import noNamespace.BotschemaDocument;
import noNamespace.BotschemaType;
import noNamespace.PerguntaType;
import noNamespace.PerguntasType;
import noNamespace.ReferenciasType;
import noNamespace.RequisicaoType;
import org.apache.xmlbeans.XmlException;

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
        return "pesquisa_aluno";
    }

    public static void main(String[] args) {
        new WebService().test();
    }

    private void test() {
        BotschemaDocument doc = BotschemaDocument.Factory.newInstance();
        BotschemaType schema = doc.addNewBotschema();
        schema.addNewPerguntas();
        processaPerguntas(schema);

        System.out.println(processar(doc.xmlText()));
    }

    @Override
    public boolean handSheck(String orquestrador) {
        return super.handSheck(orquestrador); //To change body of generated methods, choose Tools | Templates.
    }

    
    public boolean pesquisaLogica(String requisicao){
        try {
            BotschemaDocument doc = BotschemaDocument.Factory.parse(requisicao);
            BotschemaType schema = doc.getBotschema();
            RequisicaoType req = schema.getRequisicao();
            
            return true;
        } catch (XmlException ex) {
            Logger.getLogger(WebService.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
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
                formularPergunta(per, "per.email");
            }
        }
    }

    @Override
    protected void processaReferencias(BotschemaType sType) {
        if (sType.getReferencias() != null) {
            ReferenciasType ref = sType.getReferencias();
        }
    }

    @Override
    protected void processaResposta(BotschemaType schema) {
        try {
            PerguntasType perguntas = schema.getPerguntas();
            String email = perguntas.getPerguntaArray(0).getResposta();

            log("Checando se o usuario identificado com {0} está cadastrado", email);

            DaoUsuario dao = new DaoUsuario();
            schema.setResposta(dao.contains(email) + "");
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

    private void formularPergunta(PerguntaType per, String value) {
        String questao = B.getPerString(value);
        String regex = B.getPerString(value.concat(".regex"));
        String erro = B.getPerString(value.concat(".erro"));
        String resposta = B.getPerString(value.concat(".resposta"));

        if (questao != null) {
            per.setQuestao(questao);
        }
        if (regex != null) {
            per.setRegex(regex);
        }
        if (erro != null) {
            per.setMessageErro(erro);
        }
        if (resposta != null) {
            per.setResposta(resposta);
        }

    }
}

package br.edu.utfpr.cm.tsi.tcc.marcelo;

//import br.edu.utfpr.cm.tsi.tcc.marcelo.db.dao.GerarDadosMocs;
import br.edu.utfpr.cm.tsi.tcc.bd.MoscWS;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Disciplina;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Matricula;
import java.text.MessageFormat;
import noNamespace.BotschemaDocument;
import noNamespace.BotschemaType;
import noNamespace.PerguntaType;
import noNamespace.PerguntasType;
import noNamespace.ReferenciasType;
import noNamespace.RequisicaoType;
import org.neodatis.odb.NeoDatis;
import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.Query;
import org.neodatis.odb.core.query.criteria.Criterion;
//import org.neodatis.odb.core.query.criteria.Criterion;
import org.neodatis.odb.core.query.criteria.W;
import resource.B;

/**
 *
 * @author marcelo
 */
public class WebService extends AbstractWebService {

    private String BANCO = "basedados.odb";

    public WebService() {
        super();
        init();
    }

    @Override
    public String getNameServiceRef() {
        return "notas_por_aluno";
    }

    public static void main(String[] args) {
        new WebService().test();
    }

    private void test() {
        BotschemaDocument doc = BotschemaDocument.Factory.newInstance();
        BotschemaType schema = doc.addNewBotschema();
        RequisicaoType req = schema.addNewRequisicao();
        req.setEmail("-1311576900@chat.facebook.com");
        
        PerguntasType perguntas = schema.addNewPerguntas();
        PerguntaType per = perguntas.addNewPergunta();
        per.setResposta("todas");

        per = perguntas.addNewPergunta();
        per.setResposta("-1311576900@chat.facebook.com");
        
        per = perguntas.addNewPergunta();
        per.setResposta("true");
        
        schema.setProcess(true);
        
        processaPerguntas(schema);
        processaResposta(schema);

        System.out.println(schema.getResposta());
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
                formularPergunta(per, "per.disciplina");

                per = perguntas.addNewPergunta();
                formularPergunta(per, "per.email");

                per = perguntas.addNewPergunta();
                formularPergunta(per, "per.pendencia");
            }
        }
    }

    @Override
    protected void processaReferencias(BotschemaType sType) {
        if (sType.getReferencias() != null) {
            ReferenciasType ref = sType.getReferencias();
            ref.addString("#notas_por_aluno");
            ref.addString("quero");
            ref.addString("queria");
            ref.addString("desejo");
            ref.addString("ver");
            ref.addString("verificar");
            ref.addString("minhas");
            ref.addString("minha");
            ref.addString("nota");
            ref.addString("notas");
        }

    }

    @Override
    protected void processaResposta(BotschemaType schema) {
//        ODB odb = null;
        try {

            PerguntasType perguntas = schema.getPerguntas();


            
            String disciplina = perguntas.getPerguntaArray(0).getResposta();
            String email = perguntas.getPerguntaArray(1).getResposta();
            String pendencias = perguntas.getPerguntaArray(2).getResposta();
            

            StringBuilder sb = new StringBuilder();
            Objects<Matricula> matriculas = getObjectBD(Matricula.class, W.equal("usuario.messegerId", email));
            
            if(!matriculas.hasNext()){
                schema.setResposta(B.getString("nao.matriculado"));
                return;
            }
            
            Matricula matricula = matriculas.next();
            
            String resposta = B.getString("titulo.notas");
            
            for (Disciplina d : matricula.getDisciplinas()) {
                if("todas".equalsIgnoreCase(disciplina)){
                    resposta = resposta.concat(B.getString("detalhe.nota", d.getSiglaDisciplina(), d.getNomeDisciplina(), d.getNota()));
                }else{
                    String nome = d.getNomeDisciplina().toUpperCase();
                    String sigla = d.getSiglaDisciplina().toUpperCase();
                    if((nome.contains(disciplina.toUpperCase()))||(sigla.contains(disciplina.toUpperCase()))){
                        resposta = resposta.concat(B.getString("detalhe.nota", d.getSiglaDisciplina(), d.getNomeDisciplina(), d.getNota()));
                    }
                }
            }
            
            
 
            if("true".equals(pendencias)){
                resposta = resposta.concat(B.getString("lembrete.pendencias"));
            }
//            
            schema.setResposta(resposta.replace("TI_", ""));

        } catch (Exception e) {
            e.printStackTrace();
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
     
     private <A extends Object> Objects<A> getObjectBD(Class<A> type, Criterion c) {
        return getObjectBD(type, c, null);
    }
    
    private <A extends Object> Objects<A> getObjectBD(Class<A> type, Criterion c, String dia) {
        ODB odb = null;
        try {
            odb = NeoDatis.open(MoscWS.BASE_DADOS);
            Query query = odb.query(type, c);
            
            if(dia!=null){
                query.orderByAsc(dia);
            }
            Objects objects = query.objects();
            return objects;
        } finally {
            if (odb != null) {
                odb.close();
            }
        }
    }
     
    
}

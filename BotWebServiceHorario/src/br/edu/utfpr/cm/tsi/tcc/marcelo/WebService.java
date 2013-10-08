package br.edu.utfpr.cm.tsi.tcc.marcelo;

import br.edu.utfpr.cm.tsi.tcc.bd.MoscWS;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Disciplina;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Grade;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Matricula;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.xml.namespace.QName;
import noNamespace.BotschemaDocument;
import noNamespace.BotschemaType;
import noNamespace.PerguntaType;
import noNamespace.PerguntasType;
import noNamespace.ReferenciasType;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Weeks;
import org.neodatis.odb.NeoDatis;
import org.neodatis.odb.ODB;
import org.neodatis.odb.Objects;
import org.neodatis.odb.Query;
import org.neodatis.odb.core.query.criteria.Criterion;
import org.neodatis.odb.core.query.criteria.W;
import resource.B;

/**
 *
 * @author marcelo
 */
public class WebService extends AbstractWebService {

    private String BD_FILE = "basedados.odb";

    public WebService() {
        super();
//        init();
    }

    @Override
    public String getNameServiceRef() {
        return "horario";
    }

    public static void main(String[] args) {
        new WebService().test();
    }

    private void test() {
        BotschemaDocument doc = BotschemaDocument.Factory.newInstance();
        BotschemaType schema = doc.addNewBotschema();
        PerguntasType perguntas = schema.addNewPerguntas();
        PerguntaType per = perguntas.addNewPergunta();
        per.setResposta("-1311576900@chat.facebook.com");
        
        per = perguntas.addNewPergunta();
        per.setResposta("true");
        
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
                formularPergunta(per, "per.user");
//                
                per = perguntas.addNewPergunta();
                formularPergunta(per, "per.pendencia");
                
//                
//                per = perguntas.addNewPergunta();
//                formularPergunta(per, "per.ver.hoje");

            }
        }
    }

    @Override
    protected void processaReferencias(BotschemaType sType) {
        if (sType.getReferencias() != null) {
            ReferenciasType ref = sType.getReferencias();
            ref.addString("ver");
            ref.addString("meus");
            ref.addString("horarios");
            ref.addString("aulas");
            ref.addString("minhas");
        }

    }

    @Override
    protected void processaResposta(BotschemaType schema) {
        try {
            PerguntasType perguntas = schema.getPerguntas();
            String email = perguntas.getPerguntaArray(0).getResposta();
            Criterion c ;
//            Usuario usuario = (Usuario) getObjectBD(Usuario.class, c).next();
            c = W.equal("usuario.messegerId", email);
            Objects matriculas = getObjectBD(Matricula.class, c);
            
            if(!matriculas.hasNext()){
                schema.setResposta(B.getString("nao.ha.matricula"));
                return;
            }
            
            Matricula m = (Matricula) matriculas.next();
            
            
            
            String resposta = B.getString("resposta");
            for (Disciplina dis : m.getDisciplinas()) {
                c = W.equal("disciplina.siglaDisciplina", dis.getSiglaDisciplina());
               
                Objects<Grade> grades = getObjectBD(Grade.class, c, "dia");
                String aulas = "";
                while (grades.hasNext()) {
                    Grade grade = grades.next();
                    aulas = aulas.concat(grade.getPeriodo().getSigla()).concat(grades.hasNext()?",":"");
                }
                
                resposta = resposta.concat(B.getString("disciplina",
                        dis.getSiglaDisciplina(),
                        dis.getNomeDisciplina().replace("TI_", ""),
                        aulas
                        ));
                
            }
            
            String pendencia = perguntas.getPerguntaArray(1).getResposta();
            if("true".equalsIgnoreCase(pendencia)){
                resposta = resposta.concat(B.getString("lembrar.pendencia"));
            }
            schema.setResposta(resposta);
        } catch (Exception e) {
            schema.setResposta(b("Erro interno. {0}", e.getMessage()));
        }
    }

    public String respostaLogica(String docxml) {
        return "NI";
    }

    private void log(String text, Object... args) {
        System.out.println(MessageFormat.format(text, args));
    }
    
    private String toDay(String sigla){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        if(Grade.SEG.equals(sigla)){
            cal.set(Calendar.DAY_OF_WEEK, 2);
            return sdf.format(cal.getTime());
        }
        if(Grade.TER.equals(sigla)){
            cal.set(Calendar.DAY_OF_WEEK, 3);
            return sdf.format(cal.getTime());
        }
        if(Grade.QUA.equals(sigla)){
            cal.set(Calendar.DAY_OF_WEEK, 4);
            return sdf.format(cal.getTime());
        }
        if(Grade.QUI.equals(sigla)){
            cal.set(Calendar.DAY_OF_WEEK, 5);
            return sdf.format(cal.getTime());
        }
        if(Grade.SEX.equals(sigla)){
            cal.set(Calendar.DAY_OF_WEEK, 6);
            return sdf.format(cal.getTime());
        }
        if(Grade.SAB.equals(sigla)){
            cal.set(Calendar.DAY_OF_WEEK, 7);
            return sdf.format(cal.getTime());
        }
        if(Grade.DOM.equals(sigla)){
            cal.set(Calendar.DAY_OF_WEEK, 1);
            return sdf.format(cal.getTime());
        }
        return null;
    }

    private Objects getObjectBD(Class type, Criterion c, String dia) {
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
    
    private Objects getObjectBD(Class type, Criterion c) {
        return getObjectBD(type, c, null);
    }
    private Query getQuery(Class type, Criterion c) {
        ODB odb = null;
        try {
            odb = NeoDatis.open(MoscWS.BASE_DADOS);
            Query query = odb.query(type, c);
            return query;
        } finally {
            if (odb != null) {
                odb.close();
            }
        }
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

package br.edu.utfpr.cm.tsi.tcc.marcelo;

import br.edu.utfpr.cm.tsi.tcc.bd.MoscWS;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Grade;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Reserva;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Sala;
import br.edu.utfpr.cm.tsi.tcc.marcelo.utils.DateUtils;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import noNamespace.BotschemaDocument;
import noNamespace.BotschemaType;
import noNamespace.PerguntaType;
import noNamespace.PerguntasType;
import noNamespace.ReferenciasType;
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
        per.setResposta("21");
        
        per = perguntas.addNewPergunta();
        per.setResposta("14:00");
        
        per = perguntas.addNewPergunta();
        per.setResposta("B");
        
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
                formularPergunta(per, "per.data");
                
                per = perguntas.addNewPergunta();
                formularPergunta(per, "per.hora");
                
                per = perguntas.addNewPergunta();
                formularPergunta(per, "per.bloco");

            }
        }
    }

    @Override
    protected void processaReferencias(BotschemaType sType) {
        if (sType.getReferencias() != null) {
            ReferenciasType ref = sType.getReferencias();
            ref.addString("ver");
            ref.addString("salas");
            ref.addString("vagas");
            ref.addString("disponiveis");
            ref.addString("consultar");
            ref.addString("mostrar");
        }

    }

    @Override
    protected void processaResposta(BotschemaType schema) {
        try {
            PerguntasType perguntas = schema.getPerguntas();
            String user = perguntas.getPerguntaArray(0).getResposta();
            String data = perguntas.getPerguntaArray(1).getResposta();
            String hora = perguntas.getPerguntaArray(2).getResposta();
            String sigla = perguntas.getPerguntaArray(3).getResposta();
            
//            if(!data.matches(B.getString("per.data.regex"))){
//            }
//            
            
//            if(!DateUtils.validaData(data)){
//                schema.setResposta(B.getString("formato.data.invalido", data));
//                return;
//            }
            
            System.out.println(sigla.matches(B.getString("per.bloco.regex")));
            
            
            if(!hora.matches(B.getString("per.hora.regex"))){
                schema.setResposta(B.getString("formato.hora.invalido", hora));
                return;
            }
            
            Date date = DateUtils.getDate(data);
            hora = hora.replace(":", "");
            hora = hora.replace(".", "");
            
            SimpleDateFormat sdf = new SimpleDateFormat(hora.length()==3?"ddMMyyHmm":"ddMMyyHHmm");
            date = sdf.parse(DateUtils.dateToString(date, "ddMMyy")+hora);
            
            if(date.before(new Date())){
                schema.setResposta(B.getString("data.anterior.atual", date, new Date()));
                return;
            }
            Criterion c = W.ilike("sigla", sigla.toUpperCase())
                    .and(W.isNotNull("nome"));
            
            Objects<Sala> salas = getObjectBD(Sala.class, c);
            String resposta = B.getString("resposta", sigla.toUpperCase(), date);
            
            if(!salas.hasNext()){
                schema.setResposta(B.getString("nao.ha.salas", sigla));
                return;
            }
            
            while (salas.hasNext()) {
                Sala sala = salas.next();
                c = W.equal("sala.sigla", sala.getSigla())
                        .and(W.equal("cancelada", false));
                Objects<Reserva> reservas = getObjectBD(Reserva.class, c);
                resposta = resposta.concat(B.getString("salas", 
                        sala.getNome(),
                        reservas.hasNext()?B.getString("nao.disponivel"):B.getString("disponivel")));
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
    
//    private Query getQuery(Class type, Criterion c) {
//        ODB odb = null;
//        try {
//            odb = NeoDatis.open(MoscWS.BASE_DADOS);
//            Query query = odb.query(type, c);
//            return query;
//        } finally {
//            if (odb != null) {
//                odb.close();
//            }
//        }
//    }

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

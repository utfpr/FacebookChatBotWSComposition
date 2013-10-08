package br.edu.utfpr.cm.tsi.tcc.marcelo;

import br.edu.utfpr.cm.tsi.tcc.bd.MoscWS;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Reserva;
import java.io.File;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.neodatis.odb.core.query.criteria.W;
import resource.B;

public class WebService extends AbstractWebService {

    public WebService() {
        super();
        init();
    }

    @Override
    public String getNameServiceRef() {
        return "cancelarReservarSala";
    }

    public static void main(String[] args) {
        new WebService().test();
    }

    private void test() {
        String sfile = "salas.xml";

        File file = new File(sfile);
        try {
            BotschemaDocument doc = BotschemaDocument.Factory.newInstance();
            BotschemaType schema = doc.addNewBotschema();
            schema.addNewRequisicao().setEmail("-100001752061895@chat.facebook.com");
            PerguntasType perguntas = schema.addNewPerguntas();
            processaPerguntas(schema);

            Scanner scanner = new Scanner(System.in);
            String value;
//            while ("sair".equalsIgnoreCase(value)) {                
            for (PerguntaType pt : schema.getPerguntas().getPerguntaArray()) {
                System.out.println(pt.getQuestao());
                value = scanner.nextLine();
                pt.setResposta(value);
            }
            processaResposta(schema);

            System.out.println(doc);

        } catch (Exception ex) {
            Logger.getLogger(WebService.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        ODB odb = NeoDatis.open(MoscWS.BASE_DADOS);

        try {
            PerguntasType perguntas = schema.getPerguntas();
            if (perguntas != null) {
                if (perguntas.sizeOfPerguntaArray() == 0) {
                    PerguntaType per = perguntas.addNewPergunta();
                    formularPergunta(per, "per.user");
                    RequisicaoType req = schema.getRequisicao();

                    String email = req.getEmail();

                    Criterion criterion = W.equal("usuario.messegerId", email);

                    if (!existe(odb, Reserva.class, criterion)) {
                        return;
                    }
                    
                    Query query = odb.query(Reserva.class, criterion);
                    Objects<Reserva> reservas = query.objects();

                    StringBuilder sb = new StringBuilder();
                    int i = 1;
                    while (reservas.hasNext()) {
                        Reserva reserva = reservas.next();
                        sb.append(B.getString("reserva", 
                                reserva.getSala().getSigla(), 
                                reserva.getProtocolo(), 
                                reserva.getDataReserva(), 
                                reserva.getSala().getNome()));
                    }

                    per = perguntas.addNewPergunta();
                    per.setRegex("\\w\\d\\d\\d");
                    per.setQuestao(B.getString("lista.reservas", sb.toString()));
                    per.setMessageErro(B.getString("valor.fora.indice", reservas.size()));

                    per = perguntas.addNewPergunta();
                    formularPergunta(per, "per.confirma");

                }
            }

        } catch (Exception e) {
        } finally {
            if (odb != null) {
                odb.close();
            }
        }
    }

    @Override
    protected void processaReferencias(BotschemaType sType) {
        if (sType.getReferencias() != null) {
            ReferenciasType ref = sType.getReferencias();
            ref.addString("Cancelar");
            ref.addString("Excluir");
            ref.addString("reserva");
            ref.addString("sala");
            ref.addString("laboratorio");
        }
    }

    @Override
    protected void processaResposta(BotschemaType schema) {
        ODB odb = null;
        try {
            odb = NeoDatis.open(MoscWS.BASE_DADOS);
            PerguntasType perguntas = schema.getPerguntas();
            String email = perguntas.getPerguntaArray(0).getResposta();
            Date now = new Date();
            Criterion c = W.equal("usuario.messegerId", email)
                        .and(W.ge("dataReserva", now));
            
//            System.out.println(schema);
            
            Query query = odb.query(Reserva.class, c);
            Objects<Reserva> reservas = query.objects();
            
            Reserva reserva = null;
            if(reservas.hasNext()){
                String sigla = perguntas.getPerguntaArray(1).getResposta();
                c = W.equal("usuario.messegerId", email)
                        .and(W.ge("dataReserva", now))
                        .and(W.equal("sala.sigla", sigla));
                query = odb.query(Reserva.class, c);
                Objects<Reserva> objects = query.objects();
                if(!objects.hasNext()){
                    throw new Exception(B.getString("erro.sem.reserva.para.sala", sigla));
                }
                
                String confima = perguntas.getPerguntaArray(2).getResposta();
                
                if(confima.toUpperCase().trim().matches("SIM|S")){
                    odb.delete(objects.next());
                    odb.commit();
                    schema.setResposta(B.getString("reserva.cancelada.sucesso"));
                }else{
                    schema.setResposta(B.getString("desistencia.cancelamento"));
                }
            }else{
                throw new Exception(B.getString("nenhuma.reserva.para.usuario", email));
            }
            

        } catch (Exception e) {
            schema.setResposta(e.getMessage());
        } finally {
            if (odb != null) {
                odb.close();
            }
        }
    }

    private void log(String text, Object... args) {
        System.out.println(MessageFormat.format(text, args));
    }

    private void init() {
//        GerarDadosMocs.gerarBancoDados();
    }

    private Object getObjectFromBD(ODB odb, Class classe, Criterion criterion) {
        Query query = odb.query(classe, criterion);
        try {
            if (query.objects().hasNext()) {
                Object o = query.objects().next();
                return o;
            }
            return classe.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean existe(ODB odb, Class classe, Criterion criterion) {
        try {
            Query query = odb.query(classe, criterion);
            return query.objects().hasNext();
        } catch (Exception e) {
            return false;
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

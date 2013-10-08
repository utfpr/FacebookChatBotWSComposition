package br.edu.utfpr.cm.tsi.tcc.marcelo;

import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Reserva;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Sala;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Usuario;
import java.io.File;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import noNamespace.BotschemaDocument;
import noNamespace.BotschemaType;
import noNamespace.PerguntaType;
import noNamespace.PerguntasType;
import noNamespace.ReferenciasType;
import org.neodatis.odb.NeoDatis;
import org.neodatis.odb.ODB;
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
        init();
    }

    @Override
    public String getNameServiceRef() {
        return "reservarSala";
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
            PerguntasType perguntas = schema.addNewPerguntas();
            processaPerguntas(schema);

            Scanner scanner = new Scanner(System.in);
            String value = "";
//            while ("sair".equalsIgnoreCase(value)) {                
            for (PerguntaType pt : schema.getPerguntas().getPerguntaArray()) {
                System.out.println(pt.getQuestao());
                value = scanner.nextLine();
                pt.setResposta(value);
            }
            processaResposta(schema);

            System.out.println(schema.getResposta());

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
        PerguntasType perguntas = schema.getPerguntas();
        if (perguntas != null) {
            if (perguntas.sizeOfPerguntaArray() == 0) {
                PerguntaType per = perguntas.addNewPergunta();
                per.setQuestao("pendencias na biblioteca");
                per.setResposta("#biblioteca");
//                per.setRegex("true");
                per.setMessageErro(B.getString("erro.pendencia.biblioteca"));

                per = perguntas.addNewPergunta();
                per.setQuestao("Qual sala deseja reservar? ex. F102");
                per.setRegex("\\w\\d\\d\\d");
                per.setMessageErro("Informe a sigla da sala corretamente exemplo: F005 ou E103.");

                per = perguntas.addNewPergunta();
                per.setQuestao("Qual data deseja efetuar a reserva? ex. 12/05/2013");
                String datePattern = "^"
                        + "(?:(31)(\\D)(0?[13578]|1[02])\\2|(29|30)(\\D)(0?[13-9]|1[0-2])\\5|(0?[1-9]|1\\d|2[0-8])(\\D)(0?[1-9]|1[0-2])\\8)"
                        + "((?:1[6-9]|[2-9]\\d)?\\d{2})$|"
                        + "^"
                        + "(29)(\\D)(0?2)\\12((?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:16|[2468][048]|[3579][26])00)$";
                per.setRegex(datePattern);
                per.setMessageErro("Informe uma data válida (dd/mm/aaaa). Ex: 18/08/2013");

                per = perguntas.addNewPergunta();
                per.setQuestao("Qual horario da reserva? ex. 16:35");
                per.setRegex("(0[1-9]|1[0-9]|2[0123])([:.])([012345][0-9])");
                per.setMessageErro("Informe uma hora válida (hh:mm). Ex: 20:35");

                per = perguntas.addNewPergunta();
                per.setQuestao("Qual duração da reserva em minutos? ex. 75");
                per.setRegex("\\d+");
                per.setMessageErro("Informe apenas numeros. Ex: 60");

                per = perguntas.addNewPergunta();
                per.setQuestao("email");
                per.setResposta("$email");

            }
        }
    }

    @Override
    protected void processaReferencias(BotschemaType sType) {
        if (sType.getReferencias() != null) {
            ReferenciasType ref = sType.getReferencias();
            ref.addString("reservar");
            ref.addString("sala");
            ref.addString("laboratorio");
            ref.addString("alugar");
        }
    }

    @Override
    protected void processaResposta(BotschemaType schema) {
        try {

            PerguntasType perguntas = schema.getPerguntas();

            String pendencias = perguntas.getPerguntaArray(0).getResposta();
            String sigla = perguntas.getPerguntaArray(1).getResposta();
            String data = perguntas.getPerguntaArray(2).getResposta();
            String hora = perguntas.getPerguntaArray(3).getResposta();
            String duracao = perguntas.getPerguntaArray(4).getResposta();
            String email = perguntas.getPerguntaArray(5).getResposta();

            StringBuilder sb = new StringBuilder();
//            if (pendencias.equals("true")) {
//                sb.append(perguntas.getPerguntaArray(0).getMessageErro());
//                schema.setResposta(sb.toString());
//                return;
//            }

            ODB odb = NeoDatis.open(br.edu.utfpr.cm.tsi.tcc.bd.MoscWS.BASE_DADOS);
            try {
                sigla = sigla.toUpperCase();

                Date date = getDate(data, hora);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                cal.add(Calendar.MINUTE, Integer.parseInt(duracao));
                Date now = new Date();
                if (date.before(now)) {
                    throw new Exception(B.getString("erro.data.anterior.atual", cal.getTime(), now));
                }

                if (!existe(odb, Sala.class, W.equal("sigla", sigla))) {
                    throw new Exception(B.getString("sala.nao.existe", sigla));
                }

                Sala s = (Sala) getObjectFromBD(odb, Sala.class, W.equal("sigla", sigla));

                int d = Integer.parseInt(duracao);
                Criterion criterion =
                        W.equal("sala.sigla", sigla)
                        .and(W.equal("cancelada", false))
                        .and(W.ge("dataReserva", addMinutos(date, (-1 * d))).and(W.le("dataReserva", cal.getTime())));

                if (existe(odb, Reserva.class, criterion)) {
                    throw new Exception(B.getString("sala.nao.vaga", date));
                }

                criterion = W.equal("messegerId", email);
                if (!existe(odb, Usuario.class, criterion)) {
                    throw new Exception(B.getString("usuario.nao.cadastrado", email));
                }

                Usuario usuario = (Usuario) getObjectFromBD(odb, Usuario.class, criterion);
                
                
                Reserva reserva = new Reserva();
                reserva.setDataReserva(date);
                reserva.setDuracao(d);
                reserva.setSala(s);
                reserva.setUsuario(usuario);

                odb.store(reserva);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String horafim = sdf.format(cal.getTime());
                sb.append(B.getString("reserva.realizada.sucesso", s.getNome(), data, hora, horafim, reserva.getProtocolo()));

            } catch (Exception ex) {
                sb.append(ex.getMessage());
            } finally {
                if (odb != null) {
                    odb.close();
                }
            }

            schema.setResposta(sb.toString());

        } catch (Exception e) {
            schema.setResposta(b("Erro interno. {0}", e.getMessage()));
        }
    }

    private Date addMinutos(Date date, int minutos) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutos);
        return cal.getTime();
    }

    private Date getDate(String data, String hora) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyyHH:mm");
        try {
            return sdf.parse(data + hora);
        } catch (ParseException ex) {
            Logger.getLogger(WebService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
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
}

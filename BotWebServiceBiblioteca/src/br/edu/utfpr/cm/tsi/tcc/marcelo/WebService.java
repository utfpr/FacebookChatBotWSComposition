package br.edu.utfpr.cm.tsi.tcc.marcelo;

import br.edu.utfpr.cm.tsi.tcc.bd.MoscWS;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Emprestimo;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Livro;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Usuario;
import java.math.BigInteger;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import javax.xml.namespace.QName;
import noNamespace.BotschemaDocument;
import noNamespace.BotschemaType;
import noNamespace.PerguntaType;
import noNamespace.PerguntasType;
import noNamespace.ReferenciasType;
import noNamespace.RequisicaoType;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.joda.time.DateTime;
import org.joda.time.Days;
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
public class WebService extends AbstractWebService implements WS {

    private String BD_FILE = "basedados.odb";

    public WebService() {
        super();
//        init();
    }

    @Override
    public String getNameServiceRef() {
        return "biblioteca";
    }

    public static void main(String[] args) {
        new WebService().test();
    }

    private void test() {
//        BotschemaDocument doc = BotschemaDocument.Factory.newInstance();
//        BotschemaType schema = doc.addNewBotschema();
//        PerguntasType perguntas = schema.addNewPerguntas();
//        PerguntaType per = perguntas.addNewPergunta();
//        per.setQuestao("Discilplina?");
//        per.setResposta("todas");
//
//        per = perguntas.addNewPergunta();
//        per.setQuestao("email");
//        per.setResposta("email@email.com");
//
//        processaResposta(schema);

//        System.out.println(doc);

        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL("http://localhost:8080/axis2/services/WebServiceDadosUsuarios?wsdl"));
            call.setOperationName(new QName("http://marcelo.tcc.tsi.cm.utfpr.edu.br", "test"));

            System.out.println(call);
            Object ret = call.invoke(new Object[]{});
            System.out.println(ret);
        } catch (Exception e) {
            e.printStackTrace();
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
                per.setQuestao("email");
                per.setQuestao("email");
                per.setResposta("$email");


                Criterion c = W.equal("usuario.messegerId", schema.getRequisicao().getEmail()).
                        and(W.isNull("dataDevolucao"));

                Objects objects =
                        getObjectBD(Emprestimo.class, c);
                if (objects.hasNext()) {
                    per = perguntas.addNewPergunta();
                    StringBuilder sb = new StringBuilder();
                    String resposta = "";
                    resposta = resposta.concat("$nome, você possui os seguintes livros pendentes:\n");
                    sb.append("Livros pendentes:").append("\n");
                    while (objects.hasNext()) {
                        Emprestimo e = (Emprestimo) objects.next();
                        resposta = resposta.concat(B.getString("livro.pendente", e.getLivro().getNome(), e.getDataEmprestimo(), e.getProtocolo()));
//                        sb.append(B.getString("livro.pendente", e.getLivro().getNome(), e.getDataEmprestimo(), e.getProtocolo()));
                    }
                    resposta = resposta.concat("\nDeseja tentar renová-los?\n");
//                    per.setQuestao(B.getString("pendencias.livro.renovar", sb.toString()));
                    per.setQuestao(resposta);
                    per.setRegex("sim|nao|s|n");
                    per.setMessageErro("Responda apenas sim ou não!");
                }
            }

        }
    }

    @Override
    protected void processaReferencias(BotschemaType sType) {
        if (sType.getReferencias() != null) {
            ReferenciasType ref = sType.getReferencias();
            ref.addString("biblioteca");
            ref.addString("livros");
            ref.addString("pendencias");
            ref.addString("livraria");
            ref.addString("emprestimo");
            ref.addString("ver");
            ref.addString("verificar");
        }

    }

    @Override
    protected void processaResposta(BotschemaType schema) {
        ODB odb = null;
        try {

            odb = NeoDatis.open(MoscWS.BASE_DADOS);

            PerguntasType perguntas = schema.getPerguntas();

            String email = perguntas.getPerguntaArray(0).getResposta();
            StringBuilder sb = new StringBuilder();
            Criterion c = W.equal("usuario.messegerId", email).
                    and(W.isNull("dataDevolucao"));

            Objects objects = getObjectBD(Emprestimo.class, c);

            if (!objects.hasNext()) {
                log("Não há pendencias");
                sb.append(B.getString("consulta.sucess"));
            } else {

//                sb.append(B.getString("\n--------------------\n"));
                
                String renova = perguntas.getPerguntaArray(1).getResposta().toUpperCase();
                if (renova.matches("SIM|S")) {
                    objects = getObjectBD(Emprestimo.class, c);
                    while (objects.hasNext()) {
                        Emprestimo e = (Emprestimo) objects.next();
                        int dias = Days.daysBetween(new DateTime(e.getDataCadastro()), new DateTime()).getDays();
                        if (dias < 7) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(e.getDataEmprestimo());
                            e.setDataEmprestimo(cal.getTime());
                            odb.store(e);
                            odb.commit();

                            cal.add(Calendar.DAY_OF_MONTH, 7);
                            sb.append(B.getString("renovacao.concluida.sucesso", e.getLivro().getNome(), cal.getTime()));
                        } else {
                            sb.append(B.getString("renovacao.nao.realizada", e.getLivro().getNome()));
                        }
                    }
                }else{
                    schema.setResposta(B.getString("nao.deseja.renovar"));
                    return;
                }
                schema.setResposta(sb.toString());
                return;
            }
            schema.setResposta(sb.toString());

        } catch (Exception e) {
            schema.setResposta(b("Erro interno. {0}", e.getMessage()));
        } finally {
            if (odb != null) {
                odb.close();
            }
        }
    }

    @Override
    public String respostaLogica(String docxml) {
        ODB odb = null;
        try {
            odb = NeoDatis.open(MoscWS.BASE_DADOS);

            BotschemaDocument doc = BotschemaDocument.Factory.parse(docxml);
            BotschemaType schema = doc.getBotschema();
            RequisicaoType req = schema.getRequisicao();
            String email = req.getEmail();

            log("[CHECANDO] email: {0}", email);

            Query query = odb.query(Emprestimo.class, W.equal("usuario.messegerId", email));
            Objects<Emprestimo> emprestimos = query.objects();
            while (emprestimos.hasNext()) {
                Emprestimo emprestimo = emprestimos.next();
                if (emprestimo.getDataDevolucao() == null) {
                    return "true";
                }
            }

            return "false";
        } catch (Exception e) {
            log("[ERRO] {0}", e.getMessage());
            return false + "";
        } finally {
            if (odb != null) {
                odb.close();
            }
        }

    }

    private void log(String text, Object... args) {
        System.out.println(MessageFormat.format(text, args));
    }

    private Objects getObjectBD(Class type, Criterion c) {
        ODB odb = null;
        try {
            odb = NeoDatis.open(MoscWS.BASE_DADOS);
            Query query = odb.query(type, c);
            Objects objects = query.objects();
            return objects;
        } finally {
            if (odb != null) {
                odb.close();
            }
        }

    }

    private void init() {
        ODB odb = null;
        try {
            odb = NeoDatis.open(MoscWS.BASE_DADOS);
            Query query = odb.query(Usuario.class, W.equal("messegerId", "-1311576900@chat.facebook.com"));
            Usuario usuario = (Usuario) (query.count().equals(BigInteger.ZERO) ? new Usuario() : query.objects().next());
            usuario.setMessegerId("-1311576900@chat.facebook.com");
            odb.store(usuario);
            odb.commit();

            query = odb.query(Usuario.class, W.equal("messegerId", "-100001752061895@chat.facebook.com"));
            usuario = (Usuario) (query.count().equals(BigInteger.ZERO) ? new Usuario() : query.objects().next());
            usuario.setMessegerId("-1311576900@chat.facebook.com");
            odb.store(usuario);
            odb.commit();

            query = odb.query(Usuario.class, W.equal("messegerId", "-1311576900@chat.facebook.com"));
            Usuario marcelo = (Usuario) query.objects().next();
            Emprestimo emprestimo = new Emprestimo();

            Livro livro = new Livro();
            livro.setAutor("Fulano");
            livro.setIsbn("3923927392739");
            livro.setNome("Dia de furia");

            emprestimo.setLivro(livro);
            emprestimo.setUsuario(marcelo);
            emprestimo.setDataEmprestimo(new Date());

//            odb.store(emprestimo);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (odb != null) {
                odb.close();
            }
        }
    }
}

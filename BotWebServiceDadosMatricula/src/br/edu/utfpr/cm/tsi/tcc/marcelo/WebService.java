package br.edu.utfpr.cm.tsi.tcc.marcelo;

import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Usuario;
import java.math.BigInteger;
import java.text.MessageFormat;
import noNamespace.BotschemaType;
import noNamespace.PerguntaType;
import noNamespace.PerguntasType;
import noNamespace.ReferenciasType;
import org.neodatis.odb.NeoDatis;
import org.neodatis.odb.ODB;
import org.neodatis.odb.Query;
import org.neodatis.odb.core.query.criteria.W;
import resource.B;

/**
 *
 * @author marcelo
 */
public class WebService extends AbstractWebService {

    public static final String BD_DADOS_ALUNO = "ODB_dadosAluno.odb";

    public WebService() {
        super();
        init();
    }

    @Override
    public String getNameServiceRef() {
        return "matricula";
    }

    public static void main(String[] args) {
        new WebService().test();
    }

    public String test() {
//        BotschemaDocument doc = BotschemaDocument.Factory.newInstance();
//        BotschemaType schema = doc.addNewBotschema();
//        schema.addNewPerguntas();
//        processaPerguntas(schema);
//        System.out.println(doc);

        ODB odb = null;
        try {
            odb = NeoDatis.open(BD_DADOS_ALUNO);
//            DaoUsuario dao = new DaoUsuario();
//            Usuario usuario = dao.getUsuario4messegerId("mammamamamm");
//            usuario.setApelido("apelido");
//            usuario.setMessegerId("mammamamamm");
//            dao.persist(usuario);

//            System.out.println(usuario);

            Query query = odb.query(Usuario.class);
            if (query.count().equals(BigInteger.ZERO)) {
                System.out.println("Sem dados!!!");
                criarBaseDados(odb);
            }

            return null;

//            return usuario.getApelido();

        } catch (Exception e) {
            return e.getMessage();
        } finally {
            if (odb != null) {
                odb.close();
            }
        }

//        
//        DaoGenerico<Usuario> dao = new DaoGenerico<Usuario>() {
//        };
//        dao.persist(usuario);


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
                formularPergunta(per, "pendencia.biblioteca");
            
            }
        }
    }

    @Override
    protected void processaReferencias(BotschemaType sType) {
        if (sType.getReferencias() != null) {
            ReferenciasType ref = sType.getReferencias();
            ref.addString("matricula");
            ref.addString("efetuar");
            ref.addString("fazer");
            ref.addString("realizar");
        }

    }

    @Override
    protected void processaResposta(BotschemaType schema) {
        try {

            PerguntasType perguntas = schema.getPerguntas();


            String nome = perguntas.getPerguntaArray(0).getResposta();
//            String apelido = perguntas.getPerguntaArray(1).getResposta();
//            String ra = perguntas.getPerguntaArray(2).getResposta();
//            String email = perguntas.getPerguntaArray(3).getResposta();
//            String user = perguntas.getPerguntaArray(4).getResposta();
//
            ODB odb = null;
            try {
//                odb = NeoDatis.open(BD_DADOS_ALUNO);
//                Query query = odb.query(Usuario.class);
//
//                if (query.count().equals(BigInteger.ZERO)) {
//                    System.out.println("Sem dados!!!");
//                    criarBaseDados(odb);
//                    System.out.println("dados persistidos");
//                }
//                
//                query = odb.query(Usuario.class, W.equal("messegerId", user));
//                Usuario u = (Usuario) query.objects().next();
//                if(u == null){
//                    u = new Usuario();
//                }
//                u.setApelido(apelido);
//                u.setEmail(email);
//                u.setMessegerId(user);
//                u.setNome(nome);
//                u.setRa(ra);
//
//                odb.store(u);
//                odb.commit();
//                
//                schema.setResposta(B.getString("usuario.persistido.sucesso", apelido));

            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                if (odb != null) {
                    odb.close();
                }
            }


//            schema.setResposta(B.getString("usuario.persistido.sucesso", apelido));

        } catch (Exception e) {
            schema.setResposta(B.getString("erro.interno", e.getMessage()));
        }
    }

    private void log(String text, Object... args) {
        System.out.println(MessageFormat.format(text, args));
    }

    private void init() {
        //GerarDadosMocs.gerarBancoDados();
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

    private void criarBaseDados(ODB odb) {
        Usuario u = new Usuario();
        u.setApelido("Marcelo");
        u.setMessegerId("-1311576900@chat.facebook.com");
        u.setNome("Marcelo Lopes");
        u.setRa("647497");
        u.setEmail("marcelo.utfpr@me.com");

        odb.store(u);
        odb.commit();

        u = new Usuario();
        u.setApelido("Ivanilton");
        u.setMessegerId("-100001752061895@chat.facebook.com");
        u.setNome("Ivanilton Polato");
        u.setRa("-");
        u.setEmail("ipolato@gmail.com");

        odb.store(u);
        odb.commit();


    }
}

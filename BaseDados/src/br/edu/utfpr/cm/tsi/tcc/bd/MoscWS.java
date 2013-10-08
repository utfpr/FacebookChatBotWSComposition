/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.tsi.tcc.bd;

import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Disciplina;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Emprestimo;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Grade;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Livro;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Matricula;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Periodo;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Reserva;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Sala;
import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Usuario;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import noNamespace.ClassesType;
import noNamespace.ClassroomType;
import noNamespace.ClassroomsType;
import noNamespace.PeriodType;
import noNamespace.PeriodsType;
import noNamespace.SubjectType;
import noNamespace.SubjectsType;
import noNamespace.TeachersType;
import noNamespace.TimetableDocument;
import noNamespace.TimetableType;
import org.apache.xmlbeans.XmlException;
import org.neodatis.odb.NeoDatis;
import org.neodatis.odb.ODB;
import org.neodatis.odb.Query;
import org.neodatis.odb.core.query.criteria.Criterion;
import org.neodatis.odb.core.query.criteria.W;

/**
 *
 * @author marcelo
 */
public final class MoscWS {

    public static final String BASE_DADOS = "/Users/marcelo/Desktop/apache-tomcat-7.0.39/bin/basedados.odb";

    public MoscWS() throws XmlException, IOException {
//        gerarMocs();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
//            new MoscWS().criarBanco();
            new MoscWS().gerarMocs();
        } catch (XmlException | IOException ex) {
            Logger.getLogger(MoscWS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void criarBanco() {
        try {
//            HibernateUtil.getInstance().gerarBancoDados();
        } catch (Exception ex) {
            Logger.getLogger(MoscWS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void gerarMocs() throws XmlException, IOException {
        File file = new File("salas.xml");

        TimetableDocument document = TimetableDocument.Factory.parse(file);
        TimetableType timeTable = document.getTimetable();
        TeachersType teachers = timeTable.getTeachers();
        PeriodsType periods = timeTable.getPeriods();
        SubjectsType subjects = timeTable.getSubjects();
        ClassesType cursos = timeTable.getClasses();
        ClassroomsType room = timeTable.getClassrooms();

        System.out.println(timeTable.getPeriods());


//        DaoGenerico<Professor> daoProfessor = new DaoGenerico<Professor>(){};
////        daoProfessor.persist(new ProfessorParser().toList(teachers.getTeacherArray()));
//        
//        DaoGenerico<Turma> daoTurmas = new DaoGenerico<Turma>(){};
////        daoTurmas.persist(new TurmaParser().toList(classes.getClass1Array()));
//        
//        DaoGenerico<Sala> daoSala = new DaoGenerico<Sala>() {};
////        daoSala.persist(new SalaParser().toList(sala.getClassroomArray()));
//        
//        DaoUsuario daoUsuario = new DaoUsuario();
//        

        ODB odb = null;
        try {
            odb = NeoDatis.open(BASE_DADOS);

            for (ClassroomType ct : room.getClassroomArray()) {
                Sala t = (Sala) getObjectFromBD(odb, Sala.class, W.equal("sigla", ct.getShort()));
                t.setNome(ct.getName());
                t.setOriginalId(ct.getId());
                t.setSigla(ct.getShort());
                t.setCapacidade(ct.getCapacity());
                persiste(odb, t);
            }

            Usuario usuario = getUsuario(odb, "-1311576900@chat.facebook.com");
            usuario.setApelido("Marcelo");
            usuario.setEmail("marcelo.utfpr@me.com");
            usuario.setNome("Marcelo lopes da Silv");
            usuario.setMessegerId("-1311576900@chat.facebook.com");
            usuario.setRa("647497");

            persiste(odb, usuario);

            for (PeriodType periodType : timeTable.getPeriods().getPeriodArray()) {
                Periodo periodo = (Periodo) getObjectFromBD(odb, Periodo.class, W.equal("sigla", periodType.getShort()));
                periodo.setNome(periodType.getName());
                periodo.setSigla(periodType.getShort());
                periodo.setHoraEntrada(periodType.getStarttime());
                periodo.setHoraSaida(periodType.getEndtime());
                persiste(odb, periodo);
            }
            
            
            usuario = getUsuario(odb, "-100001752061895@chat.facebook.com");
            usuario.setApelido("Ivanilto");
            usuario.setEmail("ipolato@gmail.com");
            usuario.setNome("Ivanilto Polato");
            usuario.setMessegerId("-100001752061895@chat.facebook.com");
            usuario.setRa("");

            persiste(odb, usuario);


            usuario = getUsuario(odb, "-1311576900@chat.facebook.com");
            Emprestimo emprestimo = new Emprestimo();

            Livro livro = (Livro) getObjectFromBD(odb, Livro.class, W.equal("isbn", "3923927392739"));
            livro.setAutor("Fulano");
            livro.setIsbn("3923927392739");
            livro.setNome("Livro de Teste");

            emprestimo.setLivro(livro);
            emprestimo.setUsuario(usuario);
            emprestimo.setDataEmprestimo(new Date());

            persiste(odb, emprestimo);

            Matricula matricula = (Matricula) getObjectFromBD(odb, Matricula.class, W.equal("usuario.messegerId", usuario.getMessegerId()));
            matricula.setUsuario(usuario);
            matricula.setDisciplinas(new ArrayList<Disciplina>());
            matricula.getDisciplinas().add(getDisciplina(subjects, odb, "SO22A0"));
            matricula.getDisciplinas().add(getDisciplina(subjects, odb, "RC24A0"));
            matricula.getDisciplinas().add(getDisciplina(subjects, odb, "MA24A0"));
            matricula.getDisciplinas().add(getDisciplina(subjects, odb, "BD24A0"));
            matricula.getDisciplinas().add(getDisciplina(subjects, odb, "AD24A0"));
            matricula.getDisciplinas().add(getDisciplina(subjects, odb, "AL21A0"));
            
            

            persiste(odb, matricula);



            usuario = getUsuario(odb, "-100001752061895@chat.facebook.com");
            matricula = (Matricula) getObjectFromBD(odb, Matricula.class, W.equal("usuario.messegerId", usuario.getMessegerId()));
            matricula.setUsuario(usuario);
            matricula.setDisciplinas(new ArrayList<Disciplina>());
            matricula.getDisciplinas().add(getDisciplina(subjects, odb, "BD24A0"));
            matricula.getDisciplinas().add(getDisciplina(subjects, odb, "AD24A0"));
            matricula.getDisciplinas().add(getDisciplina(subjects, odb, "AL21A0"));

            persiste(odb, matricula);

            addGrade(odb, Grade.SEG, "SO22A0", "1N","2N", "3N");
            addGrade(odb, Grade.SEG, "AD24A0", "4N","5N");
            
            addGrade(odb, Grade.TER, "SO22A0", "1N","2N");
            addGrade(odb, Grade.TER, "RC24A0", "4N","5N");
            addGrade(odb, Grade.TER, "AL21A0", "3N");
            
            addGrade(odb, Grade.QUA, "BD24A0", "1N","2N", "3N");
            addGrade(odb, Grade.QUA, "RC24A0", "4N","5N");
            
            addGrade(odb, Grade.QUI, "MA24A0", "1N","2N", "3N");
            addGrade(odb, Grade.QUI, "BD24A0", "4N","5N");
            
            addGrade(odb, Grade.SEX, "MA24A0", "4N","5N");
            addGrade(odb, Grade.SEX, "AD24A0", "1N","2N");
            addGrade(odb, Grade.SEX, "AL21A0", "3N");
            

            // RESERVA DE TESTE
            Sala sala = (Sala) getObjectFromBD(odb, Sala.class, W.equal("sigla", "E007"));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            Reserva reserva = new Reserva();
            reserva.setSala(sala);
            reserva.setDataReserva(sdf.parse("12/03/2014 12:30"));
            reserva.setDuracao(30);
            reserva.setUsuario(usuario);

            persiste(odb, reserva);

            System.out.println("Base dados criada com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (odb != null) {
                odb.close();
            }
        }

//        daoUsuario.persist(usuario);

    }

    private void addGrade(ODB odb, String dia, String siglaDisciplina, String... siglasPeriodo) {

        for (String siglaPeriodo : siglasPeriodo) {

            Disciplina dis = (Disciplina) getObjectFromBD(odb, Disciplina.class, W.equal("siglaDisciplina", siglaDisciplina));
            Periodo per = (Periodo) getObjectFromBD(odb, Periodo.class, W.equal("sigla", siglaPeriodo));
            Grade grade = (Grade) getObjectFromBD(odb, Grade.class,
                    W.equal("periodo.sigla", siglaPeriodo)
                    .and(W.equal("disciplina.siglaDisciplina", siglaDisciplina)));
            
            grade.setDia(dia);
            grade.setDisciplina(dis);
            grade.setPeriodo(per);

            persiste(odb, grade);
        }
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
            Logger.getLogger(MoscWS.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Usuario getUsuario(ODB odb, String id) {
        Query query = odb.query(Usuario.class, W.equal("messegerId", id));
        Usuario usuario = (Usuario) (query.objects().hasNext() ? query.objects().next() : new Usuario());
        usuario.setMessegerId(id);
        odb.store(usuario);
        odb.commit();
        return usuario;
    }

    private Disciplina getDisciplina(SubjectsType subjects, ODB odb, String sigla) {
        SubjectType dis = null;
        for (SubjectType st : subjects.getSubjectArray()) {
            if (st.getShort().equals(sigla)) {
                dis = st;
                break;
            }
        }
        if (dis != null) {
            Disciplina disciplina = (Disciplina) getObjectFromBD(odb, Disciplina.class, W.equal("siglaDisciplina", sigla));
            disciplina.setNomeDisciplina(dis.getName());
            disciplina.setSiglaDisciplina(sigla);
            disciplina.setOriginalId(dis.getId());
            disciplina.setNota(randonNota());
            return disciplina;
        }
        throw new UnsupportedOperationException("Disciplina não localizada.");
    }

    private String randonNota() {
        Random r = new Random();
        int i = r.nextInt(100);
        return i + "";
    }

    private void persiste(ODB odb, Object object) {
        System.out.println("Persistindo: " + object);
        odb.store(object);
        odb.commit();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.tsi.tcc.bd.entidades;

/**
 *
 * @author marcelo
 */
public class Grade extends Entidade{
    public static final String SEG = "segunda";
    public static final String TER = "terça";
    public static final String QUA = "quarta";
    public static final String QUI = "quinta";
    public static final String SEX = "sexta";
    public static final String SAB = "sabado";
    public static final String DOM = "domingo";
    
    private String dia;
    private Disciplina disciplina;
    private Periodo periodo;

    /**
     * @return the dia
     */
    public String getDia() {
        return dia;
    }

    /**
     * @param dia the dia to set
     */
    public void setDia(String dia) {
        this.dia = dia;
    }

    /**
     * @return the disciplina
     */
    public Disciplina getDisciplina() {
        return disciplina;
    }

    /**
     * @param disciplina the disciplina to set
     */
    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    /**
     * @return the periodo
     */
    public Periodo getPeriodo() {
        return periodo;
    }

    /**
     * @param periodo the periodo to set
     */
    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }
    
}

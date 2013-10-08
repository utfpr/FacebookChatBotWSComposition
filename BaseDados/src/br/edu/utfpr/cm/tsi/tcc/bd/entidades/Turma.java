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
public class Turma extends Entidade{
//    id,name,short,classroomids,teacherid,grade
    private String originalID;
    private String nome;
    private String sigla;

    /**
     * @return the originalID
     */
    public String getOriginalID() {
        return originalID;
    }

    /**
     * @param originalID the originalID to set
     */
    public void setOriginalID(String originalID) {
        this.originalID = originalID;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the sigla
     */
    public String getSigla() {
        return sigla;
    }

    /**
     * @param sigla the sigla to set
     */
    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
    
    
}

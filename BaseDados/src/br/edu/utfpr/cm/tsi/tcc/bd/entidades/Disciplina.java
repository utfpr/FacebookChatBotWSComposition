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
public class Disciplina extends Entidade{
//    id,name,short
    private String originalId;
    private String nomeDisciplina;
    private String siglaDisciplina;

    private String nota;
    /**
     * @return the originalId
     */
    public String getOriginalId() {
        return originalId;
    }

    /**
     * @param originalId the originalId to set
     */
    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }

    /**
     * @return the nomeDisciplina
     */
    public String getNomeDisciplina() {
        return nomeDisciplina;
    }

    /**
     * @param nomeDisciplina the nomeDisciplina to set
     */
    public void setNomeDisciplina(String nomeDisciplina) {
        this.nomeDisciplina = nomeDisciplina;
    }

    /**
     * @return the siglaDisciplina
     */
    public String getSiglaDisciplina() {
        return siglaDisciplina;
    }

    /**
     * @param siglaDisciplina the siglaDisciplina to set
     */
    public void setSiglaDisciplina(String siglaDisciplina) {
        this.siglaDisciplina = siglaDisciplina;
    }

    /**
     * @return the nota
     */
    public String getNota() {
        return nota;
    }

    /**
     * @param nota the nota to set
     */
    public void setNota(String nota) {
        this.nota = nota;
    }
    
    
}

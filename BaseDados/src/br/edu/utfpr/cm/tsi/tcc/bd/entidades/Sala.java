/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.tsi.tcc.bd.entidades;

import javax.persistence.Entity;

/**
 *
 * @author marcelo
 */
public class Sala extends Entidade{
//    id,name,short,capacity
    private String originalId,nome,sigla,capacidade;

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

    /**
     * @return the capacidade
     */
    public String getCapacidade() {
        return capacidade;
    }

    /**
     * @param capacidade the capacidade to set
     */
    public void setCapacidade(String capacidade) {
        this.capacidade = capacidade;
    }
}

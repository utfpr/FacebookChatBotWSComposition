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
public class Professor extends Entidade{
//    id,name,short,gender,color,email,mobile
    private String originalId;
    private String messengerId;
    private String nome;
    private String apelido;
    private String email;

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
     * @return the messengerId
     */
    public String getMessengerId() {
        return messengerId;
    }

    /**
     * @param messengerId the messengerId to set
     */
    public void setMessengerId(String messengerId) {
        this.messengerId = messengerId;
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
     * @return the apelido
     */
    public String getApelido() {
        return apelido;
    }

    /**
     * @param apelido the apelido to set
     */
    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    
}

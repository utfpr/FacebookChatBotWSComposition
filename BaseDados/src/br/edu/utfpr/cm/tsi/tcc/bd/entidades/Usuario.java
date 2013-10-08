/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.tsi.tcc.bd.entidades;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author marcelo
 */
public class Usuario extends Entidade implements Serializable {
    private List<Emprestimo> emprestimos;
    private String email;
    private String messegerId;
    private String nome;
    private String apelido;
    private String ra;
    
    
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    /**
     * @return the emprestimos
     */
    public List<Emprestimo> getEmprestimos() {
        return emprestimos;
    }

    /**
     * @param emprestimos the emprestimos to set
     */
    public void setEmprestimos(List<Emprestimo> emprestimos) {
        this.emprestimos = emprestimos;
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
     * @return the messegerId
     */
    public String getMessegerId() {
        return messegerId;
    }

    /**
     * @param messegerId the messegerId to set
     */
    public void setMessegerId(String messegerId) {
        this.messegerId = messegerId;
    }

    
}

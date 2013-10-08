/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * a
 * 
 * 
 * 
 * nd open the template in the editor.
 */
package br.edu.utfpr.cm.tsi.tcc.bd.daos;

import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Usuario;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

/**
 *
 * @author marcelo
 */
public class DaoUsuario extends DaoGenerico<Usuario>{

    @Override
    public void persist(Usuario t) {
        Usuario u = getUsuario4messegerId(t.getMessegerId());
        if(u==null){
            u = new Usuario();
        }
        
        cloneEntity(t,u);
        super.persist(u);
    }
    
    public boolean contains(String email){
        return getUsuario4email(email)!=null;
    }
    
    public Usuario getUsuario4email(String email){
        Criteria c = getCriteria();
        c.add(Expression.eq("email", email));
        return (Usuario) c.uniqueResult();
    }
    
    public Usuario getUsuario4messegerId(String messegerId){
        Criteria c = getCriteria();
        c.add(Expression.eq("messegerId", messegerId));
        return (Usuario) c.uniqueResult();
    }
}

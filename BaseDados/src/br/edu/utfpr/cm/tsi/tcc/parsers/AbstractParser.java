/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.tsi.tcc.parsers;

import br.edu.utfpr.cm.tsi.tcc.bd.daos.DaoGenerico;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author marcelo
 */
public abstract class AbstractParser<T, E> implements Parser<T, E> {

    @Override
    public List<E> toList(T[] arraysXml) {
        List<E> list = new ArrayList<>();
        for (T e : arraysXml) {
            list.add(toObject(e));
        }
        return list;
    }

    @Override
    public void persiste(T[] xml) {
        DaoGenerico<E> dao = new DaoGenerico<E>() {};
        dao.persist(toList(xml));
    }
}

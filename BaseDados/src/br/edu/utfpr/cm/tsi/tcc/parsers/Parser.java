/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.tsi.tcc.parsers;

import java.util.List;

/**
 *
 * @author marcelo
 */
public interface Parser<T,E>{
    E toObject(T xml);
    T toXmlString(E object);
    List<E> toList(T[] arraysXml);
    void persiste(T[] xml);
}

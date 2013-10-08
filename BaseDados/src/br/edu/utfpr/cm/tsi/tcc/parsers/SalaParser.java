/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.tsi.tcc.parsers;

import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Sala;
import noNamespace.ClassroomType;

/**
 *
 * @author marcelo
 */
public class SalaParser extends AbstractParser<ClassroomType, Sala>{

    @Override
    public Sala toObject(ClassroomType xml) {
        Sala sala = new Sala();
        sala.setNome(xml.getName());
        sala.setOriginalId(xml.getId());
        sala.setSigla(xml.getShort());
        sala.setCapacidade(xml.getCapacity());
        return sala;
    }

    @Override
    public ClassroomType toXmlString(Sala s) {
        ClassroomType ct = ClassroomType.Factory.newInstance();
        ct.setCapacity(s.getCapacidade());
        ct.setId(s.getOriginalId());
        ct.setShort(s.getSigla());
        ct.setName(s.getNome());
        return ct;
    }
    
}

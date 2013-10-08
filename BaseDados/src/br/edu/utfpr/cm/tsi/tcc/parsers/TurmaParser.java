/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.tsi.tcc.parsers;

import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Turma;
import noNamespace.ClassType;

/**
 *
 * @author marcelo
 */
public class TurmaParser extends AbstractParser<ClassType, Turma>{

    @Override
    public Turma toObject(ClassType xml) {
        Turma turma = new Turma();
        turma.setNome(xml.getName());
        turma.setSigla(xml.getShort());
        turma.setOriginalID(xml.getId());
        return turma;
    }

    @Override
    public ClassType toXmlString(Turma turma) {
        ClassType r = ClassType.Factory.newInstance();
        r.setName(turma.getNome());
        r.setId(turma.getOriginalID());
        r.setShort(turma.getSigla());
        return r;
    }
    
}

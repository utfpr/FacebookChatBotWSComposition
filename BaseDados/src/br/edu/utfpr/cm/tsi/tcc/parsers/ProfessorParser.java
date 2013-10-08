/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.tsi.tcc.parsers;

import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Professor;
import noNamespace.TeacherType;

/**
 *
 * @author marcelo
 */
public class ProfessorParser extends AbstractParser<TeacherType, Professor>{

    @Override
    public Professor toObject(TeacherType xml) {
        Professor p = new Professor();
        p.setApelido(xml.getShort());
        p.setNome(xml.getName());
        p.setOriginalId(xml.getId());
//        p.setMessengerId(xml.getEmail());
        p.setEmail(xml.getEmail());
        return p;
    }

    @Override
    public TeacherType toXmlString(Professor p) {
        TeacherType tt = TeacherType.Factory.newInstance();
        tt.setEmail(p.getEmail());
        tt.setId(p.getOriginalId());
        tt.setName(p.getNome());
        tt.setShort(p.getApelido());
        
        return tt;
    }
    
    
}

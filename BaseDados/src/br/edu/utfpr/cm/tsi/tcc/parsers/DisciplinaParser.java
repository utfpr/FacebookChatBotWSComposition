/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.tsi.tcc.parsers;

import br.edu.utfpr.cm.tsi.tcc.bd.entidades.Disciplina;
import noNamespace.SubjectType;

/**
 *
 * @author marcelo
 */
public class DisciplinaParser extends AbstractParser<SubjectType, Disciplina> {

    @Override
    public Disciplina toObject(SubjectType xml) {
        Disciplina disciplina = new Disciplina();
        disciplina.setOriginalId(xml.getId());
        disciplina.setNomeDisciplina(xml.getName());
        disciplina.setSiglaDisciplina(xml.getShort());
        return disciplina;
    }

    @Override
    public SubjectType toXmlString(Disciplina object) {
        Disciplina disciplina = (Disciplina) object;
        SubjectType ret = SubjectType.Factory.newInstance();
        ret.setId(disciplina.getOriginalId());
        ret.setName(disciplina.getNomeDisciplina());
        ret.setShort(disciplina.getSiglaDisciplina());
        
        return ret;
    }
}

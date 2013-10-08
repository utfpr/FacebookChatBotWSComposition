/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.tsi.tcc.bd;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author marcelo
 */
public class ProtocoloFactory {
    private int sequencia;
    
    private ProtocoloFactory() {
        sequencia = 1;
    }
    
    public static ProtocoloFactory getInstance() {
        return ProtocoloFactoryHolder.INSTANCE;
    }
    
    private static class ProtocoloFactoryHolder {
        private static final ProtocoloFactory INSTANCE = new ProtocoloFactory();
    }
    
    public String getProtocolo(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = sdf.format(new Date());
        return String.format("%s%03d", dateString, sequencia++);
    }
    
    
    public static void main(String[] args) {
        System.out.println(getInstance().getProtocolo());
    }
    
}

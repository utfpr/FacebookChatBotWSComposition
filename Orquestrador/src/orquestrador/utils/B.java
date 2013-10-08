/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orquestrador.utils;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marcelo
 */
public class B {
    public static String getString(String message, Object ... args){
        return MessageFormat.format(message, args);
    }
    
    public static void log(String message, Object ... args){
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, getString(message, args));
    }

    public static void error(Exception e) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.SEVERE, e.getMessage(), e);
    }
}

package br.edu.utfpr.cm.tsi.tcc.marcelo.utils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 *
 * @author tchulla
 */
public class B {

    private ResourceBundle bundle;
    
    public B() {
        bundle = ResourceBundle.getBundle("resource/Bundle");
    }

    public static String getString(String key, Object... args) {
        return new B().get(key, args);
    }
    public static String getPerString(String key, Object... args) {
        return new B().getOnNull(key, args);
    }

    
    
    private String getOnNull(String key, Object... args) {
        try {
            return MessageFormat.format(bundle.getString(key), args);
        } catch (Exception e) {
            return null;
        }
    }
    private String get(String key, Object... args) {
        try {
            return MessageFormat.format(bundle.getString(key), args);
        } catch (Exception e) {
            return key;
        }
    }
}

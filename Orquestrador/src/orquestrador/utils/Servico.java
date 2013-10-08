/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orquestrador.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author marcelo
 */
@XStreamAlias("sevicos")
public class Servico {

    @XStreamAsAttribute
    private String urlOrquestrador;
    @XStreamAlias("WebServices")
    private List<String> webServices;
    @XStreamOmitField
    private static XStream stream;

    public Servico() {
        stream = new XStream(new DomDriver());
        stream.processAnnotations(Servico.class);
        webServices = new ArrayList<>();
    }

    public String getUrlOrquestrador() {
        return urlOrquestrador;
    }

    public void setUrlOrquestrador(String urlOrquestrador) {
        this.urlOrquestrador = urlOrquestrador;
    }

    public List<String> getWebServices() {
        return webServices;
    }

    public void setWebServices(List<String> webServices) {
        this.webServices = webServices;
    }

    public void addWebServiceUrl(String url) {
        webServices.add(url);
    }

    public static Servico load(File file) throws FileNotFoundException {
        stream = new XStream(new DomDriver());
        stream.processAnnotations(Servico.class);
        return (Servico) stream.fromXML(new FileInputStream(file));
    }

    public String toXML() {
        stream = new XStream(new DomDriver());
        stream.processAnnotations(Servico.class);
        return stream.toXML(this);
    }

    public void toXML(File file) {
        stream = new XStream(new DomDriver());
        stream.processAnnotations(Servico.class);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            stream.toXML(this, new FileOutputStream(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Servico.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Servico.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

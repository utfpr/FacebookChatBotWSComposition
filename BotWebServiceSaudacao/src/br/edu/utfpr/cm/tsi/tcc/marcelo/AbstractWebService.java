package br.edu.utfpr.cm.tsi.tcc.marcelo;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.soap.SOAPBinding;
import noNamespace.BotschemaDocument;
import noNamespace.BotschemaType;
import noNamespace.DataType;
import noNamespace.RequisicaoType;
import noNamespace.StatusType;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

/**
 *
 * @author marcelo
 */
public abstract class AbstractWebService {

    final DateTime dateIN;
    int chamadas;
    int chamadasErro;
    String CONF_FILE = "config.properties";
    String WSDL;
    String WSDL_ORQUESTRADOR;

    public AbstractWebService() {
        dateIN = new DateTime();
//        autoPublicar();
    }

    public boolean handSheck(String orquestrador) {
        WSDL_ORQUESTRADOR = orquestrador;
        log("Endereço do orquestrador: {0}", orquestrador);
        return true;
    }

    @SOAPBinding(
            style = SOAPBinding.Style.DOCUMENT,
            use = SOAPBinding.Use.LITERAL,
            parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
    public @WebResult(name = "processar")
    String processar(@WebParam(name = "req", mode = WebParam.Mode.IN) String requisicao) {
        chamadas++;
        try {
            BotschemaDocument doc = BotschemaDocument.Factory.parse(requisicao);
            BotschemaType schema = doc.getBotschema();

            if (schema.getProcess()) {
                processaPerguntas(schema);
                processaReferencias(schema);
                processaStatus(schema.getStatus());
                processaRequisicao(schema.getRequisicao());
                return doc.xmlText();
            } else {
//                doc = BotschemaDocument.Factory.newInstance();
                processaResposta(schema);
                return doc.xmlText();
            }

        } catch (Exception e) {
            chamadasErro++;
            return "Erro interno!";
        }
    }

    protected String b(String value, Object... args) {
        return MessageFormat.format(value, args);
    }

    private void log(String string, Object... args) {
        System.out.println(b(string, args));
        Logger.getLogger(getClass().getName()).log(Level.INFO, b(string, args));
    }

    protected void processaRequisicao(RequisicaoType requisicao) {
        if (requisicao != null) {
        }
    }

    protected void processaStatus(StatusType status) {

        if (status != null) {
            DataType data = status.addNewData();

            Calendar ca = Calendar.getInstance();
            data.setTime(ca.getTimeInMillis());
            data.setTimezone(ca.getTimeZone().getDisplayName());

            DateTime now = new DateTime();

            Minutes minutos = Minutes.minutesBetween(dateIN, now);
            String detalhes = String.format("Serviço operando a %d minutos.\nTotal de chamadas: %d\nTotal de chamadas com erros: %d.",
                    minutos.getMinutes(),
                    chamadas, chamadasErro);
            status.setDetalhe(detalhes);
        }
    }
    
    protected void reloadConversa(BotschemaType schema){
        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            
        } catch (Exception e) {
        }
    }

    public abstract String getNameServiceRef();

    protected abstract void processaPerguntas(BotschemaType schema);

    protected abstract void processaReferencias(BotschemaType sType);

    protected abstract void processaResposta(BotschemaType schema);
}

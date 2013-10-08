package br.edu.utfpr.cm.tsi.tcc.marcelo;

/**
 *
 * @author marcelo
 */
public interface WS {

    String getNameServiceRef();

    boolean handSheck(String orquestrador);

    String processar(String requisicao);

    String respostaLogica(String docxml);
    
}

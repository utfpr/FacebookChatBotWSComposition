/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orquestrador;

import java.util.HashMap;
import java.util.Map;
import noNamespace.PerguntaType;

/**
 *
 * @author marcelo
 */
public class Conversa {
    
    private Map<String, PerguntaType[]> mapConversas;
    private Map<String, String> mapServicos;
    private Map<String, PerguntaType> mapQuestoes;
    
    private Conversa() {
        mapConversas = new HashMap<>();
        mapServicos = new HashMap<>();
        mapQuestoes = new HashMap<>();
        
        
    }
    
    public static Conversa getInstance() {
        return DataSingletonHolder.INSTANCE;
    }
    
    private static class DataSingletonHolder {
        private static final Conversa INSTANCE = new Conversa();
    }
    
    public PerguntaType[] getPerguntas(String email){
        return mapConversas.get(email);
    }
    
    public void removePerguntaDireta(String email){
        mapQuestoes.remove(email);
    }
    
    public void putPerguntaDireta(String email, PerguntaType pergunta){
        mapQuestoes.put(email, pergunta);
    }
    
    public PerguntaType getPerguntaDireta(String email){
        return mapQuestoes.get(email);
    }
    
    public String getServico(String email){
        return mapServicos.get(email);
    }
    
    public void removeMapsFor(String email){
        mapConversas.remove(email);
        mapServicos.remove(email);
        mapQuestoes.remove(email);
    }
    
    public void setPerguntas(String email,PerguntaType[] drs){
        mapConversas.put(email, drs);
    }
    
    public void putDialogoResponse(String email, String service,  PerguntaType[] drs){
        mapConversas.remove(email);
        mapServicos.remove(email);
        
        mapConversas.put(email, drs);
        mapServicos.put(email, service);
    }
    
    public boolean existeConversa(String email){
        return mapConversas.containsKey(email);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Total de perguntas: ").append(mapConversas.size());
        sb.append("\n");
        
        for (Map.Entry<String, PerguntaType[]> entry : mapConversas.entrySet()) {
            PerguntaType[] perguntaTypes = entry.getValue();
            for (PerguntaType per : perguntaTypes) {
                sb.append(per.getQuestao()).append("\n");
            }
        }
        
        return sb.toString(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}

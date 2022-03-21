package srcClass;

import java.sql.Date;
import java.sql.Timestamp;

public class Allarme {
    private int lvlAlarm;
    private int tipoAllarme;
    private int tempoRisposta;
    private String idRicovero;
    private Timestamp momentoGenerazioneAllarme;

    public enum Livello { LIVELLO1, LIVELLO2, LIVELLO3}
    public enum TipoAllarme { Ipertensione, Ipotensione, Dispnea, Ipertermia, Ipotermia, Tachicardia, FlutterVentricolare}

    public Allarme(int lvlAlarm, TipoAllarme nomeAllarme, int tempoRisposta, String idRicovero){
            this.lvlAlarm = lvlAlarm;
            this.tempoRisposta = tempoRisposta;
            this.tipoAllarme = nomeAllarme.ordinal();
            this.idRicovero = idRicovero;
    }

    public String getIdRicovero() {
        return idRicovero;
    }

    public void setIdRicovero(String idRicovero) {
        this.idRicovero = idRicovero;
    }

    public void setTipoAllarme(TipoAllarme tipo) {
        this.tipoAllarme = tipo.ordinal();
    }
    public TipoAllarme getTipoAllarme() {
        TipoAllarme arr[] = TipoAllarme.values();
        return arr[tipoAllarme];
    }

    public int getLvlAlarm() {
        return lvlAlarm;
    }

    public void setLvlAlarm(int lvlAlarm) {
        this.lvlAlarm = lvlAlarm;
    }

    public int getTempoRisposta() {
        return tempoRisposta;
    }

    public void setTempoRisposta(int tempoRisposta) {
        this.tempoRisposta = tempoRisposta;
    }

    public Timestamp getMomentoGenerazioneAllarme() {
        return momentoGenerazioneAllarme    ;
    }
    public void setMomentoGenerazioneAllarme(Timestamp momentoGenerazioneAllarme) {
        this.momentoGenerazioneAllarme = momentoGenerazioneAllarme;
    }

    @Override
    public String toString() {
        //La lunghezza della stringa è molto importante, in quanto gli allarmi viaggiano come DataGram nel sistema informatico, dunque devono avere una lunghezza prefissata, per non incappare in errori!
        Integer ricoveroId = Integer.valueOf(idRicovero);

        String toReturn = lvlAlarm + " " + tipoAllarme + " " + tempoRisposta + " " + ricoveroId.toString();

        for(int i = ricoveroId.toString().length(); i <= 10; ++i){
            toReturn = toReturn + " ";
        }

        return toReturn;
    }



    /*public Allarme fromString(String allarme){
        String[] alarmParameter = allarme.split(" ");

        Allarme allarme = new Allarme(Integer.valueOf(alarmParameter[0]), TipoAllarme.alarmParameter[1], Integer.valueOf(alarmParameter[2]));


        return
    }*/
}

package srcClass;

import java.io.Serializable;

public class parametriVitali implements Serializable, Comparable {
    private int sistolica; //siastolica
    private int diastolica; //diastolica
    private int satOss;
    private float temperatura;
    private int ecg;


    public parametriVitali(){}
    public parametriVitali(int sistolica, int diastolica, int satOss, float temperatura, int ecg){
        this.sistolica = sistolica;
        this.diastolica = diastolica;
        this.satOss = satOss;
        this.temperatura = temperatura;
        this.ecg = ecg;
    }

    public float getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(float temperatura) {
        this.temperatura = temperatura;
    }

    public int getSatOss() {
        return satOss;
    }

    public void setSatOss(int satOss) {
        this.satOss = satOss;
    }

    public int getDiastolica() {
        return diastolica;
    }

    public void setDiastolica(int diastolica) {
        this.diastolica = diastolica;
    }

    public int getSistolica() {
        return sistolica;
    }

    public void setSistolica(int sistolica) {
        this.sistolica = sistolica;
    }

    @Override
    public int compareTo(Object o) {
        if(o == null) return -1;
        if(o.getClass() == parametriVitali.class) {
            parametriVitali obj = (parametriVitali) o;
            if(obj.getDiastolica() == this.getDiastolica()){
                if(obj.getSatOss() == this.getSatOss()){
                    if(obj.getSistolica() == this.getSistolica()){
                        if(obj.getTemperatura() == this.getTemperatura()){
                            return 0;
                        }
                    }
                }
            }
        }
        return -1;
    }

    public int getEcg() {
        return ecg;
    }

    public void setEcg(int ecg) {
        this.ecg = ecg;
    }

    public void clear(){
        this.ecg =          -1;
        this.diastolica =   -1;
        this.sistolica =    -1;
        this.satOss =       -1;
        this.temperatura =  -1;

    }
}

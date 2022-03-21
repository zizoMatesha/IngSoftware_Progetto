package sistemaMonitoraggio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.Random;
import srcClass.Allarme;
import srcClass.Ricovero;
import srcClass.parametriVitali;

import java.util.Scanner;

public class monitorStanza extends Thread{
    private Ricovero ricovero;
    private parametriVitali lastParam;
    private InetAddress group;
    private MulticastSocket multiSocket;

    public monitorStanza(Ricovero ricovero){
        this.ricovero = ricovero;
    }

    @Override
    public synchronized void run() {
        byte[] buf;
        Allarme[] allarme;
        DatagramPacket packet;

        try {
            multiSocket = new MulticastSocket();
            multiSocket.setTimeToLive(1);
            allarme = new Allarme[1];
            buf = new byte[allarme.length];
            group = InetAddress.getByName("225.0.0.1");
            packet = new DatagramPacket(buf, buf.length, group, 50153);
        }catch (IOException e){e.printStackTrace();}

        System.out.println("Running");
        //Set iniziale di parametri all'avvio del server faccio una misurazione su tutti i pazienti ricoverati
        parametriVitali temp = getRandParameter();
        try {
            DataGen.addData(ricovero, temp, 4);
            checkAlarm(temp, 4);
            lastParam = temp;
        }catch (IOException e) {
            e.printStackTrace();
        }

        //Task invio sistolica e diastolica
        new Thread(() -> {
            try {
                while(true){
                    Thread.sleep(120000);
                    parametriVitali newParametri = getRandParameter();
                    DataGen.addData(ricovero, newParametri, 1);
                    checkAlarm(newParametri, 1);
                    lastParam.setDiastolica(newParametri.getDiastolica());
                    lastParam.setSistolica(newParametri.getSistolica());
                }
            }catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }

        }).start();

        //Task invio saturazione ossigeno
        new Thread(() -> {
                try {
                    while (true) {
                        Thread.sleep(300000);
                        parametriVitali newParametri = getRandParameter();
                        DataGen.addData(ricovero, newParametri, 2);
                        checkAlarm(newParametri, 2);
                        lastParam.setSatOss(newParametri.getSatOss());
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
        }).start();

        //Task invio temperatura
        new Thread(() -> {
                try {
                    while (true) {
                        Thread.sleep(180000);
                        parametriVitali newParametri = getRandParameter();
                        DataGen.addData(ricovero, newParametri, 3);
                        checkAlarm(newParametri, 3);
                        lastParam.setTemperatura(newParametri.getTemperatura());
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
        }).start();

        //Task invio ECG
        new Thread(() -> {
                try {
                    while (true) {
                        Thread.sleep(60000);
                        parametriVitali newParametri = getRandParameter();
                        DataGen.addData(ricovero, newParametri, 5);
                        checkAlarm(newParametri, 5);
                        lastParam.setEcg(newParametri.getEcg());
                    }
                }catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
        }).start();
    }


    public Ricovero getRicovero() {
        return ricovero;
    }

    public void setRicovero(Ricovero ricovero) {
        this.ricovero = ricovero;
    }

    public static parametriVitali getRandParameter(){
        Random rnd = new Random();
        parametriVitali parametri = new parametriVitali();

        File text = new File("src/sistemaMonitoraggio/ecg.txt");
        Scanner sc = null;
        try {
            sc = new Scanner(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for(int i=0; i<(new Random()).nextInt(50); ++i)
            sc.nextInt();
        parametri.setEcg(sc.nextInt());

        parametri.setSistolica(rnd.nextInt(10)+90);
        parametri.setDiastolica(rnd.nextInt(10)+80);
        parametri.setSatOss(rnd.nextInt(20) + 80);
        Float temp;
        do{
            temp = (float)(((int)((37 + (rnd.nextGaussian()))*10))/10d);
        }while(temp<35);

        parametri.setTemperatura(temp);

        return parametri;
    }

    public void checkAlarm(parametriVitali parametri, int opt) throws IOException {

        switch(opt){
            case (1):{
                //ipertensione
                if(parametri.getDiastolica() > 140 && parametri.getSistolica() > 95){
                    Allarme allarme = new Allarme(Allarme.Livello.LIVELLO2.ordinal(), Allarme.TipoAllarme.Ipertensione, 2, ""+ricovero.getIdRicovero());
                    byte[] alarmPacket = allarme.toString().getBytes();
                    DatagramPacket packet = new DatagramPacket(alarmPacket, alarmPacket.length, group, 50153);
                    multiSocket.send(packet);
                }

                //ipotensione
                if(parametri.getSistolica() < 59){
                    Allarme allarme = new Allarme(Allarme.Livello.LIVELLO2.ordinal(), Allarme.TipoAllarme.Ipotensione, 2, ""+ricovero.getIdRicovero());
                    byte[] alarmPacket = allarme.toString().getBytes();
                    DatagramPacket packet = new DatagramPacket(alarmPacket, alarmPacket.length, group, 50153);
                    multiSocket.send(packet);
                }
            }break;
            case (2):{
                //dispnea
                if(parametri.getSatOss() < 90){
                    Allarme allarme = new Allarme(Allarme.Livello.LIVELLO3.ordinal(), Allarme.TipoAllarme.Dispnea, 1, ""+ricovero.getIdRicovero());
                    byte[] alarmPacket = allarme.toString().getBytes();
                    //System.out.println("Bytes----->" + allarme.toString().getBytes().length);
                    DatagramPacket packet = new DatagramPacket(alarmPacket, alarmPacket.length, group, 50153);
                    multiSocket.send(packet);
                }

            }break;
            case (3):{
                //ipertermia
                if(parametri.getTemperatura() > 40){
                    Allarme allarme = new Allarme(Allarme.Livello.LIVELLO2.ordinal(), Allarme.TipoAllarme.Ipertermia, 2,""+ricovero.getIdRicovero());
                    byte[] alarmPacket = allarme.toString().getBytes();
                    //System.out.println("Bytes----->" + allarme.toString().getBytes());
                    DatagramPacket packet = new DatagramPacket(alarmPacket, alarmPacket.length, group, 50153);
                    multiSocket.send(packet);
                }

                //ipotermia
                else if(parametri.getTemperatura() < 35){
                    Allarme allarme = new Allarme(Allarme.Livello.LIVELLO2.ordinal(), Allarme.TipoAllarme.Ipotermia, 2, ""+ricovero.getIdRicovero());
                    byte[] alarmPacket = allarme.toString().getBytes();
                    //System.out.println("Bytes----->" + allarme.toString().getBytes());
                    DatagramPacket packet = new DatagramPacket(alarmPacket, alarmPacket.length, group, 50153);
                    multiSocket.send(packet);
                }

            }break;
            case (5):{
                //flutter ventricolare
                if(parametri.getEcg() > 160){
                    Allarme allarme = new Allarme(Allarme.Livello.LIVELLO3.ordinal(), Allarme.TipoAllarme.FlutterVentricolare, 1, ""+ricovero.getIdRicovero());
                    byte[] alarmPacket = allarme.toString().getBytes();
                    //System.out.println("Bytes----->" + allarme.toString().getBytes());
                    DatagramPacket packet = new DatagramPacket(alarmPacket, alarmPacket.length, group, 50153);
                    multiSocket.send(packet);
                }

                //Tachicardia
                else if(parametri.getEcg() > 100){
                    Allarme allarme = new Allarme(Allarme.Livello.LIVELLO1.ordinal(), Allarme.TipoAllarme.Tachicardia, 3, ""+ricovero.getIdRicovero());
                    byte[] alarmPacket = allarme.toString().getBytes();
                    //System.out.println("Bytes----->" + allarme.toString().getBytes());
                    DatagramPacket packet = new DatagramPacket(alarmPacket, alarmPacket.length, group, 50153);
                    multiSocket.send(packet);
                }

            }break;
            case (4):{

            }break;
            default:{
                System.err.println("Default in switch case not allowed!");
            }break;
        }

    }

}

package sistemaMonitoraggio;


import srcClass.Ricovero;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServerCentrale extends Thread {
    private int numPostiLettoDisponibili = 10;
    private Socket socket = null;
    private ServerSocket server = null;
    private ServerSocket serverAlarm = null;
    private ObjectInputStream streamIn = null;
    private ObjectOutputStream streamOut = null;
    private monitorStanza currentMonitor;

    //Server centrale che accetta la prima richiesta da parte del client per un determinato ricovero,
    //verifica se il ricovero è presente nel sistema,

    public void run() {

        if (server == null) {
            try {
                server = new ServerSocket(50152); //porta per la comunicazione iniziale
                //serverAlarm = new ServerSocket(50153);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //runMonitorGenerale();
        dataBaseForMonitor dataBase = new dataBaseForMonitor();
        ArrayList<Ricovero> ricoveri = dataBase.getRicoveri();
        ArrayList<monitorStanza> monitorRicovero = new ArrayList<>();

        for (int i = 0; i < ricoveri.size(); ++i){
            monitorRicovero.add(new monitorStanza(ricoveri.get(i)));
            monitorRicovero.get(i).start();
        }

        Thread controlloNuoviRicoveri = new controlloNuoviRicoveri(ricoveri);
        controlloNuoviRicoveri.start();

        while (true) {
            try {
                socket = server.accept();

                ServeClient instanceOfMonitorForClient = new ServeClient();
                instanceOfMonitorForClient.setSocket(socket);
                streamIn = new ObjectInputStream(socket.getInputStream());
                streamOut = new ObjectOutputStream(socket.getOutputStream());
                Ricovero ric = (Ricovero) streamIn.readObject();

                instanceOfMonitorForClient.setRicovero(ric);
                instanceOfMonitorForClient.setStreamIn(streamIn);
                instanceOfMonitorForClient.setStreamOut(streamOut);

                System.out.println("Ricovero: " + ric.getIdRicovero());
                streamOut.writeBoolean(true);
                streamOut.flush();

                System.out.println(streamIn.readObject());
                System.out.println("Client accepted: " + socket);

                instanceOfMonitorForClient.start();
            }catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
        }
    }

    class controlloNuoviRicoveri extends Thread{

        ArrayList<Ricovero> ricoveriIstanziati;

        public controlloNuoviRicoveri(ArrayList<Ricovero> ricoveriIstanziati){
            this.ricoveriIstanziati = ricoveriIstanziati;
        }

        @Override
        public void run(){
            dataBaseForMonitor db = new dataBaseForMonitor();
            while(true){

                ArrayList<Ricovero> ricoveriTemp = db.getRicoveri();

                for(Ricovero ricovero: ricoveriTemp){
                    if(!ricoveriIstanziati.contains(ricovero)){
                        System.out.println("Non contenuto: " + ricovero.getPazienteRicoverato().getCognome());
                        ricoveriIstanziati.add(ricovero);
                        new monitorStanza(ricovero).start();
                    }
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

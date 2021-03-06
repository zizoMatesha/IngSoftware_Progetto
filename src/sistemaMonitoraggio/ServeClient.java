package sistemaMonitoraggio;

import srcClass.Ricovero;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class ServeClient extends Thread{

    private Socket socket = null;
    private ObjectInputStream streamIn = null;
    private ObjectOutputStream streamOut = null;

    //For alarm
    private Socket              alarmSocket = null;
    private ObjectInputStream   alarmStreamIn = null;
    private ObjectOutputStream  alarmStreamOut = null;

    private Ricovero ricovero;
    private Calendar lastValue;
    private String lastStringValue = null;

    @Override
    public void run() {

        System.out.println("<--ServerClient RUNNING-->");

        try {
            /*streamOut.writeBoolean(true);
            streamOut.flush();*/
            lastValue = Calendar.getInstance();
            lastValue.set(Calendar.SECOND, 0);
            lastValue.set(Calendar.MILLISECOND, 0);
            Calendar b2h = Calendar.getInstance();
            b2h.set(Calendar.SECOND, 0);
            b2h.set(Calendar.MILLISECOND, 0);

            int typeOfAccount = streamIn.readInt();

            //FOR LOGGED USER 2H
            if(typeOfAccount > 0){
                b2h.add(Calendar.HOUR_OF_DAY, -2);
            }
            //FOR NOT LOGGED USER 15 MIN
            else{
                b2h.add(Calendar.MINUTE, -15);
            }



            //Ritorna al client le ultime due ore di controllo dei parametri vitali
            File fn = new File("src/sistemaMonitoraggio/" + ricovero.getIdRicovero() + "_" + ricovero.getPazienteRicoverato().getCodSanitario() + ".txt");
            if(fn.exists()) {
                Scanner sc = new Scanner(fn);


                boolean flag = true; //se sto stampando un dato entro le due ore
                int readed = 0;
                while (sc.hasNext() && flag){
                    String s = sc.nextLine();
                    String[] buffer = s.split(" ");


                    int year = Integer.valueOf(buffer[0].split("/")[2]);
                    int month = Integer.valueOf(buffer[0].split("/")[1]);
                    int day = Integer.valueOf(buffer[0].split("/")[0]);
                    int hour = Integer.valueOf((buffer[1].split("\\."))[0]);
                    int min = Integer.valueOf((buffer[1].split("\\."))[1]);
                    int sec = Integer.valueOf((buffer[1].split("\\."))[2]);

                    Calendar cd = Calendar.getInstance();
                    cd.set(year, month - 1, day, hour, min, sec);
                    cd.set(Calendar.MILLISECOND, 0);

                    if(cd.compareTo(lastValue) >= 0){
                        lastValue = cd;
                        lastStringValue = s;
                    }

                    if (cd.compareTo(b2h) < 0) {
                        flag = false;
                        //System.out.println("NADA");
                    }else {
                        streamOut.writeObject(s);
                        streamOut.flush();
                        if(!streamIn.readBoolean()) socket.close();
                        if (!buffer[buffer.length-1].contains("-")) {
                            ++readed;
                        }//else System.out.println("Nuovi record letto, ma non c'?? la temperatura");
                    }
                }
                if(readed > 0) {
                    streamOut.writeObject("end");
                    streamOut.flush();
                    System.out.println("LAST VALUE: " + lastValue.getTime().toString());
                }
            }

            //ciclo creato per inviare al client gli eventuali nuovi dati relativi al cotrollo dei parametri vitali
            while(!socket.isClosed()) {
                verifyNewTemp();
            }

            System.out.println("[Closed]-> ServerClient!");



        }catch (SocketException i){
            return;
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }

    //inviare al client gli eventuali nuovi dati relativi al cotrollo dei parametri vitali e controllare la presenza di allarmi
    public void verifyNewTemp() throws IOException {

        File fn = new File("src/sistemaMonitoraggio/" + ricovero.getIdRicovero() + "_" + ricovero.getPazienteRicoverato().getCodSanitario() + ".txt");

        if(fn.exists()) {
            Scanner sc = new Scanner(fn);

            ArrayList<String> newValue = new ArrayList<>();

            boolean flag = true; //se sto stampando un dato entro le due ore
            int readed = 0;
            newValue.clear();
            while (sc.hasNext() && flag) {
                String s = sc.nextLine();
                String[] buffer = s.split(" ");

                int year = Integer.valueOf(buffer[0].split("/")[2]);
                int month = Integer.valueOf(buffer[0].split("/")[1]);
                int day = Integer.valueOf(buffer[0].split("/")[0]);
                int hour = Integer.valueOf((buffer[1].split("\\."))[0]);
                int min = Integer.valueOf((buffer[1].split("\\."))[1]);
                int sec = Integer.valueOf((buffer[1].split("\\."))[2]);

                Calendar cd = Calendar.getInstance();
                cd.set(year, month - 1, day, hour, min, sec);
                cd.set(Calendar.MILLISECOND, 0);

                //if (cd.compareTo(lastValue) > 0) lastValue = cd;

                if (cd.compareTo(lastValue) < 0){
                    flag = false;
                }else if(cd.compareTo(lastValue) == 0 && s.compareTo(lastStringValue) == 0){
                    flag = false;
                }else{
                        if(!buffer[buffer.length - 1].contains("-") || !buffer[buffer.length - 2].contains("-") || !buffer[buffer.length - 3].contains("-") || !buffer[buffer.length - 4].contains("-") || !buffer[buffer.length - 5].contains("-")) {
                            System.out.println("readed : " +  cd.getTime() + " >= " + lastValue.getTime() );
                            newValue.add(s);
                        } else {
                            //System.out.println("Nuovo record letto, ma non c'?? la temperatura + " + cd.compareTo(lastValue));
                        }
                    }

            }

            for(int i=0; i<newValue.size(); i++){
                //System.out.println("NEW VALUE = " + newValue.get(i) + " OLD: " + lastStringValue);
                String[] buffer = newValue.get(i).split(" ");

                int year = Integer.valueOf(buffer[0].split("/")[2]);
                int month = Integer.valueOf(buffer[0].split("/")[1]);
                int day = Integer.valueOf(buffer[0].split("/")[0]);
                int hour = Integer.valueOf((buffer[1].split("\\."))[0]);
                int min = Integer.valueOf((buffer[1].split("\\."))[1]);
                int sec = Integer.valueOf((buffer[1].split("\\."))[2]);

                Calendar cd = Calendar.getInstance();
                cd.set(year, month - 1, day, hour, min, sec);
                cd.set(Calendar.MILLISECOND, 0);

                if (cd.compareTo(lastValue) >= 0){
                    lastValue = cd;
                    lastValue.set(Calendar.MILLISECOND, 0);
                    lastStringValue = newValue.get(i);
                }

                //TODO: checkAlarm(newValue.get(i));

                streamOut.writeObject(newValue.get(i));
                streamOut.flush();
                if (!streamIn.readBoolean()) socket.close();
                System.out.println("[ServerClient]> Send new value");
                readed = readed+1;
            }


            if(readed > 0) {
                streamOut.writeObject("end");
                streamOut.flush();
                streamIn.readBoolean();
                System.out.println("end");
            }

        }

    }

    public void setSocket(Socket s){
        socket = s;
    }

    public Ricovero getRicovero() {
        return ricovero;
    }

    public ObjectOutputStream getStreamOut() {
        return streamOut;
    }

    public void setStreamOut(ObjectOutputStream streamOut) {
        this.streamOut = streamOut;
    }

    public ObjectInputStream getStreamIn() {
        return streamIn;
    }

    public void setStreamIn(ObjectInputStream streamIn) {
        this.streamIn = streamIn;
    }

    public void setRicovero(Ricovero ricovero) {
        this.ricovero = ricovero;
    }

    public Socket getAlarmSocket() {
        return alarmSocket;
    }

    public void setAlarmSocket(Socket alarmSocket) {
        this.alarmSocket = alarmSocket;
    }

    public ObjectInputStream getAlarmStreamIn() {
        return alarmStreamIn;
    }

    public void setAlarmStreamIn(ObjectInputStream alarmStreamIn) {
        this.alarmStreamIn = alarmStreamIn;
    }

    public ObjectOutputStream getAlarmStreamOut() {
        return alarmStreamOut;
    }

    public void setAlarmStreamOut(ObjectOutputStream alarmStreamOut) {
        this.alarmStreamOut = alarmStreamOut;
    }


}

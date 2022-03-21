package sistemaMonitoraggio;

import srcClass.Ricovero;
import srcClass.parametriVitali;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

public class DataGen {
    public static void genData(Ricovero ricovero) {
        System.out.println("Gendata()");
        for(int i=0; i<(120); ++i){
            try {
                addData(ricovero, monitorStanza.getRandParameter(), 4);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File nf = new File("src/sistemaMonitoraggio/" + ricovero.getIdRicovero() + "_" + ricovero.getPazienteRicoverato().getCodSanitario() + ".txt");
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(nf));
            LocalDateTime lc = LocalDateTime.now();
            Calendar c = Calendar.getInstance();
            //<***> c.setTimeZone(TimeZone.getTimeZone("Italy")); <***>
            c.set(Calendar.HOUR_OF_DAY, lc.getHour());
            c.set(Calendar.MINUTE, lc.getMinute());

            for(int i=1; i<=(120); ++i){
                parametriVitali param = new parametriVitali();
                param = (monitorStanza.getRandParameter());
                SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH.mm.ss");


                /*if(option == 4){
                    writer.write(""+ format1.format((c.getTime())) + " " +
                            param.getEcg() + " " +
                            param.getSistolica() + " " +
                            param.getDiastolica() + " " +
                            param.getSatOss() + " " +
                            param.getTemperatura() + "\n");
                }*/
                if(i%1 == 0){
                    writer.write(""+ format1.format((c.getTime())) + " " +"-" + " " +param.getSistolica() + " " +param.getDiastolica() + " " +"-" + " " +"-" + "\n");
                }if(i%2 == 0){
                    writer.write(""+ format1.format((c.getTime())) + " " +"-" + " " +"-" + " " +"-" + " " +param.getSatOss() + " " +"-" + "\n");
                }if(i%3 == 0){
                    writer.write(""+ format1.format((c.getTime())) + " " +"-" + " " +"-" + " " +"-" + " " +"-" + " " + param.getTemperatura() + "\n");
                }if(i%5 == 0){
                    writer.write(""+ format1.format((c.getTime())) + " " + param.getEcg() + " " + "-" + " " + "-" + " " + "-" + " " + "-" + "\n");
                }

                c.add(Calendar.MINUTE, -1);
            }
            writer.close();
        } catch (IOException e) {
            System.err.println(e);
        }

        System.out.println("finished");
    }

    public static synchronized void addData(Ricovero ric, parametriVitali param, int option) throws IOException{
        LocalDateTime lc = LocalDateTime.now();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, lc.getHour());
        c.set(Calendar.MINUTE, lc.getMinute());

        File nf = new File("src/sistemaMonitoraggio/" + ric.getIdRicovero() + "_" + ric.getPazienteRicoverato().getCodSanitario() + ".txt");

        BufferedWriter writer = null;
        ArrayList<String> lineOfFile = null;

        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH.mm.ss");

        if(nf.exists()) {
            lineOfFile = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(nf));
            String currLine;
            //salvo una copia del file su lineOfFile per aggiungere in tesat l'ultimo dato
            while( (currLine = reader.readLine()) != null)
                lineOfFile.add(currLine);

            reader.close();
        }

        writer = new BufferedWriter(new FileWriter(nf));

        if(option == 4){
            writer.write(""+ format1.format((c.getTime())) + " " +
                    param.getEcg() + " " +
                    param.getSistolica() + " " +
                    param.getDiastolica() + " " +
                    param.getSatOss() + " " +
                    param.getTemperatura() + "\n");
        }
        else if(option == 1){
            writer.write(""+ format1.format((c.getTime())) + " " +"-" + " " + param.getSistolica() + " " + param.getDiastolica() + " " +"-" + " " +"-" + "\n");
        }else if(option == 2){
            writer.write(""+ format1.format((c.getTime())) + " " +"-" + " " +"-" + " " +"-" + " " + param.getSatOss() + " " +"-" + "\n");
        }else if(option == 3){
            writer.write(""+ format1.format((c.getTime())) + " " +"-" + " " +"-" + " " +"-" + " " +"-" + " " + param.getTemperatura() + "\n");
        }else if(option == 5){
            writer.write(""+ format1.format((c.getTime())) + " " + param.getEcg() + " " + "-" + " " + "-" + " " + "-" + " " + "-" + "\n");
        }

        if(lineOfFile != null)
            for(String temp : lineOfFile){
                writer.write(temp+"\n");
            }

        writer.close();

    }
}

package sistemaMonitoraggio;

import srcClass.Ricovero;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class starter {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        ArrayList<Float> temp = new ArrayList<>();
        ArrayList<Ricovero> ricoveri = new ArrayList<>();
        ricoveri = (new dataBaseForMonitor()).getRicoveri();
        for(int i=0; i<ricoveri.size(); ++i)
            DataGen.genData(ricoveri.get(i));

        (new ServerCentrale()).start();
    }
}

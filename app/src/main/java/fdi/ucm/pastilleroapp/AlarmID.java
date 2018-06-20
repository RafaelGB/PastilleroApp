package fdi.ucm.pastilleroapp;

import java.util.ArrayList;

public class AlarmID {

    private ArrayList<Integer> idAlarmas;

    public AlarmID() {
        idAlarmas = new ArrayList<>();
    }

    public ArrayList<Integer> getIdAlarmas() {
        return idAlarmas;
    }

    public void addAlarma(int nAlarma) {
        idAlarmas.add(nAlarma);
    }

    public void deleteAlarms() {
        idAlarmas.clear();
    }

}

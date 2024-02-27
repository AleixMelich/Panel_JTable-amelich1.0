package org.amelich;

import java.io.Serializable;


/**
 * @author Aleix Melich
 * <p>
 * Aquesta classe és un POJO que representa un alumne.
 */
public class AlumnePojo implements Serializable {
    private String nomCognom;
    private int edat;
    private double nota;
    private boolean fct;

    public AlumnePojo() {
    }

    /**
     * @param nomCognom
     * @param edat
     * @param nota
     * @param fct
     */
    public AlumnePojo(String nomCognom, int edat, double nota, boolean fct) {
        this.nomCognom = nomCognom;
        this.edat = edat;
        this.nota = nota;
        this.fct = fct;
    }

    public String getNomCognom() {
        return nomCognom;
    }

    public void setNomCognom(String nomCognom) {
        this.nomCognom = nomCognom;
    }

    public int getEdat() {
        return edat;
    }

    public void setEdat(int edat) {
        this.edat = edat;
    }

    public double getNota() {
        return nota;
    }

    public void setNota(double nota) {
        this.nota = nota;
    }

    public boolean isFct() {
        return fct;
    }

    public void setFct(boolean fct) {
        this.fct = fct;
    }
}
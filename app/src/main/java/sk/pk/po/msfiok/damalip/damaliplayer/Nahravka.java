package sk.pk.po.msfiok.damalip.damaliplayer;

public class Nahravka {
    String nazov;
    String interpret;
    String zdroj;
    String date;
    boolean oblubene;

    public Nahravka(String nazov, String interpret, String zdroj, String date, boolean oblubene) {
        this.nazov = nazov;
        this.interpret = interpret;
        this.zdroj = zdroj;
        this.date = date;
        this.oblubene = oblubene;
    }

    public String getNazov() {
        return nazov;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }

    public String getInterpret() {
        return interpret;
    }

    public void setInterpret(String interpret) {
        this.interpret = interpret;
    }

    public String getZdroj() {
        return zdroj;
    }

    public void setZdroj(String zdroj) {
        this.zdroj = zdroj;
    }

    public boolean isOblubene() {
        return oblubene;
    }

    public void setOblubene(boolean oblubene) {
        this.oblubene = oblubene;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Názov      " + nazov + "\n" +
                "Interpret  " + interpret;
    }
}

package sk.pk.po.msfiok.damalip.damaliplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Nahravky {

    public static final int DATE_SORT = 1, AZ_SORT = 0, FAVOURITES_ONLY = 2;
    private static Nahravky instance;
    private List<Nahravka> nahravky;
    private List<Nahravka> savedNahravky;

    private Nahravky() {
        nahravky = new ArrayList<>();
        savedNahravky = new ArrayList<>();
    }

    public static Nahravky getInstance() {
        if (instance == null)
            instance = new Nahravky();
        return instance;
    }

    public void setOrder(int sort) {
        if (sort == AZ_SORT) {
            reviveSaved();
            Collections.sort(nahravky, new Comparator<Nahravka>() {
                @Override
                public int compare(Nahravka nahravka1, Nahravka nahravka2) {
                    return nahravka1.getNazov().compareTo(nahravka2.getNazov());
                }
            });
        } else if (sort == DATE_SORT) {
            reviveSaved();
            Collections.sort(nahravky, new Comparator<Nahravka>() {
                @Override
                public int compare(Nahravka nahravka, Nahravka t1) {
                    return nahravka.getDate().compareTo(t1.getDate());
                }
            });
        } else if (sort == FAVOURITES_ONLY) {
            List<Nahravka> favourites = new ArrayList<>();
            for (Nahravka nahravka : nahravky) {
                if (!nahravka.isOblubene())
                    savedNahravky.add(nahravka);
                else
                    favourites.add(nahravka);
            }
            nahravky.clear();
            nahravky.addAll(favourites);
        }
    }

    private void reviveSaved() {
        if (savedNahravky.size() > 0) {
            nahravky.addAll(savedNahravky);
            savedNahravky.clear();
        }
    }

    public void setNahravky(List<Nahravka> nahravky) {
        this.nahravky = nahravky;
    }

    public List<Nahravka> getNahravky() {
        return nahravky;
    }
}

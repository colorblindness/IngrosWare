package best.reich.ingros.macro;

import me.xenforu.kelo.traits.Labelable;

/**
 * made by Xen for IngrosWare
 * at 1/3/2020
 **/
public class Macro implements Labelable {
    private String label, text;
    private int key;

    public Macro(String label, int key, String text) {
        this.label = label;
        this.text = text;
        this.key = key;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public String getText() {
        return text;
    }

    public int getKey() {
        return key;
    }
}

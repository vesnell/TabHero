package pl.tabhero.utils;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class PolishComparator implements Comparator<String>{

	@Override
    public int compare(String s1, String s2) {
        Collator c = Collator.getInstance(new Locale("pl", "PL"));
        return c.compare(s1, s2);
    }

}

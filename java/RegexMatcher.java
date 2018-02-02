/*
 * Regular expression implementation using recursion
 * Supports only ^ * . $
 * C version written by Rob Pike
 * See first chapter of "Beautiful Code"
 */
import java.io.*;

public class RegexMatcher {
    private char[] re;
    private char[] text;

    public RegexMatcher(String pattern) {
        this.re = pattern.toCharArray();
    }

    public boolean match(String text) {
        this.text = text.toCharArray();
        return matchInternal(0, 0);
    }

    private boolean matchInternal(int p, int t) {
        if (p == re.length)
            return true;
        if (re[p] == '^')
            return matchInternal(p + 1, t);
        do {
            if (matchHere(p, t))
                return true;
        } while (t++ != text.length);
        return false;
    }

    private boolean matchHere(int p, int t) {
        if (p == re.length)
            return true;
        if (p != re.length - 1 && re[p + 1] == '*')
            return matchStar(re[p], p + 2, t);
        if (re[p] == '$' && p == re.length - 1)
            return t == text.length;
        if (t != text.length && (re[p] == '.' || re[p] == text[t]))
            return matchHere(p + 1, t + 1);
        return false;
    }

    /* matchStar: search for c*re at beginning of text */
    private boolean matchStar(char c, int p, int t) {
        do {
            if (matchHere(p, t))
                return true;
        } while (t != text.length && (text[t++] == c || c == '.'));
        return false;
    }

    /* matchStarV2: leftmost longest search for c*re */
    boolean matchStarV2(char c, int p, int t) {
        int cur = t;
        for (; t != text.length && (text[t] == c || c == '.'); t++)
            ;
        do {   /* * matches zero or more */
            if (matchHere(p, t))
                return true;
        } while (t-- > cur);
        return false;
    }

    private boolean grep(BufferedReader reader, String name) {
        int nmatch = 0;
        String text;
        try {
            while ((text = reader.readLine()) != null) {
                if (match(text)) {
                    nmatch++;
                    if (name != null)
                        System.out.printf("%s:", name);
                    System.out.printf("%s\n", text);
                }
            }
        } catch (IOException ex) {

        }
        return nmatch > 0;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.print("usage: grep pattern [file ...]\n");
            System.exit(1);
            return;
        }
        int nmatch = 0;
        RegexMatcher rm = new RegexMatcher(args[1]);
        if (args.length < 3) {
            if (rm.grep(new BufferedReader(new InputStreamReader(System.in)), null))
                nmatch++;
        } else {
            for (int i = 2; i < args.length; i++) {
                BufferedReader br;
                try {
                    br = new BufferedReader(new FileReader(new File(args[i])));
                } catch (FileNotFoundException ex) {
                    System.err.printf("grep: can't open %s\n", args[i]);
                    continue;
                }
                if (rm.grep(br, args.length > 3 ? args[i] : null))
                    nmatch++;
            }
        }
        System.exit(nmatch == 0 ? 1 : 0);
        return;
    }

}

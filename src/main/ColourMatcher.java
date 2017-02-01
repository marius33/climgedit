package main;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Marius on 01/02/2017.
 */
public class ColourMatcher {

    public static final int OP_E = 0;
    public static final int OP_GE = 1;
    public static final int OP_GT = 2;
    public static final int OP_LE = -1;
    public static final int OP_LT = -2;

    private final ChannelMatcher a;
    private final ChannelMatcher r;
    private final ChannelMatcher g;
    private final ChannelMatcher b;

    public ColourMatcher(String arg) {

        Pattern p = Pattern.compile("[<>=]{1,2}+[0-9a-fA-F]{2}");
        Matcher m = p.matcher(arg);

        a = new ChannelMatcher(m.group());
        r = new ChannelMatcher(m.group());
        g = new ChannelMatcher(m.group());
        b = new ChannelMatcher(m.group());

    }

    public boolean matches(Color c) {
        return a.matches(c.getAlpha()) && r.matches(c.getRed()) && g.matches(c.getGreen()) && b.matches(c.getBlue());
    }

    private final class ChannelMatcher {


        private final int v;
        private final int op;

        ChannelMatcher(String arg) {

            int len = arg.length();
            v = Integer.parseInt(arg.substring(len - 2, len), 16);
            if (arg.startsWith("=")) {
                op = OP_E;
            } else if (arg.startsWith(">=")) {
                op = OP_GE;
            } else if (arg.startsWith(">")) {
                op = OP_GT;
            } else if (arg.startsWith("<=")) {
                op = OP_LE;
            } else if (arg.startsWith("<")) {
                op = OP_LT;
            } else
                op = OP_E;

        }

        public boolean matches(int v2) {
            switch (op) {
                case OP_E:
                    return v2 == v;
                case OP_GE:
                    return v2 >= v;
                case OP_GT:
                    return v2 > v;
                case OP_LE:
                    return v2 <= v;
                case OP_LT:
                    return v2 < v;
                default:
                    return false;
            }
        }


    }

}

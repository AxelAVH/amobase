package de.amo.tools;

/**
 * Created by private on 14.01.2016.
 */
public class ConditionChecker {

    public static boolean check(String value, String cond) {

        if (cond == null) {
            return true;
        }
        cond = cond.trim();
        if ("".equals(cond)) {
            return true;
        }

        boolean negate = false;
        if (cond.startsWith("!")) {
            negate = true;
            cond = cond.substring(1);
            cond = cond.trim();
        }

        cond  = cond.toUpperCase();
        value = value.toUpperCase();


        try {
            if (cond.startsWith("*") && cond.endsWith("*")) {
                cond = cond.substring(1);
                cond = cond.substring(0, cond.length() - 1);
                if (value.contains(cond)) {
                    return !negate;
                } else {
                    return negate;
                }
            }
            if (cond.startsWith("*")) {
                cond = cond.substring(1);
                if (value.endsWith(cond)) {
                    return !negate;
                } else {
                    return negate;
                }
            }
            if (cond.endsWith("*")) {
                cond = cond.substring(0, cond.length() - 1);
                if (value.startsWith(cond)) {
                    return !negate;
                } else {
                    return negate;
                }
            }
            if (value.equals(cond)) {
                return !negate;
            } else {
                return negate;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
            return true;
        }
    }

    public static boolean check(int value, int condMin, int condMax) {
        if (condMax == Integer.MIN_VALUE) {
            condMax = Integer.MAX_VALUE;
        }
        return (value >= condMin && value <= condMax);
    }

    public static boolean checkDateString(String value, String condMin, String condMax) {
        if (condMin == null || condMin.trim().equals("")) {
            condMin = "00000000";
        }
        if (condMax == null || condMax.trim().equals("")) {
            condMax = "99999999";
        }

        return (value.compareTo(condMin) > -1) && (condMax.compareTo(value) > -1);

    }
}

package penoles.oraclebdutils.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author joseluischavez
 */
public class StringUtils {

    public static String toLowerScoreCase(String value) {

        value = value.contains("_") ? value.toLowerCase() : value;

        boolean allCaps = true;
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isUpperCase(value.charAt(i)) && !Character.isDigit(value.charAt(i))) {
                allCaps = false;
                break;
            }
        }

        value = allCaps ? value.toLowerCase() : value;

        StringBuilder builder = new StringBuilder();
        if (value != null) {
            builder.append(String.valueOf(value.charAt(0)).toLowerCase());
            for (int i = 1; i < value.length(); i++) {
                char c = value.charAt(i);

                if (Character.isUpperCase(c) && builder.charAt(builder.length() - 1) != '_') {
                    builder.append('_');
                }

                builder.append(String.valueOf(c).toLowerCase());
            }
        }
        return builder.toString().toLowerCase();
    }

    public static String toFirstUpperCased(String value) {
        StringBuilder builder = new StringBuilder();
        if (value != null) {
            if (value.length() > 2) {
                builder.append(String.valueOf(value.charAt(0)).toUpperCase());
                builder.append(value.substring(1, value.length()));
            } else {
                builder.append(value.toUpperCase());
            }
        }
        return builder.toString();
    }

    public static void deleteDuplicated(List<String> list) {
        Set<String> s = new LinkedHashSet<String>(list);
        list.clear();
        list.addAll(s);
        list.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public static String getStringUIDD() {
        /*String uuid = UUID.randomUUID().toString();
        uuid = uuid.substring(uuid.length() - 3, uuid.length());
        //if (Character.isDigit(s.charAt(0))) {
        Random r = new Random();
        char c = (char) (r.nextInt(26) + 'a');
        uuid = c + uuid;
        //}
        return uuid;*/
        String uuid = randomString(4);
        Random r = new Random();
        char c = (char) (r.nextInt(26) + 'a');
        uuid = c + uuid;
        return uuid.toLowerCase();
    }

    public static String toLowerCamelCase(String s) {

        String camelCaseString = "";

        String[] parts = s.split("_");

        if (parts != null && parts.length > 0) {
            camelCaseString = camelCaseString.concat(parts[0].toLowerCase());

            for (int i = 1; i < parts.length; i++) {
                String part = parts[i];
                camelCaseString = camelCaseString + toProperCase(part);
            }
        }

        return camelCaseString;
    }

    public static String toUpperCamelCase(String s) {
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
    }

    static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    /**
     * Creates strings like this: Uppercased_string
     *
     * @param s in param
     * @return out param
     */
    public static String toUpperCamelCaseNew(String s) {
        StringBuilder builder = new StringBuilder();

        if (s != null && !s.isEmpty()) {
            builder.append(String.valueOf(s.charAt(0)).toUpperCase());
            builder.append(s.substring(1, s.length()));
        }

        return builder.toString();
    }

    public static void main(String[] args) {

        int number = 50;
        int times = 10000;

        int suma = 0;
        for (int j = 0; j < times; j++) {

            Set<String> set = new HashSet<>();
            for (int i = 0; i < number; i++) {
                set.add(getStringUIDD());
            }

            int duplicados = number - set.size();
            //System.out.println("Duplicados = " + duplicados);
            suma += duplicados;
        }

        System.err.println("Total de errores:" + suma);
    }

    static String randomString(int len) {
        String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString().toLowerCase();
    }

    private static int getListTest(int iterations) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            String s = getStringUIDD();

            boolean exist = false;
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).equals(s)) {
                    System.err.println("Elemento duplicado");
                    exist = true;
                    return list.size();
                }
            }

            if (!exist) {
                System.out.println(s);
                list.add(s);
            }
        }

        return iterations;
    }

}

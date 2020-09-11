package joseluisch.jdbc_utils.utils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jose Luis Ch.
 */
public class ReflectUtils {

    public static boolean isClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println(className + " is Not a Class");
            return false;
        }
    }

    public static Map<String, Object> fillResultSet(ResultSet resultSet, Object mObject) throws InstantiationException, IllegalArgumentException, IllegalAccessException, SQLException {

        Map<String, Object> map = new HashMap<>();

        Class aClass = mObject.getClass();

        while (resultSet.next()) {
            Object object = aClass.newInstance();
            for (Field f : aClass.getDeclaredFields()) {
                f.setAccessible(true);
                if (f.getType().getSimpleName().equalsIgnoreCase(String.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Double.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Float.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Long.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Boolean.class.getSimpleName())
                        || f.getType().getSimpleName().equalsIgnoreCase(Integer.class.getSimpleName())) {
                    Object o = resultSet.getObject(f.getName());
                    f.set(object, o);
                }
            }
            map.put(object.toString(), object);
        }

        return map;
    }

    public static List<String> getChildClases(Class mClass) {

        List<String> list = new ArrayList<>();
        list.add(mClass.getSimpleName());

        for (Field field : mClass.getDeclaredFields()) {
            field.setAccessible(true);

            if (!field.getType().getSimpleName().equalsIgnoreCase(String.class.getSimpleName())
                    && !field.getType().getSimpleName().equalsIgnoreCase(Double.class.getSimpleName())
                    && !field.getType().getSimpleName().equalsIgnoreCase(Long.class.getSimpleName())
                    && !field.getType().getSimpleName().equalsIgnoreCase(Boolean.class.getSimpleName())
                    && !field.getType().getSimpleName().equalsIgnoreCase(Float.class.getSimpleName())
                    && !field.getType().getSimpleName().equalsIgnoreCase(Integer.class.getSimpleName())) {

                Class fieldClass = field.getType();
                if (isClass(fieldClass.getName())) {
                    list.addAll(getChildClases(fieldClass));
                }
            }
        }

        StringUtils.deleteDuplicated(list);

        return list;
    }

    public static String[] getValidFields(Class objectClass) {

        List<String> list = new ArrayList<>();

        for (Field field : objectClass.getDeclaredFields()) {
            field.setAccessible(true);

            if (field.getType().getSimpleName().equalsIgnoreCase(String.class.getSimpleName())
                    || field.getType().getSimpleName().equalsIgnoreCase(Double.class.getSimpleName())
                    || field.getType().getSimpleName().equalsIgnoreCase(Long.class.getSimpleName())
                    || field.getType().getSimpleName().equalsIgnoreCase(Boolean.class.getSimpleName())
                    || field.getType().getSimpleName().equalsIgnoreCase(Integer.class.getSimpleName())) {

                list.add(field.getName());
            }
        }

        return list.toArray(new String[list.size()]);
    }

    public static Object getChildObjectInstance(Object mObject, String name) {

        Object object = null;
        for (Field field : mObject.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (field.getType().getSimpleName().equalsIgnoreCase(name)) {
                try {
                    object = field.getType().newInstance();
                } catch (InstantiationException ex) {
                    Logger.getLogger(ReflectUtils.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(ReflectUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return object;
    }

    public static String getPropperFieldType(String type) {
        type = type.toLowerCase();
        if (type.equals(Types.BIGINT)) {
            return Integer.class.getSimpleName();
        } else if (type.contains("bit")) {
            return Integer.class.getSimpleName();
        } else if (type.contains("blob")) {
            return String.class.getSimpleName();
        } else if (type.contains("nchar")) {
            return String.class.getSimpleName();
        } else if (type.contains("varchar2")) {
            return String.class.getSimpleName();
        } else if (type.contains("varchar")) {
            return String.class.getSimpleName();
        } else if (type.contains("nvarchar2")) {
            return String.class.getSimpleName();
        } else if (type.contains("bit")) {
            return Integer.class.getSimpleName();
        } else if (type.contains("boolean")) {
            return Boolean.class.getSimpleName();
        } else if (type.contains("char")) {
            return String.class.getSimpleName();
        } else if (type.contains("nclob")) {
            return String.class.getSimpleName();
        } else if (type.contains("clob")) {
            return String.class.getSimpleName();
        } else if (type.contains("decimal")) {
            return Double.class.getSimpleName();
        } else if (type.contains("double")) {
            return Double.class.getSimpleName();
        } else if (type.contains("float")) {
            return Float.class.getSimpleName();
        } else if (type.contains("integer")) {
            return Integer.class.getSimpleName();
        } else if (type.contains("longnvarchar")) {
            return String.class.getSimpleName();
        } else if (type.contains("longvarchar")) {
            return String.class.getSimpleName();
        } else if (type.contains("numeric")) {
            return Integer.class.getSimpleName();
        } else if (type.contains("nvarchar")) {
            return String.class.getSimpleName();
        } else if (type.contains("timestamp")) {
            return Long.class.getSimpleName();
        } else if (type.contains("date")) {
            return Long.class.getSimpleName();
        } else if (type.contains("bigint")) {
            return Long.class.getSimpleName();
        } else if (type.contains("int")) {
            return Integer.class.getSimpleName();
        } else if (type.contains("number")) {
            return Integer.class.getSimpleName();
        }

        return String.class.getSimpleName();
    }

    public static boolean isValidField(Field field) {
        return field.getType().getSimpleName().equalsIgnoreCase(String.class.getSimpleName())
                || field.getType().getSimpleName().equalsIgnoreCase(Double.class.getSimpleName())
                || field.getType().getSimpleName().equalsIgnoreCase(Long.class.getSimpleName())
                || field.getType().getSimpleName().equalsIgnoreCase(Boolean.class.getSimpleName())
                || field.getType().getSimpleName().equalsIgnoreCase(Integer.class.getSimpleName());
    }

    public static boolean isNormalized(Class objectClass) {

        if (objectClass == null) {
            return false;
        }

        Stack<Class> nodeStack = new Stack<>();
        nodeStack.push(objectClass);

        while (!nodeStack.empty()) {

            Class classNode = nodeStack.pop();

            for (Class currentClass : nodeStack) {
                if (classNode.getClass().getSimpleName().equals(currentClass.getClass().getSimpleName())) {
                    return false;
                }
            }

            for (Field field : classNode.getDeclaredFields()) {
                field.setAccessible(true);
                if (!isValidField(field)) {
                    Class fieldClass = field.getType();
                    if (isClass(fieldClass.getName())) {
                        nodeStack.push(fieldClass);
                    }
                }
            }
        }

        return true;
    }

}

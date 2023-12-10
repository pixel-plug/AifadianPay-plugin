package com.meteor.aifadianpay.mysql.column;

public class Column {

    private boolean primary;
    private String name;
    private ColumnType type;
    private int[] m;

    public String toStringM() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < m.length; i++) {
            if (i == (m.length - 1)) {
                sb.append(m[i]);
            } else {
                sb.append(m[i]).append(", ");
            }
        }
        return sb.toString();
    }

    public ColumnType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isPrimary() {
        return primary;
    }

    public static Column of(String name, ColumnType type) {
        Column column = new Column();
        column.name = name;
        column.primary = false;
        column.type = type;
        return column;
    }

    public static Column of(String name, ColumnType type, boolean primary) {
        Column column = new Column();
        column.name = name;
        column.primary = primary;
        column.type = type;
        return column;
    }

    public static Column of(String name, ColumnType type, int... m) {
        Column column = new Column();
        column.name = name;
        column.primary = false;
        column.type = type;
        column.m = m;
        return column;
    }

    public static Column of(String name, ColumnType type, boolean primary, int... m) {
        Column column = new Column();
        column.name = name;
        column.primary = primary;
        column.type = type;
        column.m = m;
        return column;
    }

    public static boolean hasBracket(ColumnType column) {
        return getColumnType(column).hasBracket();
    }

    public static boolean hasM(ColumnType column) {
        return getColumnType(column).hasM();
    }

    public static Type getColumnType(ColumnType column) {
        if (column instanceof Integer) {
            return Type.INTEGER;
        } else if (column instanceof Text) {
            return Type.TEXT;
        } else if (column instanceof Char) {
            return Type.CHAR;
        } else if (column instanceof Float) {
            return Type.FLOAT;
        } else {
            return Type.BOB;
        }
    }

    interface ColumnType {

    }

    public enum Integer implements ColumnType {
        TINYINT,
        SMALLINT,
        MEDIUMINT,
        INT,
        BIGINT;
    }

    public enum Char implements ColumnType {
        CHAR,
        VARCHAR;
    }

    public enum Text implements ColumnType {
        TINYTEXT,
        TEXT,
        MEDIUMTEXT,
        LONGTEXT;
    }
    public enum Float implements ColumnType {
        FLOAT,
        DOUBLE;
    }

    public enum Bob implements ColumnType {
        TINYBLOB,
        BLOB,
        MEDIUMBLOB,
        LONGBLOB;
    }

    public enum Type {
        INTEGER(true, true), CHAR(true, true), TEXT, FLOAT(true, true), BOB;

        private boolean bracket;
        private boolean m;

        Type(boolean bracket, boolean m) {
            this.bracket = bracket;
            this.m = m;
        }

        Type() {
        }

        public boolean hasBracket() {
            return bracket;
        }

        public boolean hasM() {
            return m;
        }
    }
}

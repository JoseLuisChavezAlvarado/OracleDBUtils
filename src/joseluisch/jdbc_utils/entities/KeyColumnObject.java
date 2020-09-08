package joseluisch.jdbc_utils.entities;

/**
 *
 * @author Jose Luis Ch.
 */
public class KeyColumnObject extends SuperClass {

    private String constraint_name;
    private String table_schema;
    private String table_name;
    private String column_name;
    private String referenced_table_name;
    private String referenced_column_name;
    private String constraint_type;

    public KeyColumnObject() {
    }

    public KeyColumnObject(String constraint_name, String table_schema, String table_name, String column_name, String referenced_table_name, String referenced_column_name, String constraint_type) {
        this.constraint_name = constraint_name;
        this.table_schema = table_schema;
        this.table_name = table_name;
        this.column_name = column_name;
        this.referenced_table_name = referenced_table_name;
        this.referenced_column_name = referenced_column_name;
        this.constraint_type = constraint_type;
    }

    public String getConstraint_name() {
        return constraint_name;
    }

    public void setConstraint_name(String constraint_name) {
        this.constraint_name = constraint_name;
    }

    public String getTable_schema() {
        return table_schema;
    }

    public void setTable_schema(String table_schema) {
        this.table_schema = table_schema;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getColumn_name() {
        return column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public String getReferenced_table_name() {
        return referenced_table_name;
    }

    public void setReferenced_table_name(String referenced_table_name) {
        this.referenced_table_name = referenced_table_name;
    }

    public String getReferenced_column_name() {
        return referenced_column_name;
    }

    public void setReferenced_column_name(String referenced_column_name) {
        this.referenced_column_name = referenced_column_name;
    }

    public String getConstraint_type() {
        return constraint_type;
    }

    public void setConstraint_type(String constraint_type) {
        this.constraint_type = constraint_type;
    }

    @Override
    public String toString() {
        return constraint_name;
    }

}

package joseluisch.jdbc_utils.entities;

/**
 *
 * @author joseluischavez
 */
public class TableDetails {

    private String field;
    private String type;

    private String nullable;
    private String key;

    public TableDetails() {
    }

    public TableDetails(String field, String type) {
        this.field = field;
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNullable() {
        return nullable;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}

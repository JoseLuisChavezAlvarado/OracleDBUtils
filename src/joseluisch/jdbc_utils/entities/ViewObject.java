package joseluisch.jdbc_utils.entities;

/**
 *
 * @author Jose Luis Chavez
 */
public class ViewObject {
    
    private String TABLE_NAME;

    public ViewObject() {
    }

    public ViewObject(String TABLE_NAME) {
        this.TABLE_NAME = TABLE_NAME;
    }

    public String getTABLE_NAME() {
        return TABLE_NAME;
    }

    public void setTABLE_NAME(String TABLE_NAME) {
        this.TABLE_NAME = TABLE_NAME;
    }    
    
}

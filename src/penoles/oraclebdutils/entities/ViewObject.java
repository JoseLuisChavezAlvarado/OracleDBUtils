package penoles.oraclebdutils.entities;

/**
 *
 * @author Jose Luis Chavez
 */
public class ViewObject {

    private String table_name;

    public ViewObject() {
    }

    public ViewObject(String table_name) {
        this.table_name = table_name;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

}

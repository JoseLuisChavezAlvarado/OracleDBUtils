package penoles.oraclebdutils.singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import penoles.oraclebdutils.controllers.DatabaseConfigController;
import penoles.oraclebdutils.controllers.UserTableController;
import penoles.oraclebdutils.controllers.WMultivluadaController;
import penoles.oraclebdutils.information_schema.InformationSchemaColumnsController;
import penoles.oraclebdutils.information_schema.InformationSchemaViewsController;
import penoles.oraclebdutils.entities.KeyColumnObject;
import penoles.oraclebdutils.entities.TableDetails;
import penoles.oraclebdutils.entities.UserTable;
import penoles.oraclebdutils.entities.ViewObject;
import penoles.oraclebdutils.entities.VwMultivaluada;

/**
 *
 * @author Jose Luis Ch.
 */
public class DataInstance {

    public static DataInstance instance;

    private Map<String, List<TableDetails>> tableDetailsMap;
    private List<KeyColumnObject> keyColumnObjectList;
    private List<VwMultivaluada> vwMultivaluadaList;
    private List<ViewObject> viewObjectList;
    private List<UserTable> userTablesList;

    public static DataInstance getInstance() {
        if (instance == null) {
            instance = new DataInstance();
        }
        return instance;
    }

    public List<KeyColumnObject> getKeyColumnObjectList() {
        if (keyColumnObjectList == null || keyColumnObjectList.isEmpty()) {
            keyColumnObjectList = InformationSchemaColumnsController.get();
        }
        return keyColumnObjectList;
    }

    public List<ViewObject> getViewObjectList() {
        if (viewObjectList == null || viewObjectList.isEmpty()) {
            viewObjectList = InformationSchemaViewsController.get();
        }
        return viewObjectList;
    }

    public List<TableDetails> getTableDetailsList(String tableName) {

        if (tableDetailsMap == null) {
            tableDetailsMap = new HashMap<>();
        }

        List<TableDetails> list = tableDetailsMap.get(tableName);
        if (list == null || list.isEmpty()) {
            Map<String, TableDetails> map = DatabaseConfigController.getTableDetails(tableName);
            list = new ArrayList<>();
            for (Map.Entry<String, TableDetails> entry : map.entrySet()) {
                TableDetails value = entry.getValue();
                list.add(value);
            }
            tableDetailsMap.put(tableName, list);
        }
        return list;
    }

    public List<UserTable> getUserTablesList() {
        if (userTablesList == null || userTablesList.isEmpty()) {
            userTablesList = UserTableController.get();
        }
        return userTablesList;
    }

    public List<VwMultivaluada> getVwMultivaluadaList() {
        if (vwMultivaluadaList == null || vwMultivaluadaList.isEmpty()) {
            vwMultivaluadaList = WMultivluadaController.get();
        }
        return vwMultivaluadaList;
    }

}

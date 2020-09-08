package joseluisch.jdbc_utils.singleton;

import joseluisch.jdbc_utils.entities.UserTable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import joseluisch.jdbc_utils.controllers.DatabaseConfigController;
import joseluisch.jdbc_utils.controllers.UserTableController;
import joseluisch.jdbc_utils.controllers.information_schema.InformationSchemaColumnsController;
import joseluisch.jdbc_utils.controllers.information_schema.InformationSchemaViewsController;
import joseluisch.jdbc_utils.entities.KeyColumnObject;
import joseluisch.jdbc_utils.entities.TableDetails;
import joseluisch.jdbc_utils.entities.ViewObject;

/**
 *
 * @author Jose Luis Ch.
 */
public class DataInstance {

    public static DataInstance instance;

    private Map<String, List<TableDetails>> tableDetailsMap;
    private List<KeyColumnObject> keyColumnObjectList;
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

}

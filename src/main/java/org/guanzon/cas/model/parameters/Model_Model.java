package org.guanzon.cas.model.parameters;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import javax.sql.rowset.CachedRowSet;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GEntity;
import org.json.simple.JSONObject;

/**
 * @author Michael Cuison
 */
public class Model_Model implements GEntity {

    final String XML = "Model_Model.xml";

    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode

    /**
     * Entity constructor
     *
     * @param foValue - GhostRider Application Driver
     */
    public Model_Model(GRider foValue) {
        if (foValue == null) {
            System.err.println("Application Driver is not set.");
            System.exit(1);
        }

        poGRider = foValue;

        initialize();
    }

    /**
     * Gets edit mode of the record
     *
     * @return edit mode
     */
    @Override
    public int getEditMode() {
        return pnEditMode;
    }

    /**
     * Gets the column index name.
     *
     * @param fnValue - column index number
     * @return column index name
     */
    @Override
    public String getColumn(int fnValue) {
        try {
            return poEntity.getMetaData().getColumnLabel(fnValue);
        } catch (SQLException e) {
        }
        return "";
    }

    /**
     * Gets the column index number.
     *
     * @param fsValue - column index name
     * @return column index number
     */
    @Override
    public int getColumn(String fsValue) {
        try {
            return MiscUtil.getColumnIndex(poEntity, fsValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Gets the total number of column.
     *
     * @return total number of column
     */
    @Override
    public int getColumnCount() {
        try {
            return poEntity.getMetaData().getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Gets the table name.
     *
     * @return table name
     */
    @Override
    public String getTable() {
        return "Model";
    }

    /**
     * Gets the value of a column index number.
     *
     * @param fnColumn - column index number
     * @return object value
     */
    @Override
    public Object getValue(int fnColumn) {
        try {
            return poEntity.getObject(fnColumn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the value of a column index name.
     *
     * @param fsColumn - column index name
     * @return object value
     */
    @Override
    public Object getValue(String fsColumn) {
        try {
            return poEntity.getObject(MiscUtil.getColumnIndex(poEntity, fsColumn));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets column value.
     *
     * @param fnColumn - column index number
     * @param foValue - value
     * @return result as success/failed
     */
    @Override
    public JSONObject setValue(int fnColumn, Object foValue) {
        try {
            poJSON = MiscUtil.validateColumnValue(System.getProperty("sys.default.path.metadata") + XML, MiscUtil.getColumnLabel(poEntity, fnColumn), foValue);
            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            poEntity.updateObject(fnColumn, foValue);
            poEntity.updateRow();

            poJSON = new JSONObject();
            poJSON.put("result", "success");
            poJSON.put("value", getValue(fnColumn));
        } catch (SQLException e) {
            e.printStackTrace();
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }

        return poJSON;
    }

    /**
     * Sets column value.
     *
     * @param fsColumn - column index name
     * @param foValue - value
     * @return result as success/failed
     */
    @Override
    public JSONObject setValue(String fsColumn, Object foValue) {
        poJSON = new JSONObject();

        try {
            return setValue(MiscUtil.getColumnIndex(poEntity, fsColumn), foValue);
        } catch (SQLException e) {
            e.printStackTrace();
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        return poJSON;
    }

    /**
     * Set the edit mode of the entity to new.
     *
     * @return result as success/failed
     */
    @Override
    public JSONObject newRecord() {
        pnEditMode = EditMode.ADDNEW;

        //replace with the primary key column info
        setModelID(MiscUtil.getNextCode(getTable(), "sModelIDx", true, poGRider.getConnection(), ""));

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }

    /**
     * Opens a record.
     *
     * @param fsCondition - filter values
     * @return result as success/failed
     */
    @Override
    public JSONObject openRecord(String fsCondition) {
        poJSON = new JSONObject();

        String lsSQL = MiscUtil.makeSelect(this);

        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, "sModelIDx = " + SQLUtil.toSQL(fsCondition));

        ResultSet loRS = poGRider.executeQuery(lsSQL);

        try {
            if (loRS.next()) {
                for (int lnCtr = 1; lnCtr <= loRS.getMetaData().getColumnCount(); lnCtr++) {
                    setValue(lnCtr, loRS.getObject(lnCtr));
                }

                pnEditMode = EditMode.UPDATE;

                poJSON.put("result", "success");
                poJSON.put("message", "Record loaded successfully.");
            } else {
                poJSON.put("result", "error");
                poJSON.put("message", "No record to load.");
            }
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }

        return poJSON;
    }

    /**
     * Save the entity.
     *
     * @return result as success/failed
     */
    @Override
    public JSONObject saveRecord() {
        poJSON = new JSONObject();

        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            String lsSQL;
            if (pnEditMode == EditMode.ADDNEW) {
                //replace with the primary key column info
                setModelID(MiscUtil.getNextCode(getTable(), "sModelIDx", true, poGRider.getConnection(), ""));

                lsSQL = MiscUtil.makeSQL(this);

                if (!lsSQL.isEmpty()) {
                    if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), "") > 0) {
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record saved successfully.");
                    } else {
                        poJSON.put("result", "error");
                        poJSON.put("message", poGRider.getErrMsg());
                    }
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "No record to save.");
                }
            } else {
                Model_Model loOldEntity = new Model_Model(poGRider);

                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecord(this.getModelID());

                if ("success".equals((String) loJSON.get("result"))) {
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sModelIDx = " + SQLUtil.toSQL(this.getModelID()), "xBrandNme");

                    if (!lsSQL.isEmpty()) {
                        if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), "") > 0) {
                            poJSON.put("result", "success");
                            poJSON.put("message", "Record saved successfully.");
                        } else {
                            poJSON.put("result", "error");
                            poJSON.put("message", poGRider.getErrMsg());
                        }
                    } else {
                        poJSON.put("result", "success");
                        poJSON.put("message", "No updates has been made.");
                    }
                } else {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Record discrepancy. Unable to save record.");
                }
            }
        } else {
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid update mode. Unable to save record.");
            return poJSON;
        }

        return poJSON;
    }

    /**
     * Prints all the public methods used<br>
     * and prints the column names of this entity.
     */
    @Override
    public void list() {
        Method[] methods = this.getClass().getMethods();

        System.out.println("--------------------------------------------------------------------");
        System.out.println("LIST OF PUBLIC METHODS FOR " + this.getClass().getName() + ":");
        System.out.println("--------------------------------------------------------------------");
        for (Method method : methods) {
            System.out.println(method.getName());
        }

        try {
            int lnRow = poEntity.getMetaData().getColumnCount();

            System.out.println("--------------------------------------------------------------------");
            System.out.println("ENTITY COLUMN INFO");
            System.out.println("--------------------------------------------------------------------");
            System.out.println("Total number of columns: " + lnRow);
            System.out.println("--------------------------------------------------------------------");

            for (int lnCtr = 1; lnCtr <= lnRow; lnCtr++) {
                System.out.println("Column index: " + (lnCtr) + " --> Label: " + poEntity.getMetaData().getColumnLabel(lnCtr));
                if (poEntity.getMetaData().getColumnType(lnCtr) == Types.CHAR
                        || poEntity.getMetaData().getColumnType(lnCtr) == Types.VARCHAR) {

                    System.out.println("Column index: " + (lnCtr) + " --> Size: " + poEntity.getMetaData().getColumnDisplaySize(lnCtr));
                }
            }
        } catch (SQLException e) {
        }

    }

    /**
     * Description: Sets the sModelIDx of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setModelID(String fsValue) {
        return setValue("sModelIDx", fsValue);
    }

    /**
     * @return The sModelIDx of this record.
     */
    public String getModelID() {
        return (String) getValue("sModelIDx");
    }

    /**
     * Description: Sets the sModelCde of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setModelCode(String fsValue) {
        return setValue("sModelCde", fsValue);
    }

    /**
     * @return The sModelCde of this record.
     */
    public String getModelCode() {
        return (String) getValue("sModelCde");
    }

    /**
     * Description: Sets the sDescript of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setModelDescription(String fsValue) {
        return setValue("sDescript", fsValue);
    }

    /**
     * @return The sDescript of this record.
     */
    public String getModelDescription() {
        return (String) getValue("sDescript");
    }

    /**
     * Description: Sets the sBrandIDx of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBrandID(String fsValue) {
        return setValue("sBrandIDx", fsValue);
    }

    /**
     * @return The sBrandIDx of this record.
     */
    public String getBrandID() {
        return (String) getValue("sBrandIDx");
    }
    
    /**
     * Description: Sets the sSeriesID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setSeriesID(String fsValue) {
        return setValue("sSeriesID", fsValue);
    }

    /**
     * @return The sSeriesID of this record.
     */
    public String getSeriesID() {
        return (String) getValue("sSeriesID");
    }
    
    /**
     * Description: Sets the sVrntIDxx of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setVariantID(String fsValue) {
        return setValue("sVrntIDxx", fsValue);
    }

    /**
     * @return The sVrntIDxx of this record.
     */
    public String getVariantID() {
        return (String) getValue("sVrntIDxx");
    }

    /**
     * Description: Sets the nYearModl of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setYearModel(String fsValue) {
        return setValue("nYearModl", fsValue);
    }

    /**
     * @return The nYearModl of this record.
     */
    public int getYearModel() {
        return (int) getValue("nYearModl");
    }

    /**
     * Description: Sets the cEndOfLfe of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setEndOfLife(String fsValue) {
        return setValue("cEndOfLfe", fsValue);
    }

    /**
     * @return The cEndOfLfe of this record.
     */
    public String getEndOfLife() {
        return (String) getValue("cEndOfLfe");
    }

    /**
     * Sets record as active.
     *
     * @param fbValue
     * @return result as success/failed
     */
    public JSONObject setActive(boolean fbValue) {
        return setValue("cRecdStat", fbValue ? "1" : "0");
    }

    /**
     * @return If record is active.
     */
    public boolean isActive() {
        return ((String) getValue("cRecdStat")).equals("1");
    }

    /**
     * Sets the user encoded/updated the record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setModifiedBy(String fsValue) {
        return setValue("sModified", fsValue);
    }

    /**
     * @return The user encoded/updated the record
     */
    public String getModifiedBy() {
        return (String) getValue("sModified");
    }

    /**
     * Sets the date and time the record was modified.
     *
     * @param fdValue
     * @return result as success/failed
     */
    public JSONObject setModifiedDate(Date fdValue) {
        return setValue("dModified", fdValue);
    }

    /**
     * @return The date and time the record was modified.
     */
    public Date getModifiedDate() {
        return (Date) getValue("dModified");
    }
    
    private void initialize() {
        try {
            poEntity = MiscUtil.xml2ResultSet(System.getProperty("sys.default.path.metadata") + XML, getTable());

            poEntity.last();
            poEntity.moveToInsertRow();

            MiscUtil.initRowSet(poEntity);
            poEntity.updateString("cRecdStat", RecordStatus.ACTIVE);

            poEntity.insertRow();
            poEntity.moveToCurrentRow();

            poEntity.absolute(1);

            pnEditMode = EditMode.UNKNOWN;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

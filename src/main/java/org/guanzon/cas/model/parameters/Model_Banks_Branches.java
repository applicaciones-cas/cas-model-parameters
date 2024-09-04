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
public class Model_Banks_Branches implements GEntity {

    final String XML = "Model_Banks_Branches.xml";

    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode

    /**
     * Entity constructor
     *
     * @param foValue - GhostRider Application Driver
     */
    public Model_Banks_Branches(GRider foValue) {
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
        return "Banks_Branches";
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
        setBranchesBanksID(MiscUtil.getNextCode(getTable(), "sBrBankID", true, poGRider.getConnection(), poGRider.getBranchCode()));

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

        String lsSQL = getSQL();

        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, " a.sBrBankID = " + SQLUtil.toSQL(fsCondition));

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
                setBranchesBanksID(MiscUtil.getNextCode(getTable(), "sBrBankID", true, poGRider.getConnection(), poGRider.getBranchCode()));

                lsSQL = makeSQL();

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
                Model_Banks_Branches loOldEntity = new Model_Banks_Branches(poGRider);

                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecord(this.getBranchesBanksID());

                if ("success".equals((String) loJSON.get("result"))) {
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sBrBankID = " + SQLUtil.toSQL(this.getBranchesBanksID()), "xBankName»xBankCode»xTownName");

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
     * Sets the Banks Branches ID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBranchesBanksID(String fsValue) {
        return setValue("sBrBankID", fsValue);
    }

    /**
     * @return The Banks Branches ID of this record.
     */
    public String getBranchesBanksID() {
        return (String) getValue("sBrBankID");
    }

    /**
     * Sets the Banks Branches Name of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBranchesBanksName(String fsValue) {
        return setValue("sBrBankNm", fsValue);
    }

    /**
     * @return The Banks Branches Name of this record.
     */
    public String getBranchesBanksName() {
        return (String) getValue("sBrBankNm");
    }

    /**
     * Sets the Banks Branches Code of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBranchesBanksCode(String fsValue) {
        return setValue("sBrBankCD", fsValue);
    }

    /**
     * @return The Banks Branches Code of this record.
     */
    public String getBranchesBanksCode() {
        return (String) getValue("sBrBankCD");
    }

    /**
     * Sets the Banks Branches ID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBanksID(String fsValue) {
        return setValue("sBankIDxx", fsValue);
    }

    /**
     * @return The Banks Branches ID of this record.
     */
    public String getBanksID() {
        return (String) getValue("sBankIDxx");
    }

    /**
     * Sets the Banks Branches Contact of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setContactPerson(String fsValue) {
        return setValue("sContactP", fsValue);
    }

    /**
     * @return The Banks Branches Contact of this record.
     */
    public String getContactPerson() {
        return (String) getValue("sContactP");
    }

    /**
     * Sets the Banks Branches Address of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setAddress(String fsValue) {
        return setValue("sAddressx", fsValue);
    }

    /**
     * @return The Banks Branches Address of this record.
     */
    public String getAddress() {
        return (String) getValue("sAddressx");
    }

    /**
     * Sets the TownID of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setTownID(String fsValue) {
        return setValue("sTownIDxx", fsValue);
    }

    /**
     * @return The TownID of this record.
     */
    public String getTownID() {
        return (String) getValue("sTownIDxx");
    }

    /**
     * Sets the Banks Branches TellNo this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setTelephoneNumber(String fsValue) {
        return setValue("sTelNoxxx", fsValue);
    }

    /**
     * @return The Banks Branches TellNo this record.
     */
    public String getTelephoneNumber() {
        return (String) getValue("sTelNoxxx");
    }

    /**
     * Sets the Banks Branches FaxNo of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setFaxNumber(String fsValue) {
        return setValue("sFaxNoxxx", fsValue);
    }

    /**
     * @return The Banks Branches FaxNo of this record.
     */
    public String getFaxNumber() {
        return (String) getValue("sFaxNoxxx");
    }

    /**
     * Sets the Banks Branches RecdStat of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setRecdStat(String fsValue) {
        return setValue("cRecdStat", fsValue);
    }

    /**
     * @return The Contact Person Address Town Name of this record.
     */
    public String getRecdStat() {
        return (String) getValue("cRecdStat");
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

    /**
     * Sets the xBankName of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBankName(String fsValue) {
        return setValue("xBankName", fsValue);
    }

    /**
     * @return The xBankName of this record.
     */
    public String getBankName() {
        return (String) getValue("xBankName");
    }

    /**
     * Sets the xBankCode of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setBankCode(String fsValue) {
        return setValue("xBankCode", fsValue);
    }

    /**
     * @return The xBankCode of this record.
     */
    public String getBankCode() {
        return (String) getValue("xBankCode");
    }

    /**
     * Sets the xTownName of this record.
     *
     * @param fsValue
     * @return result as success/failed
     */
    public JSONObject setTownName(String fsValue) {
        return setValue("xTownName", fsValue);
    }

    /**
     * @return The xTownName of this record.
     */
    public String getTownName() {
        return (String) getValue("xTownName");
    }

    /**
     * Gets the SQL statement for this entity.
     *
     * @return SQL Statement
     */
    public String makeSQL() {
        return MiscUtil.makeSQL(this, "xBankName»xBankCode»xTownName");
    }

    /**
     * Gets the SQL Select statement for this entity.
     *
     * @return SQL Select Statement
     */
    public String makeSelectSQL() {
        return MiscUtil.makeSelect(this, "xBankName»xBankCode»xTownName");
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

    public String getSQL() {
        String lsSQL = "SELECT"
                + "  a.sBrBankID sBrBankID "
                + ", a.sBrBankNm sBrBankNm "
                + ", a.sBrBankCD sBrBankCD "
                + ", a.sBankIDxx sBankIDxx "
                + ", a.sContactP sContactP "
                + ", a.sAddressx sAddressx "
                + ", a.sTownIDxx sTownIDxx "
                + ", a.sTelNoxxx sTelNoxxx "
                + ", a.sFaxNoxxx sFaxNoxxx "
                + ", a.cRecdStat cRecdStat "
                + ", a.sModified sModified "
                + ", a.dModified dModified "
                + ", b.sBankName xBankName "
                + ", b.sBankCode xBankCode "
                + ", c.sTownName xTownName "
                + " FROM " + getTable() + " a"
                + " LEFT JOIN Banks b ON a.sBankIDxx = b.sBankIDxx"
                + " LEFT JOIN TownCity c ON a.sTownIDxx = c.sTownIDxx";

        return lsSQL;
    }
}

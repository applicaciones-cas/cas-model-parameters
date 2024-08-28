/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.guanzon.cas.model.parameters;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import javax.sql.rowset.CachedRowSet;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.RecordStatus;
import org.guanzon.appdriver.iface.GEntity;
import org.json.simple.JSONObject;

/**
 *
 * @author user
 */
public class Model_Branch implements GEntity{
    
    final String XML = "Model_Branch.xml";

    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode
    
    
    public Model_Branch(GRider foValue) {
        if (foValue == null) {
            System.err.println("Application Driver is not set.");
            System.exit(1);
        }

        poGRider = foValue;

        initialize();
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

    @Override
    public String getColumn(int fnValue) {
        try {
            return poEntity.getMetaData().getColumnLabel(fnValue);
        } catch (SQLException e) {
        }
        return "";
    }

    @Override
    public int getColumn(String fsValue) {
        try {
            return MiscUtil.getColumnIndex(poEntity, fsValue);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int getColumnCount() {
        try {
            return poEntity.getMetaData().getColumnCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public int getEditMode() {
        return pnEditMode;
    }

    @Override
    public String getTable() {
        return "Branch";
    }

    @Override
    public Object getValue(int fnColumn) {
        try {
            return poEntity.getObject(fnColumn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object getValue(String fsColumn) {
        try {
            return poEntity.getObject(MiscUtil.getColumnIndex(poEntity, fsColumn));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

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

    @Override
    public JSONObject newRecord() {
        pnEditMode = EditMode.ADDNEW;

        //replace with the primary key column info
        setBranchCd(MiscUtil.getNextCode(getTable(), "sBranchCd", true, poGRider.getConnection(), poGRider.getBranchCode()));

        poJSON = new JSONObject();
        poJSON.put("result", "success");
        return poJSON;
    }

    @Override
    public JSONObject openRecord(String fsCondition) {
        poJSON = new JSONObject();

        String lsSQL = MiscUtil.makeSelect(this); 

        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, " sBranchCd = " + SQLUtil.toSQL(fsCondition));

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

    @Override
    public JSONObject saveRecord() {
        poJSON = new JSONObject();

        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            String lsSQL;
            if (pnEditMode == EditMode.ADDNEW) {
                //replace with the primary key column info
//                setBranchCd(MiscUtil.getNextCode(getTable(), "sBranchCd", true, poGRider.getConnection(), poGRider.getBranchCode()));
                setBranchCd("Aw01");

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
                Model_Branch loOldEntity = new Model_Branch(poGRider);

                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecord(this.getBranchCd());

                if ("success".equals((String) loJSON.get("result"))) {
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sBranchCd = " + SQLUtil.toSQL(this.getBranchCd()));

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
    
    
    
    public JSONObject setBranchCd(String fsValue) {
        return setValue("sBranchCd", fsValue);
    }
    
    public String getBranchCd() {
        return (String) getValue("sBranchCd");
    }
    /////////////////////////////////////////////////////////
    public JSONObject setDescription(String fsValue) {
        return setValue("sDescript", fsValue);
    }
    
    public String getDescription() {
        return (String) getValue("sDescript");
    }
    /////////////////////////////////////////////////////////
    public JSONObject setAddress(String fsValue) {
        return setValue("sAddressx", fsValue);
    }
    
    public String getAddress() {
        return (String) getValue("sAddressx");
    }
    /////////////////////////////////////////////////////////
    public JSONObject setContact(String fsValue) {
        return setValue("sContactx", fsValue);
    }
    
    public String getContact() {
        return (String) getValue("sContactx");
    }
    /////////////////////////////////////////////////////////
    public JSONObject setTeleNum(String fsValue) {
        return setValue("sTelNumbr", fsValue);
    }
    
    public String getTeleNum() {
        return (String) getValue("sTelNumbr");
    }
    /////////////////////////////////////////////////////////
    public JSONObject setActive(boolean fbValue) {
        return setValue("cRecdStat", fbValue ? "1" : "0");
    }
    
    public boolean isActive() {
        return ((String) getValue("cRecdStat")).equals("1");
    }
    /////////////////////////////////////////////////////////
    public JSONObject setBranchNm(String fsValue) {
        return setValue("sBranchNm", fsValue);
    }
    
    public String getBranchNm() {
        return (String) getValue("sBranchNm");
    }
    /////////////////////////////////////////////////////////
    public JSONObject setModifiedBy(String fsValue) {
        return setValue("sModified", fsValue);
    }
    
    public String getModifiedBy() {
        return (String) getValue("sModified");
    }
    /////////////////////////////////////////////////////////
    public JSONObject setModifiedDate(Date fdValue) {
        return setValue("dModified", fdValue);
    }
    
    public Date getModifiedDate() {
        return (Date) getValue("dModified");
    }
    /////////////////////////////////////////////////////////
    public String makeSQL() {
        return MiscUtil.makeSQL(this);
    }
    
    public String makeSelectSQL() {
        return MiscUtil.makeSelect(this);
    }
    /////////////////////////////////////////////////////////
    public String getSQL(){
        String lsSQL = "SELECT" +
                            "  sBranchCd" +
                            ", sBranchNm" +
                            ", sDescript" +
                            ", sCompnyID" +
                            ", sAddressx" +
                            ", sTownIDxx" +
                            ", sManagerx" +
                            ", sSellCode" +
                            ", cWareHous" +
                            ", sTelNumbr" +
                            ", cRecdStat" +
                            ", sContactx" +
                            ", dExportxx" +
                            ", cSrvcCntr" +
                            ", cAutomate" +
                            ", cMainOffc" +
                            ", sModified" +
                            ", dModified" +
                        " FROM " + System.getProperty("sys.table") +
                        " WHERE 0=1";
        
        return lsSQL;
    }
    
    
    
    
    
    
    
    
    
}

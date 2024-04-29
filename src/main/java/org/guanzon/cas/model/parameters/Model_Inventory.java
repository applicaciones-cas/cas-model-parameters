


package org.guanzon.cas.model.parameters;

import java.lang.reflect.Method;
import java.math.BigDecimal;
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
public class Model_Inventory implements GEntity{
    final String XML = "Model_Inventoy.xml";
    
    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode
    
    /**
     * Entity constructor
     * 
     * @param foValue - GhostRider Application Driver
     */
    public Model_Inventory(GRider foValue){
        if (foValue == null){
            System.err.println("Application Driver is not set.");
            System.exit(1);
        }
        
        poGRider = foValue;
        
        initialize();
    }
    
    /**
     * Gets edit mode of the record
     * @return edit mode
     */
    @Override
    public int getEditMode() {
        return pnEditMode;
    }
    
    /**
     * Gets the column index name.
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
     * @return table name
     */
    @Override
    public String getTable() {
        return "Inventory";
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
            if ("error".equals((String) poJSON.get("result"))) return poJSON;
            
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
        setStockID(MiscUtil.getNextCode(getTable(), "sInvTypCd", true, poGRider.getConnection(), poGRider.getBranchCode()));
        
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
        
        String lsSQL = MiscUtil.makeSelect(this, "xBankName»xBankCode»xTownName");
        
        //replace the condition based on the primary key column of the record
        lsSQL = MiscUtil.addCondition(lsSQL, fsCondition);
        
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        try {
            if (loRS.next()){
                for (int lnCtr = 1; lnCtr <= loRS.getMetaData().getColumnCount(); lnCtr++){
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
        
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE){
            String lsSQL;
            if (pnEditMode == EditMode.ADDNEW){
                //replace with the primary key column info
                setStockID(MiscUtil.getNextCode(getTable(), "sInvTypCd", true, poGRider.getConnection(), poGRider.getBranchCode()));
                
                lsSQL = makeSQL();
                
                if (!lsSQL.isEmpty()){
                    if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), "") > 0){
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
                Model_Inventory loOldEntity = new Model_Inventory(poGRider);
                
                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecord(this.getStockID());
                
                if ("success".equals((String) loJSON.get("result"))){
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sInvTypCd = " + SQLUtil.toSQL(this.getStockID()), "xBankName»xBankCode»xTownName");
                    
                    if (!lsSQL.isEmpty()){
                        if (poGRider.executeQuery(lsSQL, getTable(), poGRider.getBranchCode(), "") > 0){
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

            for (int lnCtr = 1; lnCtr <= lnRow; lnCtr++){
                System.out.println("Column index: " + (lnCtr) + " --> Label: " + poEntity.getMetaData().getColumnLabel(lnCtr));
                if (poEntity.getMetaData().getColumnType(lnCtr) == Types.CHAR ||
                    poEntity.getMetaData().getColumnType(lnCtr) == Types.VARCHAR){

                    System.out.println("Column index: " + (lnCtr) + " --> Size: " + poEntity.getMetaData().getColumnDisplaySize(lnCtr));
                }
            }
        } catch (SQLException e) {
        }
        
    }
    
    /**
     * Sets the Stock ID of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setStockID(String fsValue){
        return setValue("sStockIDx", fsValue);
    }
    
    /**
     * @return The Stock ID of this record.
     */
    public String getStockID(){
        return (String) getValue("sStockIDx");
    }
    
    /**
     * Sets the Bar Code of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setBarCode(String fsValue){
        return setValue("sBarCodex", fsValue);
    }
    
    /**
     * @return The Bar Code of this record. 
     */
    public String getBarCodex(){
        return (String) getValue("sBarCodex");
    }
    
    /**
     * Sets the Description of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setDescription(String fsValue){
        return setValue("sDescript", fsValue);
    }
    
    /**
     * @return The Description of this record. 
     */
    public String getDescription(){
        return (String) getValue("sDescript");
    }
    
        /**
     * Sets the Brief Description of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setBriefDescription(String fsValue){
        return setValue("sDescript", fsValue);
    }
    
    /**
     * @return The Brief Description of this record. 
     */
    public String getBriefDescription(){
        return (String) getValue("sBriefDsc");
    }
    
            /**
     * Sets the Alt Bar Code of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setAltBarCode(String fsValue){
        return setValue("sAltBarCd", fsValue);
    }
    
    /**
     * @return The Alt Bar Code of this record. 
     */
    public String getAltBarCode(){
        return (String) getValue("sAltBarCd");
    }
    
                /**
     * Sets the Category Code 1 of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setCategoryCode1(String fsValue){
        return setValue("sCategCd1", fsValue);
    }
    
    /**
     * @return The Category Code 1 of this record. 
     */
    public String getCategoryCode1(){
        return (String) getValue("sCategCd1");
    }
    
                    /**
     * Sets the Category Code 2 of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setCategoryCode2(String fsValue){
        return setValue("sCategCd2", fsValue);
    }
    
    /**
     * @return The Category Code 2 of this record. 
     */
    public String getCategoryCode2(){
        return (String) getValue("sCategCd2");
    }
                        /**
     * Sets the Category Code 3 of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setCategoryCode3(String fsValue){
        return setValue("sCategCd3", fsValue);
    }
    
    /**
     * @return The Category Code 3 of this record. 
     */
    public String getCategoryCode3(){
        return (String) getValue("sCategCd3");
    }
    
    
    
    
                            /**
     * Sets the Brand Code of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setBrandCode(String fsValue){
        return setValue("sBrandCde", fsValue);
    }
    
    /**
     * @return The Brand Code of this record. 
     */
    public String getBrandCode(){
        return (String) getValue("sBrandCde");
    }
    
                              /**
     * Sets the Model Code of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setModelCode(String fsValue){
        return setValue("sModelCde", fsValue);
    }
    
    /**
     * @return The Model Code of this record. 
     */
    public String getModelCode(){
        return (String) getValue("sModelCde");
    }
    
    
        
                              /**
     * Sets the Color Code of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setColorCode(String fsValue){
        return setValue("sColorCde", fsValue);
    }
    
    /**
     * @return The Color Code of this record. 
     */
    public String getColorCode(){
        return (String) getValue("sColorCde");
    }
                                  /**
     * Sets the Measure ID of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setMeasureID(String fsValue){
        return setValue("sMeasurID", fsValue);
    }
    
    /**
     * @return The Measure ID of this record. 
     */
    public String getMeasureID(){
        return (String) getValue("sMeasurID");
    }
    
                                      /**
     * Sets the Unit Price of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setUnitPrice(BigDecimal fnValue){
        return setValue("nUnitPrce", fnValue);
    }
    
    /**
     * @return The Unit Price of this record. 
     */
    public BigDecimal getUnitPrice(){
        return (BigDecimal) getValue("nUnitPrce");
    }
    
      /**
     * Sets the Sell Price of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setSellPrice(BigDecimal fnValue){
        return setValue("nSelPrice", fnValue);
    }
    
    /**
     * @return The Sell Price of this record. 
     */
    public BigDecimal getSellPrice(){
        return (BigDecimal) getValue("nSelPrice");
    }
    
    
          /**
     * Sets the Disc Level 1 of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setDiscLevel1(BigDecimal fnValue){
        return setValue("nDiscLev1", fnValue);
    }
    
    /**
     * @return The Disc Level 1 of this record. 
     */
    public BigDecimal getDiscLevel1(){
        return (BigDecimal) getValue("nDiscLev1");
    }
              /**
     * Sets the Disc Level 2 of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setDiscLevel2(BigDecimal fnValue){
        return setValue("nDiscLev2", fnValue);
    }
    
    /**
     * @return The Disc Level 2 of this record. 
     */
    public BigDecimal getDiscLevel2(){
        return (BigDecimal) getValue("nDiscLev2");
    }          
    
    /**
     * Sets the Disc Level 3 of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setDiscLevel3(BigDecimal fnValue){
        return setValue("nDiscLev3", fnValue);
    }
    
    /**
     * @return The Disc Level 3 of this record. 
     */
    public BigDecimal getDiscLevel3(){
        return (BigDecimal) getValue("nDiscLev1");
    }
        /**
     * Sets the Dealer Disc of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setDealerDisc(BigDecimal fnValue){
        return setValue("nDealrDsc", fnValue);
    }
    
    /**
     * @return The Dealer Disc of this record. 
     */
    public BigDecimal getDealerDisc(){
        return (BigDecimal) getValue("nDealrDsc");
    }
    
    
    
    /**
     * Sets the Minimum Level of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setMinimumLevel(int fnValue){
        return setValue("nMinLevel", fnValue);
    }
    
    /**
     * @return The Minimum Level of this record. 
     */
    public int getMinimumLevel(){
        return (int) getValue("nMinLevel");
    }
    
    
    
    
        /**
     * Sets the Maximum Level of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setMaximumLevel(int fnValue){
        return setValue("nMaxLevel", fnValue);
    }
    
    /**
     * @return The Maximum Level of this record. 
     */
    public int getMaximumLevel(){
        return (int) getValue("nMaxLevel");
    }
    
            /**
     * Sets the Combo Inventory of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setComboInventory(int fnValue){
        return setValue("cComboInv", fnValue);
    }
    
    /**
     * @return The Combo Inventory of this record. 
     */
    public int getComboInventory(){
        return (int) getValue("cComboInv");
    }
    
    
                /**
     * Sets the With Promo of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setWithPromo(String fsValue){
        return setValue("cWthPromo", fsValue);
    }
    
    /**
     * @return The With Promo of this record. 
     */
    public String getWithPromo(){
        return (String) getValue("cWthPromo");
    }
    
    
                   /**
     * Sets the Serialize of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setSerialize(String fsValue){
        return setValue("cSerialze", fsValue);
    }
    
    /**
     * @return The Serialize of this record. 
     */
    public String getSerialize(){
        return (String) getValue("cSerialze");
    }
    
                       /**
     * Sets the Unit Type of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setUnitType(String fsValue){
        return setValue("cUnitType", fsValue);
    }
    
    /**
     * @return The Unit Type of this record. 
     */
    public String getUnitType(){
        return (String) getValue("cUnitType");
    }
    
    
    
                           /**
     * Sets the Inventory Status of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setInventoryStatus(String fsValue){
        return setValue("cInvStatx", fsValue);
    }
    /**
     * @return The Inventory Status of this record. 
     */
    public String getInventoryStatus(){
        return (String) getValue("cInvStatx");
    }
    
    
        
     /**
     * Sets the With Export of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setWithExport(String fsValue){
        return setValue("cWthExprt", fsValue);
    }
    /**
     * @return The With Export of this record. 
     */
    public String getWithExport(){
        return (String) getValue("cWthExprt");
    }
    
    
                               /**
     * Sets the Supersede of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setSupersede(String fsValue){
        return setValue("sSupersed", fsValue);
    }
    /**
     * @return The Supersede of this record. 
     */
    public String getSupersede(){
        return (String) getValue("sSupersed");
    }
    
    
                                   /**
     * Sets the Record Status of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setRecordStatus(String fsValue){
        return setValue("cRecdStat", fsValue);
    }
    /**
     * @return The Record Status of this record. 
     */
    public String getRecordStatus(){
        return (String) getValue("cRecdStat");
    }
    
    
    
                                       /**
     * Sets the Modified of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setModified(String fsValue){
        return setValue("sModified", fsValue);
    }
    /**
     * @return The Modified of this record. 
     */
    public String getModified(){
        return (String) getValue("sModified");
    }
    
       
    /**
     * Sets the date the record was modified.
     * 
     * @param fdValue 
     * @return result as success/failed
     */
    public JSONObject setModifiedDate(Date fdValue){
        return setValue("dModified", fdValue);
    }
    
    /**
     * @return The date the record was modified.
     */
    public Date getModifiedDate(){
        return (Date) getValue("dModified");
    }
    
    
   
    

    
    /**
     * Gets the SQL statement for this entity.
     * 
     * @return SQL Statement
     */
    public String makeSQL(){
        return MiscUtil.makeSQL(this, "xCategNm1»xCategNm2»xCategNm3»xCategNm4»xBrandNme»xModelNme»xModelDsc»xColorNme»xMeasurNm»xInvTypNm»xSuperCde»xSuperDsc");
    }
    
    private void initialize(){
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
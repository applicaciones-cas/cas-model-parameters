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
public class Model_PO_Quotation_Master implements GEntity{
    final String XML = "Model_PO_Quotation_Master.xml";
    
    GRider poGRider;                //application driver
    CachedRowSet poEntity;          //rowset
    JSONObject poJSON;              //json container
    int pnEditMode;                 //edit mode
    
    /**
     * Entity constructor
     * 
     * @param foValue - GhostRider Application Driver
     */
    public Model_PO_Quotation_Master(GRider foValue){
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
        return "Made";
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
        setTransactionNumber(MiscUtil.getNextCode(getTable(), "sMadeIDxx", true, poGRider.getConnection(), poGRider.getBranchCode()));
        
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
        
        String lsSQL = MiscUtil.makeSelect(this, "xSupplier»xAddressx»xCPerson1»xCPPosit1»xCPMobil1»xTermName»xCategrNm»xInvTypNm");
        
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
                setTransactionNumber(MiscUtil.getNextCode(getTable(), "sMadeIDxx", true, poGRider.getConnection(), poGRider.getBranchCode()));
                
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
                Model_PO_Quotation_Master loOldEntity = new Model_PO_Quotation_Master(poGRider);
                
                //replace with the primary key column info
                JSONObject loJSON = loOldEntity.openRecord(this.getTransactionNumber());
                
                if ("success".equals((String) loJSON.get("result"))){
                    //replace the condition based on the primary key column of the record
                    lsSQL = MiscUtil.makeSQL(this, loOldEntity, "sMadeIDxx = " + SQLUtil.toSQL(this.getTransactionNumber()), "xSupplier»xAddressx»xCPerson1»xCPPosit1»xCPMobil1»xTermName»xCategrNm»xInvTypNm");
                    
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
     * Sets the TransactionNumber of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setTransactionNumber(String fsValue){
        return setValue("sTransNox", fsValue);
    }
    
    /**
     * @return The TransactionNumber of this record.
     */
    public String getTransactionNumber(){
        return (String) getValue("sTransNox");
    }
    
    /**
     * Sets the ReferenceNumber of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setReferenceNumber(String fsValue){
        return setValue("sReferNox", fsValue);
    }
    
    /**
     * @return The ReferenceNumber of this record. 
     */
    public String getReferenceNumber(){
        return (String) getValue("sReferNox");
    }
    
     /**
     * Sets the Supplier of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setSupplier(String fsValue){
        return setValue("sSupplier", fsValue);
    }
    
    /**
     * @return The Supplier of this record. 
     */
    public String getSupplier(){
        return (String) getValue("sSupplier");
    }
    
     /**
     * Sets the AddressID of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setAddressID(String fsValue){
        return setValue("sAddrssID", fsValue);
    }
    
    /**
     * @return The AddressID of this record. 
     */
    public String getAddressID(){
        return (String) getValue("sAddrssID");
    }
    
     /**
     * Sets the ContactID of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setContactID(String fsValue){
        return setValue("sContctID", fsValue);
    }
    
    /**
     * @return The ContactID of this record. 
     */
    public String getContactID(){
        return (String) getValue("sContctID");
    }
    
    /**
     * Sets the Transaction of this record.
     * 
     * @param fdValue 
     * @return result as success/failed
     */
    public JSONObject setTransaction(Date fdValue){
        return setValue("dTransact", fdValue);
    }
    
    /**
     * @return The Transaction of this record. 
     */
    public Date getTransaction(){
        return (Date) getValue("dTransact");
    }
    
    /**
     * Sets the ReferenceDate of this record.
     * 
     * @param fdValue 
     * @return result as success/failed
     */
    public JSONObject setReferenceDate(Date fdValue){
        return setValue("dReferDte", fdValue);
    }
    
    /**
     * @return The ReferenceDate of this record. 
     */
    public Date getReferenceDate(){
        return (Date) getValue("dReferDte");
    }
    
    /**
     * Sets the TermCode of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setTermCode(String fsValue){
        return setValue("sTermCode", fsValue);
    }
    
    /**
     * @return The TermCode of this record. 
     */
    public String getTermCode(){
        return (String) getValue("sTermCode");
    }
    
    /**
     * Sets the Validity of this record.
     * 
     * @param fdValue 
     * @return result as success/failed
     */
    public JSONObject setValidity(Date fdValue){
        return setValue("dValidity", fdValue);
    }
    
    /**
     * @return The Validity of this record. 
     */
    public Date getValidity(){
        return (Date) getValue("dValidity");
    }
    
    /**
     * Sets the GrossAmount of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setGrossAmount(BigDecimal fnValue){
        return setValue("nGrossAmt", fnValue);
    }
    
    /**
     * @return The GrossAmount of this record. 
     */
    public BigDecimal getGrossAmount(){
        return (BigDecimal) getValue("nGrossAmt");
    }
    
    /**
     * Sets the Discount of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setDiscount(BigDecimal fnValue){
        return setValue("nDiscount", fnValue);
    }
    
    /**
     * @return The Discount of this record. 
     */
    public BigDecimal getDiscount(){
        return (BigDecimal) getValue("nDiscount");
    }
    
    /**
     * Sets the AddDiscx of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setAddDiscx(BigDecimal fnValue){
        return setValue("nAddDiscx", fnValue);
    }
    
    /**
     * @return The AddDiscx of this record. 
     */
    public BigDecimal getAddDiscx(){
        return (BigDecimal) getValue("nAddDiscx");
    }
    
    /**
     * Sets the VatRatex of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setVatRatex(BigDecimal fnValue){
        return setValue("nVatRatex", fnValue);
    }
    
    /**
     * @return The VatRatex of this record. 
     */
    public BigDecimal getVatRatex(){
        return (BigDecimal) getValue("nVatRatex");
    }
    
    /**
     * Sets the VatAmtxx of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setVatAmtxx(BigDecimal fnValue){
        return setValue("nVatAmtxx", fnValue);
    }
    
    /**
     * @return The VatAmtxx of this record. 
     */
    public BigDecimal getVatAmtxx(){
        return (BigDecimal) getValue("nVatAmtxx");
    }
    
    /**
     * Sets the VATAdded of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setVATAdded(String fsValue){
        return setValue("cVATAdded", fsValue);
    }
    
    /**
     * @return The VATAdded of this record. 
     */
    public String getVATAdded(){
        return (String) getValue("cVATAdded");
    }
    
    /**
     * Sets the TWithHld of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setTWithHld(BigDecimal fnValue){
        return setValue("nTWithHld", fnValue);
    }
    
    /**
     * @return The TWithHld of this record. 
     */
    public BigDecimal getTWithHld(){
        return (BigDecimal) getValue("nTWithHld");
    }
    
    /**
     * Sets the Freight of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setFreightx(BigDecimal fnValue){
        return setValue("nFreightx", fnValue);
    }
    
    /**
     * @return The Freight of this record. 
     */
    public BigDecimal getFreightx(){
        return (BigDecimal) getValue("nFreightx");
    }
    
    /**
     * Sets the TransactionTotal of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setTransactionTotal(BigDecimal fnValue){
        return setValue("nTranTotl", fnValue);
    }
    
    /**
     * @return The TransactionTotal of this record. 
     */
    public BigDecimal getTransactionTotal(){
        return (BigDecimal) getValue("nTranTotl");
    }
    
    /**
     * Sets the Remarks of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setRemarks(String fsValue){
        return setValue("sRemarksx", fsValue);
    }
    
    /**
     * @return The Remarks of this record. 
     */
    public String getRemarks(){
        return (String) getValue("sRemarksx");
    }
    
    /**
     * Sets the EntryNumber of this record.
     * 
     * @param fnValue 
     * @return result as success/failed
     */
    public JSONObject setEntryNumber(Integer fnValue){
        return setValue("nEntryNox", fnValue);
    }
    
    /**
     * @return The EntryNumber of this record. 
     */
    public Integer getEntryNumber(){
        return (Integer) getValue("nEntryNox");
    }
    
    /**
     * Sets the CategoryCode of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setCategoryCode(String fsValue){
        return setValue("sCategrCd", fsValue);
    }
    
    /**
     * @return The CategoryCode of this record. 
     */
    public String getCategoryCode(){
        return (String) getValue("sCategrCd");
    }
    
     /**
     * Sets the TransactionStatus of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setTransactionStatus(String fsValue){
        return setValue("cTranStat", fsValue);
    }
    
    /**
     * @return The TransactionStatus of this record. 
     */
    public String getTransactionStatus(){
        return (String) getValue("cTranStat");
    }
    
     /**
     * Sets the Prepared of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setPrepared(String fsValue){
        return setValue("sPrepared", fsValue);
    }
    
    /**
     * @return The Prepared of this record. 
     */
    public String getPrepared(){
        return (String) getValue("sPrepared");
    }
    
    /**
     * Sets the Prepared of this record.
     * 
     * @param fdValue 
     * @return result as success/failed
     */
    public JSONObject setPreparedDate(Date fdValue){
        return setValue("dPrepared", fdValue);
    }
    
    /**
     * @return The Prepared of this record. 
     */
    public Date getPreparedDate(){
        return (Date) getValue("dPrepared");
    }
   
    /**
     * Sets the RecorddStatus of this record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setRecorddStatus(String fsValue){
        return setValue("cRecdStat", fsValue);
    }
    
    /**
     * @return The RecorddStatus of this record. 
     */
    public String getRecorddStatus(){
        return (String) getValue("cRecdStat");
    }
    
    /**
     * Sets record as active.
     * 
     * @param fbValue
     * @return result as success/failed
     */
    public JSONObject setActive(boolean fbValue){
        return setValue("cRecdStat", fbValue ? "1" : "0");
    }
    
    /**
     * @return If record is active. 
     */
    public boolean isActive(){
        return ((String) getValue("cRecdStat")).equals("1");
    }
    
    /**
     * Sets the user encoded/updated the record.
     * 
     * @param fsValue 
     * @return result as success/failed
     */
    public JSONObject setModifiedBy(String fsValue){
        return setValue("sModified", fsValue);
    }
    
    /**
     * @return The user encoded/updated the record 
     */
    public String getModifiedBy(){
        return (String) getValue("sModified");
    }
    
    /**
     * Sets the date and time the record was modified.
     * 
     * @param fdValue 
     * @return result as success/failed
     */
    public JSONObject setModifiedDate(Date fdValue){
        return setValue("dModified", fdValue);
    }
    
    /**
     * @return The date and time the record was modified.
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
        return MiscUtil.makeSQL(this, "xSupplier»xAddressx»xCPerson1»xCPPosit1»xCPMobil1»xTermName»xCategrNm»xInvTypNm");
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
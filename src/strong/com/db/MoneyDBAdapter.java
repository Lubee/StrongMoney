package strong.com.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MoneyDBAdapter {

  public static final String DATABASE_NAME = "strongmoney.db";
  
  public static final String ACCOUNT_TABLE ="account";
  public static final String ACCOUNT_ID ="account_id";
  public static final String  TIME  ="time";
  public static final String REMARK ="remark";
  public static final String AMOUNT ="amount";
  public static final String AITEM_NAME ="item_name";
  public static final String AITEM_TYPE ="item_type";
  
  public static final String ITEM_TABLE ="item";
  public static final String ITEM_ID ="_id";
  public static final String ITEM_TYPE ="item_type";
  public static final String ITEM_NAME ="item_name";
  
  private static final int DATABASE_VERSION = 1;
  
  protected static final String ACCOUNT_TABLE_DDL ="CREATE TABLE IF NOT EXISTS " + ACCOUNT_TABLE + " (" 
  + ACCOUNT_ID + " INTEGER primary key autoincrement, " 
  + " "+ TIME + "  CHAR(10),"
  + " "+ REMARK + " VARCHAR, "
  + " "+ AMOUNT + " FLOAT DEFAULT '0', "
  + " "+ AITEM_NAME + " VARCHAR, "
  + " "+ AITEM_TYPE + " INTEGER DEFAULT '0');";
  
  protected static final String ITEM_TABLE_DDL ="CREATE TABLE IF NOT EXISTS " + ITEM_TABLE + " (" 
  + ITEM_ID + " INTEGER primary key autoincrement, " 
  + " "+ ITEM_NAME + " VARCHAR, "
  + " "+ ITEM_TYPE + " INTEGER DEFAULT '0');";
  
  private static final String DROP_ACCOUNT_DLL = "DROP TABLE IF EXISTS "+ ACCOUNT_TABLE;;
  private static final String DROP_ITEM_DLL = "DROP TABLE IF EXISTS "+ ITEM_TABLE;;
  private final Context mCtx;
  private DatabaseHelper mDbHelper;
  private SQLiteDatabase mDb;

  private static class DatabaseHelper extends SQLiteOpenHelper {

    DatabaseHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(ACCOUNT_TABLE_DDL);
      db.execSQL(ITEM_TABLE_DDL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      db.execSQL(DROP_ACCOUNT_DLL);
      db.execSQL(DROP_ITEM_DLL);
      onCreate(db);
    }
  }
  public MoneyDBAdapter(Context ctx) {
    this.mCtx = ctx;
  }

  public MoneyDBAdapter open() throws SQLException {
    mDbHelper = new DatabaseHelper(mCtx);
    mDb = mDbHelper.getWritableDatabase();
    return this;
  }
  
  /**
   * 查询所有收支项
   *上午09:33:02_2011-6-16
   * @return
   */
  public Cursor quryItems() {
    return mDb.query(ITEM_TABLE, new String[] { ITEM_ID, ITEM_NAME,
        ITEM_TYPE}, null, null, null, null, ITEM_TYPE + " asc");
  }

  /**
   * 按照id号查询收支项
   *上午09:32:33_2011-6-16
   * @param rowId
   * @return
   * @throws SQLException
   */
  public Cursor quryItemById(long rowId) throws SQLException {
    Cursor mCursor =
    mDb.query(true, ITEM_TABLE, new String[] { ITEM_ID, ITEM_NAME,
        ITEM_TYPE }, ITEM_ID + "=" + rowId, null, null,
        null, null, null);
    if (mCursor != null) {
      mCursor.moveToFirst();
    }
    return mCursor;

  }
  
  /**
   * 修改收支项
   *上午09:32:20_2011-6-16
   * @param rowId
   * @param itemName
   * @param itemType
   * @return
   */
  public boolean updateItem(long rowId,String itemName, int itemType){
    ContentValues args = new ContentValues();
    args.put(ITEM_NAME, itemName);
    args.put(ITEM_TYPE, itemType);
    return mDb.update(ITEM_TABLE, args, ITEM_ID + "=" + rowId, null) > 0;  
  }
  
  /**
   * 添加收支项
   *上午09:32:09_2011-6-16
   * @param itemName
   * @param itemType
   * @return
   */
  public long insertItem( String itemName, int itemType){
    ContentValues args = new ContentValues();
    args.put(ITEM_NAME, itemName);
    args.put(ITEM_TYPE, itemType);
    return mDb.insert(ITEM_TABLE, null, args);
    
  }
  /**
   * 删除收支项
   *上午09:29:04_2011-6-16
   * @param rowId
   * @return
   */
  public boolean deleteItem(long rowId){
    return mDb.delete(ITEM_TABLE,  ITEM_ID + "=" + rowId, null) > 0;
    
  }
  /**
   * 查询所有帐目
   *上午09:12:07_2011-6-16
   * @return
   */
  public Cursor quryAcounts() {
    return mDb.query(ACCOUNT_TABLE, new String[] { ACCOUNT_ID, TIME,
        AMOUNT, AITEM_NAME,AITEM_TYPE,REMARK }, null, null, null, null, TIME+" desc,"+ACCOUNT_ID + " desc");
  }

  /**
   * 根据id号查询帐目
   *上午09:12:16_2011-6-16
   * @param rowId
   * @return
   * @throws SQLException
   */
  public Cursor quryAcountById(long rowId) throws SQLException {
    Cursor mCursor =
    mDb.query(true, ACCOUNT_TABLE, new String[] { ACCOUNT_ID, TIME,
        AMOUNT, AITEM_NAME,AITEM_TYPE,REMARK }, ACCOUNT_ID + "=" + rowId, null, null,
        null, null, null);
    if (mCursor != null) {
      mCursor.moveToFirst();
    }
    return mCursor;

  }
  
  /**
   * 修改帐目 
   *上午09:28:26_2011-6-16
   * @param rowId
   * @param time
   * @param amount
   * @param itemName
   * @param itemType
   * @param remark
   * @return
   */
  public boolean updateAccount(long rowId, String time, float amount, String itemName, int itemType, String remark){
    ContentValues args = new ContentValues();
    args.put(TIME, time);
    args.put(AMOUNT, amount);
    args.put(AITEM_NAME, itemName);
    args.put(AITEM_TYPE, itemType);
    args.put(REMARK, remark);
    return mDb.update(ACCOUNT_TABLE, args, ACCOUNT_ID + "=" + rowId, null) > 0;  
  }
  
  /**
   * 添加帐目
   *上午09:28:48_2011-6-16
   * @param time
   * @param amount
   * @param itemName
   * @param itemType
   * @param remark
   * @return
   */
  public long insertAccount( String time, float amount, String itemName, int itemType, String remark){
    ContentValues args = new ContentValues();
    args.put(TIME, time);
    args.put(AMOUNT, amount);
    args.put(AITEM_NAME, itemName);
    args.put(AITEM_TYPE, itemType);
    args.put(REMARK, remark);
    return mDb.insert(ACCOUNT_TABLE, null, args);
    
  }
  /**
   * 删除帐目
   *上午09:29:04_2011-6-16
   * @param rowId
   * @return
   */
  public boolean deleteAccount(long rowId){
    return mDb.delete(ACCOUNT_TABLE,  ACCOUNT_ID + "=" + rowId, null) > 0;
    
  }
  
  /**
   * 按照条件获取收支项目,0为支出，1为收入
   *下午02:31:58_2011-6-16
   * @return
   */
  public Cursor queryTotalInOut(int type){
    Cursor mCursor =
      mDb.query(true, ACCOUNT_TABLE, new String[] {"sum(amount)" }, AITEM_TYPE + "=" + type, null, null,
          null, null, null);
      if (mCursor != null) {
        mCursor.moveToFirst();
      }
      return mCursor;
  }
}

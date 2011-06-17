package strong.com.money;

import java.text.MessageFormat;

import strong.com.adapter.AccountAdapter;
import strong.com.adapter.ItemAdapter;
import strong.com.db.MoneyDBAdapter;
import android.app.TabActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends TabActivity implements OnTabChangeListener{

  private TabHost myTabhost;
  protected int myMenuSettingTag=0;
  protected Menu myMenu;
  private static final int myMenuResources[] = { R.menu.a_menu,
    R.menu.b_menu, R.menu.c_menu};
  
  private MoneyDBAdapter dbAdapter;
//  private Cursor myCursor ;
//  private Cursor itemCursor ;
//  private AccountAdapter accountAdapterList;
//  private ItemAdapter itemAdapterList;
  private static final int ACTIVITY_CREATE = 0;
  private static final int ACTIVITY_EDIT = 1;
  
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        initLayoutTabHost();
        myTabhost.setOnTabChangedListener(this);
        dbAdapter = new MoneyDBAdapter(this);
        dbAdapter.open();
        
        createAccountViewList();
        createItemViewList();
    }
    
    private void initLayoutTabHost(){
      myTabhost=this.getTabHost();//从TabActivity上面获取放置Tab的TabHost
      LayoutInflater.from(this).inflate(R.layout.main, myTabhost.getTabContentView(), true);
      //from(this)从这个TabActivity获取LayoutInflater
      //R.layout.main 存放Tab布局
      //通过TabHost获得存放Tab标签页内容的FrameLayout
      //是否将inflate 拴系到根布局元素上
    // myTabhost.setBackgroundColor(Color.argb(150, 22, 70, 150));
      
     //myTabhost.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.list_bg));
     //设置一下TabHost的颜色
      myTabhost
      .addTab(myTabhost.newTabSpec("acount")// 制造一个新的标签TT
              .setIndicator("账目")
              // 设置一下显示的标题为KK，设置一下标签图标为ajjc ,getResources().getDrawable(R.drawable.ajjc
              .setContent(R.id.acount_layout));
      myTabhost
      .addTab(myTabhost.newTabSpec("statistics")// 制造一个新的标签TT
              .setIndicator("统计")
              .setContent(R.id.statistics_layout));
      myTabhost
      .addTab(myTabhost.newTabSpec("item")// 制造一个新的标签TT
              .setIndicator("收支项")
              .setContent(R.id.item_layout));
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      
      // Hold on to this
      myMenu = menu;
      myMenu.clear();//清空MENU菜单
      // Inflate the currently selected menu XML resource.
      MenuInflater inflater = getMenuInflater();        
          //从TabActivity这里获取一个MENU过滤器
      switch (myMenuSettingTag) {
      case 1:
        inflater.inflate(myMenuResources[0], menu);
              //动态加入数组中对应的XML MENU菜单
        break;
      case 2:
        inflater.inflate(myMenuResources[1], menu);
        break;
      case 3:
        inflater.inflate(myMenuResources[2], menu);
        break;
      default:
        inflater.inflate(myMenuResources[0], menu);
        break;
      }
      return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public void onTabChanged(String tagString) {
      
      if (tagString.equals("acount")) {
        myMenuSettingTag = 1;
      }
      if (tagString.equals("statistics")) {
        myMenuSettingTag = 2;
      }
      if (tagString.equals("item")) {
        myMenuSettingTag = 3;
      }
      if (myMenu != null) {
        onCreateOptionsMenu(myMenu);
      }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
        Intent intent) {
      super.onActivityResult(requestCode, resultCode, intent);
      reflushAccountViewList();
    }
    
    private void reflushAccountViewList(){
      createAccountViewList();
    }
    private void createAccountViewList(){
      ListView myListView = (ListView) findViewById(R.id.list);
      TextView myTextView = (TextView) findViewById(R.id.browse_empty);
      myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            
        }});
      
      myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
          Cursor c =  dbAdapter.quryAcountById(id);
          Intent i = new Intent(MainActivity.this, AccountEditActivity.class);
          i.putExtra(MoneyDBAdapter.AITEM_NAME, c.getString(c.getColumnIndexOrThrow(MoneyDBAdapter.AITEM_NAME)));
          i.putExtra(MoneyDBAdapter.ACCOUNT_ID, c.getString(c.getColumnIndexOrThrow(MoneyDBAdapter.ACCOUNT_ID)));
          i.putExtra(MoneyDBAdapter.AITEM_TYPE, c.getString(c.getColumnIndexOrThrow(MoneyDBAdapter.AITEM_TYPE)));
          i.putExtra(MoneyDBAdapter.AMOUNT, c.getString(c.getColumnIndexOrThrow(MoneyDBAdapter.AMOUNT)));
          i.putExtra(MoneyDBAdapter.REMARK, c.getString(c.getColumnIndexOrThrow(MoneyDBAdapter.REMARK)));
          i.putExtra(MoneyDBAdapter.TIME, c.getString(c.getColumnIndexOrThrow(MoneyDBAdapter.TIME)));
          String s = c.getString(c.getColumnIndexOrThrow(MoneyDBAdapter.AMOUNT));
          Toast.makeText(MainActivity.this, "id:"+id+"金额："+s, Toast.LENGTH_SHORT ).show();
          startActivityForResult(i, ACTIVITY_EDIT);
          c.close();
          return true;
        }
      });
      Cursor  myCursor = dbAdapter.quryAcounts();
      //dbAdapter.insertAccount("2011-12-15", 103.1f, "gg十", 1, "");

      if (myCursor.moveToFirst()) {
        myTextView.setVisibility(View.GONE);
        AccountAdapter accountAdapterList = new AccountAdapter(this, myCursor);
        myListView.setAdapter(accountAdapterList);
      } else {
        myTextView.setVisibility(View.VISIBLE);
      }
      
     Cursor  totalOutCursor = dbAdapter.queryTotalInOut(0);
     Cursor  totalinCursor = dbAdapter.queryTotalInOut(1);
     float in_account =totalinCursor.getFloat(0);
     float out_account = totalOutCursor.getFloat(0);
     float total = in_account+out_account; 
     String total_foot = MessageFormat.format(getString(R.string.total_foot),in_account,out_account,total);
     TextView tview = (TextView) findViewById(R.id.name_foot);
     tview.setText(total_foot);
     
     totalOutCursor.close();
     totalinCursor.close();
     myCursor.close();
    }
    
   private void createItemViewList(){
     ListView myListView = (ListView) findViewById(R.id.itemlist);
     TextView myTextView = (TextView) findViewById(R.id.ibrowse_empty);
     myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
       @Override
       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

       }});
     
    // dbAdapter.insertItem("衣服",0);
     Cursor itemCursor = dbAdapter.quryItems();
     //System.out.println(itemCursor.getCount());
     if (itemCursor.moveToFirst()) {
       myTextView.setVisibility(View.GONE);
       ItemAdapter itemAdapterList = new ItemAdapter(this, itemCursor);
       myListView.setAdapter(itemAdapterList);
     } else {
       myTextView.setVisibility(View.VISIBLE);
     }
     
     itemCursor.close();
   }
}
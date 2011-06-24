package strong.com.money;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;

import strong.com.adapter.AccountAdapter;
import strong.com.adapter.ItemAdapter;
import strong.com.db.MoneyDBAdapter;
import strong.com.util.DateUtil;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends TabActivity implements OnTabChangeListener {

  private TabHost myTabhost;
  protected int myMenuSettingTag = 0;
  protected Menu myMenu;
  private static final int myMenuResources[] = { R.menu.a_menu, R.menu.b_menu, R.menu.c_menu };

  private MoneyDBAdapter dbAdapter;
  private static final int ACTIVITY_EDIT = 1;

  private static long rowId;

  private ArrayList<String> list = new ArrayList<String>();

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initLayoutTabHost();
    myTabhost.setOnTabChangedListener(this);
    dbAdapter = new MoneyDBAdapter(this);
    dbAdapter.open();

    createAccountViewList();
    createItemViewList();
    initSpList();
    initTongJiView();
  }

  private void initLayoutTabHost() {
    myTabhost = this.getTabHost();// 从TabActivity上面获取放置Tab的TabHost
    LayoutInflater.from(this).inflate(R.layout.main, myTabhost.getTabContentView(), true);
    // from(this)从这个TabActivity获取LayoutInflater
    // R.layout.main 存放Tab布局
    // 通过TabHost获得存放Tab标签页内容的FrameLayout
    // 是否将inflate 拴系到根布局元素上
    // myTabhost.setBackgroundColor(Color.argb(150, 22, 70, 150));

    myTabhost.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.list_bg));
    // 设置一下TabHost的颜色
    myTabhost.addTab(myTabhost.newTabSpec("acount")// 制造一个新的标签TT
        .setIndicator("账目"
        // 设置一下显示的标题为KK，设置一下标签图标为ajjc
            , getResources().getDrawable(R.drawable.account)).setContent(R.id.acount_layout));
    myTabhost.addTab(myTabhost.newTabSpec("statistics")// 制造一个新的标签TT
        .setIndicator("统计", getResources().getDrawable(R.drawable.statics)).setContent(R.id.statistics_layout));
    myTabhost.addTab(myTabhost.newTabSpec("item")// 制造一个新的标签TT
        .setIndicator("收支项", getResources().getDrawable(R.drawable.amount)).setContent(R.id.item_layout));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    // Hold on to this
    myMenu = menu;
    myMenu.clear();// 清空MENU菜单
    // Inflate the currently selected menu XML resource.
    MenuInflater inflater = getMenuInflater();
    // 从TabActivity这里获取一个MENU过滤器
    switch (myMenuSettingTag) {
    case 1:
      inflater.inflate(myMenuResources[0], menu);
      // 动态加入数组中对应的XML MENU菜单
      break;
    case 2:
      // inflater.inflate(myMenuResources[1], menu);

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

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.account_add:
      editAccount(-1);
      break;
    case R.id.account_edit:
      editAccount(rowId);
      rowId = 0;
      break;
    case R.id.account_del:
      deleteAccount(rowId);
      rowId = 0;
      break;
    case R.id.about:
      showAbout();

      break;
    case R.id.item_add:
      createEditAmountView(-1);
      break;
    case R.id.item_edit:
      createEditAmountView(rowId);
      rowId = 0;
      break;
    case R.id.item_del:
      deleteItem(rowId);
      rowId = 0;
      break;
    default:
      break;
    }
    return false;

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

  // 长按菜单响应函数
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case 0:
      editAccount(rowId);
      rowId = 0;
      break;
    case 1:
      deleteAccount(rowId);
      rowId = 0;
      break;
    case 2:
      createEditAmountView(rowId);
      rowId = 0;
      break;
    case 3:
      deleteItem(rowId);
      rowId = 0;
      break;
    default:
      rowId = 0;
      break;
    }
    return super.onContextItemSelected(item);
  }
  
  private void showAbout(){
    
    final String url = getString(R.string.url);
    String about = getString(R.string.ABOUT_MESSAGE).replace("{url}", url);
    TextView tv = new TextView(this);
    tv.setLinksClickable(true);

    tv.setAutoLinkMask(Linkify.WEB_URLS);
    tv.setGravity(Gravity.CENTER);
    tv.setText(about);
    tv.setTextSize(12f);
    String versionName = "";
    try {
      String packageName = MainActivity.class.getPackage().getName();
      PackageInfo info = getPackageManager().getPackageInfo(packageName, 0);
      versionName = info.versionName;
    } catch (PackageManager.NameNotFoundException e) {
    }
    ScrollView scrollPanel = new ScrollView(this);
    scrollPanel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    scrollPanel.addView(tv);
    try {
        new AlertDialog.Builder(this).setTitle(getString(R.string.ABOUT_TITLE) + " " + versionName).setView(scrollPanel).setIcon(R.drawable.my_icon)
            .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
              }
            }).setNeutralButton(R.string.website, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int whichButton) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
              }
            }).show();
    } catch (NumberFormatException e) {
    }
  }
  /**
   * 编辑帐目 上午11:45:24_2011-6-20
   * 
   * @param id
   */
  private void editAccount(long id) {
    Intent i = new Intent(MainActivity.this, AccountEditActivity.class);
    if (id > 0) {
      Cursor c = dbAdapter.quryAcountById(id);
      i.putExtra(MoneyDBAdapter.AITEM_NAME, c.getString(c.getColumnIndexOrThrow(MoneyDBAdapter.AITEM_NAME)));
      i.putExtra(MoneyDBAdapter.ACCOUNT_ID, c.getInt(c.getColumnIndexOrThrow(MoneyDBAdapter.ACCOUNT_ID)));
      i.putExtra(MoneyDBAdapter.AITEM_TYPE, c.getInt(c.getColumnIndexOrThrow(MoneyDBAdapter.AITEM_TYPE)));
      i.putExtra(MoneyDBAdapter.AMOUNT, Math.abs(c.getFloat(c.getColumnIndexOrThrow(MoneyDBAdapter.AMOUNT))));
      i.putExtra(MoneyDBAdapter.REMARK, c.getString(c.getColumnIndexOrThrow(MoneyDBAdapter.REMARK)));
      i.putExtra(MoneyDBAdapter.TIME, c.getString(c.getColumnIndexOrThrow(MoneyDBAdapter.TIME)));
      c.close();
      startActivityForResult(i, ACTIVITY_EDIT);
    } else if (id == 0) {
      Toast.makeText(MainActivity.this, "没有选中记录!", Toast.LENGTH_SHORT).show();
    } else if (id == -1) {
      startActivityForResult(i, ACTIVITY_EDIT);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    if (resultCode == RESULT_OK) {
      reflushAccountViewList();
    }
  }

  /**
   * 删除 下午12:48:43_2011-6-20
   * 
   * @param id
   */
  private void deleteAccount(long id) {
    dbAdapter.deleteAccount(id);
    reflushAccountViewList();
  }

  private void deleteItem(long rowId) {
    dbAdapter.deleteItem(rowId);
    createItemViewList();
  }

  private void reflushAccountViewList() {
    createAccountViewList();
  }

  private static String where ;
  private void createAccountViewList() {
    ImageButton imagBtn = (ImageButton) findViewById(R.id.imageButton);
    imagBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        final Dialog mGPSOffsetDialog = new Dialog(MainActivity.this);
        mGPSOffsetDialog.setCancelable(true);
        mGPSOffsetDialog.setTitle(R.string.show_menu);

        final LinearLayout mainPanel = new LinearLayout(MainActivity.this);
        mainPanel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mainPanel.setOrientation(LinearLayout.VERTICAL);

        RadioGroup modesRadioGroup = new RadioGroup(MainActivity.this);
        LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
            RadioGroup.LayoutParams.WRAP_CONTENT);

        modesRadioGroup.addView(buildRadioButton(getResources().getString((R.string.cur_month)), 2), 0, layoutParams);
        modesRadioGroup.addView(buildRadioButton(getResources().getString((R.string.cur_year)), 1), 0, layoutParams);
        modesRadioGroup.addView(buildRadioButton(getResources().getString((R.string.cur_all)), 0), 0, layoutParams);
       final SharedPreferences settings = getSharedPreferences("setting", 0);
       int check = settings.getInt("show_menu", 2);
       modesRadioGroup.check(check);
       mainPanel.addView(modesRadioGroup);
       modesRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
         public void onCheckedChanged(RadioGroup group, int checkedId) {
           settings.edit().putInt("show_menu", checkedId).commit();
           if(checkedId==0){
             where = null;
           }
           if(checkedId==1){
             where =MoneyDBAdapter.TIME+" like '"+ DateUtil.getCurrentYear()+"%'";
           }           
           if(checkedId==2){
             where= MoneyDBAdapter.TIME+" like '"+ DateUtil.getCurrentYearMonth()+"%'";
           }
           reflushAccountViewList();
           mGPSOffsetDialog.dismiss();
         }
       });
        mGPSOffsetDialog.setContentView(mainPanel);
        mGPSOffsetDialog.show();
      }
    });
    final ListView myListView = (ListView) findViewById(R.id.list);
    TextView myTextView = (TextView) findViewById(R.id.browse_empty);

    myListView.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        rowId = id;
      }
      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    myListView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        myListView.setItemChecked(position, false);
        rowId = id;
      }
    });

    myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        rowId = id;
        return false;
      }
    });
    myListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
      public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("菜单");
        menu.add(0, 0, 0, "编辑");
        menu.add(0, 1, 0, "删除");
      }
    });
    Cursor myCursor = dbAdapter.quryAcounts(where);

    if (myCursor.moveToFirst()) {
      myTextView.setVisibility(View.GONE);
      AccountAdapter accountAdapterList = new AccountAdapter(this, myCursor);
      myListView.setAdapter(accountAdapterList);
    } else {
      myTextView.setVisibility(View.VISIBLE);
    }

    Cursor totalOutCursor = dbAdapter.queryTotalOut(where);
    Cursor totalinCursor = dbAdapter.queryTotalIn(where);
    float in_account = totalinCursor.getFloat(0);
    float out_account = totalOutCursor.getFloat(0);
    float total = in_account + out_account;
    String total_foot = MessageFormat.format(getString(R.string.total_foot), in_account, out_account, total);
    TextView tview = (TextView) findViewById(R.id.name_foot);
    tview.setText(total_foot);

    totalOutCursor.close();
    totalinCursor.close();
    myCursor.close();
  }

  private void createItemViewList() {
    final ListView myListView = (ListView) findViewById(R.id.itemlist);
    TextView myTextView = (TextView) findViewById(R.id.ibrowse_empty);
    myListView.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        rowId = id;
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });

    myListView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        myListView.setItemChecked(position, false);
        rowId = id;

      }
    });
    myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        rowId = id;
        return false;
      }
    });

    myListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
      public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("菜单");
        menu.add(0, 2, 0, "编辑");
        menu.add(0, 3, 0, "删除");
      }
    });
    Cursor itemCursor = dbAdapter.quryItems();
    if (itemCursor.moveToFirst()) {
      myTextView.setVisibility(View.GONE);
      ItemAdapter itemAdapterList = new ItemAdapter(this, itemCursor);
      myListView.setAdapter(itemAdapterList);
    } else {
      myTextView.setVisibility(View.VISIBLE);
    }
    itemCursor.close();
  }

  private RadioButton buildRadioButton(String label, int id) {
    RadioButton btn = new RadioButton(this);
    btn.setText(label);
    btn.setId(id);
    return btn;
  }

  private void createEditAmountView(final long id) {
    if (id > 0 || id == -1) {
      final Cursor c = dbAdapter.quryItemById(id);
      final Dialog mGPSOffsetDialog;
      mGPSOffsetDialog = new Dialog(this);
      mGPSOffsetDialog.setCancelable(true);
      mGPSOffsetDialog.setTitle(R.string.edit_amountitem);

      final LinearLayout mainPanel = new LinearLayout(this);
      mainPanel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
      mainPanel.setOrientation(LinearLayout.VERTICAL);

      final TextView mTextView = new TextView(this);
      mTextView.setPadding(5, 5, 5, 0);
      String msg = getString(R.string.edit_itemname);
      mTextView.setText(msg);
      mainPanel.addView(mTextView);

      LayoutParams elementParams = new LayoutParams(200, LayoutParams.WRAP_CONTENT);
      final EditText nameText = new EditText(this);
      nameText.setTextSize(15);
      nameText.setPadding(5, 5, 5, 0);
      if (id > 0) {
        nameText.setText(c.getString(c.getColumnIndexOrThrow(MoneyDBAdapter.ITEM_NAME)));
      }
      nameText.setLayoutParams(elementParams);
      mainPanel.addView(nameText);

      RadioGroup modesRadioGroup = new RadioGroup(this);

      LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
          RadioGroup.LayoutParams.WRAP_CONTENT);

      final RadioButton zhichu = buildRadioButton(getResources().getString((R.string.zhichu)), 0);
      modesRadioGroup.addView(zhichu, 0, layoutParams);
      RadioButton shou = buildRadioButton(getResources().getString(R.string.shouru), 1);
      modesRadioGroup.addView(shou, 0, layoutParams);

      if (id > 0) {
        modesRadioGroup.check(c.getInt(c.getColumnIndexOrThrow(MoneyDBAdapter.ITEM_TYPE)));
      } else {
        modesRadioGroup.check(0);
      }
      mainPanel.addView(modesRadioGroup);

      final Button saveButton = new Button(this);
      saveButton.setText(R.string.confirm);
      saveButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View arg0) {
          int type;
          if (zhichu.isChecked()) {
            type = 0;
          } else {
            type = 1;
          }
          if (id > 0) {
            if (c.getString(c.getColumnIndexOrThrow(MoneyDBAdapter.ITEM_NAME)).equals(nameText.getText().toString())) {
              Toast.makeText(MainActivity.this, "收支项已存在", Toast.LENGTH_SHORT);
            }
            dbAdapter.updateItem(c.getInt(0), nameText.getText().toString(), type);
            c.close();
          } else {
            dbAdapter.insertItem(nameText.getText().toString(), type);
          }
          createItemViewList();
          mGPSOffsetDialog.dismiss();
        }
      });
      mainPanel.addView(saveButton);

      mGPSOffsetDialog.setContentView(mainPanel);
      mGPSOffsetDialog.show();
    } else if (id == 0) {
      Toast.makeText(MainActivity.this, "没有选中记录!", Toast.LENGTH_SHORT).show();
    }
  }

  private static int index = 0;

  /**
   * 初始化统计界面的下拉 下午01:30:10_2011-6-23
   */
  private void initTongJiView() {
    final Spinner yearSp = (Spinner) findViewById(R.id.option_year);
    final Spinner monthSp = (Spinner) findViewById(R.id.option_year_for_month);
    Button yearBtn = (Button) findViewById(R.id.a_search1);
    Button monthBtn = (Button) findViewById(R.id.a_search2);

    final Button startBtn = (Button) findViewById(R.id.option_start);
    final Button endBtn = (Button) findViewById(R.id.option_end);
    Button zBtn = (Button) findViewById(R.id.a_search3);

    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    yearSp.setAdapter(adapter);
    yearSp.setSelection(index);
    monthSp.setAdapter(adapter);
    monthSp.setSelection(index);

    startBtn.setText(DateUtil.getCurrentTime());
    startBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Calendar c = DateUtil.getStringToCalender(startBtn.getText().toString());
        new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month = (monthOfYear + 1)<10 ? "0"+(monthOfYear + 1): (monthOfYear + 1)+"";
            String day = dayOfMonth<10 ? "0"+dayOfMonth: dayOfMonth+"";
            startBtn.setText(year + "-" +month+ "-" + day);
          }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
      }
    });
    endBtn.setText(DateUtil.getCurrentTime());
    endBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Calendar c = DateUtil.getStringToCalender(endBtn.getText().toString());
        new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month = (monthOfYear + 1)<10 ? "0"+(monthOfYear + 1): (monthOfYear + 1)+"";
            String day = dayOfMonth<10 ? "0"+dayOfMonth: dayOfMonth+"";
            endBtn.setText(year + "-" + month + "-" + day);
          }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
      }
    });

    yearBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent mIntent = new Intent(MainActivity.this, StatisticsActivty.class);
        mIntent.putExtra("year", yearSp.getSelectedItem().toString());
        startActivity(mIntent);
      }
    });

    monthBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent mIntent = new Intent(MainActivity.this, StatisticsMonthActivty.class);
        mIntent.putExtra("year_month", monthSp.getSelectedItem().toString());
        startActivity(mIntent);
      }
    });
    
    zBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String start = startBtn.getText().toString();
        String end = endBtn.getText().toString();
        if (start == null || start.length() <= 0 || end == null || end.length() <= 0 ||start.compareTo(end) >= 0) {
          Toast.makeText(MainActivity.this, "时间不正确!", Toast.LENGTH_SHORT).show();
        } else {
          Intent mIntent = new Intent(MainActivity.this, StatisticsActivty.class);
          mIntent.putExtra("start", start);
          mIntent.putExtra("end", end);
          startActivity(mIntent);
        }
      }
    });
  }

  private void initSpList() {
    String[] ls = getResources().getStringArray(R.array.yeararray);
    String currentYear = String.valueOf(DateUtil.getCurrentYear());
    // 获取XML中定义的数组
    for (int i = 0; i < ls.length; i++) {
      if (currentYear.equals(ls[i])) {
        index = i;
      }
      list.add(ls[i]);
    }

  }
}
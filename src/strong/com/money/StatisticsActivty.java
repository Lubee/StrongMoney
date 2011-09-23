package strong.com.money;

import java.text.MessageFormat;

import strong.com.adapter.StatisAdapter;
import strong.com.db.MoneyDBAdapter;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class StatisticsActivty extends Activity {
  private MoneyDBAdapter dbAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.statistics_list);
    dbAdapter = new MoneyDBAdapter(this);
    dbAdapter.open();
    
    initListView();
  }

  private void initListView() {
    Bundle bundle = getIntent().getExtras();
    final ListView myListView = (ListView) findViewById(R.id.s_list);
    TextView myTextView = (TextView) findViewById(R.id.s_empty); 
    Cursor cursor = null;
    Cursor totalOutCursor =null;
    Cursor totalinCursor = null;
    if (bundle != null && !bundle.isEmpty()) {
      String year = bundle.getString("year");
      String where =null;
      if (year != null) {
         cursor = dbAdapter.queryAccountByYear(year);
          where = MoneyDBAdapter.TIME+" like '"+ year+"%'";
         totalOutCursor = dbAdapter.queryTotalOut(where);
         totalinCursor = dbAdapter.queryTotalIn(where);
         setTitle(year+"年统计数据");
      }
      String start = bundle.getString("start");
      String end = bundle.getString("end");
      if (start != null && end != null) {
        cursor = dbAdapter.queryAccountByDate(start, end);
        where = MoneyDBAdapter.TIME+" <='"+ end +"' and "+ MoneyDBAdapter.TIME + " >='"+start+"'";
        totalOutCursor = dbAdapter.queryTotalOut(where);
        totalinCursor = dbAdapter.queryTotalIn(where);
        setTitle(start+"至"+end+"统计数据");
      }
    }
    
    if (cursor.moveToFirst()) {
      myTextView.setVisibility(View.GONE);
      StatisAdapter statAdapterList = new StatisAdapter(this, cursor);
      myListView.setAdapter(statAdapterList);
    } else {
      myTextView.setVisibility(View.VISIBLE);
    }
    cursor.close();

    float in_account = totalinCursor.getFloat(0);
    float out_account = totalOutCursor.getFloat(0);
    float total = in_account + out_account;
    String total_foot = MessageFormat.format(getString(R.string.total_foot), String.valueOf(in_account), String.valueOf(out_account), String.valueOf(total));
    TextView tview = (TextView) findViewById(R.id.s_foot);
    tview.setText(total_foot);

    totalOutCursor.close();
    totalinCursor.close();
    
  }
}

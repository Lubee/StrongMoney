package strong.com.money;

import java.util.ArrayList;

import strong.com.adapter.StatisMonthAdapter;
import strong.com.db.MoneyDBAdapter;
import strong.com.pojo.Account;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class StatisticsMonthActivty extends Activity {
  private MoneyDBAdapter dbAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.statistics_month_list);
    dbAdapter = new MoneyDBAdapter(this);
    dbAdapter.open();

    initListView();
  }

  private void initListView() {
    Bundle bundle = getIntent().getExtras();
    final ListView myListView = (ListView) findViewById(R.id.sm_list);
    ArrayList<Account> list = null;
    if (bundle != null && !bundle.isEmpty()) {
      String year = bundle.getString("year_month");
      if (year != null) {
        list = dbAdapter.queryAccountByMonth(year);
      }
    }
    if (list != null && list.size() > 0) {
      StatisMonthAdapter statAdapterList = new StatisMonthAdapter(this, list);
      myListView.setAdapter(statAdapterList);
    }
  }
}

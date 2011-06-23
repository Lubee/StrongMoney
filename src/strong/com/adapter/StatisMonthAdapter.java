package strong.com.adapter;

import java.util.ArrayList;

import strong.com.db.MoneyDBAdapter;
import strong.com.money.R;
import strong.com.pojo.Account;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StatisMonthAdapter extends BaseAdapter {

  private Context _ctx;
  private ArrayList<Integer> monthList;
  private ArrayList<Float> saccountList;
  private ArrayList<Float> zaccountList;
  private ArrayList<Float> yamountList;

  
  private LayoutInflater mlin; //to get the Context's layout
  
  static class Holder{
    TextView name;
    TextView samount;
    TextView zamount;
    TextView yamount;
  }
  
  public StatisMonthAdapter(Context ctx, ArrayList<Account> list){
    _ctx = ctx;
    mlin = LayoutInflater.from(ctx);
    monthList = new ArrayList<Integer>();
    saccountList =new ArrayList<Float>();
    zaccountList =new ArrayList<Float>();
    yamountList = new ArrayList<Float>();
    
    int size = list.size();
    for (int i=0; i<size; i++)
    {
      Account ac = list.get(i);
      monthList.add(ac.getMonth());
      saccountList.add(ac.getsAmount());
      zaccountList.add(ac.getzAmount());
      yamountList.add(ac.getyAmount());
    }
  }
  
  @Override
  public int getCount() {
    return monthList.size();
  }

  @Override
  public Object getItem(int position) {
    return getView(position,null,null);
  }

  @Override
  public long getItemId(int position) {
    return monthList.get(position);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Holder holder = null;
    if (convertView == null) {
      convertView = mlin.inflate(R.layout.statistics_list_month_item, null);
      holder = new Holder();
      holder.name = (TextView)convertView.findViewById(R.id.st_month);
      holder.samount = (TextView)convertView.findViewById(R.id.sr_amount);
      holder.zamount = (TextView)convertView.findViewById(R.id.zc_amount);
      holder.yamount = (TextView)convertView.findViewById(R.id.ye_amount);
      convertView.setTag(holder);
    } else {
      holder=(Holder)convertView.getTag();
    }

    holder.name.setText(String.valueOf(monthList.get(position)));
    holder.samount.setText(String.valueOf(saccountList.get(position)));
    holder.zamount.setText(String.valueOf(zaccountList.get(position)));
    holder.yamount.setText(String.valueOf(yamountList.get(position)));
    return convertView;
  }

}

package strong.com.adapter;

import java.util.ArrayList;

import strong.com.db.MoneyDBAdapter;
import strong.com.money.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StatisAdapter extends BaseAdapter {

  private Context _ctx;
  private ArrayList<String> accountNameList;
  private ArrayList<Float> amountList;

  
  private LayoutInflater mlin; //to get the Context's layout
  
  static class Holder{
    TextView name;
    TextView amount;
  }
  
  public StatisAdapter(Context ctx, Cursor myCursor){
    _ctx = ctx;
    mlin = LayoutInflater.from(ctx);
    accountNameList = new ArrayList<String>();
    amountList = new ArrayList<Float>();
    
    for (int i=0; i<myCursor.getCount(); i++)
    {
      amountList.add(myCursor.getFloat(0));
      accountNameList.add(myCursor.getString(myCursor.getColumnIndexOrThrow(MoneyDBAdapter.AITEM_NAME)));
      myCursor.moveToNext();
    }
    myCursor.close();
  }
  
  @Override
  public int getCount() {
    return accountNameList.size();
  }

  @Override
  public Object getItem(int position) {
    return getView(position,null,null);
  }

  @Override
  public long getItemId(int position) {
    return Math.round(amountList.get(position));
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Holder holder = null;
    if (convertView == null) {
      convertView = mlin.inflate(R.layout.statistics_list_item, null);
      holder = new Holder();
      holder.name = (TextView)convertView.findViewById(R.id.stati_name);
      holder.amount = (TextView)convertView.findViewById(R.id.stati_amount);
      convertView.setTag(holder);
    } else {
      holder=(Holder)convertView.getTag();
    }

    holder.name.setText(accountNameList.get(position));
    holder.amount.setText(amountList.get(position).toString());
    
    return convertView;
  }

}

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

public class AccountAdapter extends BaseAdapter {

  private Context _ctx;
  private ArrayList<Integer> accountIDList;
  private ArrayList<String> accountNameList;
  private ArrayList<String> accountTimeList;
  private ArrayList<Float> amountList;

  
  private LayoutInflater mlin; //to get the Context's layout
  
  static class Holder{
    TextView name;
    TextView time;
    TextView amount;
  }
  
  public AccountAdapter(Context ctx, Cursor myCursor){
    _ctx = ctx;
    mlin = LayoutInflater.from(ctx);
    accountIDList = new ArrayList<Integer>();
    accountNameList = new ArrayList<String>();
    accountTimeList = new ArrayList<String>();
    amountList = new ArrayList<Float>();
    
    for (int i=0; i<myCursor.getCount(); i++)
    {
      accountIDList.add(myCursor.getInt(0));
      accountNameList.add(myCursor.getString(myCursor.getColumnIndexOrThrow(MoneyDBAdapter.AITEM_NAME)));
      accountTimeList.add(myCursor.getString(myCursor.getColumnIndexOrThrow(MoneyDBAdapter.TIME)));
      amountList.add(myCursor.getFloat(myCursor.getColumnIndexOrThrow(MoneyDBAdapter.AMOUNT)));
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
    return accountIDList.get(position);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Holder holder = null;
    if (convertView == null) {
      convertView = mlin.inflate(R.layout.account_list, null);
      holder = new Holder();
      holder.name = (TextView)convertView.findViewById(R.id.account_name);
      holder.amount = (TextView)convertView.findViewById(R.id.amount);
      holder.time = (TextView)convertView.findViewById(R.id.account_time);
      convertView.setTag(holder);
    } else {
      holder=(Holder)convertView.getTag();
    }

    holder.name.setText(accountNameList.get(position));
    holder.amount.setText(amountList.get(position).toString());
    holder.time.setText(accountTimeList.get(position));
    
    return convertView;
  }

}

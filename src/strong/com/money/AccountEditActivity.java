package strong.com.money;

import java.util.ArrayList;
import java.util.Calendar;

import strong.com.db.MoneyDBAdapter;
import strong.com.pojo.Item;
import strong.com.util.DateUtil;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AccountEditActivity extends Activity {

  private MoneyDBAdapter dbAdapter;

  private Spinner itemNameSp;
  private EditText amountEdit;
  private Button timeBtn;
  private EditText remarkEdit;
  private EditText typeEdit;

  private Button confirmBtn;
  private Button cancelBtn;

  private ArrayList<Item> list = new ArrayList<Item>();

  private long id;
  private String itemName;
  private Integer type;
  private int index = -1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.account_edit);
    dbAdapter = new MoneyDBAdapter(this);
    dbAdapter.open();

    itemNameSp = (Spinner) findViewById(R.id.edit_aitemname);
    amountEdit = (EditText) findViewById(R.id.edit_amount);
    timeBtn = (Button) findViewById(R.id.edit_time);
    remarkEdit = (EditText) findViewById(R.id.edit_remark);
    typeEdit = (EditText) findViewById(R.id.edit_type);
    confirmBtn = (Button) findViewById(R.id.a_confirm);
    cancelBtn = (Button) findViewById(R.id.a_cancel);

    initEditView();
    setList();
    initItemNameSpinner();
    setBtnListener();
  }

  private void initItemNameSpinner() {
    ArrayAdapter<Item>  adapter = new ArrayAdapter<Item>(this, android.R.layout.simple_spinner_item, list);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    itemNameSp.setAdapter(adapter);
    itemNameSp.setSelection(index, true);
    itemNameSp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Item item = ((Item) itemNameSp.getSelectedItem());
        // Cursor c = dbAdapter.quryItemById(item.getItem_id());
        typeEdit.setText(String.valueOf(item.getItem_type()));
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
      }
    });
  }

  private void setList() {
    Cursor c = dbAdapter.quryItems();
    if (c.moveToFirst()) {
      for (int i = 0; i < c.getCount(); i++) {
        Item item = new Item();
        item.setItem_id(c.getInt(0));
        item.setItem_type(c.getInt(c.getColumnIndexOrThrow(MoneyDBAdapter.ITEM_TYPE)));
        item.setItem_name(c.getString(c.getColumnIndexOrThrow(MoneyDBAdapter.ITEM_NAME)));
        if (itemName != null && item.getItem_name().equals(itemName)) {
          index = i;
        }
        list.add(item);
        c.moveToNext();
      }
    }
    c.close();
    if (itemName != null && type != null && index < 0) {
      Item item = new Item();
      item.setItem_id(index);
      item.setItem_name(itemName);
      item.setItem_type(type);
      list.add(item);
      index = list.size() - 1;
    }
  }

  private void setBtnListener() {
    confirmBtn.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        try {
          type = Integer.parseInt(typeEdit.getText().toString());
          float amount = (type - 1) < 0 ? (-1) * Float.parseFloat(amountEdit.getText().toString()) : Float.parseFloat(amountEdit.getText().toString());
          if (id > 0) {
            dbAdapter.updateAccount(id, timeBtn.getText().toString(), amount, ((Item) itemNameSp.getSelectedItem()).getItem_name(), type, remarkEdit.getText().toString());
          } else {
            dbAdapter.insertAccount(timeBtn.getText().toString(), amount, ((Item) itemNameSp.getSelectedItem()).getItem_name(), type, remarkEdit.getText().toString());
          }
          Intent mIntent = new Intent();
          setResult(RESULT_OK, mIntent);
          finish();
        } catch (NumberFormatException e) {
          Toast.makeText(AccountEditActivity.this, "金额不能为空!", Toast.LENGTH_SHORT).show();
        }
      }
    });
    cancelBtn.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        finish();
      }
    });
    timeBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Calendar c = DateUtil.getStringToCalender(timeBtn.getText().toString().trim());
        new DatePickerDialog(AccountEditActivity.this, new DatePickerDialog.OnDateSetListener() {
          public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month = (monthOfYear + 1)<10 ? "0"+(monthOfYear + 1): (monthOfYear + 1)+"";
            timeBtn.setText(year + "-" + month+ "-" + dayOfMonth);
          }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

      }
    });
  }

  private void initEditView() {
    Intent i = getIntent();
    Bundle bundle = i.getExtras();
    if (bundle != null && !bundle.isEmpty()) {
      itemName = bundle.getString(MoneyDBAdapter.AITEM_NAME);
      Float amount = bundle.getFloat(MoneyDBAdapter.AMOUNT);
      if (amount != null) {
        amountEdit.setText(amount.toString());
      }
      String time = bundle.getString(MoneyDBAdapter.TIME);
      if (time != null) {
        timeBtn.setText(time);
      } else {
        timeBtn.setText(DateUtil.getCurrentTime());
      }
      String remark = bundle.getString(MoneyDBAdapter.REMARK);
      if (remark != null) {
        remarkEdit.setText(remark);
      }
      type = bundle.getInt(MoneyDBAdapter.AITEM_TYPE);
      if (type != null) {
        typeEdit.setText(type.toString());
      }

      id = bundle.getInt(MoneyDBAdapter.ACCOUNT_ID);

    } else {
      timeBtn.setText(DateUtil.getCurrentTime());
    }
  }

}

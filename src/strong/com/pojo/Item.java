package strong.com.pojo;

public class Item {
  private int item_type;
  private int item_id;
  private String item_name;
  public int getItem_type() {
    return item_type;
  }
  public void setItem_type(int item_type) {
    this.item_type = item_type;
  }
  public int getItem_id() {
    return item_id;
  }
  public void setItem_id(int item_id) {
    this.item_id = item_id;
  }
  public String getItem_name() {
    return item_name;
  }
  public void setItem_name(String item_name) {
    this.item_name = item_name;
  }
  @Override
  public String toString() {
    return this.item_name;
  }
  
}

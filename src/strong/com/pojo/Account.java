package strong.com.pojo;

public class Account {
  private float sAmount;
  private float zAmount;
  private float yAmount;
  private int month;

  public float getsAmount() {
    return sAmount;
  }

  public void setsAmount(float sAmount) {
    this.sAmount = sAmount;
  }

  public float getzAmount() {
    return zAmount;
  }

  public void setzAmount(float zAmount) {
    this.zAmount = zAmount;
  }

  public float getyAmount() {
    return this.sAmount-this.zAmount;
  }

  public void setyAmount(float yAmount) {
    this.yAmount = yAmount;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

}

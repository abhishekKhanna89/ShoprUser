package model;

public class RecyclerModel {
    String date,addMoney,transactionId,greenPrice,redPrice,
            balance,received;

    public RecyclerModel(String date, String addMoney, String transactionId, String greenPrice, String redPrice, String balance, String received) {
        this.date = date;
        this.addMoney = addMoney;
        this.transactionId = transactionId;
        this.greenPrice = greenPrice;
        this.redPrice = redPrice;
        this.balance = balance;
        this.received = received;
    }

    public String getDate() {
        return date;
    }

    public String getAddMoney() {
        return addMoney;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getGreenPrice() {
        return greenPrice;
    }

    public String getRedPrice() {
        return redPrice;
    }

    public String getBalance() {
        return balance;
    }

    public String getReceived() {
        return received;
    }
}

package com.shoppr.shoper.Model.WalletHistory;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("wallet_transactions")
    @Expose
    private List<WalletTransaction> walletTransactions = null;
    @SerializedName("balance")
    @Expose
    private Integer balance;

    public List<WalletTransaction> getWalletTransactions() {
        return walletTransactions;
    }

    public void setWalletTransactions(List<WalletTransaction> walletTransactions) {
        this.walletTransactions = walletTransactions;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

}

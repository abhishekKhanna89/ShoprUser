package com.shoppr.shoper.Model.WalletHistory;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class History {

    @SerializedName("Friday, Jun 26, 2020")
    @Expose
    private List<FridayJun262020> fridayJun262020 = null;
    @SerializedName("Tuesday, Jun 30, 2020")
    @Expose
    private List<TuesdayJun302020> tuesdayJun302020 = null;
    @SerializedName("Sunday, Jun 28, 2020")
    @Expose
    private List<SundayJun282020> sundayJun282020 = null;

    public List<FridayJun262020> getFridayJun262020() {
        return fridayJun262020;
    }

    public void setFridayJun262020(List<FridayJun262020> fridayJun262020) {
        this.fridayJun262020 = fridayJun262020;
    }

    public List<TuesdayJun302020> getTuesdayJun302020() {
        return tuesdayJun302020;
    }

    public void setTuesdayJun302020(List<TuesdayJun302020> tuesdayJun302020) {
        this.tuesdayJun302020 = tuesdayJun302020;
    }

    public List<SundayJun282020> getSundayJun282020() {
        return sundayJun282020;
    }

    public void setSundayJun282020(List<SundayJun282020> sundayJun282020) {
        this.sundayJun282020 = sundayJun282020;
    }

}


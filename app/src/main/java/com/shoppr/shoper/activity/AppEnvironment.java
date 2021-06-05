package com.shoppr.shoper.activity;


/**
 * Created by Rahul Hooda on 14/7/17.
 */
public enum AppEnvironment {

    SANDBOX {
        @Override
        public String merchant_Key() {
            return "Cf8wcQiQ";
        }

        @Override
        public String merchant_ID() {
            return "7406162";
        }

        @Override
        public String furl() {
            return "http://shoprs.co.in/api/success-url";
        }

        @Override
        public String surl() {
            return "http://shoprs.co.in/api/success-url";
        }

        @Override
        public String salt() {
            return "seVTUgzrgE";
        }

        @Override
        public boolean debug() {
            return true;
        }
    },
    PRODUCTION {
        @Override
        public String merchant_Key() {
            return "Cf8wcQiQ";
        }
        @Override
        public String merchant_ID() {
            return "7406162";
        }
        @Override
        public String furl() {
            return "http://shoprs.co.in/api/success-url";
        }

        @Override
        public String surl() {
            return "http://shoprs.co.in/api/success-url";
        }

        @Override
        public String salt() {
            return "seVTUgzrgE";
        }

        @Override
        public boolean debug() {
            return false;
        }
    };

    public abstract String merchant_Key();

    public abstract String merchant_ID();

    public abstract String furl();

    public abstract String surl();

    public abstract String salt();

    public abstract boolean debug();


}

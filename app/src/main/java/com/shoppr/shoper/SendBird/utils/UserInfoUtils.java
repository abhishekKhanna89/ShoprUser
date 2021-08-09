package com.shoppr.shoper.SendBird.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sendbird.calls.User;
import com.shoppr.shoper.R;
import com.squareup.picasso.Picasso;

/*
public class UserInfoUtils {

    public static void setProfileImage(Context context, User user, ImageView imageViewProfile) {
        if (user != null && imageViewProfile != null) {
            String profileUrl = user.getProfileUrl();
            if (TextUtils.isEmpty(profileUrl)) {
                imageViewProfile.setBackgroundResource(R.drawable.icon_avatar);
            } else {
                ImageUtils.displayCircularImageFromUrl(context, user.getProfileUrl(), imageViewProfile);
            }
        }
    }

    public static void setUserId(User user, TextView textViewUserId) {
        if (user != null && textViewUserId != null) {
            textViewUserId.setText(user.getUserId());
        }
    }
}
*/
public class UserInfoUtils {

    public static void setProfileImage(Context context, User user, ImageView imageViewProfile) {
        if (user != null && imageViewProfile != null) {
            String profileUrl = user.getProfileUrl();
            if (TextUtils.isEmpty(profileUrl)) {
                imageViewProfile.setBackgroundResource(R.drawable.icon_avatar);
            } else {
                Picasso.get().load(user.getProfileUrl()).into(imageViewProfile);
            }
        }
    }

    public static void setNickname(Context context, User user, TextView textViewNickname) {
        if (user != null && textViewNickname != null) {
            String nickname = user.getNickname();
            if (TextUtils.isEmpty(nickname)) {
                textViewNickname.setText(context.getString(R.string.calls_empty_nickname));
            } else {
                textViewNickname.setText(nickname);
            }
        }
    }

    public static void setUserId(Context context, User user, TextView textViewUserId) {
        if (user != null && textViewUserId != null) {
            textViewUserId.setText(context.getString(R.string.calls_user_id_format, user.getUserId()));
        }
    }

    public static void setNicknameOrUserId(User user) {
        if (user != null) {
            String nickname = user.getNickname();
            /*if (TextUtils.isEmpty(nickname)) {
                textViewNickname.setText(user.getUserId());
            } else {
                textViewNickname.setText(nickname);
            }*/
        }
    }

    public static String getNicknameOrUserId(User user) {
        String nicknameOrUserId = "";
        if (user != null) {
            nicknameOrUserId = user.getNickname();
           /* if (TextUtils.isEmpty(nicknameOrUserId)) {
                nicknameOrUserId = user.getUserId();
            }*/
        }
        return nicknameOrUserId;
    }

    public static void setUserId(User user, TextView textViewUserId) {
        if (user != null && textViewUserId != null) {
            textViewUserId.setText(user.getUserId());
        }
    }

}
package com.shoppr.shoper.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.shoppr.shoper.BuildConfig;
import com.shoppr.shoper.LoginActivity;
import com.shoppr.shoper.Model.ChatMessage.Chat;
import com.shoppr.shoper.Model.ChatMessage.ChatMessageModel;
import com.shoppr.shoper.Model.ChatModel;
import com.shoppr.shoper.Model.InitiateVideoCall.InitiateVideoCallModel;
import com.shoppr.shoper.Model.Send.SendModel;
import com.shoppr.shoper.Model.TerminateChat.TerminateChatModel;
import com.shoppr.shoper.R;
import com.shoppr.shoper.SendBird.utils.ActivityUtils;
import com.shoppr.shoper.SendBird.utils.AuthenticationUtils;
import com.shoppr.shoper.SendBird.utils.PrefUtils;
import com.shoppr.shoper.SendBird.utils.ToastUtils;
import com.shoppr.shoper.Service.ApiExecutor;
import com.shoppr.shoper.Service.ApiService;
import com.shoppr.shoper.adapter.ChatMessageAdapter;
import com.shoppr.shoper.util.ApiFactory;
import com.shoppr.shoper.util.CommonUtils;
import com.shoppr.shoper.util.ImageArr;
import com.shoppr.shoper.util.ImagePath_MarshMallow;
import com.shoppr.shoper.util.SessonManager;
import com.shoppr.shoper.util.SocketInfo;
import com.shoppr.shoper.util.UploadGalleryImages;
import com.shoppr.shoper.util.UploadProductAudio;
import com.shoppr.shoper.util.UploadProductImages;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private static final String[] MANDATORY_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,   // for VoiceCall and VideoCall
            Manifest.permission.CAMERA          // for VideoCall
    };
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    boolean flag = false;
    public static int chat_id;
    RecyclerView chatRecyclerView;
    SessonManager sessonManager;
    List<Chat> chatList;
    EditText editText;
    CountDownTimer countDownTimer;

    ImageButton chooseImage;
    LinearLayoutCompat llSend;
    /*Todo:- BroadCast Receiver*/
    BroadcastReceiver mMessageReceiver;
    String body;
    List<ChatModel> msgDtoList;
    private static String baseUrl = ApiExecutor.baseUrl;
    String uploadedImage = "";
    /*Todo:- Voice Recorder*/
    //private boolean isRecording = false;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;
    private MediaRecorder mediaRecorder;
    private String recordFile;

    String pathforaudio;
    int shopId;
    /*Todo:- UserDP*/
    CircleImageView userDp;
    TextView userName;
    String shopperName = "", shopperPic = "";
    FabSpeedDial fabSpeedDial;

    //String TAG="lakshmi";

    BottomSheetDialog bottomSheetDialog;
    TextView cart_badge;
    /*Todo:- Recording Library*/
    RecordView recordView;
    RecordButton recordButton;
    /*Todo:- image upload*/
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    File destination;
    String userChoosenTask;
    byte[] byteArray;
    public final static int PERM_REQUEST_CODE_DRAW_OVERLAYS = 1234;
    private Uri fileUri = null;

    private Socket mSocket;
    private Boolean isConnected = true;
    private ArrayList<String> uploadedImageArray = new ArrayList<>();
    private String uploadedType = "text";
    private ArrayList<String> finalPathImageArray = new ArrayList<>();

    String TAG = "TAG";
    private static String mFileName = null;
    String recordPath;
    String imagePath = "";
    private boolean isSocketConnected = false, isUserConnected = false, isUserActive = false, isShowConnection = true;


    private void SocketConnection() {

         /* try {
            mSocket = IO.socket(SocketInfo.SOCKET_URL);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.connect();
        } catch (URISyntaxException e) {
           *//* Toast.makeText(ChatActivity.this.getApplicationContext(),
                    "user Connected" + chat_id+",,,,,,"+e.getMessage(), Toast.LENGTH_SHORT).show();*//*
        }*/
        try {
            mSocket = IO.socket(SocketInfo.SOCKET_URL);
            mSocket.on("connected", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("TAG_connected", "connected");
                    isSocketConnected = true;
                    isUserConnected = false;
                    callConnectUser();
                    chatMessageList(chat_id);
                    if (isShowConnection) {
                        isShowConnection = false;
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
//                            }
//                        });
                    }
                }

            }).on("history", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("TAG", "history");
                    Log.e("TAG", "jsonArray_history1 " + args[0]);
                    JSONObject jsonObject = (JSONObject) args[0];
                    Log.e("TAG", "jsonArray_history1 " + jsonObject);

                    isSocketConnected = true;
                    isUserConnected = true;
                    chatMessageList(chat_id);
                  /*  try {
                        JSONObject data = jsonObject.getJSONObject("data");
                        Log.e("TAG", "data " + data);
                        Chat chatMessage = new Chat();
                        JSONArray jsonArray = data.getJSONArray("chats");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            chatMessage.setId(jsonObject1.getInt("id"));
                            chatMessage.setType(jsonObject1.getString("type"));
                            chatMessage.setMessage(jsonObject1.getString("message"));
                            chatMessage.setCreatedAt(jsonObject1.getString("created_at"));
                            chatMessage.setStatus(jsonObject1.getString("status"));
                            chatList.add(chatMessage);
                        }

                        System.out.println("chatListchatListchatListchatList" + chatList);


                        ChatMessageAdapter chatMessageAdapter1 = new ChatMessageAdapter(ChatActivity.this, chatList);
                        chatRecyclerView.setAdapter(chatMessageAdapter1);
                        chatRecyclerView.scrollToPosition(chatList.size() - 1);
                        chatRecyclerView.smoothScrollToPosition(chatRecyclerView.getAdapter().getItemCount());
                        chatMessageAdapter1.notifyDataSetChanged();
                        if (chatList.size() != 0) {
                            // progress_bar.setVisibility(View.GONE);
                            ChatActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //  chatAdapter .updateAdapter(chatList);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ChatActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        chatRecyclerView.smoothScrollToPosition(chatRecyclerView.getAdapter().getItemCount() - 1);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }
                                    }, 500);
                                }
                            });
                        } else {
                            //progress_bar.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                }
            }).on("leave", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("TAG", "leave");
                    isSocketConnected = false;
                    isUserConnected = false;
                }

            }).on("get_message", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("TAG", "get_message");
                    try {
                        Log.e("TAG", "jsonArray " + args[0]);
                        JSONObject jsonArray = (JSONObject) args[0];
                        Log.e("TAG", "jsonArray " + jsonArray);
                        Log.e("TAG", "jsonArray_history_response" + jsonArray);
                        String status = jsonArray.getString("status");
                        String message = jsonArray.getString("message");
                        ChatActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                if (status.equals("failed")) {
                                    Toast.makeText(ChatActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                      /*  String code = jsonArray.getString("code");
                        Log.e("TAG", "code " + code);*/

                        JSONObject data = jsonArray.getJSONObject("data");
                        int message_id = data.getInt("message_id");


                           /* JSONObject data =jsonArray.getJSONObject("msgdata");
                            Log.e("TAG", "data "+data);
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.setId(data.getString("id"));
                            chatMessage.setSender_id(data.getString("sender_id"));
                            chatMessage.setReceiver_id(data.getString("receiver_id"));
                            chatMessage.setGroup_id(data.getString("group_id"));
                            chatMessage.setSend_by(data.getString("send_by"));
                            chatMessage.setMsg_type(data.getString("msg_type"));
                            chatMessage.setMsg_url(data.getString("msg_url"));
                            chatMessage.setMessage(data.getString("message"));
                            chatMessage.setCreated_at(data.getString("created_at"));
                            chatMessage.setStatus(data.getString("status"));
                            chatMessage.setMessage_read_status(data.getString("message_read_status"));*/
                          /* // chatList.add(chatMessage);

                            ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter(ChatActivity.this, chatList);
                            chatRecyclerView.setAdapter(chatMessageAdapter);
                            chatRecyclerView.scrollToPosition(chatList.size() - 1);
                            chatRecyclerView.smoothScrollToPosition(chatRecyclerView.getAdapter().getItemCount());
                            chatMessageAdapter.notifyDataSetChanged();
                           if (chatList.size() != 0) {
                               // progress_bar.setVisibility(View.GONE);
                               ChatActivity.this.runOnUiThread(new Runnable() {
                                   @Override
                                   public void run() {
                                      //  chatAdapter .updateAdapter(chatList);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                               ChatActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                       try {
                                                           chatRecyclerView.smoothScrollToPosition(chatRecyclerView.getAdapter().getItemCount() - 1);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        } }
                                               });}
                                       }, 500);
                                    }
                               });
                            }else {
                               //progress_bar.setVisibility(View.VISIBLE);
                           }*/

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }).on("chat_message", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("TAG", "chat_message");
                    chatMessageList(chat_id);

                    /*try {
                        JSONObject jsonArray = new JSONObject(args[0].toString());
                        Log.e("TAG", "jsonArray "+jsonArray);

                        String code = jsonArray.getString("code");
                        final   String msg = jsonArray.getString("msg");
                        Log.e("TAG", "code "+code);

                        if (code.equalsIgnoreCase("200")) {
                            JSONObject data =jsonArray.getJSONObject("msgdata");
                            Log.e("TAG", "data "+data);
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.setId(data.getString("id"));
                            chatMessage.setSender_id(data.getString("sender_id"));
                            chatMessage.setReceiver_id(data.getString("receiver_id"));
                            chatMessage.setGroup_id(data.getString("group_id"));
                            chatMessage.setSend_by(data.getString("send_by"));
                            chatMessage.setMsg_type(data.getString("msg_type"));
                            chatMessage.setMsg_url(data.getString("msg_url"));
                            chatMessage.setMessage(data.getString("message"));
                            chatMessage.setCreated_at(data.getString("created_at"));
                            chatMessage.setStatus(data.getString("status"));
                            chatMessage.setMessage_read_status(data.getString("message_read_status"));
                            datalist.add(chatMessage);
                            if (datalist.size() != 0) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress_bar.setVisibility(View.GONE);
                                        chatAdapter .updateAdapter(datalist);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            chatlist.smoothScrollToPosition(chatlist.getAdapter().getItemCount() - 1);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                            }
                                        }, 500);
                                    }
                                });

                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress_bar.setVisibility(View.GONE);
                                    }
                                });
                            }
                            String  currentUserID = Variables.sharedPreferences.getString(Variables.u_mobile, "");
                            //   Log.e("TAG", "currentUserID : "+currentUserID);
                            //   Log.e("TAG", "message_read_status : "+data.getString("message_read_status"));
                            ///  Log.e("TAG", "receiver_id : "+data.getString("receiver_id"));
                            if(data.getString("message_read_status").equalsIgnoreCase("0") && data.getString("receiver_id").equalsIgnoreCase(currentUserID)){
                                try {
                                    JSONObject jsonObjectSend = new JSONObject();
                                    jsonObjectSend.put("msg_id", data.getString("id"));
                                    jsonObjectSend.put("group_id", group_id);
                                    //   Log.e("TAG", "jsonObject seen: "+jsonObjectSend);
                                    mSocket.emit("seen", jsonObjectSend);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }else  {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }

            }).on("disconnect", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("TAG", "disconnect");
                    isSocketConnected = false;
                    isUserConnected = false;
                }

            }).on("delete_chat", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("TAG", "delete_chat");
                  /*  try {
                        JSONObject jsonArray = new JSONObject(args[0].toString());
                        Log.e("TAG", "jsonArray "+jsonArray);
                        String code = jsonArray.getString("code");
                        final   String msg = jsonArray.getString("msg");
                        Log.e("TAG", "code "+code);
                        if (code.equalsIgnoreCase("200")) {
                            datalist = new ArrayList<>();
                            datalist.clear();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatAdapter.updateAdapter(datalist);
                                    Toast.makeText(getApplicationContext(), "Chat has been deleted", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else  {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
            }).on("block_user", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("TAG", "block_user");
                   /* try {
                        JSONObject jsonArray = new JSONObject(args[0].toString());
                        Log.e("TAG", "jsonArray "+jsonArray);
                        String code = jsonArray.getString("code");
                        Log.e("TAG", "code "+code);
                        final   String msg = jsonArray.getString("msg");
                        if (code.equalsIgnoreCase("200")) {
                            JSONObject data =jsonArray.getJSONObject("data");
                            Log.e("TAG", "data "+data);
                            if(data.getString("block_by_user_id").equalsIgnoreCase(Variables.sharedPreferences.getString(Variables.u_mobile, ""))){
                                isBlockOther  = "1";
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "This user has been blocked", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                isBlockYou = "1";
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "You have been blocked", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                        }else  {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
            }).on("seen", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject jsonArray = null;
                    try {
                        jsonArray = new JSONObject(args[0].toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("TAG", "seen");
                    Log.e("TAG", "jsonArray_Seen");
                    Log.e("TAG", "jsonArray_Seen " + jsonArray);
                    /*try {
                        JSONObject jsonArray = new JSONObject(args[0].toString());
                        Log.e("TAG", "jsonArray "+jsonArray);
                        String code = jsonArray.getString("code");
                        Log.e("TAG", "code "+code);
                        final   String msg = jsonArray.getString("msg");
                        if (code.equalsIgnoreCase("200")) {
                            String  msg_id =jsonArray.getString("msg_id");
                            Log.e("TAG", "msg_id "+msg_id);
                            final   int index =  getMessageIndex(msg_id);
                            Log.e("TAG", "index "+index);
                            if(index != -1){
                                datalist.get(index).setMessage_read_status("1");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        chatAdapter.updateAdapter(datalist);
                                    }
                                });

                            }
                        }else  {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
            }).on("delete_message", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("TAG", "delete_message");
                   /* try {
                        JSONObject jsonArray = new JSONObject(args[0].toString());
                        Log.e("TAG", "jsonArray "+jsonArray);
                        String code = jsonArray.getString("code");
                        Log.e("TAG", "code "+code);
                        final   String msg = jsonArray.getString("msg");
                        if (code.equalsIgnoreCase("200")) {
                            String  msg_id =jsonArray.getString("msg_id");
                            Log.e("TAG", "msg_id "+msg_id);
                            final   int index =  getMessageIndex(msg_id);
                            Log.e("TAG", "index "+index);
                            if(index != -1){
                                datalist.remove(index);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Message has been deleted",Toast.LENGTH_SHORT).show();
                                        chatAdapter.updateAdapter(datalist);
                                    }
                                });

                            }
                        }else  {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
            }).on("unblock_user", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.e("TAG", "unblock_user");
                   /* try {
                        JSONObject jsonArray = new JSONObject(args[0].toString());
                        Log.e("TAG", "jsonArray "+jsonArray);
                        String code = jsonArray.getString("code");
                        Log.e("TAG", "code "+code);
                        final   String msg = jsonArray.getString("msg");
                        if (code.equalsIgnoreCase("200")) {
                            JSONObject data =jsonArray.getJSONObject("data");
                            Log.e("TAG", "data "+data);
                            if(data.getString("unblock_to_user_id").equalsIgnoreCase(Variables.sharedPreferences.getString(Variables.u_mobile, ""))){
                                isBlockYou  = "0";
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "You have been unblocked", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                isBlockOther = "0";
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "This user has been unblocked", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }else  {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
            });
            mSocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateGalleryImages() {
        final ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        UploadGalleryImages task = new UploadGalleryImages(ChatActivity.this, new UploadGalleryImages.CallBackImageUpload() {
            @Override
            public void onImageUpload(ArrayList<String> imageUrl) {
                Log.e("TAG", "onImageUpload:  in after async task " + imageUrl.size());
                uploadedImageArray = new ArrayList<>();
                uploadedImageArray.clear();
                uploadedImageArray = imageUrl;
                uploadedType = "imageArray";
                for (int i = 0; i < uploadedImageArray.size(); i++) {
                    Log.e("TAG", "image: " + uploadedImageArray.get(i));
                    if (i == uploadedImageArray.size() - 1) {
                        sendChatMessage(uploadedImageArray.get(i), "image", false);
                    } else {
                        sendChatMessage(uploadedImageArray.get(i), "image", true);
                    }

                }

                progressDialog.dismiss();
            }

            @Override
            public void onErrorOccured(String error) {
                progressDialog.dismiss();
                Toast.makeText(ChatActivity.this, error, Toast.LENGTH_SHORT).show();
            }

        });
        ArrayList<ImageArr> arrayList = new ArrayList<>();
        for (int i = 0; i < finalPathImageArray.size(); i++) {
            ImageArr imageArr = new ImageArr();
            imageArr.setId(String.valueOf(i));
            File file = new File(compressImage(finalPathImageArray.get(i), progressDialog));
            imageArr.setImage(file);
            arrayList.add(imageArr);

        }
        task.execute(arrayList);
    }


    private void callConnectUser() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("chat_id", chat_id);
            jsonObject.put("user_id", sessonManager.getUserId());
            mSocket.emit("join_room", jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callConnectUser();
                    Toast.makeText(ChatActivity.this.getApplicationContext(),
                            "user Connected", Toast.LENGTH_SHORT).show();
                    if (!isConnected) {
                       /* Toast.makeText(ChatActivity.this.getApplicationContext(),
                                "user Connected", Toast.LENGTH_SHORT).show();*/
                        isConnected = true;
                    }
                }
            });
        }
    };
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "diconnected");
                    isConnected = false;
                  /*  Toast.makeText(ChatActivity.this.getApplicationContext(),
                            "diconnected", Toast.LENGTH_SHORT).show();*/
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("user Error connecting" + chat_id);
                    Log.e(TAG, "Error connecting");
                   /* Toast.makeText(ChatActivity.this.getApplicationContext(),
                            "Error connecting", Toast.LENGTH_SHORT).show();*/
                }
            });
        }
    };

    private void sendChatMessage(String mesgText, String type, boolean status) {
        try {
            Log.e("TAG", "currentUserID : $currentUserID");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("group_id", chat_id);
            jsonObject.put("chat_id", chat_id);
            jsonObject.put("user_id", sessonManager.getUserId());
            //  jsonObject.put("receiver_id", "27");
            jsonObject.put("type", type);
            if (type.equalsIgnoreCase("text")) {
                jsonObject.put("message", mesgText);
                jsonObject.put("file_path", "");
            } else {
                jsonObject.put("message", "Product Image");
                // jsonObject.put("msg_url", mesgText);
                jsonObject.put("file_path", mesgText);
            }
            Log.e("TAG", "jsonObject :" + jsonObject);
            mSocket.emit("chat_message", jsonObject);
            chatMessageList(chat_id);

            editText.getText().clear();
            llSend.setVisibility(View.GONE);
            recordButton.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG_Exception", e.getMessage());

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sessonManager = new SessonManager(this);

        //askForPermissioncamera(Manifest.permission.CAMERA, CAMERA);
        System.out.println("tokenOfUser" + sessonManager.getToken());
        checkPermissions1();
        // permissionToDrawOverlays();
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        chatRecyclerView.setHasFixedSize(false);
        chatRecyclerView.setNestedScrollingEnabled(false);
        System.out.println("getUserId" + sessonManager.getUserId());

        mFileName = ChatActivity.this.getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.mp3";


        recordPath = getExternalFilesDir("/").getAbsolutePath();

        //Get current date and time
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();

        //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
        recordFile = "Recording_" + formatter.format(now) + ".mp3";

        /*Todo:- Recording Library*/
        recordView = (RecordView) findViewById(R.id.record_view);
        recordButton = (RecordButton) findViewById(R.id.record_button);
        recordButton.setRecordView(recordView);
        recordButton.setListenForRecord(true);
        checkPermissions();
        //Cancel Bounds is when the Slide To Cancel text gets before the timer . default is 8
        recordView.setCancelBounds(8);
        recordView.setSmallMicColor(Color.parseColor("#c2185b"));
        //prevent recording under one Second
        recordView.setLessThanSecondAllowed(false);
        recordView.setSlideToCancelText("Slide To Cancel");
        recordView.setCustomSounds(0, R.raw.record_finished, 0);
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                recordView.setVisibility(View.VISIBLE);
                Log.d("RecordView", "onStart");
                startRecording();
            }

            @Override
            public void onCancel() {
                //Toast.makeText(ChatActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onCancel");
            }

            @Override
            public void onFinish(long recordTime) {
                recordView.setVisibility(View.GONE);
                String time = getHumanTimeText(recordTime);
                stopRecording();
                //Toast.makeText(ChatActivity.this, "onFinishRecord - Recorded Time is: " + time, Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onFinish");

                Log.d("RecordTime", time);
            }

            @Override
            public void onLessThanSecond() {
                // Toast.makeText(ChatActivity.this, "OnLessThanSecond", Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onLessThanSecond");
            }
        });

        //Socket Chat Programming
        SocketConnection();
        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                recordView.setVisibility(View.GONE);
                Log.d("RecordView", "Basket Animation Finished");
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String chat_status = getIntent().getStringExtra("chat_status");
            if (chat_status != null && chat_status.equalsIgnoreCase("1")) {
                Log.d("hello", chat_status);
                chat_id = Integer.parseInt(extras.getString("findingchatid"));
                //chatMessageList(chat_id);
            } else if (chat_status != null && chat_status.equalsIgnoreCase("0")) {
                // Log.d("Chsgss", String.valueOf(chat_id));
                chat_id = getIntent().getIntExtra("findingchatid", 0);
                //chatMessageList(chat_id);
            } else if (chat_status != null && chat_status.equalsIgnoreCase("2")) {

                // Log.d("Chsgss", String.valueOf(chat_id));
                chat_id = getIntent().getIntExtra("findingchatid", 0);
                //chatMessageList(chat_id);
            } else {

                String value = String.valueOf(getIntent().getExtras().get("chat_id"));
                chat_id = Integer.parseInt(value);
                //chatMessageList(chat_id);
                //Log.d(TAG, "Key: " + "abcd" + " Value: " + value);
            }
        }

        if (chatList == null) {
            chatMessageList(chat_id);
        } else if (chatList.size() > 0) {
            ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter(ChatActivity.this, chatList);
            chatRecyclerView.setAdapter(chatMessageAdapter);
            //chatMessageAdapter.setHasStableIds(true);
            chatRecyclerView.scrollToPosition(chatList.size() - 1);
            chatRecyclerView.smoothScrollToPosition(chatRecyclerView.getAdapter().getItemCount());
            //chatRecyclerView.getLayoutManager().scrollToPosition(chatList.size()-1);
            chatMessageAdapter.notifyDataSetChanged();
        }

        Log.d("ChatIdForTesting", "" + chat_id);
        /*Todo:- UserDP*/
        userDp = findViewById(R.id.userDp);
        userName = findViewById(R.id.userName);

        editText = findViewById(R.id.editText);
        llSend = findViewById(R.id.llSend);
        chooseImage = findViewById(R.id.chooseImage);

        /*Todo:- Cart Count View*/
        cart_badge = findViewById(R.id.cart_badge);

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                //startDialog();
            }
        });
        msgDtoList = new ArrayList<>();

        fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_speed_dial);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.fab_terminate:
                        new android.app.AlertDialog.Builder(ChatActivity.this)
                                .setTitle("Are you sure want to end your order?")
                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        terminate();
                                    }
                                })
                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(R.drawable.splash_transparent)
                                .show();
                        break;
                }
                return false;
            }
        });
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                chatMessageList(Integer.parseInt(String.valueOf(chat_id)));
            }
        };
        IntentFilter i = new IntentFilter();
        i.addAction("message_subject_intent");
        LocalBroadcastManager.getInstance(ChatActivity.this).registerReceiver(mMessageReceiver, i);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    llSend.setVisibility(View.GONE);
                    recordButton.setVisibility(View.VISIBLE);
                } else {
                    llSend.setVisibility(View.VISIBLE);
                    recordButton.setVisibility(View.GONE);
                    recordView.setVisibility(View.GONE);
                    llSend.setEnabled(true);
                    llSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String msgContent = editText.getText().toString();


                            if (!TextUtils.isEmpty(msgContent)) {
                                if (CommonUtils.isOnline(ChatActivity.this)) {
                                    sendChatMessage(msgContent, "text", false);
                                    Log.d("verfy===", String.valueOf(chat_id));
                                }
                            }


                            llSend.setEnabled(false);
                          /*  if (!TextUtils.isEmpty(msgContent)) {
                                Log.d("verfy===", String.valueOf(chat_id));
                                if (CommonUtils.isOnline(ChatActivity.this)) {
                                    //sessonManager.showProgress(ChatActivity.this);
                                    TextTypeRequest textTypeRequest = new TextTypeRequest();
                                    textTypeRequest.setType("text");
                                    textTypeRequest.setMessage(msgContent);
                                    sendChatMessage(msgContent,"text",false);

                                   Call<SendModel> call = ApiExecutor.getApiService(ChatActivity.this)
                                            .apiSend("Bearer " + sessonManager.getToken(), chat_id, textTypeRequest);
                                    call.enqueue(new Callback<SendModel>() {
                                        @Override
                                        public void onResponse(Call<SendModel> call, Response<SendModel> response) {
                                            llSend.setEnabled(true);
                                            //sessonManager.hideProgress();
                                            if (response.body() != null) {
                                                SendModel sendModel = response.body();
                                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                                    editText.getText().clear();
                                                    llSend.setVisibility(View.GONE);
                                                    recordButton.setVisibility(View.VISIBLE);
                                                    chatMessageList(chat_id);
                                                    //Toast.makeText(ChatActivity.this, ""+response.body().getStatus(), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(ChatActivity.this, "" + sendModel.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<SendModel> call, Throwable t) {
                                            //sessonManager.hideProgress();
                                        }
                                    });
                                } else {
                                    CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
                                }

                            }*/
                        }
                    });

                }
                //
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //startTimer();
    }


    private void startTimer() {
        countDownTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                //long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000);
                // Toast.makeText(ChatActivity.this, ""+sec, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFinish() {
                startTimer();
                //Toast.makeText(ChatActivity.this, "ff", Toast.LENGTH_SHORT).show();
                chatMessageList(chat_id);
            }
        }.start();
    }


    private void terminate() {
        Call<TerminateChatModel> call = ApiExecutor.getApiService(this)
                .apiChatTerminate("Bearer " + sessonManager.getToken(), chat_id);
        call.enqueue(new Callback<TerminateChatModel>() {
            @Override
            public void onResponse(Call<TerminateChatModel> call, Response<TerminateChatModel> response) {
                //   System.out.println("terminate_response"+chat_id+","+new Gson().toJson(response.body()));
                if (response.body() != null) {
                    if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                        Toast.makeText(ChatActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        chatMessageList(chat_id);
                    } else {
                        Toast.makeText(ChatActivity.this, "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TerminateChatModel> call, Throwable t) {
            }
        });
    }


    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }


    private void checkPermissions1() {
        ArrayList<String> deniedPermissions = new ArrayList<>();
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }

        if (deniedPermissions.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(deniedPermissions.toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
            } else {
                ToastUtils.showToast(this, "Permission denied.");
            }
        }
    }

    private void chatMessageList(int chat_id) {
        Log.d("chatiddssss", String.valueOf(chat_id));
        if (CommonUtils.isOnline(ChatActivity.this)) {
            //sessonManager.showProgress(ChatActivity.this);
            Call<ChatMessageModel> call = ApiExecutor.getApiService(this).apiChatMessage("Bearer " + sessonManager.getToken(), chat_id);
            call.enqueue(new Callback<ChatMessageModel>() {
                @Override
                public void onResponse(Call<ChatMessageModel> call, Response<ChatMessageModel> response) {
                    //sessonManager.hideProgress();
                    if (response.body() != null) {
                        ChatMessageModel chatMessageModel = response.body();
                        Log.d("chatResponse_count1_response", new Gson().toJson(response.body()));
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                            if (chatMessageModel.getData() != null) {
                                chatList = chatMessageModel.getData().getChats();
                                String cartCount = chatMessageModel.getData().getItems_count();
                                if (cartCount.equalsIgnoreCase("0")) {
                                    cart_badge.setVisibility(View.GONE);
                                } else {
                                    cart_badge.setVisibility(View.VISIBLE);
                                    cart_badge.setText(cartCount);
                                }
                                Log.d("chatResponse_count1", chatList.size() + "," + cartCount);

                                Picasso.get().load(chatMessageModel.getData().getShoppr().getImage()).into(userDp);
                                userName.setText(chatMessageModel.getData().getShoppr().getName());
                                shopperName = chatMessageModel.getData().getShoppr().getName();
                                shopperPic = chatMessageModel.getData().getShoppr().getImage();
                                if (chatList.size() == 0) {
                                    // cart_badge.setVisibility(View.GONE);

                                } else {
                                    //  cart_badge.setVisibility(View.VISIBLE);
                                    // cart_badge.setText(cartCount);
                                    ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter(ChatActivity.this, chatList);
                                    chatRecyclerView.setAdapter(chatMessageAdapter);
                                    chatRecyclerView.scrollToPosition(chatList.size() - 1);
                                    chatRecyclerView.smoothScrollToPosition(chatRecyclerView.getAdapter().getItemCount());
                                    chatMessageAdapter.notifyDataSetChanged();
                                }
                                //chatRecyclerView.getLayoutManager().scrollToPosition(chatList.size()-1);

                            }
                        } else {
                            Toast.makeText(ChatActivity.this, "" + chatMessageModel.getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().getStatus().equalsIgnoreCase("failed")) {
                                if (response.body().getMessage().equalsIgnoreCase("logout")) {
                                    AuthenticationUtils.deauthenticate(ChatActivity.this, isSuccess -> {
                                        if (getApplication() != null) {
                                            sessonManager.setToken("");
                                            PrefUtils.setAppId(ChatActivity.this, "");
                                            Toast.makeText(ChatActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                                            finishAffinity();

                                        } else {

                                        }
                                    });
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ChatMessageModel> call, Throwable t) {
                    //sessonManager.showProgress(ChatActivity.this);
                }
            });

        } else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    /*Todo:-Image Upload*/
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(ChatActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);


    }

    private void cameraIntent() {
       /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);*/

        if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ChatActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(ChatActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.CAMERA
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA);
        } else {
            captureImage();
        }


    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            fileUri = getOutputMediaFileUri(ChatActivity.this);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        } else {
            fileUri = getOutputMediaFileUri(ChatActivity.this);
            File file = new File(fileUri.getPath());
            Uri photoUri = FileProvider.getUriForFile(ChatActivity.this, getPackageName() + ".fileprovider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    //Get Uri Of captured Image
    public static Uri getOutputMediaFileUri(Context context) {
        File mediaStorageDir = new File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "profile");
        //If File is not present create directory
        if (!mediaStorageDir.exists()) {
            if (mediaStorageDir.mkdir())
                Log.e("Create Directory", "Main Directory Created : " + mediaStorageDir);
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());//Get Current timestamp
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");//create image path with system mill and image format
        return Uri.fromFile(mediaFile);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
            // onSelectFromGalleryResult(data);
            {
                //   onSelectFromGalleryResult(data);
                Bitmap bm = null;
                if (data != null) {
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                bm.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                byteArray = stream.toByteArray();
                destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(stream.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.e("TAG", "finalPathImageimagePath: " + e.getMessage() + "Heelo");

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("TAG", "finalPathImageimagePath: " + e.getMessage() + "Heelo");

                }
                Uri ImageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(ChatActivity.this.getContentResolver(), ImageUri);

                    String path = getRealPathFromURI(ChatActivity.this, getImageUri(ChatActivity.this, bitmap));
                    File imageFile = new File(path);
                    flag = true;
                    System.out.println("imageFile" + imageFile.getName());
                    imagePath=imageFile.getPath();
                    Log.e("TAG", "finalPathImageimagePath: " + imageFile + "Heelo");

                    Log.e("TAG", "finalPathImageimagePath: " + imageFile.getName());
                    Log.e("TAG", "finalPathImageimagePath: " + imageFile.getPath());
                    // updateGalleryImages();
                      updateImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // head_image.setImageBitmap(bitmap);


            } else if (requestCode == REQUEST_CAMERA)
                // onCaptureImageResult(data);
                try {
                    if (Build.VERSION.SDK_INT > 22)
                        imagePath = ImagePath_MarshMallow.getPath(ChatActivity.this, fileUri);
                    else {
                        imagePath = fileUri.getPath();
                    }
                    Log.e("TAG", "finalPathImage : " + imagePath);
                    updateImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            Log.e("TAG", "finalPathImageArray : " + finalPathImageArray.size() + imagePath);
        }

        if (requestCode == PERM_REQUEST_CODE_DRAW_OVERLAYS) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
                if (!Settings.canDrawOverlays(this)) {
                    // ADD UI FOR USER TO KNOW THAT UI for SYSTEM_ALERT_WINDOW permission was not granted earlier...
                } else {
                    Log.d("lakshmi", "granted");

                }
            }
        }
    }

    public static Uri getImageUri(Context inContext, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), bitmap, Calendar.getInstance().getTime() + "", null);
        return Uri.parse(path);
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        try {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = context.getContentResolver().query(contentUri,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            return cursor.getString(columnIndex);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        bm.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byteArray = stream.toByteArray();
        destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(stream.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProfileUpdateAPI();
    }

    private void updateImage() {
        final ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        //progressDialog.show();
        UploadProductImages task = new UploadProductImages(ChatActivity.this, new UploadProductImages.CallBackImageUpload() {
            @Override
            public void onImageUpload(String imageUrl) {
               try {
                   Log.e("TAG", "onImageUpload:  in after async task " + imageUrl);
                   uploadedImage = imageUrl;
                   uploadedType = "image";
                   if (isSocketConnected) {
                       sendChatMessage(imageUrl, "image", false);
                   } else {
                       Toast.makeText(getApplicationContext(), "Please wait connecting...", Toast.LENGTH_SHORT).show();
                       isShowConnection = true;
                       callConnectUser();
                   }
               }
               catch (Exception e)
               {

               }
                progressDialog.dismiss();
            }

            @Override
            public void onErrorOccured(String error) {
                progressDialog.dismiss();
                Toast.makeText(ChatActivity.this, error, Toast.LENGTH_SHORT).show();
            }

        });
        // System.out.println("imagePath_DDDDDDDDDDDDDDDDDDDDDD"+imagePath);
      /*  File file = new File(compressImage(imagePath, progressDialog));
        task.execute(file);*/

        File file = new File(imagePath);
        task.execute(file);

    }

    private void updateAudio() {
        final ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        // progressDialog.show();
        UploadProductAudio task = new UploadProductAudio(ChatActivity.this, new UploadProductAudio.CallBackImageUpload() {
            @Override
            public void onImageUpload(String audioUrl) {
                Log.e("TAG", "onImageUpload:  in after async task " + audioUrl);
                uploadedImage = audioUrl;
                uploadedType = "audio";
                if (isSocketConnected) {
                    sendChatMessage(audioUrl, "audio", false);
                } else {
                    Toast.makeText(getApplicationContext(), "Please wait connecting...", Toast.LENGTH_SHORT).show();
                    isShowConnection = true;
                    callConnectUser();
                }

                progressDialog.dismiss();
            }

            @Override
            public void onErrorOccured(String error) {
                progressDialog.dismiss();
                Toast.makeText(ChatActivity.this, error, Toast.LENGTH_SHORT).show();
            }

        });

        File file = new File(recordPath + "/" + recordFile);
        // File file = new File(recordFile);
        task.execute(file);

    }


    public String compressImage(String filePath, ProgressDialog progressDialog) {

        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
            Toast.makeText(getApplicationContext(), "exception 1", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
            Toast.makeText(getApplicationContext(), "exception 2", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        if (bmp != null) {
            bmp.recycle();
        } else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "exception 3", Toast.LENGTH_SHORT).show();
        }
//      check the rotation of the image and display it properly
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "exception 4", Toast.LENGTH_SHORT).show();
        }

        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0);
        Log.d("EXIF", "Exif: " + orientation);
        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
            Log.d("EXIF", "Exif: " + orientation);
        } else if (orientation == 3) {
            matrix.postRotate(180);
            Log.d("EXIF", "Exif: " + orientation);
        } else if (orientation == 8) {
            matrix.postRotate(270);
            Log.d("EXIF", "Exif: " + orientation);
        }
        scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                true);

        FileOutputStream out = null;
        String filename = getFilename();
        try {

            out = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

//          write the compressed bitmap at the destination specified by filename.
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        return filename;
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "profile");
        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
    }


    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        byteArray = bytes.toByteArray();
        destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

      /*  int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
        for(int i = 0; i < count; i++) {
            Uri selectedImage = data.getClipData().getItemAt(i).getUri();
            if (Build.VERSION.SDK_INT > 22)
                imagePath = ImagePath_MarshMallow.getPath(ChatActivity.this, selectedImage);
            else {
                imagePath = selectedImage.getPath();
            }
            finalPathImageArray.add(imagePath);

        }*/

        updateGalleryImages();
        finalPathImageArray.add(imagePath);

        ProfileUpdateAPI();
    }

    private void ProfileUpdateAPI() {
        if (CommonUtils.isOnline(ChatActivity.this)) {
            //sessonManager.showProgress(ChatActivity.this);
            HashMap<String, RequestBody> partMap = new HashMap<>();
            partMap.put("type", ApiFactory.getRequestBodyFromString("image"));
            RequestBody backBike = RequestBody
                    .create(MediaType.parse("image/*"), byteArray);
            MultipartBody.Part imageArray1 = MultipartBody.Part.createFormData("file", destination.getName(), backBike);
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + sessonManager.getToken());
            ApiService iApiServices = ApiFactory.createRetrofitInstance(baseUrl).create(ApiService.class);
            iApiServices.apiImageSend(headers, chat_id, imageArray1, partMap)
                    .enqueue(new Callback<SendModel>() {
                        @Override
                        public void onResponse(Call<SendModel> call, Response<SendModel> response) {
                            System.out.println("ressssssss" + new Gson().toJson(response.body()));
                            //sessonManager.hideProgress();
                            // Log.d("res",response.message());
                            if (response.body() != null) {
                                SendModel sendModel = response.body();
                                if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {
                                    chatMessageList(chat_id);
                                    // Toast.makeText(ChatActivity.this, "" + sendModel.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ChatActivity.this, sendModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SendModel> call, Throwable t) {
                            //sessonManager.hideProgress();
                            Log.e("TAG", "onFailure: on image upload ProfileUpdateAPI");
                        }
                    });
        } else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }


    public static class Utility {
        public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public static boolean checkPermission(final Context context) {
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("External storage permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();

                    } else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }

    /*Todo:- Audio Upload*/
    private void stopRecording() {
        //Stop Timer, very obvious
        //Change text on page to file saved
        //filenameText.setText("Recording Stopped, File Saved : " + recordFile);
        //Stop media recorder and set it to null for further use to record new audio
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        //uploadFile();

        updateAudio();


    }

    private void uploadFile() {
        if (CommonUtils.isOnline(ChatActivity.this)) {
            //sessonManager.showProgress(ChatActivity.this);
            File file = new File(pathforaudio);

            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            //RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
            System.out.println("fileToUpload_SSSSSSSSSSSSSSSSSSSSS" + fileToUpload);

            ApiService iApiServices = ApiFactory.createRetrofitInstance(baseUrl).create(ApiService.class);

            //Call call = getResponse.uploadFile(fileToUpload, filename);
            HashMap<String, RequestBody> partMap = new HashMap<>();
            partMap.put("type", ApiFactory.getRequestBodyFromString("audio"));
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + sessonManager.getToken());

            iApiServices.apiAudioSend(headers, chat_id, fileToUpload, partMap)
                    .enqueue(new Callback<SendModel>() {
                        @Override
                        public void onResponse(Call<SendModel> call, Response<SendModel> response) {
                            //sessonManager.hideProgress();
                            if (response.body() != null) {
                                SendModel sendModel = response.body();
                                if (response.body().getStatus() != null && response.body().getStatus().equalsIgnoreCase("success")) {
                                    chatMessageList(chat_id);
                                    Toast.makeText(ChatActivity.this, "" + sendModel.getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ChatActivity.this, "" + sendModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SendModel> call, Throwable t) {
                            //sessonManager.hideProgress();
                        }
                    });
        } else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }


    private void startRecording() {


        //filenameText.setText("Recording, File Name : " + recordFile);

        //Setup Media Recorder for recording
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        // mediaRecorder.setOutputFile(mFileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


        pathforaudio = recordPath + "/" + recordFile;

        Log.d("recordpath====", recordPath + "/" + recordFile);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Start Recording
        mediaRecorder.start();
    }

    private boolean checkPermissions() {
        //Check permission
        if (ActivityCompat.checkSelfPermission(this, recordPermission) == PackageManager.PERMISSION_GRANTED) {
            //Permission Granted
            return true;
        } else {
            //Permission not granted, ask for permission
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (flag) {
            stopRecording();
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        chatMessageList(chat_id);
        //Toast.makeText(this, "Restart", Toast.LENGTH_SHORT).show();
        //ChatActivity.this.finish();
    }


    public void back(View view) {
        onBackPressed();
    }

    public void help(View view) {
        String number = "+919315957968";
        String url = "https://api.whatsapp.com/send?phone=" + number;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
      /*  startActivity(new Intent(ChatActivity.this,HelpActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));*/
    }

    public void MyCart(View view) {
        startActivity(new Intent(ChatActivity.this, ViewCartActivity.class)
                .putExtra("valueId", "1")
                .putExtra("chat_id", chat_id)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void calling(View view) {
        /*bottomSheetDialog=new BottomSheetDialog(this,R.style.CustomBottomSheetDialog);
        bottomSheetDialog.setContentView(getLayoutInflater().inflate(R.layout.calling_bottom_dialog,null));
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show()*/
        ;

        /*boolean isChatProgress = MyPreferences.getBoolean(ChatActivity.this, ConstantValue.KEY_IS_CHAT_PROGRESS);
        int chatId = MyPreferences.getInt(ChatActivity.this, ConstantValue.KEY_CHAT_ID);
        if (isChatProgress&&(chat_id==chatId))
        initializationVoice(view);
        else {
            Toast.makeText(this, "Your order is terminated or completed, so can't make call. ", Toast.LENGTH_SHORT).show();
        }*/

        initializationVoice(view);
    }

    public void initializationVoice(View view) {
        initializationVoice(chat_id);
    }

    public void initializationVideo(View view) {
        initializationVideo(chat_id);
    }

    private void initializationVideo(int chat_id) {
        if (CommonUtils.isOnline(this)) {
            Call<InitiateVideoCallModel> call = ApiExecutor.getApiService(this)
                    .apiInitiateVideoCall("Bearer " + sessonManager.getToken(), chat_id);
            call.enqueue(new Callback<InitiateVideoCallModel>() {
                @Override
                public void onResponse(Call<InitiateVideoCallModel> call, Response<InitiateVideoCallModel> response) {
                    if (response.body() != null) {
                        InitiateVideoCallModel initiateVideoCallModel = response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                            if (initiateVideoCallModel.getData() != null) {
                                String savedUserId = initiateVideoCallModel.getData().getUser_id();
                                //PrefUtils.setCalleeId(ChatActivity.this, savedUserId);
                                //CallService.dial(ChatActivity.this, savedUserId, true);
                                ActivityUtils.startCallActivityAsCaller(ChatActivity.this,
                                        savedUserId, shopperName, shopperPic, true);
                                PrefUtils.setCalleeId(ChatActivity.this, savedUserId);
                                //bottomSheetDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(ChatActivity.this, "" + initiateVideoCallModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<InitiateVideoCallModel> call, Throwable t) {

                }
            });
        } else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }

    private void initializationVoice(int chat_id) {
        if (CommonUtils.isOnline(this)) {
            Call<InitiateVideoCallModel> call = ApiExecutor.getApiService(this)
                    .apiInitiateVideoCall("Bearer " + sessonManager.getToken(), chat_id);
            call.enqueue(new Callback<InitiateVideoCallModel>() {
                @Override
                public void onResponse(Call<InitiateVideoCallModel> call, Response<InitiateVideoCallModel> response) {
                    if (response.body() != null) {
                        InitiateVideoCallModel initiateVideoCallModel = response.body();
                        if (response.body().getStatus() != null && response.body().getStatus().equals("success")) {

                            if (initiateVideoCallModel.getData() != null) {
                                String savedUserId = initiateVideoCallModel.getData().getUser_id();
                                // PrefUtils.setCalleeId(ChatActivity.this, savedUserId);

                                ActivityUtils.startCallActivityAsCaller(ChatActivity.this,
                                        savedUserId, shopperName, shopperPic, false);
                                PrefUtils.setCalleeId(ChatActivity.this, savedUserId);
                                // CallService.dial(ChatActivity.this, savedUserId, false);
                                // SendBirdCall.Options.

                                //bottomSheetDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(ChatActivity.this, "" + initiateVideoCallModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<InitiateVideoCallModel> call, Throwable t) {

                }
            });
        } else {
            CommonUtils.showToastInCenter(ChatActivity.this, getString(R.string.please_check_network));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            boolean allowed = true;

            for (int result : grantResults) {
                allowed = allowed && (result == PackageManager.PERMISSION_GRANTED);
            }

            if (!allowed) {
                ToastUtils.showToast(this, "Permission denied.");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatMessageList(chat_id);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
    }

    public void yourDesiredMethod() {
        chatMessageList(chat_id);
    }

    public void hideTerminateButton() {
        fabSpeedDial.setVisibility(View.GONE);
    }

    public void permissionToDrawOverlays() {
        int version = Build.VERSION.SDK_INT;
        System.out.println("versionversionversion" + version);
        if (android.os.Build.VERSION.SDK_INT >= 23) {   //Android M Or Over
            if (!Settings.canDrawOverlays(this)) {
                if (version >= 30) {
                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                    System.out.println("versionversionversion30");
                } else {
                    System.out.println("versionversionversion29");
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, PERM_REQUEST_CODE_DRAW_OVERLAYS);
                }
            }
        }

        final int currentTime = (int) System.currentTimeMillis();
        final String channelId = getPackageName() + currentTime;

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = getString(R.string.app_name);
            NotificationChannel channel = new NotificationChannel(channelId, channelName,
                    serviceData.isHeadsUpNotification ? NotificationManager.IMPORTANCE_HIGH : NotificationManager.IMPORTANCE_LOW);

            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = getString(R.string.app_name);

            String description = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }




       /* Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);*/
    }


}

package com.lunger.smartchat;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.lunger.smartchat.adapter.ChatAdapter;
import com.lunger.smartchat.model.ChatModel;
import com.lunger.smartchat.model.ReplyModel;
import com.lunger.smartchat.util.SpeechRecognizer;
import com.lunger.smartchat.util.SystemBarTintManager;
import com.turing.androidsdk.InitListener;
import com.turing.androidsdk.SDKInit;
import com.turing.androidsdk.SDKInitBuilder;
import com.turing.androidsdk.TuringApiManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import turing.os.http.core.ErrorMessage;
import turing.os.http.core.HttpConnectionListener;
import turing.os.http.core.RequestResult;


/**
 * Created by Lunger on 2016/11/29.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "wbl";
    private EditText et_input;
    private Button btn_send;
    private ListView lv_chat;
    private List<ChatModel> mMessages = new ArrayList<>();
    private TuringApiManager mTuringApiManager;
    private ChatAdapter mChatAdapter;
    private boolean isLock = false;
    private SpeechRecognizer mSpeechRecognizer;
    /**
     * 申请的turing的api key
     **/
    private final String TURING_APIKEY = "f2c5861a955b4ec1952367c88f98ccb6";
    /**
     * 申请的secret
     **/
    private final String TURING_SECRET = "96292ab3bf0d7ad2";
    private final String UNIQUE_ID = "124122541325";
    private SpeechSynthesizer mTts;
    private RadioGroup rg_language;
    private ImageView iv_voice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置系统通知栏颜色
        changeSystemBarColor();
        setContentView(R.layout.activity_main);
        findViews();
        initTuring();
        initVoiceEngine();
        mSpeechRecognizer = new SpeechRecognizer(this);
        initListener();
        mChatAdapter = new ChatAdapter(this, mMessages);
        lv_chat.setAdapter(mChatAdapter);
    }

    private void changeSystemBarColor() {
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);//通知栏所需颜色
    }

    private void initVoiceEngine() {
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        mTts = SpeechSynthesizer.createSynthesizer(this, null);
        //2.合成参数设置，详见 科大讯飞MSC API手册(Android) SpeechSynthesizer 类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoxin");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "60");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "100");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        //保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
        //如果不需要保存合成音频，注释该行代码
        //mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
    }


    private void initTuring() {
        // turingSDK初始化
        SDKInitBuilder builder = new SDKInitBuilder(this)
                .setSecret(TURING_SECRET).setTuringKey(TURING_APIKEY).setUniqueId(UNIQUE_ID);
        SDKInit.init(builder, new InitListener() {
            @Override
            public void onFail(String error) {
                Log.d(TAG, error);
            }

            @Override
            public void onComplete() {
                // 获取userid成功后，才可以请求Turing服务器，需要请求必须在此回调成功，才可正确请求
                mTuringApiManager = new TuringApiManager(MainActivity.this);
                mTuringApiManager.setHttpListener(new HttpConnectionListener() {
                    /**
                     * 网络请求回调
                     */
                    @Override
                    public void onSuccess(RequestResult result) {
                        if (result != null) {
                            try {
                                Log.d(TAG, result.getContent().toString());
                                JSONObject result_obj = new JSONObject(result.getContent()
                                        .toString());
                                if (result_obj.has("text")) {
                                    onTuringReply(new Gson().fromJson(result_obj.toString(), ReplyModel.class));
                                }
                            } catch (JSONException e) {
                                Log.d(TAG, "JSONException:" + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onError(ErrorMessage errorMessage) {
                        Log.d(TAG, errorMessage.getMessage());
                    }
                });
            }
        });
    }

    private void onTuringReply(ReplyModel reply) throws JSONException {
        //tv_output.setText(reply.getText());
        speaking(reply.getText());
    }

    private void speaking(final String reply) {
        //开始合成
        Log.d(TAG, "开始合成 ：" + reply);
        mTts.startSpeaking(reply, new SynthesizerListener() {
            //会话结束回调接口，没有错误时，error为null
            public void onCompleted(SpeechError error) {
                Log.d("wbl", "onCompleted...");
                isLock = false;
            }

            //缓冲进度回调
            //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
            public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
                // Log.d("wbl", "onBufferProgress...");
            }

            //开始播放
            public void onSpeakBegin() {
                Log.d("wbl", "onSpeakBegin...");
                btn_send.setEnabled(false);
                isLock = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMessages.add(new ChatModel(null, reply, true));
                        mChatAdapter.notifyDataSetChanged();
                        lv_chat.smoothScrollToPosition(mMessages.size() -1);
                    }
                }, 500);
            }

            //暂停播放
            public void onSpeakPaused() {

            }

            //播放进度回调
            //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
            public void onSpeakProgress(int percent, int beginPos, int endPos) {

            }

            //恢复播放回调接口
            public void onSpeakResumed() {
            }

            //会话事件回调接口
            public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
            }
        });
    }

    private void findViews() {
        et_input = (EditText) findViewById(R.id.et_input);
        btn_send = (Button) findViewById(R.id.btn_send);
        rg_language = (RadioGroup) findViewById(R.id.rg_language);
        lv_chat = (ListView) findViewById(R.id.lv_chat);
        iv_voice = (ImageView) findViewById(R.id.iv_voice);
    }

    private void initListener() {
        btn_send.setOnClickListener(this);
        rg_language.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                String tmp = "xiaoxin";
                switch (id) {
                    case R.id.rb_xiaoxin:
                        tmp = "xiaoxin";
                        break;
                    case R.id.rb_yueyu:
                        tmp = "xiaomei";
                        break;
                    case R.id.rb_sichuan:
                        tmp = "aisxrong";
                        break;

                }
                mTts.setParameter(SpeechConstant.VOICE_NAME, tmp);//设置发音人
            }
        });
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0 && isLock == false) {
                    btn_send.setEnabled(true);
                } else {
                    btn_send.setEnabled(false);
                    mChatAdapter.notifyDataSetChanged();
                }
            }
        });

        mSpeechRecognizer.setOnRecognizeListener(new SpeechRecognizer.OnRecognizeListener() {
            @Override
            public void onRecognizeSuccess(String msg) {
                if (!TextUtils.isEmpty(msg)) {
                    mMessages.add(new ChatModel(msg, null, false));
                    mTuringApiManager.requestTuringAPI(msg);
                    mChatAdapter.notifyDataSetChanged();
                }
            }
        });

        iv_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpeechRecognizer.startSpeechRecognize();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                String input = et_input.getText().toString().trim();
                mTuringApiManager.requestTuringAPI(input);
                et_input.setText("");
                mMessages.add(new ChatModel(input, null, false));
                mChatAdapter.notifyDataSetChanged();
                lv_chat.smoothScrollToPosition(mMessages.size() -1);
                break;
        }
    }

}

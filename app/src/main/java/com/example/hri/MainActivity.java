package com.example.hri;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.ListenBuilder;
import com.aldebaran.qi.sdk.builder.LookAtBuilder;
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.builder.TransformBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.actuation.Actuation;
import com.aldebaran.qi.sdk.object.actuation.Animate;
import com.aldebaran.qi.sdk.object.actuation.Animation;
import com.aldebaran.qi.sdk.object.actuation.Frame;
import com.aldebaran.qi.sdk.object.actuation.FreeFrame;
import com.aldebaran.qi.sdk.object.actuation.LookAt;
import com.aldebaran.qi.sdk.object.actuation.LookAtMovementPolicy;
import com.aldebaran.qi.sdk.object.actuation.Mapping;
import com.aldebaran.qi.sdk.object.conversation.AutonomousReactionImportance;
import com.aldebaran.qi.sdk.object.conversation.AutonomousReactionValidity;
import com.aldebaran.qi.sdk.object.conversation.BaseQiChatExecutor;
import com.aldebaran.qi.sdk.object.conversation.Bookmark;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.QiChatExecutor;
import com.aldebaran.qi.sdk.object.conversation.QiChatVariable;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.aldebaran.qi.sdk.object.geometry.Transform;
import com.aldebaran.qi.sdk.object.geometry.Vector3;
import com.aldebaran.qi.sdk.util.PhraseSetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {
    private static final String TAG = "MyActivity";
    private static final String TAG_EVENTS = "VarInfo";
    private Chat chat;

    //private QiChatVariable faceRecognitionVar, StopVar;
    private QiChatVariable variable_face;

    // Store the LookAt action.
    //private LookAt lookAt1;
    //private LookAt lookAt2;

    // Store the action execution future.
    //private Future<Void> lookAtFuture;
    //private Future<Void> animationFuture;
    //private QiChatbot qiChatbot0;
    //private QiChatbot qiChatbot1;
    private Future<Void> chatFuture;
    private QiChatbot qiChatbot;

    private QiChatVariable var_stop1, var_stop2,var_stop3,var_stop4,ChatBotDirection;
    public static int requestCode;
    private int ChatBotSelector = 0;//se a zero, andiamo in updatelistobj1. se a
    private boolean RepeatSay = true;

    //private String recognized_or_not = "0"; //1 riconosciuto, 0 no

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register the RobotLifecycleCallbacks to this Activity.

        QiSDK.register(this, this);
        //openAppOnTablet();
    }

    @Override
    protected void onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        super.onDestroy();
        QiSDK.unregister(this, this);
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {

        printString("Starting onRobotFocusGained()");

        // Create the builder with the context. Set the text to say. Build the say action.
        Say say = SayBuilder.with(qiContext)
                .withText("Hey, you got my attention. tell me \"Hi Pepper\" to start the conversation!")
                .build();

        // Execute the action.

        Topic topicIntro = TopicBuilder.with(qiContext) // Create the builder using the QiContext.
                .withResource(R.raw.intro) // Set the topic resource.
                .build(); // Build the topic.

        Topic topicUpdate = TopicBuilder.with(qiContext) // Create the builder using the QiContext.
                .withResource(R.raw.updatelistobj) // Set the topic resource.
                .build(); // Build the topic.

        Topic topicFaceRecogn = TopicBuilder.with(qiContext) // Create the builder using the QiContext.
                .withResource(R.raw.facerecognition) // Set the topic resource.
                .build(); // Build the topic.

        Topic topicBuildList = TopicBuilder.with(qiContext) // Create the builder using the QiContext.
                .withResource(R.raw.buildlistobj) // Set the topic resource.
                .build(); // Build the topic.

        Topic topicInitialize = TopicBuilder.with(qiContext) // Create the builder using the QiContext.
                .withResource(R.raw.initializebuildlistobj) // Set the topic resource.
                .build(); // Build the topic.


        qiChatbot = QiChatbotBuilder.with(qiContext)
                .withTopic(topicIntro, topicUpdate, topicFaceRecogn, topicInitialize, topicBuildList)
                .build();

        ChatBotDirection = qiChatbot.variable("ChatBotDirection");

        if(RepeatSay){ChatBotDirection.async().setValue("0");}

        //VAR face recognition, anything but 0 if recognized, 0 if not
        variable_face = qiChatbot.variable("faceRecognitionVar");

        //VAR group event
        var_stop1 = qiChatbot.variable("StopVar1");

        //VAR i see you are talking with someone else
        var_stop2 = qiChatbot.variable("StopVar2");

        //VAR pepper is unable to follow the user
        var_stop3 = qiChatbot.variable("StopVar3");

        //VAR pepper path
        var_stop4 = qiChatbot.variable("StopVar4");

        //set the events var. "det" if fixed, "rand" if random
        set_variables("det"); //var_stop1, var_stop2, var_stop3, var_stop4, variable_face);

        //print ALL the vars
        print_variables(); //var_stop1, var_stop2, var_stop3, var_stop4, variable_face, ChatBotDirection);


        // Map the executor name from the topic to our qiChatExecutor
        Map<String, QiChatExecutor> executors = new HashMap<>();

        executors.put("FaceRecognitionExecutor", new MyQiChatExecutor(qiContext, 0));
        executors.put("WavingExecutor", new MyQiChatExecutor(qiContext, 1));
        executors.put("TakePicExecutor", new MyQiChatExecutor(qiContext, 2));
        executors.put("WalkExecutor", new MyQiChatExecutor(qiContext, 3));
        executors.put("ScanExecutor", new MyQiChatExecutor(qiContext, 4));
        executors.put("ShowTabletExecutor", new MyQiChatExecutor(qiContext, 5));
        executors.put("GoodbyeExecutor", new MyQiChatExecutor(qiContext, 6));
        executors.put("TabletExecutor", new MyQiChatExecutor(qiContext, 7));
        executors.put("TabletKillerExecutor", new MyQiChatExecutor(qiContext, 8));


        // Set the executors to the qiChatbot
        qiChatbot.setExecutors(executors);

        if(!RepeatSay){
            Log.i("OSS","RepeatSay is false, changing ChatBotDirection value");
            if(variable_face.getValue().equals("0")){
                ChatBotDirection.setValue("2");
            }
            else if(variable_face.getValue().equals("1")){
                ChatBotDirection.setValue("1");
            }
        }

        if (RepeatSay) {
            say.run();
            RepeatSay=false;
        }


        chat = ChatBuilder.with(qiContext)
                .withChatbot(qiChatbot)
                .build();

        chat.addOnStartedListener(() -> Log.i(TAG, "Discussion started."));
        if (chat != null) {chat.removeAllOnStartedListeners();}


        chatFuture = chat.async().run();
        chatFuture.thenConsume(
                future -> {if (future.hasError()) {
                    Log.e(TAG, "Discussion finished with error.", future.getError());}
                });

    }

    public void set_variables(String mode){

        //}, QiChatVariable var_stop1, QiChatVariable var_stop2, QiChatVariable var_stop3,
        //QiChatVariable var_stop4, QiChatVariable variable_face) {

        if(mode.equals("det"))
        {
            var_stop1.async().setValue("0"); //0 means no group event in the beginning
            var_stop2.async().setValue("4");
            var_stop3.async().setValue("1");
            var_stop4.async().setValue("1");
            variable_face.async().setValue("1");
        }
        else if(mode.equals("rand"))
        {
            //VAR face recognition, anything but 0 if recognized, 0 if not
            if (Math.random() > 0.5) {
                variable_face.async().setValue("0");
            } else {
                variable_face.async().setValue("1");
            }

            if (Math.random() < 0.9) {
                var_stop1.async().setValue("0"); //NO group
            } else {
                var_stop1.async().setValue("1");
            }

            double rand = Math.random();
            var_stop2.async().setValue("1"); //0 is for the default case
            if (rand >= 0.25 && rand < 0.5) {
                var_stop2.async().setValue("2");
            }
            if (rand >= 0.5 && rand < 0.75) {
                var_stop2.async().setValue("3");
            }
            if (rand >= 0.75) {
                var_stop2.async().setValue("4");
            }

            if (Math.random() > 0.5) {
                var_stop3.async().setValue("0");  //no ev
            } else {
                var_stop3.async().setValue("1");
            }

            if (Math.random() > 0.5) {
                var_stop4.async().setValue("0"); //no ev
            } else {
                var_stop4.async().setValue("1");
            }
        }
    }

    public void print_variables()//QiChatVariable var_stop1, QiChatVariable var_stop2,
                                //QiChatVariable var_stop3, QiChatVariable var_stop4,
                                //QiChatVariable variable_face, QiChatVariable ChatBotDirection)
    {
        Log.i(TAG_EVENTS, "var_stop1: " + var_stop1.getValue());
        Log.i(TAG_EVENTS, "var_stop2: " + var_stop2.getValue());
        Log.i(TAG_EVENTS, "var_stop3: " + var_stop3.getValue());
        Log.i(TAG_EVENTS, "var_stop4: " + var_stop4.getValue());
        Log.i(TAG_EVENTS, "var_facerec: " + variable_face.getValue());
        Log.i(TAG_EVENTS, "ChatBotDirection: " + ChatBotDirection.getValue());
    }

    @Override
    public void onRobotFocusLost() {
        // The robot focus is lost.
        // Remove on started listeners from the LookAt action.

    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // The robot focus is refused.
    }



    class MyQiChatExecutor extends BaseQiChatExecutor {
        private final QiContext qiContext;
        private int animationSelector;
        //private String TAG;

        MyQiChatExecutor(QiContext context, int animationSelector) {
            super(context);
            this.qiContext = context;
            this.animationSelector = animationSelector;
        }

        @Override
        public void runWith(List<String> params) {
           boolean test = true;

            try {
                AnimationExecutor(qiContext, animationSelector);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void stop() {
            // This is called when chat is canceled or stopped
            Log.i(TAG, "QiChatExecutor stopped");
        }



    }


    private void AnimationExecutor(QiContext qiContext, int animationSelector) throws InterruptedException {

        if(animationSelector==0) {
            Animation myAnimation = AnimationBuilder.with(qiContext)
                    .withResources(R.raw.curious_a001)
                    .build();

            // Build the action.
            Animate animate = AnimateBuilder.with(qiContext)
                    .withAnimation(myAnimation)
                    .build();

            animate.run();

        }
        else if(animationSelector==1){
            Animation myAnimation = AnimationBuilder.with(qiContext)
                    .withResources(R.raw.hello_a010)
                    .build();

            // Build the action.
            Animate animate = AnimateBuilder.with(qiContext)
                    .withAnimation(myAnimation)
                    .build();

            animate.run();

        }
        else if(animationSelector==2){
            Animation myAnimation = AnimationBuilder.with(qiContext)
                    .withResources(R.raw.take_pic_b002)
                    .build();

            // Build the action.
            Animate animate = AnimateBuilder.with(qiContext)
                    .withAnimation(myAnimation)
                    .build();

            animate.run();
        }

        else if(animationSelector==3){
            Animation myAnimation = AnimationBuilder.with(qiContext)
                    .withResources(R.raw.walk_run_b001)
                    .build();

            // Build the action.
            Animate animate = AnimateBuilder.with(qiContext)
                    .withAnimation(myAnimation)
                    .build();

            animate.run();
        }

        else if(animationSelector==4){
            Animation myAnimation = AnimationBuilder.with(qiContext)
                    .withResources(R.raw.cautious_a001)
                    .build();

            // Build the action.
            Animate animate = AnimateBuilder.with(qiContext)
                    .withAnimation(myAnimation)
                    .build();

            animate.run();
        }

        else if(animationSelector==5){
            Animation myAnimation = AnimationBuilder.with(qiContext)
                    .withResources(R.raw.show_tablet_a004)
                    .build();

            // Build the action.
            Animate animate = AnimateBuilder.with(qiContext)
                    .withAnimation(myAnimation)
                    .build();

            animate.run();
        }

        else if(animationSelector==6){
            Animation myAnimation = AnimationBuilder.with(qiContext)
                    .withResources(R.raw.hello_a010)
                    .build();

            // Build the action.
            Animate animate = AnimateBuilder.with(qiContext)
                    .withAnimation(myAnimation)
                    .build();

            animate.run();
        }

        else if(animationSelector==7){
            requestCode = openAppOnTablet();
        }

        else if(animationSelector==8){
        }

    }


        public int openAppOnTablet() {
            Intent intent = new Intent(this, ListOfObj.class);
            intent.putExtra("requestCode", 1);
            int requestCode = intent.getExtras().getInt("requestCode");
            startActivity(intent);

            return requestCode;
        }


        public static void printValue(int value) {
            Log.i("", String.valueOf(value));
        }

        public static void printString(String text) {
            Log.i("", text);
        }

    }





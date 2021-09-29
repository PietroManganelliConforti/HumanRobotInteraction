package com.example.hri;


import android.os.Bundle;
import android.util.Log;

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
    private static final String TAG_EVENTS = "EventsInfo";
    private Chat chat;
    //private QiChatVariable faceRecognitionVar, StopVar;
    private QiChatVariable variable_face,var_stop1;
    // Store the LookAt action.
    private LookAt lookAt1;
    private LookAt lookAt2;
    // Store the action execution future.
    private Future<Void> lookAtFuture;
    private Future<Void> animationFuture;
    private Future<Void> chatFuture;
    private QiChatbot qiChatbot;
    private Map<String, Bookmark> bookmarksIntro;
    private Map<String, Bookmark> bookmarksFaceRecogn;
    private Map<String, Bookmark> bookmarksBuildList;
    private Map<String, Bookmark> bookmarksInitialize;
    private QiChatVariable var_stop2,var_stop3,var_stop4;

    //private String recognized_or_not = "0"; //1 riconosciuto, 0 no

    //Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register the RobotLifecycleCallbacks to this Activity.

        QiSDK.register(this, this);
    }

    @Override
    protected void onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        super.onDestroy();
        QiSDK.unregister(this, this);
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {


        Say say = SayBuilder.with(qiContext) // Create the builder with the context.
                .withText("Hey, you got my attention. tell me \"Hi Pepper\" to start the conversation!") // Set the text to say.
                .build(); // Build the say action.

        // Execute the action.

        Topic topicIntro = TopicBuilder.with(qiContext) // Create the builder using the QiContext.
                .withResource(R.raw.intro) // Set the topic resource.
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

        bookmarksIntro = topicIntro.getBookmarks();
        bookmarksFaceRecogn = topicFaceRecogn.getBookmarks();
        bookmarksBuildList = topicBuildList.getBookmarks();
        bookmarksInitialize = topicInitialize.getBookmarks();



        qiChatbot = QiChatbotBuilder.with(qiContext)
                .withTopic(topicIntro, topicFaceRecogn, topicInitialize, topicBuildList)
                .build();

        //VAR face recognition, anything but 0 if recognized, 0 if not
        variable_face = qiChatbot.variable("faceRecognitionVar");
        if(Math.random()>0.5) {
            variable_face.async().setValue("0");
        }else{
            variable_face.async().setValue("1");
        }

        //VAR group event
        var_stop1 = qiChatbot.variable("StopVar1");
        if(Math.random()<0.9) {
            var_stop1.async().setValue("0"); //NO group
        }else{
            var_stop1.async().setValue("1");
        }

        //VAR i see you are talking with someone else
        var_stop2 = qiChatbot.variable("StopVar2");
        double rand = Math.random();
        var_stop2.async().setValue("1"); //0 is for the default case
        if(rand >= 0.25 && rand < 0.5) {var_stop2.async().setValue("2");}
        if(rand >= 0.5 && rand < 0.75) {var_stop2.async().setValue("3");}
        if(rand >= 0.75) {var_stop2.async().setValue("4");}

        //VAR pepper is unable to follow the user
        var_stop3 = qiChatbot.variable("StopVar3");
        if(Math.random()>0.5) {
            var_stop3.async().setValue("0");  //no ev
        }else{
            var_stop3.async().setValue("1");
        }

        //VAR pepper path
        var_stop4 = qiChatbot.variable("StopVar4");
        if(Math.random()>0.5) {
            var_stop4.async().setValue("0"); //no ev
        }else{
            var_stop4.async().setValue("1");
        }
        //Log.i(String.valueOf(Math.random()), "Discussion started.");


        all_events(var_stop1,var_stop2,var_stop3,var_stop4,variable_face);

        Map<String, QiChatExecutor> executors = new HashMap<>();

        // Map the executor name from the topic to our qiChatExecutor
        executors.put("FaceRecognitionExecutor", new MyQiChatExecutor(qiContext, 0));
        executors.put("WavingExecutor", new MyQiChatExecutor(qiContext, 1));
        executors.put("TakePicExecutor", new MyQiChatExecutor(qiContext, 2));
        executors.put("WalkExecutor", new MyQiChatExecutor(qiContext, 3));
        executors.put("ScanExecutor", new MyQiChatExecutor(qiContext, 4));
        executors.put("ShowTabletExecutor", new MyQiChatExecutor(qiContext, 5));

        //executors.put("animationStopper", new MyQiChatExecutorStopper(qiContext, 5, lookAtFuture));

        // Set the executors to the qiChatbot
        qiChatbot.setExecutors(executors);

        say.run();

        Log.i( TAG_EVENTS, "var_stop1: "+ var_stop1.getValue());
        Log.i( TAG_EVENTS, "var_stop2: "+ var_stop2.getValue());
        Log.i( TAG_EVENTS, "var_stop3: "+ var_stop3.getValue());
        Log.i( TAG_EVENTS, "var_stop4: "+ var_stop4.getValue());
        Log.i( TAG_EVENTS, "var_facerec: "+ variable_face.getValue());

        chat = ChatBuilder.with(qiContext)
                .withChatbot(qiChatbot)
                .build();

        chat.addOnStartedListener(() -> Log.i(TAG, "Discussion started."));
        if (chat != null) {
            chat.removeAllOnStartedListeners();
        }
        //Future<Void> chatFuture = chat.async().run();

        chatFuture = chat.async().run();
        chatFuture.thenConsume(future -> {
            if (future.hasError()) {
                Log.e(TAG, "Discussion finished with error.", future.getError());
            }
        });



    }

    public void all_events(QiChatVariable e1,QiChatVariable e2,
                           QiChatVariable e3,QiChatVariable e4,
                           QiChatVariable eFace){
        e1.async().setValue("1");
        e2.async().setValue("4");
        e3.async().setValue("1");
        e4.async().setValue("1");
        eFace.async().setValue("0");
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

    //private Future<Void> FaceRecognitionAnimation(QiContext qiContext) throws InterruptedException {
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
    }



    public static void Print(boolean condition)
    {
        System.out.println(condition);
    }
};



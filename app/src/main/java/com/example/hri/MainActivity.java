package com.example.hri;


import android.os.Bundle;
import android.util.Log;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ChatBuilder;
import com.aldebaran.qi.sdk.builder.ListenBuilder;
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder;
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.builder.TopicBuilder;
import com.aldebaran.qi.sdk.design.activity.RobotActivity;
import com.aldebaran.qi.sdk.object.conversation.Chat;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.QiChatVariable;
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.aldebaran.qi.sdk.util.PhraseSetUtil;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {
    private static final String TAG = "MyActivity";
    private static final String TAG_EVENTS = "EventsInfo";
    private Chat chat;
    //private QiChatVariable faceRecognitionVar, StopVar;
    private QiChatVariable variable_face,var_stop1,var_stop2,var_stop3,var_stop4;

    //private String recognized_or_not = "0"; //1 riconosciuto, 0 no




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



        QiChatbot qiChatbot = QiChatbotBuilder.with(qiContext)
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
        Future<Void> chatFuture = chat.async().run();
        
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
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // The robot focus is refused.
    }

}
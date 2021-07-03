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
    private Chat chat;
    private QiChatVariable faceRecognition;
    private String recognized_or_not = "0"; //1 riconosciuto, 0 no
    private void assignVariable(String value) {
        // Set the value.
        faceRecognition.async().setValue(value);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register the RobotLifecycleCallbacks to this Activity.
        QiSDK.register(this, this);
        //(assignVariable(recognized_or_not);


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



        say.run();


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

    @Override
    public void onRobotFocusLost() {
        // The robot focus is lost.
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // The robot focus is refused.
    }
}
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
import com.aldebaran.qi.sdk.object.conversation.QiChatbot;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.object.conversation.Topic;
import com.aldebaran.qi.sdk.util.PhraseSetUtil;


public class MainActivity extends RobotActivity implements RobotLifecycleCallbacks {
    private static final String TAG = "MyActivity";
    private Chat chat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register the RobotLifecycleCallbacks to this Activity.
        QiSDK.register(this, this);

    }

    @Override
    protected void onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        // The robot focus is gained.
        //Say say = SayBuilder.with(qiContext) // Create the builder with the context.
        //        .withText("Hello human!!") // Set the text to say.
        //        .build(); // Build the say action.
        //// Execute the action.
        //say.run();
        PhraseSet phraseSet = PhraseSetBuilder.with(qiContext)
                .withTexts("Hello", "Hi")
                .build();
        Topic topic = TopicBuilder.with(qiContext) // Create the builder using the QiContext.
                .withResource(R.raw.hello) // Set the topic resource.
                .build(); // Build the topic.
        QiChatbot qiChatbot = QiChatbotBuilder.with(qiContext)
                .withTopic(topic)
                .build();

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
        // Create the PhraseSet 1.
        //PhraseSet phraseSetYes = PhraseSetBuilder.with(qiContext) // Create the builder using the QiContext.
        //        .withTexts("yes", "OK", "alright", "let's do this") // Add the phrases Pepper will listen to.
        //        .build(); // Build the PhraseSet.

        // Create the PhraseSet 2.
        //PhraseSet phraseSetNo = PhraseSetBuilder.with(qiContext) // Create the builder using the QiContext.
        //        .withTexts("no", "Sorry", "I can't") // Add the phrases Pepper will listen to.
        //        .build(); // Build the PhraseSet.

        //Listen listen = ListenBuilder.with(qiContext)
        //        .withPhraseSets(phraseSetYes, phraseSetNo)
        //        .build();
        //ListenResult listenResult = listen.run();

        //Log.i(TAG, "Heard phrase: " + listenResult.getHeardPhrase().getText()); // Prints "Heard phrase: forwards".
        //PhraseSet matchedPhraseSet = listenResult.getMatchedPhraseSet();
        //if (PhraseSetUtil.equals(matchedPhraseSet, phraseSetYes)) {
        //    Log.i(TAG, "Heard phrase set: yes");
        //} else if (PhraseSetUtil.equals(matchedPhraseSet, phraseSetNo)) {
        //    Log.i(TAG, "Heard phrase set: no");
        //}
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
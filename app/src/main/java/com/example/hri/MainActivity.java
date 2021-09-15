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
        variable_face.async().setValue("0");

        //VAR face recognition, anything but 0 if recognized, 0 if not
        var_stop1 = qiChatbot.variable("StopVar1");
        var_stop1.async().setValue( "0" ); //(int)(Math.random() * 10) doesn't work, why?

        //VAR face recognition, anything but 0 if recognized, 0 if not
        var_stop2 = qiChatbot.variable("StopVar2");
        var_stop2.async().setValue( String.valueOf(  (int)(Math.random() * 10)   ) );

        //VAR face recognition, anything but 0 if recognized, 0 if not
        var_stop3 = qiChatbot.variable("StopVar3");
        var_stop3.async().setValue( String.valueOf(  (int)(Math.random() * 10)   ) );

        //VAR face recognition, anything but 0 if recognized, 0 if not
        var_stop4 = qiChatbot.variable("StopVar4");
        var_stop4.async().setValue( String.valueOf(  (int)(Math.random() * 10)   ) );
        //Log.i(String.valueOf(Math.random()), "Discussion started.");

        Map<String, QiChatExecutor> executors = new HashMap<>();

        // Map the executor name from the topic to our qiChatExecutor
        executors.put("FaceRecognitionExecutor", new MyQiChatExecutor(qiContext));
        //executors.put("animationStopper", new MyQiChatExecutorStopper(qiContext, 5, lookAtFuture));

        // Set the executors to the qiChatbot
        qiChatbot.setExecutors(executors);

        say.run();

        Log.i( TAG_EVENTS, "var_stop1: "+ var_stop1.getValue());
        Log.i( TAG_EVENTS, "var_stop2: "+ var_stop2.getValue());
        Log.i( TAG_EVENTS, "var_stop3: "+ var_stop3.getValue());
        Log.i( TAG_EVENTS, "var_stop4: "+ var_stop4.getValue());

        chat = ChatBuilder.with(qiContext)
                .withChatbot(qiChatbot)
                .build();

        chat.addOnStartedListener(() -> Log.i(TAG, "Discussion started."));
        if (chat != null) {
            chat.removeAllOnStartedListeners();
        }
        chatFuture = chat.async().run();
        chatFuture.thenConsume(future -> {
            if (future.hasError()) {
                Log.e(TAG, "Discussion finished with error.", future.getError());
            }
        });



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
        private String TAG;

        MyQiChatExecutor(QiContext context) {
            super(context);
            this.qiContext = context;
        }

        @Override
        public void runWith(List<String> params) {
            boolean test = true;

            try {
                animationFuture=FaceRecognitionAnimation(qiContext);
                chatFuture.wait();
                //while(test){ Print(animationFuture.isDone()); }

                while(test){
                    while(animationFuture.isDone()){
                    animationFuture.requestCancellation();
                    animationFuture.notify();
                    qiChatbot.goToBookmark(bookmarksIntro.get("notremembered"), AutonomousReactionImportance.HIGH, AutonomousReactionValidity.IMMEDIATE);
//                    boolean notremeberedStatus = qiChatbot.bookmarkStatus("notremebered");
//                    Print(notremeberedStatus);
                        test=false;
                    break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


        @Override
        public void stop() {
            // This is called when chat is canceled or stopped
            Log.i(TAG, "QiChatExecutor stopped");
        }


        private Future<Void> FaceRecognitionAnimation(QiContext qiContext) throws InterruptedException {


            Animation myAnimation = AnimationBuilder.with(qiContext)
                    .withResources(R.raw.curious_a001)
                    .build();
            // Build the action.
            Animate animate = AnimateBuilder.with(qiContext)
                    .withAnimation(myAnimation)
                    .build();

            animationFuture = animate.async().run();


            return animationFuture;

//            while(animationFuture.isDone()){
//                animationFuture.requestCancellation();
//                animationFuture.notify();
//                break;
//            }
//
//            // Get the Actuation service from the QiContext.
//            Actuation actuation = qiContext.getActuation();
//            Frame robotFrameK = actuation.robotFrame();
//            Frame gazeFrame = actuation.gazeFrame();
//            // Create a transform corresponding to a vector3 translation.
//            Transform transform = TransformBuilder.create().fromTranslation(new Vector3(3,5,6));
//
//
//            // Get the Mapping service from the QiContext.
//            Mapping mapping = qiContext.getMapping();
//
//            // Create a FreeFrame with the Mapping service.
//            FreeFrame targetFrame = mapping.makeFreeFrame();
//
//            // Update the target location relatively to Pepper's current location.
//            //targetFrame.update(robotFrameK, transform, 0L); //per guardare cio' che voglio guardare, rispetto a dove è pepper ora, ho bisogno di una trasformazione "transform".
//            targetFrame.update(robotFrameK, transform, 0L); //per guardare cio' che voglio guardare, rispetto a dove è pepper ora, ho bisogno di una trasformazione "transform".
//
//            // Create a LookAt action.
//            lookAt1 = LookAtBuilder.with(qiContext) // Create the builder with the context.
//                    .withFrame(targetFrame.frame()) // Set the target frame.
//                    .build(); // Build the LookAt1 action.
//
//            // Set the LookAt policy to look with the head only.
//            lookAt1.setPolicy(LookAtMovementPolicy.HEAD_ONLY);
//
//            // Run the LookAt action (a)synchronously.
//            lookAtFuture = lookAt1.async().run();
//            //lookAtFuture= lookAt1.async().run();
//
//
//
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    lookAtFuture.requestCancellation();
//                    System.out.println(lookAtFuture.isDone());
//
//                }
//            }, 5*1000);

            }

        }

    class MyQiChatExecutorStopper extends BaseQiChatExecutor {
        private final QiContext qiContext;
        private String TAG;
        private int time;
        private Future<Void> future;

        MyQiChatExecutorStopper(QiContext context, int time, Future<Void> future) {
            super(context);
            this.qiContext = context;
            this.time = time;
            this.future= future;
        }

        @Override
        public void runWith(List<String> params) {

            sleepFunction(qiContext);
//            try {
//                System.out.println("sono qui prima dello sleep");
//                chatFuture.wait;
//                System.out.println("sono qui dopo dello sleep");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }

        private void animate(QiContext qiContext) {
        }



        @Override
        public void stop() {
            // This is called when chat is canceled or stopped
        }


        private void sleepFunction(QiContext qiContext )  {
            try {
                this.future.get(time, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            } finally {
                this.future.requestCancellation();
            }
        }

    }


    public static void Print(boolean condition)
    {
        System.out.println(condition);
    }
};



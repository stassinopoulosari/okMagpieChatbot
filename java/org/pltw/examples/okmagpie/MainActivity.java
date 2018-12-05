package org.pltw.examples.okmagpie;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Main Activity
 * @author Ari Stassinopoulos
 * @version 4 DEC 2018
 */
public class MainActivity extends AppCompatActivity {
    /**
     * A tag used to identify the activity
     */
    private static final String TAG = MainActivity.class.getName();

    /**
     * A text-to-speech object used to respond to the query with a voice
     */
    TextToSpeech maggiesVoice;

    /**
     * An EditText for user input
     */
    EditText keystrokeEditText;

    /**
     * A submit button for the user input
     */
    Button submitTextInputButton;

    /**
     * A Magpie4 used to parse the user input
     */
    Magpie4 maggie;

    /**
     * A Date used to store when the bot will next allow a response (so empty responses are not constantly submitted)
     */
    Date nextResponseAvailable;


    private final int REQ_CODE_SPEECH_INPUT = 100;

    /**
     * Function run on creation of the activity
     * @param savedInstanceState A saved instance state passed to the super
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MP", new ResponseLoader(this).getLoadedResponse().getRandomResponseForKeyword("random"));
        keystrokeEditText = (EditText)findViewById(R.id.keystrokeInput);
        submitTextInputButton = (Button)findViewById(R.id.submitTextInput);
        maggie = new Magpie4(this);
        final SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        nextResponseAvailable = new Date();
        maggiesVoice=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    maggiesVoice.setLanguage(Locale.US);
                    maggiesVoice.setPitch(1.8f);
                }
            }
        });

        submitTextInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keystrokeEditText.setText("");
                String userInput = keystrokeEditText.getText().toString();
                processUserInput(userInput);
            }
        });

        keystrokeEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String userInput = keystrokeEditText.getText().toString();
                    keystrokeEditText.setText("");
                    processUserInput(userInput);
                    return false;
                }
                return true;
            }
        });

    }

    /**
     * Process the input from the user.
     * @param userInput: A string with the user input.
     */
    private void processUserInput(String userInput){
        if(new Date().compareTo(nextResponseAvailable) > 0) {
            String response = maggie.getResponse(userInput);
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                maggiesVoice.speak(response, TextToSpeech.QUEUE_FLUSH, null, null);

            nextResponseAvailable.setTime(new Date().getTime() + 1000);
        }
    }
}

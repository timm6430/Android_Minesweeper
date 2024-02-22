package edu.pacificu.cs.timm6430minesweeperandroid;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AboutView extends AppCompatActivity {
    /**
     * Shows about page to the screen
     *
     * @param savedInstanceState - bundle object
     */
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_about);

    }
}

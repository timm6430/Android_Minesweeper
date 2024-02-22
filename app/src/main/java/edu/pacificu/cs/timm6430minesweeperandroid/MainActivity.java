package edu.pacificu.cs.timm6430minesweeperandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import edu.pacificu.cs.minesweeper.Minesweeper;

public class MainActivity extends AppCompatActivity {
    MinesweeperView mMinesweeperView;

    /**
     * Creates new game when app gets loaded up
     *
     * @param savedInstanceState - bundle object
     */
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        //setContentView(R.layout.activity_main);
        newGame (Minesweeper.DIFFICULTY.EASY, Minesweeper.RANDOM_SEED.RANDOM);
        setContentView (mMinesweeperView);
    }

    /**
     * Creates option menu
     *
     * @param menu - view of the menu
     */
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater ();
        inflater.inflate (R.menu.menu, menu);
        return true;
    }

    /**
     * Handles when the user selects one of the menus.
     *
     * @param item - menu item selected
     */
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId ()) {
            case R.id.menuAbout:
                Log.d ("onOptionsItemSelected", "About");
                Intent intent = new Intent (this, AboutView.class);
                startActivity (intent);
                return true;
            case R.id.menuDifficulty:
                Log.d ("onOptionsItemSelected", "menuDifficulty");
                return true;
            case R.id.menuSeed:
                Log.d ("onOptionsItemSelected", "menuSeed");
                return true;
            case R.id.Easy:
                Log.d ("onOptionsItemSelected", "Easy");
                newGame (Minesweeper.DIFFICULTY.EASY,
                        mMinesweeperView.getSeed ());
                return true;
            case R.id.Medium:
                Log.d ("onOptionsItemSelected", "Medium");
                newGame (Minesweeper.DIFFICULTY.MEDIUM,
                        mMinesweeperView.getSeed ());
                return true;
            case R.id.Hard:
                Log.d ("onOptionsItemSelected", "Hard");
                newGame (Minesweeper.DIFFICULTY.HARD,
                        mMinesweeperView.getSeed ());
                return true;
            case R.id.ZeroSeed:
                Log.d ("onOptionsItemSelected", "Zero Seed");
                newGame (mMinesweeperView.getDifficulty (),
                        Minesweeper.RANDOM_SEED.ZERO);
                return true;
            case R.id.TimeOfDaySeed:
                Log.d ("onOptionsItemSelected", "Time Of Day Seed");
                newGame (mMinesweeperView.getDifficulty (),
                        Minesweeper.RANDOM_SEED.RANDOM);
                return true;
            default:
                return super.onOptionsItemSelected (item);
        }
    }

    /**
     * Starts a new game
     *
     * @param difficulty - difficulty level
     * @param randomSeed - seed value
     */
    public void newGame (Minesweeper.DIFFICULTY difficulty,
                         Minesweeper.RANDOM_SEED randomSeed) {
        mMinesweeperView = new MinesweeperView (this, difficulty, randomSeed);
        mMinesweeperView.setUpBoard ();
        setContentView (mMinesweeperView);
    }

    /**
     * Restarts the game. The difficulty and seed stay the same
     *
     */
    public void restartGame () {
        mMinesweeperView = new MinesweeperView (this,
                mMinesweeperView.getDifficulty (), mMinesweeperView.getSeed ());
        mMinesweeperView.setUpBoard ();
        setContentView (mMinesweeperView);
    }


}
package edu.pacificu.cs.timm6430minesweeperandroid;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import edu.pacificu.cs.minesweeper.Minesweeper;
import edu.pacificu.cs.minesweeper.MinesweeperCell;
import edu.pacificu.cs.minesweeper.MinesweeperGrid;

public class MinesweeperView extends View {
    private final int LANDSCAPE_WIDTH = 1794;
    private final int RECTANGLES_PER_ROW = 9;
    private final int RECTANGLES_PER_COL = 9;

    private Paint mBackground = new Paint ();
    private Paint mDarkLines = new Paint ();
    private Paint mBombBackground = new Paint ();

    private int mRectangleHeight, mRectangleWidth;
    private int mXCoordSelectedRect, mYCoordSelectedRect;
    private int mBombCount, mOpenCellsForWin;

    private boolean mbGameContinue = true;
    private boolean mbLandscape = false;

    private Rect mSelectedRectangle = new Rect ();
    private Rect mOpenedRectangle = new Rect ();
    private Paint mSelectedRectanglePaint = new Paint ();
    private Paint mForeground = new Paint (Paint.ANTI_ALIAS_FLAG);

    private Minesweeper mMinesweeper;
    private MinesweeperGrid mMinesweeperGrid;
    private MinesweeperCell mMinesweeperCell;
    private MainActivity mMainActivity;

    /**
     * Constructor for MinesweeperView
     *
     * @param context    - mainActivity context
     * @param difficulty - difficulty level
     * @param randomSeed - seed value

     */
    public MinesweeperView (Context context,
                            Minesweeper.DIFFICULTY difficulty,
                            Minesweeper.RANDOM_SEED randomSeed) {
        super (context);
        setFocusable (true);
        setFocusableInTouchMode (true);
        this.mMainActivity = (MainActivity) context;
        mMinesweeper = new Minesweeper (difficulty, randomSeed);
        mMinesweeperGrid = new MinesweeperGrid ();

    }

    /**
     * Handles when the phone gets rotated. Keeps rectangle proportional.
     *
     * @param viewWidth     - new screen width size
     * @param viewHeight    - new screen height size
     * @param oldViewWidth  - old screen width size
     * @param oldViewHeight - old screen height size
     */
    @Override
    protected void onSizeChanged (int viewWidth, int viewHeight,
                                  int oldViewWidth, int oldViewHeight) {
        mRectangleWidth = viewWidth / getWidth ();
        mRectangleHeight = viewHeight / getHeight ();
        super.onSizeChanged (viewWidth, viewHeight, oldViewWidth, oldViewHeight);
        Log.d ("MinesweeperView", "new width = " + Integer.toString (viewWidth)
                + " new height = " + Integer.toString (viewHeight));
        if (mRectangleWidth == LANDSCAPE_WIDTH) {
            mbLandscape = true;
        } else {
            mbLandscape = false;
        }
    }

    /**
     * Draws the game board to the screen
     *
     * @param canvas - game board canvas
     */
    @Override
    protected void onDraw (Canvas canvas) {
        mBackground.setColor (getResources ().getColor (R.color.cSteelblue));
        mDarkLines.setColor (Color.BLACK);
        mBombBackground.setColor (Color.RED);
        canvas.drawRect (0, 0, getWidth (), getHeight (), mBackground);
        if (!mbLandscape) {
            mRectangleHeight = getHeight () / RECTANGLES_PER_COL;
            mRectangleWidth = getWidth () / RECTANGLES_PER_ROW;
        }

        mSelectedRectanglePaint.setColor (Color.CYAN);

        for (int i = 0; i < (int) RECTANGLES_PER_ROW; i++) {
            // Draw Horizontal Line
            canvas.drawLine (i * mRectangleWidth, 0,
                    i * mRectangleWidth, getHeight (), mDarkLines);
        }

        for (int i = 0; i < (int) RECTANGLES_PER_COL; i++) {
            // Draw Horizontal Line
            canvas.drawLine (0, i * mRectangleHeight, getWidth (),
                    i * mRectangleHeight, mDarkLines);
        }

        drawBoard (canvas);
    }

    /**
     * Handles click event from user. Opens the cell and
     * checks if the user won or lost.
     *
     * @param event - mouse click from user
     */
    public boolean onTouchEvent (MotionEvent event) {
        if (event.getAction () != MotionEvent.ACTION_DOWN) {
            return super.onTouchEvent (event);
        }
        int xCoordinate = (int) event.getX ();
        int yCoordinate = (int) event.getY ();
        while (mbGameContinue) {
            selectRectangle (xCoordinate, yCoordinate);

            if (!mbGameContinue) {
                loseAlert ();
            }
            if (mOpenCellsForWin == mMinesweeperGrid.getOpenedCellCount ()) {
                winAlert ();
            }
            return true;
        }
        return true;
    }

    /**
     * Finds the rectangle that got selected
     *
     * @param xCoordinate - col of the game board
     * @param yCoordinate - row of the game
     * @param rectangle - Rectangle that got selected by user
     */
    private void setSelectedRectangle (int xCoordinate, int yCoordinate,
                                       Rect rectangle) {
        rectangle.set ((xCoordinate * mRectangleWidth),
                (yCoordinate * mRectangleHeight),
                (xCoordinate * mRectangleWidth + mRectangleWidth - 1),
                (yCoordinate * mRectangleHeight + mRectangleHeight - 1));
    }

    /**
     * Open the selected rectangle
     *
     * @param xCoord - x coordinate of screen where the user clicked
     * @param yCoord - y coordinate of screen where the user clicked
     */
    private void selectRectangle (int xCoord, int yCoord) {
        invalidate (mSelectedRectangle);
        mXCoordSelectedRect = xCoord / mRectangleWidth;
        mYCoordSelectedRect = yCoord / mRectangleHeight;
        setSelectedRectangle (mXCoordSelectedRect, mYCoordSelectedRect,
                mSelectedRectangle);
        mbGameContinue = mMinesweeper.openGridCell (mMinesweeperGrid,
                mYCoordSelectedRect, mXCoordSelectedRect);
        invalidate (mSelectedRectangle);
    }

    /**
     * Calculate and place bombs on game board
     */
    public void setUpBoard () {
        mBombCount = mMinesweeper.calculateBombCount (mMinesweeperGrid);
        mMinesweeperGrid.setBombCount (mBombCount);
        mMinesweeper.placeBombs (mMinesweeperGrid);
        mOpenCellsForWin = mMinesweeperGrid.getTotalCellCount () -
                mMinesweeperGrid.getBombCount ();
    }

    /**
     * Draws the game board
     *
     * @param canvas - game board canvas
     */
    private void drawBoard (Canvas canvas) {
        for (int row = 0; row < RECTANGLES_PER_ROW; row++) {
            for (int col = 0; col < RECTANGLES_PER_COL; col++) {
                mMinesweeperCell = (MinesweeperCell)
                        mMinesweeperGrid.getCell (row, col);
                if (mMinesweeperCell.mValue == " ") {
                    setSelectedRectangle (row, col, mOpenedRectangle);
                    canvas.drawRect (mOpenedRectangle, mSelectedRectanglePaint);
                }
                else if (mMinesweeperCell.mValue != "@" &&
                        mMinesweeperCell.mValue != ".") {
                    setSelectedRectangle (row, col, mOpenedRectangle);
                    canvas.drawRect (mOpenedRectangle, mSelectedRectanglePaint);
                    drawText (canvas, row, col);
                }
                else if (mMinesweeperCell.mValue == "@" &&
                        mMinesweeperCell.getIsOpened ()) {
                    setSelectedRectangle (row, col, mOpenedRectangle);
                    canvas.drawRect (mOpenedRectangle, mBombBackground);
                    drawText (canvas, row, col);
                }
            }
        }
    }

    /**
     * Draws text on rectangle
     *
     * @param canvas - game board canvas
     * @param row - row of cell
     * @param col - col of cell
     */
    private void drawText (Canvas canvas, int row, int col) {
        mForeground.setStyle (Paint.Style.FILL);
        mForeground.setTextSize (mRectangleHeight * .75f);
        mForeground.setTextScaleX (mRectangleWidth /
                (mRectangleHeight * (float) 0.75));

        mForeground.setTextAlign (Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = mForeground.getFontMetrics ();
        float xFontCoord = mRectangleWidth / 2;
        float yFontCoord = mRectangleHeight / 2
                - (fontMetrics.ascent + fontMetrics.descent) / 2;

        canvas.drawText (mMinesweeperCell.mValue,
                row * mRectangleWidth + xFontCoord,
                col * mRectangleHeight + yFontCoord, mForeground);
    }

    /**
     * Print a message if the user loses, user can start a new game or exit app
     */
    private void loseAlert () {
        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder (mMainActivity);
        AlertDialog dialog;
        alertDialogBuilder.setTitle ("Boooom!!! You Lose.");
        alertDialogBuilder.setMessage ("Play Again?");
        alertDialogBuilder.setNegativeButton ("No",
                new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                mMainActivity.finish ();
                System.exit (0);
            }
        });
        alertDialogBuilder.setPositiveButton ("Yes",
                new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                mMainActivity.restartGame ();
            }
        });
        dialog = alertDialogBuilder.create ();
        dialog.show ();
    }

    /**
     * Print a message if the user wins, user can start a new game or exit app
     */
    private void winAlert () {
        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder (mMainActivity);
        AlertDialog dialog;
        alertDialogBuilder.setTitle ("Congratulations! You win!");
        alertDialogBuilder.setMessage ("Play Again?");
        alertDialogBuilder.setNegativeButton ("No",
                new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                mMainActivity.finish ();
                System.exit (0);
            }
        });
        alertDialogBuilder.setPositiveButton ("Yes",
                new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                mMainActivity.restartGame ();
            }
        });
        dialog = alertDialogBuilder.create ();
        dialog.show ();
    }

    /**
     * Return the game current difficulty level
     */
    public Minesweeper.DIFFICULTY getDifficulty ()
    {
        return mMinesweeper.getDifficulty ();
    }

    /**
     * Return the game current seed
     */
    public Minesweeper.RANDOM_SEED getSeed ()
    {
        return mMinesweeper.getSeed ();
    }
}

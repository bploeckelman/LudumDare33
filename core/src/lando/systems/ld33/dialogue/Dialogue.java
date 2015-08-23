package lando.systems.ld33.dialogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld33.World;
import lando.systems.ld33.utils.Assets;

/**
 * Author: Ian McNamara <ian.mcnamara@wisc.edu>
 * Teaching and Research Application Development
 * Copyright 2015 Board of Regents of the University of Wisconsin System
 */
public class Dialogue extends InputAdapter {

    // FINAL
    private static final Texture BLACK = new Texture("black.png");
    private static final float MARGIN = 32f;
    private static final float LINE_HEIGHT = 20f;
    private static final float CPS = 20f;
    private static final char SPACE = ' ';
    private static final float DEBOUNCETIME = .2f;

    // SETTINGS
    private float alpha = 0.7f;

    // FLAGS
    private boolean isShown = false;
    private boolean atEndOfMessage = false;
    private boolean isComplete = false;
    private boolean fastForward = false;

    // Base Properties
    private float startX;
    private float startY;
    private float width;
    private float height;
    private Array<String> messages;

    // Measurements
    private float fontDrawX;
    private float fontDrawTopY;
    private float fontDrawWidth;

    // Message vars
    private int currentMessageIndex;
    private String currentMessage;
    private int currentMessageCharIndex;
    private Array<String> currentMessageLines;
    private String renderString;
    private GlyphLayout finalLayout = new GlyphLayout();
    private GlyphLayout measuringLayout = new GlyphLayout();

    // Time Tracking
    private float updateTime = 0f;
    private float keyDeBounce;

    /**
     *
     * @param startTileX Starting tile x
     * @param startTileY Starting tile y
     * @param widthInTiles Width in tiles
     * @param heightInTiles Height in tiles
     */
    public Dialogue() {}

    // -----------------------------------------------------------------------------------------------------------------

    public void show(int startTileX, int startTileY, int widthInTiles, int heightInTiles, Array<String> messages) {

        this.startX = startTileX * World.PIXELS_PER_TILE;
        this.startY = startTileY * World.PIXELS_PER_TILE;
        this.width = widthInTiles * World.PIXELS_PER_TILE;
        this.height = heightInTiles * World.PIXELS_PER_TILE;
        this.messages = messages;

        this.fontDrawX = this.startX + MARGIN;
        this.fontDrawTopY = this.startY + this.height - MARGIN;
        this.fontDrawWidth = this.width - (MARGIN * 2);

        this.nextMessage(true);

        this.isShown = true;
        this.isComplete = false;
        this.keyDeBounce = DEBOUNCETIME;
    }

    private void hide() {
        this.isShown = false;
    }

    public boolean isActive() {
        return (this.isShown && !this.isComplete);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean keyUp(int keycode) {
        if (keyDeBounce > 0) return false;
        keyDeBounce = DEBOUNCETIME;
        Gdx.app.log("DEBUG", "keyUp");

        if (!isShown) {
            Gdx.app.log("DEBUG", "keyUp | not shown");
            // Don't block if we're hidden.
            return false;
        }

        if (atEndOfMessage) {

            Gdx.app.log("DEBUG", "keyUp | at and... go to next.");
            // Get the next message going.
            nextMessage();

        } else {

            Gdx.app.log("DEBUG", "keyUp | FF");
            // Fast forward to the end of the message.
            this.fastForward = true;

        }

        // Input handled.
        return true;


    }

    // -----------------------------------------------------------------------------------------------------------------

    private float getFontDrawX() {
        return this.fontDrawX;
    }
    private float getFontDrawY(int lineNumber) {
        return this.fontDrawTopY + (LINE_HEIGHT * lineNumber);
    }

    private void nextMessage() {
        this.nextMessage(false);
    }
    private void nextMessage(boolean isFirst) {

        if (isFirst) {
            this.currentMessageIndex = 0;
        } else {
            this.currentMessageIndex++;
        }

        // Are we out of messages?
        if (this.currentMessageIndex >= this.messages.size) {
            // Todo: might want to split this out of here, especially if we animate the show/hide
            this.hide();
            this.isComplete = true;
            return;
        }

        // Update the shit
        this.currentMessage = this.messages.get(this.currentMessageIndex);
        this.currentMessageLines = new Array<String>();
        this.currentMessageCharIndex = 0;
        this.finalLayout.setText(Assets.font, "");

        this.atEndOfMessage = false;
        this.updateTime = 0;

    }

    private Array<String> wrapLine(String givenLine) {

        Array<String> processedLines = new Array<String>();

        // Cram that string into our measuring layout
        this.measuringLayout.setText(Assets.font, givenLine);
        // Does it fit?
        if (this.measuringLayout.width <= this.fontDrawWidth) {
            processedLines.add(givenLine);
            return processedLines;
        } else {
            // It doesn't fit.
            int wrapIndex = givenLine.length() - 1;
            String wrappedString = givenLine.substring(0, wrapIndex);
            this.measuringLayout.setText(Assets.font, wrappedString);
            while (this.measuringLayout.width > this.fontDrawWidth) {
                // It still didn't fit.  Shorten it.
                wrapIndex--;
                if (wrapIndex <= 2) {
                    Gdx.app.log("ERROR",
                            "Wrapping has gone horribly horribly wrong.  GivenLine='" +
                                    givenLine + "' and text area width='" + String.valueOf(this.fontDrawWidth) + "'");
                    Gdx.app.exit();
                }
                wrappedString = givenLine.substring(0, wrapIndex);
                this.measuringLayout.setText(Assets.font, wrappedString);
            }

            // We have a string that'll fit now.  Let's make some decisions on how to split up the lines though.
            char lastChar = wrappedString.charAt(wrappedString.length() - 1);
            char secondToLastChar = wrappedString.charAt(wrappedString.length() - 2);
            char firstChar = givenLine.charAt(wrapIndex);
            String newLine;
            if (lastChar != SPACE && secondToLastChar != SPACE && firstChar != SPACE) {

                // We're splitting up a word.  Use the '-'
                processedLines.add(wrappedString.substring(0, wrappedString.length() - 1) + "-");
                newLine = lastChar + givenLine.substring(wrapIndex, givenLine.length());

            } else if (secondToLastChar == SPACE && lastChar != SPACE && firstChar != SPACE) {

                // Push the word onto the next line
                processedLines.add(wrappedString.substring(0, wrappedString.length() - 1));
                newLine = lastChar + givenLine.substring(wrapIndex, givenLine.length());

            } else {

                processedLines.add(wrappedString);
                newLine = givenLine.substring(wrapIndex, givenLine.length());

            }

            // The new line might need wrapping...
            newLine = newLine.trim();
            Array<String> wrapResult = wrapLine(newLine);
            for (int i = 0; i < wrapResult.size; i++) {
                processedLines.add(wrapResult.get(i));
            }

            return processedLines;

        }
    }


    // -----------------------------------------------------------------------------------------------------------------

    public void render(SpriteBatch batch) {

        if (this.isShown) {
            batch.setColor(1, 1, 1, this.alpha);
            batch.draw(BLACK, this.startX, this.startY, this.width, this.height);
            batch.setColor(1, 1, 1, 1);

            Assets.font.draw(batch, finalLayout, this.getFontDrawX(), this.getFontDrawY(0));

        }

    }
    public void update(float dt) {
        keyDeBounce -= dt;
        if (!isShown || atEndOfMessage || isComplete) {
            return;
        }

        this.updateTime += dt;
        int totalCharsToShow;

        if (this.fastForward) {
            this.fastForward = false;
            totalCharsToShow = this.currentMessage.length();
        } else {
            totalCharsToShow = MathUtils.floor(this.updateTime * CPS);
        }

        // Cap it to the length of the current message.
        totalCharsToShow = Math.min(this.currentMessage.length(), totalCharsToShow);

        // If we get to show more than we currently are:
        if (totalCharsToShow > this.currentMessageCharIndex) {

            // What new characters are to be shown?
            String charsToAdd = this.currentMessage.substring(this.currentMessageCharIndex, totalCharsToShow);

            int i;

            // Add a line if we need a new one.
            if (this.currentMessageLines.size == 0) {
                this.currentMessageLines.add("");
            }

            // Pop off the last line
            String currentLine = this.currentMessageLines.pop();
            currentLine += charsToAdd;

            // Wrap it?
            Array<String> wrapResult = wrapLine(currentLine);
            // Add the line(s) back into the collection
            for (i = 0; i < wrapResult.size; i++) {
                this.currentMessageLines.add(wrapResult.get(i));
            }

            // Build the render string and set the text in the glyph
            for (i = 0; i < this.currentMessageLines.size; i++) {
                if (i > 0) {
                    renderString += "\n" + this.currentMessageLines.get(i);
                } else {
                    renderString = this.currentMessageLines.get(i);
                }
            }
            this.finalLayout.setText(Assets.font, renderString);

            // Check for end of message
            if (totalCharsToShow == this.currentMessage.length()) {
                this.atEndOfMessage = true;
            }

            // Update the current thing
            this.currentMessageCharIndex = totalCharsToShow;

        }

    }

}

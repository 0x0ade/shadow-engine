package net.fourbytes.shadow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import net.fourbytes.shadow.utils.Options;

public class TextInputLevel extends MenuLevel {

    public String option;
    public String[] string;

    public String indicator = "|";
    public float indicatorTime = 0f;

    public TextInputLevel(String option) {
        this(null, option);
    }

    public TextInputLevel(String[] string) {
        this(null, string);
    }

    public TextInputLevel(MenuLevel parent, String option) {
        this(parent, option, new String[] {Options.getString(option, "")});
    }

    public TextInputLevel(MenuLevel parent, String[] string) {
        this(parent, null, string);
    }

    public TextInputLevel(MenuLevel parent, String option, String[] string) {
        super(parent);
        this.option = option;
        this.string = string;

        items.add(new MenuItem(this, "Back", new Runnable(){public void run() {
            if (TextInputLevel.this.option != null) {
                Options.putString(TextInputLevel.this.option, TextInputLevel.this.string[0]);
                Options.flush();
            }
            Gdx.input.setOnscreenKeyboardVisible(false);
            Shadow.level = TextInputLevel.this.parent;
        }}));

        Gdx.input.setOnscreenKeyboardVisible(true);

        ready = true;
    }


    @Override
    public void keyDown(Input.Key key) {
        if (Shadow.level != this || key == Input.enter || key == Input.androidBack || key == Input.pause) {
            items.get(0).action.run();
        }
    }

    public void keyTyped(char c) {
        if (Shadow.level != this) {
            return;
        }

        if (c == '\b' && string[0].length() > 0) {
            string[0] = string[0].substring(0, string[0].length()-1);
        }

        string[0] = string[0] + c;
        string[0] = string[0].replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "");
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);
        indicatorTime += delta;
        if (indicatorTime >= 0.5f) {
            indicator = " ";
        }
        if (indicatorTime >= 1f) {
            indicator = "|";
            indicatorTime = 0f;
        }
    }

    @Override
    public void renderImpl() {
        showtitle = false;
        super.renderImpl();

        Rectangle vp = Shadow.cam.camrec;
        BitmapFont.TextBounds bounds = font.getBounds(string[0]+indicator);

        String txt = string[0]+indicator;
        float x = vp.x + vp.width / 2f - bounds.width / 2f;
        float y = vp.y + vp.height / 2f - bounds.height / 2f;

        font.setColor(0f, 0f, 0f, 0.5f);
        font.draw(Shadow.spriteBatch, txt, x + 0.0825f, y + 0.0825f);
        font.setColor(1f, 1f, 1f, 1f);
        font.draw(Shadow.spriteBatch, txt, x, y);
    }

}

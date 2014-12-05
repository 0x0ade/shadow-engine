package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class IntroLevel extends MenuLevel {

    public float time = 0f;
    public float timeFull = 0.7f;

    public IntroLevel() {
        super();
        dirtify = true;
        ready = true;
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);
        time += delta;
        if (time >= timeFull) {
            Shadow.level = new TitleLevel();
        }
    }

    @Override
    public void renderImpl() {
        showtitle = false;
        super.renderImpl();

        Rectangle vp = Shadow.cam.camrec;

        if (logo == null) {
            logo = Images.getImage("logo");
        }
        logo.setScale(font.getScaleX(), font.getScaleY());
        logo.setPosition(vp.x + vp.width / 2f - logo.getWidth()*logo.getScaleX()/2f,
                vp.y + vp.height / 2f - logo.getHeight()*logo.getScaleY()/2f - ((time / 0.6f)*(time / 0.6f))*2f);
        logo.draw(Shadow.spriteBatch, Math.max(1f - (time / 0.6f), 0f));

    }

}

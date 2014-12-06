package net.fourbytes.shadow;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import net.fourbytes.shadow.utils.Garbage;

public class IntroLevel extends MenuLevel {

    public float time = 0f;
    public float timeFull = 2f;

    public Color[] colors;

    public IntroLevel() {
        super();
        dirtify = true;
        ready = true;

        Shadow.cam.bg.c1.set(0f, 0f, 0f, 1f);
        Shadow.cam.bg.c2.set(0f, 0f, 0f, 1f);

        colors = new Color[3];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = new Color(
                    MathUtils.random(0.5f, 1f),
                    MathUtils.random(0.5f, 1f),
                    MathUtils.random(0.5f, 1f),
                    MathUtils.random(0.5f, 1f)
            );
        }
    }

    @Override
    public void tick(float delta) {
        super.tick(delta);
        time += delta;
        if (time >= timeFull) {
            Shadow.level = new TitleLevel();
        }

    }

    public Image light;

    @Override
    public void renderImpl() {
        showtitle = false;
        super.renderImpl();

        Rectangle vp = Shadow.cam.camrec;

        if (light == null) {
            light = Images.getImage("light_alpha");
        }
        light.setSize(Math.max(vp.width, vp.height), Math.max(vp.width, vp.height));

        Garbage.colors.next();

        light.setColor(colors[0]);
        light.setPosition(vp.x - vp.width * 0.9f + (time/2f) * vp.width * 1.125f,
                vp.y + vp.height / 2f - light.getHeight() / 2f);
        light.draw(Shadow.spriteBatch, MathUtils.sin(MathUtils.PI2 * (time/2f)));

        light.setColor(colors[1]);
        light.setPosition(vp.x + vp.width / 2f - light.getWidth() / 2f,
                vp.y - vp.height * 1.1f + (time/1.7f) * vp.height * 0.5f);
        light.draw(Shadow.spriteBatch, MathUtils.sin(MathUtils.PI2 * (time/1.66f)));

        light.setColor(colors[2]);
        light.setPosition(vp.x - vp.width * 1.1f + (time/2f) * vp.width * 0.8f,
                vp.y + vp.height / 2f - light.getHeight() / 2f);
        light.draw(Shadow.spriteBatch, MathUtils.sin(MathUtils.PI2 * (time/1.44f)));

        if (logo == null) {
            logo = Images.getImage("logo_4f");
        }
        logo.setScale(font.getScaleX(), font.getScaleY());
        logo.setPosition(vp.x + vp.width / 2f - logo.getWidth() * logo.getScaleX() / 2f,
                vp.y + vp.height / 2f - logo.getHeight() * logo.getScaleY() / 2f -
                        (1f - MathUtils.sin(MathUtils.PI / 2f * (time / 2f))) * 2f);
        logo.setColor(0f, 0f, 0f, 1f);
        logo.draw(Shadow.spriteBatch, time / 2f);

    }

    @Override
    public void keyDown(Input.Key key) {
        if (Shadow.level != this) {
            return;
        }
        tick(timeFull);
    }

    @Override
    public void keyUp(Input.Key key) {
        if (Shadow.level != this) {
            return;
        }
    }

}

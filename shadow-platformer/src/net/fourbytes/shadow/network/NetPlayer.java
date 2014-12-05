package net.fourbytes.shadow.network;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.entities.Player;
import net.fourbytes.shadow.map.IsSaveable;

public class NetPlayer extends Player {

    @IsSaveable
    public long timestamp;

    public String username = "";

    public NetPlayer(Vector2 position, Layer layer) {
        super(position, layer);
        Input.keylisteners.removeValue(this, true);
        canInteract = false;
    }

    @Override
    public void tick(float delta) {
        movement.set(0f, 0f);
        slowdown = 0f;
        objgravity = 0f;
        solid = false;
        super.tick(delta);
        texupdate = true;
    }

    @Override
    public TextureRegion getTexture(int id) {
        return Images.split("player", 16, 16)[facingLeft?0:1][frame];
    }

    @Override
    public void keyDown(Input.Key key) {
    }

    @Override
    public void keyUp(Input.Key key) {
    }

    public BitmapFont font;

    @Override
    public void renderHealth() {
        if (this == layer.level.player) {
            return;
        }

        if (hframe >= 1f) {
            return;
        }

        float alpha = (1f-hframe)*2f;
        if (alpha > 1f) {
            alpha = 1f;
        }

        if (font == null) {
            font = Fonts.light_small;
        }

        if (username == null) {
            username = "";
        }

        font.setScale(Shadow.vieww/Shadow.dispw, -Shadow.viewh/Shadow.disph);
        BitmapFont.TextBounds tb = font.getBounds(username);
        font.setColor(1f, 1f, 1f, alpha);
        font.draw(Shadow.spriteBatch, username, pos.x + rec.width/2f - tb.width/2f, pos.y - 0.4f + tb.height);

        super.renderHealth();
    }

    @Override
    public void render() {
        super.render();
    }

}

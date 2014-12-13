package net.fourbytes.shadow.blocks;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import net.fourbytes.shadow.Musics;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.Sounds;
import net.fourbytes.shadow.systems.ITimeDaySystem;
import net.fourbytes.shadow.utils.Garbage;

public class BlockMusic extends BlockType {

    public int slot;
    public float radius;

    public BlockMusic() {
        this(0, 32f);
    }

    public BlockMusic(int slot, float radius) {
        this.slot = slot;
        this.radius = radius;
    }

    @Override
    public void init() {
        dynamic = false;
        blending = false;
        tickAlways = true;
        tickInView = true;
        solid = false;
        passSunlight = true;
        alpha = 0f;
    }

    @Override
    public void tick(float delta) {
        Music musicDay = Musics.playing.get(slot);
        Music musicNight = Musics.playing.get(-slot);

        Vector2 pos = Garbage.vec2s.getNext();
        pos.set(this.pos);
        pos.add(rec.width / 2f, rec.height / 2f);

        Rectangle vp = Shadow.cam.camrec;

        Vector2 origpos = Garbage.vec2s.getNext();
        origpos.set(vp.x + vp.width/2f, vp.y + vp.height/2f);
        if (Shadow.level != null && Shadow.level.player != null) {
            origpos.set(Shadow.level.player.pos);
            origpos.add(Shadow.level.player.rec.width/2f, Shadow.level.player.rec.height/2f);
        }

        float maxdistsq = radius * radius;

        float distx = pos.x - origpos.x;
        float disty = pos.y - origpos.y;
        float distsq = distx * distx + disty * disty;

        float vol = 0f;
        if (distsq <= maxdistsq) {
            vol = 0.5f * MathUtils.cos((float) Math.sqrt(distsq) * (MathUtils.PI / radius)) + 0.5f;
        }

        float volDay = 1f;
        ITimeDaySystem time = layer.level.systems.get(ITimeDaySystem.class);
        if (time != null && musicNight != null) {
            volDay = 0.5f + 0.7f *
                    MathUtils.sin(time.getTimeNormalized() * 1.75f * MathUtils.PI + 1.5f * MathUtils.PI);
        }

        if (musicDay != null) {
            musicDay.setVolume(vol * volDay);
        }

        if (musicNight != null) {
            musicNight.setVolume(vol * (1f - volDay));
        }
    }

    @Override
    public void preRender() {
    }

    @Override
    public void render() {
    }

}

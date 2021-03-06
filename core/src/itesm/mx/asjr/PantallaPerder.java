package itesm.mx.asjr;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.audio.Music;

/**
 * Created by AlanJoseph, dc on 06/09/2016.
 */
public class PantallaPerder implements Screen
{


    private final Juego juego;
    private Stage escena;


    private final AssetManager assetManager = new AssetManager();

    private Texture texturaFondoA;
    private Texture texturaBtnBack;
    private Texture texturaHazPerdido;
    private Music GameOverMusic;



    public PantallaPerder(Juego juego) {this.juego = juego;}

    private void cargarTexturas(){

        assetManager.load("FondoHazPerdido.png", Texture.class);
        assetManager.load("MenuPrincipal.png", Texture.class);
        assetManager.load("hazperdido.png", Texture.class);
        assetManager.load("GameOverSound.mp3", Music.class);



        assetManager.finishLoading();


        GameOverMusic = assetManager.get("GameOverSound.mp3");
        texturaFondoA = assetManager.get("FondoHazPerdido.png");
        texturaBtnBack = assetManager.get("MenuPrincipal.png");
        texturaHazPerdido = assetManager.get("hazperdido.png");

        GameOverMusic.play();

    }


    @Override
    public void show()
    {
        cargarTexturas();
        escena = new Stage();


        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();

        Gdx.input.setInputProcessor(escena);

        Image imgFondoA = new Image(texturaFondoA);

        float escalaX = ancho  / imgFondoA.getWidth();
        float escalaY = alto / imgFondoA.getHeight();
        imgFondoA.setScale(escalaX, escalaY);
        escena.addActor(imgFondoA);



        TextureRegionDrawable trdBtnBack = new TextureRegionDrawable( new TextureRegion(texturaBtnBack));
        ImageButton btnBack = new ImageButton(trdBtnBack);
        btnBack.setPosition(ancho/2 - btnBack.getWidth()/2, 0.2f*alto);
        escena.addActor(btnBack);


        Image botonMenuPrincipal = new Image(texturaHazPerdido);
        botonMenuPrincipal.setPosition(ancho/2-botonMenuPrincipal.getWidth()/2, 0.4f*alto);
        escena.addActor(botonMenuPrincipal);


        btnBack.addListener( new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Regresar al menu pricipal
                juego.setScreen( new PantallaPrincipal(juego) );
            }
        });

    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0,1,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        escena.draw();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

        dispose();

    }

    @Override
    public void dispose() {
        texturaFondoA.dispose();
        escena.dispose();
        texturaBtnBack.dispose();
        texturaHazPerdido.dispose();
    }
}

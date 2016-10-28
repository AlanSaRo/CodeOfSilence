package itesm.mx.asjr;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;


/**
 * Created by dc on 10/13/16.
 */
public class PantallaMapa implements Screen
{

    // Para el espacio en donde ocurre el juego
    public static final int ANCHO_MAPA = 2560 ;
    public static final int ANCHO_CAMARA = 1280;
    public static final int ALTO_CAMARA = 480;

    // Cámara
    private OrthographicCamera camara;
    private Viewport vista;

    // HUD. Los componentes en la pantalla que no se mueven
    private OrthographicCamera camaraHUD; // Cámara fija
    private StretchViewport vistaHUD;

    // Escena para HUD
    private Stage escena;

    // SpriteBatch sirve para administrar los trazos
    private SpriteBatch batch;
    private final Juego juego;

    // Para el mapa
    private TiledMap mapa;  // Información del mapa en memoria
    private OrthogonalTiledMapRenderer rendererMapa;    // Dibuja el mapa

    // Personajes animado
    //private Texture texturaMario;
    private Personaje mario;
    private Enemigo bowser;
    private Enemigo bowser1;
    private Enemigo bowser2;
    private Enemigo bowser3;
    private Enemigo bowser4;
    private Enemigo bowser5;
    private Enemigo bowser6;
    private Enemigo bowser7;
    private Enemigo bowser8;
    private Enemigo bowser9;
    private Texture texturaPersonaje;
    private Texture texturaEnemigo;


    // Pad
    private Touchpad pad;

    // Action Button
    private TextButton.TextButtonStyle textButtonStyle;
    public static TextButton actionButton;
    private BitmapFont font;

    // Para las balas
    ArrayList<Bullet> bulletList = new ArrayList<Bullet>();
    ArrayList<Object> bulletUseless = new ArrayList<Object>();

    // Musica
    private Music musicaFondo;

    // Para una barra de vida
    private Texture healthBar, healthContainer;
    int vida = 32;


    // Construtor por default.
    public PantallaMapa(Juego juego) {  // Constructor
        this.juego = juego;
    }

    @Override
    // Aquí es donde se obtienen las propiedades iniciales de nuestra pantalla.
    public void show() {
        inicializarCamara();
        crearEscena();
        cargarMapa();   // Nuevo
        crearPad();
        inicializarVida();


        Gdx.gl.glClearColor(1,1,1,1);
    }

    // Método para dibujar la barra de vida, inicializarla con vida de 32.
    private void inicializarVida() {
        int ancho = 1;
        int alto = 1;
        // Se dibuja un pixmap para el contenedor de la barra de vida.
        Pixmap pixmap1 = drawPixmap(ancho, alto, 1,0,0);
        // Se dibuja otro pixmap para la barra de vida
        Pixmap pixmap2 = drawPixmap(ancho,alto, 0,0,0);
        // Se inicializan las texturas.
        healthBar = new Texture(pixmap1);
        healthContainer = new Texture (pixmap2);
    }

    private Pixmap drawPixmap(int ancho, int alto, int r, int g, int b) {
        // Éste método dibuja el mapa de pixeles que representa la barra de vida.
        Pixmap pixmap = new Pixmap (ancho, alto, Pixmap.Format.RGBA8888);
        pixmap.setColor(r,g,b,1);
        pixmap.fill();
        return pixmap;
    }


    // Este botón dibuja en pantalla un botón para que el jugador pueda disparar.
    private void createActionButtion(){

        // Crea las texturas.
        Skin skin = new Skin();
        skin.add("actionUp", new Texture ("boton_disparo.png"));
        skin.add("actionDown", new Texture ("boton_disparo_inactivo.png"));
        font = new BitmapFont();

        // Características del botón.
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("actionUp");
        textButtonStyle.down = skin.getDrawable("actionDown");
        textButtonStyle.font = font;

        // Crea un botón de acción con las texturas y las características creadas.
        actionButton = new TextButton("Disparar", textButtonStyle);
        actionButton.setBounds(1130, 50, 150 ,150);

        // Agrega el objeto a la pantalla.
        escena.addActor(actionButton);

        actionButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("ActionButton", "Click sobre el botón de acción");
            }
        });

    }

    private void crearPad() {

        // Para cargar las texturas y convertirlas en Drawable
        Skin skin = new Skin();
        skin.add("touchBackground", new Texture("touchBackground.png"));
        skin.add("touchKnob", new Texture("touchKnob.png"));

        // Carcaterísticas del pad
        Touchpad.TouchpadStyle tpEstilo = new Touchpad.TouchpadStyle();
        tpEstilo.background = skin.getDrawable("touchBackground");
        tpEstilo.knob = skin.getDrawable("touchKnob");

        // Crea el pad, revisa la clase Touchpad para entender los parámetros
        pad = new Touchpad(20,tpEstilo);
        pad.setBounds(0,0,200,200); // Posición y tamaño
        pad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (mario.getEstadoMovimiento()!= Personaje.EstadoMovimiento.INICIANDO) {
                    Touchpad p = (Touchpad) actor;

                    if (p.getKnobPercentX() > 0 && p.getKnobPercentY()<.25 && p.getKnobPercentY()>-.25) {    //Derecha
                        mario.setEstadoMovimiento(Personaje.EstadoMovimiento.MOV_DERECHA);
                    } else if (p.getKnobPercentX() < 0 && p.getKnobPercentY()<.25 && p.getKnobPercentY()>-.25) { // Izquierda
                        mario.setEstadoMovimiento(Personaje.EstadoMovimiento.MOV_IZQUIERDA);
                    } else if (p.getKnobPercentY() > .25 ) { //Arriba
                        mario.setEstadoMovimiento(Personaje.EstadoMovimiento.MOV_ARRIBA);
                    }else if (p.getKnobPercentY() < -.1) { //Abajo
                        mario.setEstadoMovimiento(Personaje.EstadoMovimiento.MOV_ABAJO);
                    } else if (p.getKnobPercentX() == 0){    // Nada
                        mario.setEstadoMovimiento(Personaje.EstadoMovimiento.QUIETO);
                    }
                }

                moverEnemigo(bowser,mario);
                moverEnemigo(bowser1,mario);
                moverEnemigo(bowser2,mario);
                moverEnemigo(bowser3,mario);
                moverEnemigo(bowser4,mario);
                moverEnemigo(bowser5,mario);
                moverEnemigo(bowser6,mario);
                moverEnemigo(bowser7,mario);
                moverEnemigo(bowser8,mario);
                moverEnemigo(bowser9,mario);


            }
        });

        escena.addActor(pad);
        pad.setColor(1,1,1,0.4f);
        Gdx.input.setInputProcessor(escena);

    }

    private void crearEscena() {
        batch = new SpriteBatch();

        escena = new Stage();
        escena.setViewport(vistaHUD);
        crearPad();
        createActionButtion();
    }

    public void moverEnemigo(Enemigo enemy, Personaje character){

        if (character.getX() < enemy.getX() && character.getY() == enemy.getY()){
            enemy.setEstadoMovimiento(Enemigo.EstadoMovimiento.MOV_IZQUIERDA);
        }
        else if (character.getX() > enemy.getX() && character.getY() == enemy.getY()){
            enemy.setEstadoMovimiento(Enemigo.EstadoMovimiento.MOV_DERECHA);
        }

        else if (character.getY() < enemy.getY() && character.getX() == enemy.getX()){
            enemy.setEstadoMovimiento(Enemigo.EstadoMovimiento.MOV_ABAJO);
        }
        else if (character.getY() > enemy.getY() && character.getX() == enemy.getX()){
            enemy.setEstadoMovimiento(Enemigo.EstadoMovimiento.MOV_ARRIBA);
        }

    }




    private void cargarMapa() {

        AssetManager manager = new AssetManager();
        manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        //manager.load("MarioCompleto.tmx", TiledMap.class);
        manager.load("bar_codeofsilence5.tmx", TiledMap.class);
        manager.load("sprite_completo.png", Texture.class);
        //manager.load("MonitoSprite.png", Texture.class);
        manager.load("sprite_completo_enemigo.png", Texture.class);



        // Carga música
        manager.load("Jailhouse.mp3", Music.class);

        manager.finishLoading();
        mapa = manager.get("bar_codeofsilence5.tmx");
        //texturaMario = manager.get("MonitoSprite.png");
        texturaPersonaje = manager.get("sprite_completo.png");
        texturaEnemigo = manager.get("sprite_completo_enemigo.png");



        // Crea el objeto que dibujará el mapa
        rendererMapa = new OrthogonalTiledMapRenderer(mapa,batch);
        rendererMapa.setView(camara);

        // Audio

        musicaFondo = manager.get("Jailhouse.mp3");
        musicaFondo.setLooping(true);
        musicaFondo.play();


        // Personaje y Enemigo
        mario = new Personaje(texturaPersonaje);

        bowser = new Enemigo(texturaEnemigo);
        bowser.setPosition(400,800);
        bowser1 = new Enemigo(texturaEnemigo);
        bowser1.setPosition(800,800);
        bowser2 = new Enemigo(texturaEnemigo);
        bowser2.setPosition(1000,800);
        bowser3 = new Enemigo(texturaEnemigo);
        bowser3.setPosition(1200,800);
        bowser4 = new Enemigo(texturaEnemigo);
        bowser4.setPosition(1400,800);
        bowser5 = new Enemigo(texturaEnemigo);
        bowser5.setPosition(1600,800);
        bowser6 = new Enemigo(texturaEnemigo);
        bowser6.setPosition(1800,800);
        bowser7 = new Enemigo(texturaEnemigo);
        bowser7.setPosition(2000,800);
        bowser8 = new Enemigo(texturaEnemigo);
        bowser8.setPosition(2200,800);
        bowser9 = new Enemigo(texturaEnemigo);
        bowser9.setPosition(2400,800);


    }

    private void inicializarCamara() {
        camara = new OrthographicCamera(ANCHO_CAMARA, ALTO_CAMARA);
        camara.position.set(ANCHO_CAMARA/2, ALTO_CAMARA /2,0);
        camara.update();
        vista = new StretchViewport(ANCHO_CAMARA, PantallaMapa.ALTO_CAMARA,camara);

        //Cámara para HUD

        camaraHUD = new OrthographicCamera(ANCHO_CAMARA, PantallaMapa.ALTO_CAMARA);
        camaraHUD.position.set(ANCHO_CAMARA/2, PantallaMapa.ALTO_CAMARA /2, 0);
        camaraHUD.update();
        vistaHUD = new StretchViewport(ANCHO_CAMARA, PantallaMapa.ALTO_CAMARA,camaraHUD);

    }

    @Override
    public void render(float delta) {
        //El método render va a dibujar en pantalla lo que le digamos. Recibe un tiempo delta.

        // actualizar cámara (para recorrer el mundo completo)
        actualizarCamara();
        // Actualización del personaje en el mapa
        mario.actualizar(mapa);
        bowser.actualizar(mapa);
        bowser1.actualizar(mapa);
        bowser2.actualizar(mapa);
        bowser3.actualizar(mapa);
        bowser4.actualizar(mapa);
        bowser5.actualizar(mapa);
        bowser6.actualizar(mapa);
        bowser7.actualizar(mapa);
        bowser8.actualizar(mapa);
        bowser9.actualizar(mapa);

        // Borra el frame actual
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // escala la pantalla de acuerdo a la cámara y vista
        batch.setProjectionMatrix(camara.combined);
        rendererMapa.setView(camara);
        rendererMapa.render();  // Dibuja el mapa

        // Batch
        batch.begin();
        mario.render(batch);    // Dibuja el personaje
        bowser.render(batch);
        bowser1.render(batch);
        bowser2.render(batch);
        bowser3.render(batch);
        bowser4.render(batch);
        bowser5.render(batch);
        bowser6.render(batch);
        bowser7.render(batch);
        bowser8.render(batch);
        bowser9.render(batch);


        batch.draw(healthContainer, mario.getX(), mario.getY()+33, 32, 5);//Dibuja la barra de vida.
        batch.draw(healthBar, mario.getX(), mario.getY()+34 ,vida,4);
        for(Bullet bill : bulletList){
            bill.draw(batch);
        }
        batch.end();

        // Avanza la bala durante un tiempo determinado.
        // Checa si hay colisión con algún enemigo.
        for(Bullet bill: bulletList){
            bill.update(Gdx.graphics.getDeltaTime());
            if (bill.isDead()) this.bulletUseless.add(bill);


            // Bowser :
            if(( (bill.getHitbox().getX() - bowser.getX()) >= 32 ) && ( (bill.getHitbox().getX() - bowser.getX()) <= 32 )){
                Gdx.app.log("bowser","gets hit");
                if(bowser.getY()<bill.getHitbox().getY()){
                    if( (bill.getHitbox().getY() - bowser.getX() <= 32)){
                        bulletUseless.add(bowser);
                    }
                }
                else if (bowser.getY()>bill.getHitbox().getY()){
                    if( (bowser.getY() - bill.getHitbox().getY()) <= 32 ){
                        bulletUseless.add(bowser);
                    }
                }
            }

            // Bowser 1:
            if(( (bill.getHitbox().getX() - bowser1.getX()) >= 32 ) && ( (bill.getHitbox().getX() - bowser1.getX()) <= 32 )){
                Gdx.app.log("bowser","gets hit");
                if(bowser1.getY()<bill.getHitbox().getY()){
                    if( (bill.getHitbox().getY() - bowser1.getX() <= 32)){
                        bulletUseless.add(bowser1);
                    }
                }
                else if (bowser1.getY()>bill.getHitbox().getY()){
                    if( (bowser1.getY() - bill.getHitbox().getY()) <= 32 ){
                        bulletUseless.add(bowser1);
                    }
                }
            }

            // Bowser 2:
            if(( (bill.getHitbox().getX() - bowser2.getX()) >= 32 ) && ( (bill.getHitbox().getX() - bowser2.getX()) <= 32 )){
                Gdx.app.log("bowser","gets hit");
                if(bowser2.getY()<bill.getHitbox().getY()){
                    if( (bill.getHitbox().getY() - bowser2.getX() <= 32)){
                        bulletUseless.add(bowser2);
                    }
                }
                else if (bowser2.getY()>bill.getHitbox().getY()){
                    if( (bowser2.getY() - bill.getHitbox().getY()) <= 32 ){
                        bulletUseless.add(bowser2);
                    }
                }
            }

            // Bowser 3:
            if(( (bill.getHitbox().getX() - bowser3.getX()) >= 32 ) && ( (bill.getHitbox().getX() - bowser3.getX()) <= 32 )){
                Gdx.app.log("bowser","gets hit");
                if(bowser3.getY()<bill.getHitbox().getY()){
                    if( (bill.getHitbox().getY() - bowser3.getX() <= 32)){
                        bulletUseless.add(bowser3);
                    }
                }
                else if (bowser3.getY()>bill.getHitbox().getY()){
                    if( (bowser3.getY() - bill.getHitbox().getY()) <= 32 ){
                        bulletUseless.add(bowser3);
                    }
                }
            }

            // Bowser 4:
            if(( (bill.getHitbox().getX() - bowser4.getX()) >= 32 ) && ( (bill.getHitbox().getX() - bowser4.getX()) <= 32 )){
                Gdx.app.log("bowser","gets hit");
                if(bowser4.getY()<bill.getHitbox().getY()){
                    if( (bill.getHitbox().getY() - bowser4.getX() <= 32)){
                        bulletUseless.add(bowser4);
                    }
                }
                else if (bowser4.getY()>bill.getHitbox().getY()){
                    if( (bowser4.getY() - bill.getHitbox().getY()) <= 32 ){
                        bulletUseless.add(bowser4);
                    }
                }
            }

            // Bowser 5:
            if(( (bill.getHitbox().getX() - bowser5.getX()) >= 32 ) && ( (bill.getHitbox().getX() - bowser5.getX()) <= 32 )){
                Gdx.app.log("bowser","gets hit");
                if(bowser5.getY()<bill.getHitbox().getY()){
                    if( (bill.getHitbox().getY() - bowser5.getX() <= 32)){
                        bulletUseless.add(bowser5);
                    }
                }
                else if (bowser5.getY()>bill.getHitbox().getY()){
                    if( (bowser5.getY() - bill.getHitbox().getY()) <= 32 ){
                        bulletUseless.add(bowser5);
                    }
                }
            }

            // Bowser 6:
            if(( (bill.getHitbox().getX() - bowser6.getX()) >= 32 ) && ( (bill.getHitbox().getX() - bowser6.getX()) <= 32 )){
                Gdx.app.log("bowser","gets hit");
                if(bowser6.getY()<bill.getHitbox().getY()){
                    if( (bill.getHitbox().getY() - bowser6.getX() <= 32)){
                        bulletUseless.add(bowser6);
                    }
                }
                else if (bowser6.getY()>bill.getHitbox().getY()){
                    if( (bowser6.getY() - bill.getHitbox().getY()) <= 32 ){
                        bulletUseless.add(bowser6);
                    }
                }
            }

            // Bowser 7:
            if(( (bill.getHitbox().getX() - bowser7.getX()) >= 32 ) && ( (bill.getHitbox().getX() - bowser7.getX()) <= 32 )){
                Gdx.app.log("bowser","gets hit");
                if(bowser7.getY()<bill.getHitbox().getY()){
                    if( (bill.getHitbox().getY() - bowser7.getX() <= 32)){
                        bulletUseless.add(bowser7);
                    }
                }
                else if (bowser7.getY()>bill.getHitbox().getY()){
                    if( (bowser7.getY() - bill.getHitbox().getY()) <= 32 ){
                        bulletUseless.add(bowser7);
                    }
                }
            }

            // Bowser 8:
            if(( (bill.getHitbox().getX() - bowser8.getX()) >= 32 ) && ( (bill.getHitbox().getX() - bowser8.getX()) <= 32 )){
                Gdx.app.log("bowser","gets hit");
                if(bowser8.getY()<bill.getHitbox().getY()){
                    if( (bill.getHitbox().getY() - bowser8.getX() <= 32)){
                        bulletUseless.add(bowser8);
                    }
                }
                else if (bowser8.getY()>bill.getHitbox().getY()){
                    if( (bowser8.getY() - bill.getHitbox().getY()) <= 32 ){
                        bulletUseless.add(bowser8);
                    }
                }
            }

            // Bowser 9:
            if(( (bill.getHitbox().getX() - bowser9.getX()) >= 32 ) && ( (bill.getHitbox().getX() - bowser9.getX()) <= 32 )){
                Gdx.app.log("bowser","gets hit");
                if(bowser9.getY()<bill.getHitbox().getY()){
                    if( (bill.getHitbox().getY() - bowser9.getX() <= 32)){
                        bulletUseless.add(bowser9);
                    }
                }
                else if (bowser9.getY()>bill.getHitbox().getY()){
                    if( (bowser9.getY() - bill.getHitbox().getY()) <= 32 ){
                        bulletUseless.add(bowser9);
                    }
                }
            }



        }

        // Limpia los dos ArrayList
        while(bulletUseless.size()!=0){
            //if(bulletUseless.get(0) == bowser){Gdx.app.log("arrayList","Aqui está bowser");}
            bulletList.remove(bulletUseless.get(0));
            bulletUseless.remove(0);
        }

        // Dibuja el HUD
        batch.setProjectionMatrix(camaraHUD.combined);
        escena.draw();

        // Prueba si el enemigo está atacando a mario
        if (mario.getX() == bowser.getX()){
            vida--;
        }

        // Aquí es donde el personaje pierde
        if(vida == 0){
            // Gdx.app.log("PantallaMapa", "Has perdido maldito bastardo");
            juego.setScreen(new PantallaPerder(juego));

        }



        //Aqui es donde el personaje gana

        //if(){
          // juego.setScreen(new PantallaGanar(juego));

        //}



        // Dependiendo de donde esté disparando el jugador, la bala se mueve en esa dirección
        if(actionButton.getClickListener().isPressed()){

            // Si el personaje se est moviendo a la derecha
            if(mario.getEstadoMovimiento() == Personaje.EstadoMovimiento.MOV_DERECHA){
                bulletList.add(new Bullet((int) mario.getX(), (int) mario.getY(), 0));
                Gdx.app.log("render" , "Derecha");
            }
            //Si el personaje se está moviendo hacia la izquierda
            else if (mario.getEstadoMovimiento() == Personaje.EstadoMovimiento.MOV_IZQUIERDA) {
                bulletList.add(new Bullet((int) mario.getX(), (int) mario.getY(), (float)Math.PI));
            }
            // Si el personaje se está moviendo hacia arriba
            else if (mario.getEstadoMovimiento() == Personaje.EstadoMovimiento.MOV_ARRIBA) {
                bulletList.add(new Bullet((int) mario.getX(), (int) mario.getY(), 90 * (float) Math.PI / 180));
            }
            // Si el personaje se está moviendo hacia abajo
            else if (mario.getEstadoMovimiento() == Personaje.EstadoMovimiento.MOV_ABAJO){
                bulletList.add(new Bullet((int) mario.getX(), (int) mario.getY(), -(90 * (float) Math.PI / 180)));
            }
        }


    }

    // Actualiza la posición de la cámara para que el personaje esté en el centro,
    // excepto cuando está en la primera y última parte del mundo.
    private void actualizarCamara() {
        float posX = mario.getX();
        float posY = mario.getY();

        // Si está en la parte 'media'
        if (posX>=ANCHO_CAMARA/2 && posX<=ANCHO_MAPA-ANCHO_CAMARA/2) {
            // El personaje define el centro de la cámara
            camara.position.set((int)posX, camara.position.y, 0);
        } else if (posX>ANCHO_MAPA-ANCHO_CAMARA/2) {    // Si está en la última mitad
            // La cámara se queda a media pantalla antes del fin del mundo  :)
            camara.position.set(ANCHO_MAPA-ANCHO_CAMARA/2, camara.position.y, 0);
        } else if ( posX<ANCHO_CAMARA/2 ) { // La primera mitad
            camara.position.set(ANCHO_CAMARA/2, PantallaMapa.ALTO_CAMARA /2,0);
        }
        camara.update();

        // Si está en la parte 'media'
        if (posY>=ANCHO_CAMARA/2 && posY<=ANCHO_MAPA-ANCHO_CAMARA/2) {
            // El personaje define el centro de la cámara
            camara.position.set((int)posY, camara.position.y, 0);
        } else if (posY>ANCHO_MAPA-ANCHO_CAMARA/2) {    // Si está en la última mitad
            // La cámara se queda a media pantalla antes del fin del mundo  :)
            camara.position.set(ANCHO_MAPA-ANCHO_CAMARA/2, camara.position.y, 0);
        } else if ( posY<ANCHO_CAMARA/2 ) { // La primera mitad
            camara.position.set(ANCHO_CAMARA/2, PantallaMapa.ALTO_CAMARA /2,0);
        }
        camara.update();


    }


    @Override
    public void resize(int width, int height) {
        // A éste método se llama cuando se tiene que hacer más grande o más chica la pantalla.
        vista.update(width, height);
        vistaHUD.update(width, height);
    }

    @Override
    public void pause() {
        // Salir de la aplicación o de la pantalla va a ocacionar que el juego se pause.
        // Un botón de pausa también ocacionaría lo mismo.

    }

    @Override
    public void resume() {
        // Regresar a la aplicación ocacionará que la aplicación se resuma.
        // Un botón de resumir desde el menú de pausa ocacionaría lo mismo.

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        // texturaMario.dispose();
        mapa.dispose();
        escena.dispose();
        musicaFondo.dispose();
        healthContainer.dispose();
        healthBar.dispose();
        // texturaMario.dispose();
        texturaEnemigo.dispose();
        texturaPersonaje.dispose();


    }

}

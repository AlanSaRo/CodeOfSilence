package itesm.mx.asjr;

import com.badlogic.gdx.Game;

/**
 * ADMINISTRADOR DE PANTALLAS
 * Created by AlanJoseph on 06/09/2016.
 */
public class Juego extends Game
{

    @Override
    public void create()
    {

        setScreen(new Silence(this));


    }

}
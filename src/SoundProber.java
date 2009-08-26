import javax.microedition.media.*;
import javax.microedition.media.control.*;
import java.util.*;
import java.io.*;

public class SoundProber implements Runnable
{

    public boolean wav_ok=false;
    public boolean mp3_16kbit_ok=false;
    public boolean mp3_32kbit_ok=false;
    public boolean mp3_64kbit_ok=false;

    public boolean probing_done=false;

    // constructor
    public SoundProber()
    {
	// fire up thread
	new Thread(this).start();
    }

    public boolean probe_sound(String file,String type)
    {
	try {
	    Player probe_player= Manager.createPlayer(getClass().getResourceAsStream(file),"audio/"+ type);
	    probe_player.realize();
	    probe_player.prefetch();
	    probe_player.setLoopCount(1);
	    // TODO check if we need to actually .start() aka play the audio
            probe_player.deallocate();
	    probe_player.close();
	}
	catch (Exception e)  { 
	    return false;
	}	
	return true;
    }

    // thread
    public void run()
    {
	wav_ok=probe_sound("snd.wav","x-wav");
	mp3_16kbit_ok=probe_sound("snd_16kbit.mp3","mp3");
	mp3_32kbit_ok=probe_sound("snd_32kbit.mp3","mp3");
	mp3_64kbit_ok=probe_sound("snd_64kbit.mp3","mp3");
	System.gc();
	probing_done=true;
    }
}
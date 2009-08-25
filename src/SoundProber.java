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

    public SoundProber()
    {
	new Thread(this).start();
    }

    public void run()
    {
	probing_done=true;
    }
}
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

    public String protocol_types="";

    // constructor
    public SoundProber()
    {
	// fire up thread
	new Thread(this).start();
    }

    public boolean probe_sound(String file,String type)
    {
	try {
	    Player probe_player= Manager.createPlayer(getClass().getResourceAsStream(file),type);
	    probe_player.realize();
	    probe_player.prefetch();
	    probe_player.setLoopCount(1);
	    // TODO check if we need to actually .start() aka play the audio
            probe_player.deallocate();
	    probe_player.close();
	}
	catch (Exception e)  { 
	    protocol_types+=e.toString();
	    return false;
	}	
	return true;
    }

    // thread
    public void run()
    {
	String[] mp3_types={"AUDIO/MP3","audio/mp3","AUDIO/MPEG3","audio/mpeg3"};
	String[] wav_types={"AUDIO/X-WAV","audio/x-wav"};

	String mp3_type=null;
	String wav_type=null;

	String types[] = Manager.getSupportedContentTypes(null);
	for (int i = 0; i < types.length; i++) {
	    String protocols[] =Manager.getSupportedProtocols(types[i]);
	    for (int ii = 0; ii < protocols.length; ii++) {
		protocol_types+=types[i]+" : " + protocols[ii] + "\n";
		

		for (int iii=0;iii<mp3_types.length;iii++)
		    if (protocols[ii].equals("file")&&types[i].equals(mp3_types[iii]))
			mp3_type=mp3_types[iii];

		for (int iii=0;iii<wav_types.length;iii++)
		    if (protocols[ii].equals("file")&&types[i].equals(wav_types[iii]))
			wav_type=wav_types[iii];

	    }
	}

	protocol_types+="\nselected mp3 type: "+mp3_type + "\n";
	protocol_types+="selected wav type: "+wav_type + "\n";

	// note: null as type is OK too - then the device detects the type it selves http://discussion.forum.nokia.com/forum/showthread.php?t=90643

	mp3_16kbit_ok=probe_sound("snd_16kbit.mp3",mp3_type);
	mp3_32kbit_ok=probe_sound("snd_32kbit.mp3",mp3_type);
	mp3_64kbit_ok=probe_sound("snd_64kbit.mp3",mp3_type);

	wav_ok=probe_sound("snd.wav",wav_type);
	
	if ((!wav_ok)&&(wav_type!=null))
	    {
	    protocol_types+="reprobing wav with null as type\n";
	    wav_ok=probe_sound("snd.wav",null);
	    }

	if (!wav_ok)
	    {
	    protocol_types+="reprobing wav with audio_basic as type\n";
	    wav_ok=probe_sound("snd.wav","audio/basic");
	    }

	if ((!mp3_32kbit_ok)&&(mp3_type!=null))
	    {
	    protocol_types+="reprobing mp3 with null as type\n";
	    mp3_32kbit_ok=probe_sound("snd_32kbit.mp3",null);
	    }

	if (!mp3_32kbit_ok)
	    {
	    protocol_types+="reprobing mp3 with audio/basic as type\n";
	    mp3_32kbit_ok=probe_sound("snd_32kbit.mp3","audio-basic");
	    }

	System.gc(); // clean up after messing around with big files
	probing_done=true;
    }
}
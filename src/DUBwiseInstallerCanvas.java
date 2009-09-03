/***************************************************************
 *
 * Canvas  of DUBwise Installer
 *                                                           
 * Author:        Marcus -LiGi- Bueschleb
 * Mailto:        LiGi @at@ LiGi DOTT de                    
 * 
 * 
 * this class detects some propertys of the Phone
 *
 ***************************************************************/

import javax.microedition.lcdui.*;
import java.io.*;
import javax.microedition.io.*;
import javax.microedition.rms.*;

public class DUBwiseInstallerCanvas
    extends Canvas
    implements Runnable
{




    String encoding="";
    String platform="";
    String locale="";
    String configuration="";
    String profiles="";
    String hostname="";
    String location_api_version="";
    String sensor_api_version="";
    String bluetooth_api_version="";
  
    String comports="";

    int canvas_width=-1;
    int canvas_height=-1;

    int canvas_full_width=-1;
    int canvas_full_height=-1;

    boolean jsr179=false;
    boolean devicecontrol=false;
    boolean bluetooth=false;
    boolean cldc11=false;
    boolean fileapi=false;

    boolean pointer=false;
    boolean sensorapi=false;
    
    boolean symbian=false;

    int rms_avail;
    long free_mem;
    long total_mem;

    public SoundProber sound_prober;

    public String result_as_url_params()
    {
	return result_as(true);
    }


    public String result_as_text()
    {
	return result_as(false);
    }

    public String result_as(boolean url_format)
    {
	String d="\n";

	if (url_format) d="&";
	return 
	    "encoding="+InstallHelper.Conditional_URL_Encode(encoding, url_format)+d+
	    "platform="+InstallHelper.Conditional_URL_Encode(platform, url_format)+d+
	    "locale="+InstallHelper.Conditional_URL_Encode(locale, url_format)+d+
	    "configuration="+InstallHelper.Conditional_URL_Encode(configuration, url_format)+d+
	    "profiles="+InstallHelper.Conditional_URL_Encode(profiles, url_format)+d+
	    "hostname="+InstallHelper.Conditional_URL_Encode(hostname, url_format)+d+
	    "location_api_version="+InstallHelper.Conditional_URL_Encode(location_api_version, url_format)+d+
	    "sensor_api_version="+InstallHelper.Conditional_URL_Encode(sensor_api_version, url_format)+d+
	    "comports="+InstallHelper.Conditional_URL_Encode(comports, url_format)+d+
	    "canvas_width="+InstallHelper.Conditional_URL_Encode(""+canvas_width, url_format)+d+
	    "canvas_height="+InstallHelper.Conditional_URL_Encode(""+canvas_height, url_format)+d+
	    "canvas_full_width="+InstallHelper.Conditional_URL_Encode(""+canvas_full_width, url_format)+d+
	    "canvas_full_height="+InstallHelper.Conditional_URL_Encode(""+canvas_full_height, url_format)+d+
	    "rms_avail="+InstallHelper.Conditional_URL_Encode(""+rms_avail, url_format)+d+
	    "free_mem="+InstallHelper.Conditional_URL_Encode(""+free_mem, url_format)+d+
	    "total_mem="+InstallHelper.Conditional_URL_Encode(""+total_mem, url_format)+d+
	    "snd_wav="+sound_prober.wav_ok+d+
	    "snd_mp3_16kbit="+sound_prober.mp3_16kbit_ok+d+
	    "snd_mp3_32kbit="+sound_prober.mp3_32kbit_ok+d+
	    "snd_mp3_64kbit="+sound_prober.mp3_64kbit_ok+
	    (symbian?d+"symbian=true":"")+
	    (sensorapi?d+"sensorapi=true":"")+
	    (fileapi?d+"fileapi=true":"")+
	    (cldc11?d+"cldc11=true":"")+
	    (bluetooth?d+"bluetooth=true":"")+
	    (devicecontrol?d+"devicecontrol=true":"")+
	    (jsr179?d+"jsr179=true":"")+d
	    +"protocol_types="+InstallHelper.Conditional_URL_Encode(sound_prober.protocol_types, url_format);

    }


    DUBwiseInstaller root;
    private boolean fs_check=true; // fullscreen check 


    public boolean try_class(String class_name)
    {
	try
	    {
		Class.forName(class_name);
		return true;
	    }

	catch (Exception e)
	    {
		return false;
	    }

    }
    public DUBwiseInstallerCanvas(DUBwiseInstaller _root)

    {
	root=_root;
	sound_prober=new SoundProber();
	new Thread(this).start();
    }


    public void run()
    {

	repaint();
	serviceRepaints();
	
	try
	    {
		Thread.sleep(500);
	    }
	catch (Exception e)
	    {}


	try {
	    RecordStore recStore = RecordStore.openRecordStore("test", true );
	    rms_avail=recStore.getSizeAvailable();
	    }
	catch ( Exception e) {} 
	System.gc();  // to have better results measuring the Mem
	free_mem=Runtime.getRuntime().freeMemory();
	total_mem= Runtime.getRuntime().totalMemory();

	jsr179=try_class("javax.microedition.location.LocationProvider");;
	devicecontrol=try_class("com.nokia.mid.ui.DeviceControl");
	bluetooth=try_class("javax.bluetooth.DiscoveryAgent");
	cldc11=try_class("java.lang.Math");
	fileapi=try_class("javax.microedition.io.file.FileConnection");
	comports=System.getProperty("microedition.commports");
	sensorapi=try_class("javax.microedition.sensor.SensorManager");
	
	encoding=System.getProperty("microedition.encoding");
	platform=System.getProperty("microedition.platform");
	locale=System.getProperty("microedition.locale");
	configuration= System.getProperty("microedition.configuration");
	profiles=System.getProperty("microedition.profiles");
	hostname=System.getProperty("microedition.hostname");
	location_api_version=System.getProperty("microedition.location.version");
	sensor_api_version=System.getProperty("microedition.sensor.version");
	bluetooth_api_version = System.getProperty ("bluetooth.api.version");
	pointer = this.hasPointerEvents();


	// test for symbian src: http://discussion.forum.nokia.com/forum/showthread.php?t=96615
	try { 
	    Class.forName("com.symbian.gcf.NativeInputStream");
	    symbian=true;
	}
	catch (ClassNotFoundException e) { 
	    // stay on false in this case
	}


	root.canvas_hw_detect_finish(); // notify that we are done

    }


    public void paint(Graphics g)
    {

	if (fs_check) try
	    {

		setFullScreenMode(false);
		canvas_width=this.getWidth();
		canvas_height=this.getHeight();

		setFullScreenMode(true);
		canvas_full_width=this.getWidth();
		canvas_full_height=this.getHeight();


	    }
	catch (Exception e)
	    {
		canvas_width=this.getWidth();
		canvas_height=this.getHeight();
		canvas_full_width=this.getWidth();
		canvas_full_height=this.getHeight();

	    }

	fs_check=false;
	g.setColor(0);
	g.fillRect(0,0,canvas_full_width,canvas_full_height);
	g.setColor(0x00BB00);
	g.drawString("Detecting Hardware",0,0,Graphics.LEFT  | Graphics.TOP);

    }
}

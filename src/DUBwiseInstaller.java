/***********************************************************************
 *                                                           
 * DUBwise == Digital UFO Broadcasting with intelligent service equipment
 * main MIDLet Source file
 *                                                           
 * Author:        Marcus -LiGi- Bueschleb
 * Mailto:        LiGi @at@ LiGi DOTT de                    
 *
 ************************************************************************/


import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;



public class DUBwiseInstaller
    extends MIDlet
    implements Runnable,CommandListener
{

    public final static byte VERSION_MINOR = 18;
    public final static byte VERSION_MAJOR = 0;
    
    public final static String VERSION_STR = ""+VERSION_MAJOR+"."+VERSION_MINOR;
    
    public String[] installmethod_strings={"online Install","Download JAR","show Install Code"};
    public String[] installoption_strings={"default","full featured","minimal","custom","expert"};

    public final static byte INSTALLOPTIONID_DEFAULT=0;
    public final static byte INSTALLOPTIONID_FULL=1;
    public final static byte INSTALLOPTIONID_MINIMAL=2;
    public final static byte INSTALLOPTIONID_CUSTOM=3;
    public final static byte INSTALLOPTIONID_EXPERT=4;

    public final static byte INSTALLMETHODID_ONLINE=0;
    public final static byte INSTALLMETHODID_DOWNJAR=1;
    public final static byte INSTALLMETHODID_SHOWCODE=2;
    public final static byte INSTALLMETHODID_SHOWURL=3;

    public Display display;
    public  DUBwiseInstallerCanvas canvas;
    private DUBwiseProps props;


    public boolean app_running=false;  // to prevent 2 instances


    String browser_dest="";


    String helper_url=null;
    String download_url=null;

    boolean http_possible;


    Form installoptions_form;
    Form install_form;
    Form device_info_form;
    Form expert_form;


    ChoiceGroup installmethod_choice;
    ChoiceGroup installoption_choice;

    ChoiceGroup sound_choice;
    ChoiceGroup firmware_choice;

    ChoiceGroup optional_features_choice;

    ChoiceGroup res_choice;
    ChoiceGroup device_features_choice;
    ChoiceGroup installsrc_choice;


    Command ok_command;
    Command back_command;
    Command exit_command;

    String device_code;
    String install_code;

    protected void startApp()
        throws MIDletStateChangeException
    {

       if (app_running)return;
       display  = Display.getDisplay(this);

       canvas=new  DUBwiseInstallerCanvas(this);

       // fire up canvas
       display.setCurrent(canvas);
    }

    public String getJARURL()
    {
	    return download_url+props.installsrc_str()+"/"+props.getJARFileName();
    }

    public String getJADURL()
    {
	    return download_url+props.installsrc_str()+"/"+props.getJADFileName();
    }

    // fire up browser for online install
    public void run()
    {
	System.out.println("trying to get device id");
	device_code=InstallHelper.post_http("http://dubwise-download.appspot.com/device_info",canvas.result_as_url_params());
	System.out.println("got device id:"+device_code);

	if ( device_code=="err")
	    {
		Alert browser_start_alert = new Alert("Error", "Network Error", null, AlertType.ERROR);
		browser_start_alert.setTimeout(Alert.FOREVER);
		display.setCurrent( browser_start_alert );
	    }
	else
	    {
		Alert browser_start_alert = new Alert("Information", "opening browser - please wait", null, AlertType.INFO);
		browser_start_alert.setTimeout(Alert.FOREVER);
		display.setCurrent( browser_start_alert );

		System.out.println("download code:"+props.get_code());
		install_code=InstallHelper.post_http("http://dubwise-download.appspot.com/install_request","device_id="+device_code+"&code="+props.get_code() +"&source="+getAppProperty("DOWNLOAD_SOURCE" ) + "&installer_version="+VERSION_STR + "&installoption="+installoption_strings[installoption_choice.getSelectedIndex()]);
		
		System.out.println("got install id:"+install_code);

		try
		    {
			Thread.sleep(500);
			String browser_url="http://dubwise-download.appspot.com/midlet_download/"+install_code;
			if ((installmethod_choice.getSelectedIndex()==1))
			    browser_url+=".jar";
			else
			    browser_url+=".jad";

			System.out.println("open browser with url: " + browser_url);
			platformRequest(browser_url);
			
		    }
		catch ( Exception e) {} 
	    }
	
	notifyDestroyed(); //quit

    }
    

    public void canvas_hw_detect_finish() 
    { 

	props=new DUBwiseProps();
	props.set_res_by_screensize(canvas.canvas_width,canvas.canvas_height);

	props.bluetooth=canvas.bluetooth;
	props.jsr179=canvas.jsr179;
	props.fileapi=canvas.fileapi;
	props.devicecontrol=canvas.devicecontrol;
	props.cldc11=canvas.cldc11;


	System.out.println("building props");

	// create forms

	install_form = new Form("Install DUBwise");
	installoptions_form=new Form("Install Options");
	expert_form=new Form("Expert Settings");


	// create widgets

	res_choice = new ChoiceGroup(
				     "Resolution (Detected " +canvas.canvas_full_width + "x"+ canvas.canvas_full_height+")",
			Choice.EXCLUSIVE,
			props.res_strings,
			null);

	res_choice.setSelectedIndex(props.res_select,true);
	expert_form.append(res_choice);


	device_features_choice = new ChoiceGroup(
						 "Features ",
			Choice.MULTIPLE,
			props.feature_strings,
			null);


	device_features_choice.setSelectedIndex(0,props.bluetooth);
	device_features_choice.setSelectedIndex(1,props.jsr179);
	device_features_choice.setSelectedIndex(2,props.fileapi);
	device_features_choice.setSelectedIndex(3,props.devicecontrol);
	device_features_choice.setSelectedIndex(4,props.cldc11);

	expert_form.append(device_features_choice);

	System.out.println("install build");
	/* Install form */

	optional_features_choice = new ChoiceGroup(
						 "Optional Features ",
			Choice.MULTIPLE,
			props.optional_feature_strings,
			null);



	//

	installmethod_choice = new ChoiceGroup(
					       "Install Method",
					       Choice.EXCLUSIVE,
					       installmethod_strings,
					       null);



	installoption_choice = new ChoiceGroup (
						"Install Options",
						Choice.EXCLUSIVE,
						installoption_strings,
						null);


	/*	device_features_choice = new ChoiceGroup(
						 "Features ",
			Choice.MULTIPLE,
			feature_strings,
			null);

	*/


	firmware_choice = new ChoiceGroup(
				     "Firmwares:",
			Choice.EXCLUSIVE,
			props.firmware_strings,
			null);

	firmware_choice.setSelectedIndex(props.firmware_select,true);



	sound_choice = new ChoiceGroup(
						 "Sound ",
			Choice.EXCLUSIVE,
			props.sound_strings,
			null);

	sound_choice.setSelectedIndex(props.sound_select,true);

	install_form.append(installmethod_choice);
	install_form.append(installoption_choice);

	installoptions_form.append(optional_features_choice);
	installoptions_form.append(firmware_choice);

	installoptions_form.append(sound_choice);

	ok_command=new Command("OK", Command.OK, 1);
	back_command=new Command("Back", Command.BACK, 2);
	exit_command=new Command("Exit", Command.EXIT, 3);
	
	expert_form.addCommand(ok_command);
	expert_form.addCommand(back_command);

	installoptions_form.addCommand(ok_command);
	installoptions_form.addCommand(back_command);


	install_form.addCommand(ok_command);
	install_form.addCommand(exit_command);

	// set this class as the command listener
	installoptions_form.setCommandListener(this);
	install_form.setCommandListener(this);
	expert_form.setCommandListener(this);


	
	if (!canvas.bluetooth)
	    {
		
		Alert myAlert = new Alert("Warning", "The Bluetooth API (JSR-82) on your Phone was not found. Without this API DUBwise is not very usefull for most users ( You can only connect via TCP/IP or Serial port ). \n If you have a Windows Mobile Phone this issue can be solved by using DUBwise with a diffrent JRE ( PhoneME or J9 ).\n There is Information in the Internet to show you how to do that - just use your preferred search engine.", null, AlertType.ERROR);

		myAlert.setTimeout(Alert.FOREVER);
		
		display.setCurrent( myAlert,install_form );
	    }
	else
	display.setCurrent( install_form );
    }


    public void show_url_form()
    {


	Form url_form = new Form("FileName");
	TextField txtField = new TextField(
				 "FileName", props.getJADFileName() , 250, TextField.ANY);
	url_form.append(txtField);
	url_form.setCommandListener(this);
	url_form.addCommand(ok_command);
	url_form.addCommand(back_command);
	display.setCurrent(url_form);
    }


    public void show_install_code()
    {
	
	Alert myAlert = new Alert("Install Code", "The install code is:" + props.get_code(), null, AlertType.INFO);
	myAlert.setTimeout(Alert.FOREVER);
	display.setCurrent( myAlert,install_form );

    }


    public void process_edit_form()
    {

	// copy form to props
	props.res_select=res_choice.getSelectedIndex();
	props.firmware_select=firmware_choice.getSelectedIndex();
	props.sound_select=sound_choice.getSelectedIndex();
	//	installsrc_select=	installsrc_choice.getSelectedIndex();

	props.bluetooth=	device_features_choice.isSelected(0);
	props.jsr179=device_features_choice.isSelected(1);
	props.fileapi=    	device_features_choice.isSelected(2);
	props.devicecontrol=	device_features_choice.isSelected(3);
	props.cldc11      =	device_features_choice.isSelected(4);

	props.j2memaps = optional_features_choice.isSelected(0);

	switch (installmethod_choice.getSelectedIndex())
	    {
		
	    case INSTALLMETHODID_ONLINE:
	    case INSTALLMETHODID_DOWNJAR:
		// online Install -> fire up browser	
		new Thread(this).start();
		break;

	    case INSTALLMETHODID_SHOWCODE:
		show_install_code();
		break;

	    case INSTALLMETHODID_SHOWURL:
		// show URL -> fire up browser	
		show_url_form();
		break;
	    }
	

    }

    public void commandAction( Command cmd, Displayable dis) 
    {
	switch(cmd.getCommandType())
	    {

	    case Command.BACK:
		if(dis==expert_form)
		    display.setCurrent(installoptions_form);
		else
		    switch(installoption_choice.getSelectedIndex())
			{		    
			case INSTALLOPTIONID_CUSTOM:
			    display.setCurrent(installoptions_form);
			case INSTALLOPTIONID_EXPERT:
			    display.setCurrent(expert_form);
			    break;
			default:
			    display.setCurrent(install_form);
			}
		
		break;


	    case Command.EXIT:
		notifyDestroyed();
		break;

	    case Command.ITEM:
		display.setCurrent(expert_form);
		break;

	    case Command.OK:
		if (dis==install_form) 
		    
		    switch(installoption_choice.getSelectedIndex())
			    {
			    case INSTALLOPTIONID_DEFAULT:
				firmware_choice.setSelectedIndex(0,true);
				if (canvas.sound_prober.mp3_32kbit_ok)
				    sound_choice.setSelectedIndex(1,true);
				else if (canvas.sound_prober.mp3_64kbit_ok)
				    sound_choice.setSelectedIndex(2,true);
				else if (canvas.sound_prober.wav_ok)
				    sound_choice.setSelectedIndex(3,true);
				else
				    sound_choice.setSelectedIndex(0,true);

				process_edit_form();
				break;
			    case INSTALLOPTIONID_MINIMAL:
				firmware_choice.setSelectedIndex(0,true);
				sound_choice.setSelectedIndex(0,true);
				process_edit_form();
				break;

			    case INSTALLOPTIONID_FULL:
				firmware_choice.setSelectedIndex(1,true);
				sound_choice.setSelectedIndex(1,true);
				process_edit_form();
				break;
				

			    case INSTALLOPTIONID_EXPERT:

			        device_info_form= new Form("Device Information");
				device_info_form.append(new StringItem("", canvas.result_as_text(),Item.PLAIN));
				device_info_form.addCommand(ok_command);
				device_info_form.setCommandListener(this);
				display.setCurrent( device_info_form);
				break;
			    case INSTALLOPTIONID_CUSTOM:

				display.setCurrent(installoptions_form);
				break;
			    }
		else  if (dis==installoptions_form) 
		    {

		    switch(installoption_choice.getSelectedIndex())
			    {

			    case INSTALLOPTIONID_CUSTOM:
				process_edit_form();
				break;
			    case INSTALLOPTIONID_EXPERT:
				display.setCurrent(expert_form);
				break;

			    }

		    }
		else  if (dis==expert_form) 
		    process_edit_form();
		else if (dis==device_info_form)
		    display.setCurrent(installoptions_form);
		break;
	    }

    }


    protected void pauseApp()     {}   // not needed right now
    protected void destroyApp(boolean arg0)  {    }



    
}

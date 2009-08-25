/***************************************************************
 *
 * Canvas  of DUBwise Installer
 *                                                           
 * Author:        Marcus -LiGi- Bueschleb
 * Mailto:        LiGi @at@ LiGi DOTT de                    
 * 
 * 
 * defines propertys of the Phone
 *
 ***************************************************************/

public interface DeviceProps
{

    String encoding="";
    String platform="";
    String locale="";
    String config="";
    String profiles="";
    String hostname="";
    String loc_version="";
    String sensor_api_version="";
  


    String comports="";
    String props="";


    int canvas_width=-1;
    int canvas_height=-1;

    int canvas_full_width=-1;
    int canvas_full_height=-1;

    boolean fullscreen=false;
    boolean locationprovider=false;
    boolean devicecontrol=false;
    boolean bluetooth=false;
    boolean cldc11=false;
    boolean fileapi=false;

    boolean sensorapi=false;



    // end  values to detect

}

/* DO NOT EDIT THIS FILE - it is machine generated */
#include <map>
#include <jni.h>
#include <pgrflycapture.h>

#define _MAX_CAMS       32    

#ifdef __cplusplus
extern "C" {
#endif

typedef std::map<FlyCaptureCameraSerialNumber, FlyCaptureContext> Connections;
static Connections connections;

FlyCapturePixelFormat convertFormatIndex(unsigned int index) {
    switch(index) {
        case 0: // LUMINACE_8
            return FLYCAPTURE_MONO8;
        case 1: // LUMINACE_16
            return FLYCAPTURE_MONO16;
        case 2: // RED_8_GREEN_8_BLUE_8
            return FLYCAPTURE_RGB8;
        default:
            return (FlyCapturePixelFormat) 0x00000001;
    }
}

    
JNIEXPORT jint JNICALL Java_de_telekom_laboratories_capture_spi_FlyCaptureNative_deviceHandles
  (JNIEnv *env, jclass type)
{
    FlyCaptureError   error;
    
    FlyCaptureInfoEx  arInfo[ _MAX_CAMS ];
    unsigned int	     uiSize = _MAX_CAMS;      
    
   if(flycaptureBusEnumerateCamerasEx( arInfo, &uiSize ) != FLYCAPTURE_OK) {
       return (jint) 0L;
   } else {
       return (jint) uiSize;
   }
}
    
JNIEXPORT jlong JNICALL Java_de_telekom_laboratories_capture_spi_FlyCaptureNative_createDeviceHandle
    (JNIEnv *env, jclass type, jint index)
{
    FlyCaptureInfoEx  arInfo[ _MAX_CAMS ];
    unsigned int      uiSize = _MAX_CAMS;          
    
   if(flycaptureBusEnumerateCamerasEx( arInfo, &uiSize ) != FLYCAPTURE_OK) {
       printf("ERROR: flyCapture driver problem!");
       return (jlong) 0L;
   }   
    
   if(index < 0 || index >= uiSize) {
       //printf("ERROR: %d < %d", uiSize, index);
       return (jlong) uiSize;
   }
    
    return (jlong) (arInfo + index)->SerialNumber;
}

JNIEXPORT void JNICALL Java_de_telekom_laboratories_capture_spi_FlyCaptureNative_releaseDeviceHandle
  (JNIEnv *env, jclass type, jlong sNumber)
{
    const FlyCaptureCameraSerialNumber serial = (FlyCaptureCameraSerialNumber) sNumber;
    
    // disconnect devices, if connected
    const Connections::iterator connection = connections.find(serial);
    if(connection != connections.end()) {
        FlyCaptureContext context = connection->second;
        flycaptureStop( context );
        flycaptureDestroyContext( context );        
        connections.erase(serial);
    }
}



JNIEXPORT jboolean JNICALL Java_de_telekom_laboratories_capture_spi_FlyCaptureNative_connect
  (JNIEnv *env, jclass type, jlong sNumber, jobject videoMode)
{
    const FlyCaptureCameraSerialNumber serial = (FlyCaptureCameraSerialNumber) sNumber;
      
    // allow only conncection per camera
    const Connections::iterator connection = connections.find(serial);
    if(connection != connections.end()) {
        return (jboolean) false;
    }

    FlyCaptureContext context;
    if(flycaptureCreateContext( &context ) != FLYCAPTURE_OK) {
        return (jboolean) false;
    }
    
    
    
    if(flycaptureInitializeFromSerialNumber(context, serial) != FLYCAPTURE_OK) {
        flycaptureDestroyContext( context );
        return (jboolean) false;
    }
        
    jclass videomodeType = env->GetObjectClass(videoMode);
    
    //jmethodID xID      = env->GetMethodID(videomodeType, "getX", "()I");
    //jmethodID yID      = env->GetMethodID(videomodeType, "getY", "()I");    
    jmethodID widthID  = env->GetMethodID(videomodeType, "getWidth", "()I");
    jmethodID heightID = env->GetMethodID(videomodeType, "getHeight", "()I");
    jmethodID foramtID = env->GetMethodID(videomodeType, "getFormat", "()Lde/telekom/laboratories/capture/VideoMode$Format;");
        
    //unsigned int x      = env->CallIntMethod(videoMode, xID);
    //unsigned int y      = env->CallIntMethod(videoMode, yID);
    unsigned int width  = env->CallIntMethod(videoMode, widthID);
    unsigned int height = env->CallIntMethod(videoMode, heightID); 
    
    jobject   formatObj  = env->CallObjectMethod(videoMode, foramtID);
    jclass    formatType = env->GetObjectClass(formatObj);
    
    jmethodID ordinalID = env->GetMethodID(formatType, "ordinal", "()I");
    unsigned int ordinal = env->CallIntMethod(formatObj, ordinalID);
    
    FlyCapturePixelFormat format = convertFormatIndex(ordinal);
    
//    int mode;
//    for(mode=0; mode<=7; mode++) {
//        
//       bool		  bAvailable;
//       unsigned int	  uiMaxImageSizeCols;
//       unsigned int	  uiMaxImageSizeRows;
//       unsigned int	  uiUnitSizeHorz;
//       unsigned int	  uiUnitSizeVert;
//       unsigned int       uiPixelFormats;
//       
//       const int status = flycaptureQueryCustomImage(context, mode, 
//                                    &bAvailable,
//                                    &uiMaxImageSizeCols,
//                                    &uiMaxImageSizeRows,
//                                    &uiUnitSizeHorz,
//                                    &uiUnitSizeVert,
//                                    &uiPixelFormats);
//        if(status != FLYCAPTURE_OK) {
//            flycaptureDestroyContext( context );
//            return (jboolean) false;
//        }
//        if(!bAvailable) {
//            continue;
//        } else if((x % uiUnitSizeHorz) || (y % uiUnitSizeVert) || (width % uiUnitSizeHorz) || (height % uiUnitSizeVert)) {
//            //printf("%d %d\n", uiUnitSizeHorz, uiUnitSizeVert);
//            continue;
//        } else if((x+width) > uiMaxImageSizeCols || (y+height) > uiMaxImageSizeRows) {
//            printf("%d %d <-> %d %d\n", uiMaxImageSizeCols, uiMaxImageSizeRows, (x+width), (y+height));
//            continue;
//        } else if((format & uiPixelFormats) == 0) {
//            continue;
//        }
//       
//        if(flycaptureStartCustomImage(context, mode, x, y, width, height, 100.0f, format) != FLYCAPTURE_OK) {                    
//            //printf("cannot start");
//            continue;
//        } else {
//            //printf("x: %d, y: %d, width: %d, height: %d\n", x, y, width, height);
//            connections[serial] = context;      
//            return (jboolean) true;
//        }       
//    }
    
    if(format != FLYCAPTURE_MONO8) {
        flycaptureDestroyContext( context );
        return (jboolean) false;
    }
    
    FlyCaptureVideoMode camera1VideoMode;
    if(width == 640 && height == 480) {
        camera1VideoMode = FLYCAPTURE_VIDEOMODE_640x480Y8;
    } else if(width == 1024 && height == 768) {
        camera1VideoMode = FLYCAPTURE_VIDEOMODE_1024x768Y8;
    } else {
        flycaptureDestroyContext( context );                
        return (jboolean) false;
    }
    
    bool supported = false;
    FlyCaptureFrameRate frameRate;
    
    if( flycaptureCheckVideoMode(context, camera1VideoMode, FLYCAPTURE_FRAMERATE_30, &supported ) != FLYCAPTURE_OK) {
        flycaptureDestroyContext( context );        
        return (jboolean) false;
    } 
    if(supported) {
        frameRate = FLYCAPTURE_FRAMERATE_30;
    } else {
        if( !supported && (flycaptureCheckVideoMode(context, camera1VideoMode, FLYCAPTURE_FRAMERATE_15, &supported ) != FLYCAPTURE_OK)) {
            flycaptureDestroyContext( context );        
            return (jboolean) false;
        } else {
            frameRate = FLYCAPTURE_FRAMERATE_15;
        }     
    }
    
    
//    FlyCaptureVideoMode vMode = FLYCAPTURE_VIDEOMODE_ANY;
//    FlyCaptureFrameRate fRate = FLYCAPTURE_FRAMERATE_ANY;    
//    if(flycaptureGetCurrentVideoMode(context, &vMode, &fRate) == FLYCAPTURE_OK) {
//        printf("VideoMode: %d + FrameRate: %d\n", vMode, fRate);
//    } else {
//        flycaptureDestroyContext( context );        
//        return (jboolean) false;        
//    }        

    
    if(flycaptureStart(context, camera1VideoMode, frameRate ) == FLYCAPTURE_OK)
    {         
        connections[serial] = context;        
        return (jboolean) true;
    }               
    
//    FlyCaptureVideoMode camera1VideoMode = FLYCAPTURE_VIDEOMODE_ANY;
//    FlyCaptureFrameRate frameRate = FLYCAPTURE_FRAMERATE_ANY;    
//    if(flycaptureStart(context, camera1VideoMode, frameRate ) == FLYCAPTURE_OK)
//        if(flycaptureGetCurrentVideoMode(context, &camera1VideoMode, &frameRate) != FLYCAPTURE_OK) {
//            flycaptureStop( context );
//            flycaptureDestroyContext( context );
//        } else {
//            jmethodID setWidthID  = env->GetMethodID(videomodeType, "setWidth", "(I)");
//            jmethodID setHeightID = env->GetMethodID(videomodeType, "setHeight", "(I)");
////            jmethodID setForamtID = env->GetMethodID(videomodeType, "setFormat", "(Lde/telekom/laboratories/capture/VideoMode$Format;)");            
//            
//            env->CallVoidMethod(videoMode, setWidthID, 
//            env->CallVoidMethod(videoMode, setHeightID, 
//        }
//        connections[serial] = context;        
//        return (jboolean) true;
//    }        
    
    flycaptureDestroyContext( context );
    return (jboolean) false;
}

JNIEXPORT void JNICALL Java_de_telekom_laboratories_capture_spi_FlyCaptureNative_disconnect
  (JNIEnv *env, jclass type, jlong sNumber)
{
    const FlyCaptureCameraSerialNumber serial = (FlyCaptureCameraSerialNumber) sNumber;
    
    // not connected: return
    const Connections::iterator connection = connections.find(serial);
    if(connection == connections.end()) {
        return;
    }

    FlyCaptureContext context = connection->second;
        
    flycaptureStop( context );
    flycaptureDestroyContext( context );
    
    connections.erase(serial);

}

JNIEXPORT jboolean JNICALL Java_de_telekom_laboratories_capture_spi_FlyCaptureNative_capture
  (JNIEnv *env, jclass type, jlong sNumber, jobject buffer)
{
    const FlyCaptureCameraSerialNumber serial = (FlyCaptureCameraSerialNumber) sNumber;
    
    const Connections::iterator connection = connections.find(serial);
    if(connection == connections.end()) {
        return (jboolean) false;
    }

    FlyCaptureContext context = connection->second;    
    
    FlyCaptureImage image;
    memset( &image, 0x0, sizeof( FlyCaptureImage ) );
    if(flycaptureGrabImage2( context, &image ) != FLYCAPTURE_OK) {
       return (jboolean) false;
    }
    
    jlong capacity = env->GetDirectBufferCapacity(buffer);    
    unsigned int sizeInBytes = image.iRows * image.iRowInc;    
        
    // shoudl never happen, but who knows
    if(capacity < sizeInBytes) {
        //printf("cap: %d, size: %d\n", capacity, sizeInBytes);
        return (jboolean) false;
    }
    
    unsigned char* address = (unsigned char*) env->GetDirectBufferAddress(buffer);
    memcpy(address, image.pData, sizeInBytes*sizeof(unsigned char));
        
    return (jboolean) true;
   
}

#ifdef __cplusplus
}
#endif

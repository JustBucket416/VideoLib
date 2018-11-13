// IMediaAidlInterface.aidl
package justbucket.videolib;

// Declare any non-default types here with import statements

interface IMediaAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    /*void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);*/

            void changePlayState(boolean play);

            void playAudio(String audio);

            void seekTo(int millis);

            void stopService();
}

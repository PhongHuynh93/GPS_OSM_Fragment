package dhbk.android.gps_osm_fragment.Voice;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.CountDownTimer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by huynhducthanhphong on 4/21/16.
 */
public class VIetnameseSpeak {
    private final ArrayList mMp3List;
    ArrayList<CountDownTimer> cdTimerList;

    public VIetnameseSpeak(Context context, String speakString) {
        mMp3List = new ArrayList();
        ArrayList<String> arrayStringOfSpeakString = getArrayStringOfSpeakString(speakString);

        for (int i = 0; i < arrayStringOfSpeakString.size(); i++) {
            try {
                AssetFileDescriptor afd = context.getAssets().openFd("audio/" + ((String) arrayStringOfSpeakString.get(i)) + ".mp3");
                MediaPlayer mp = new MediaPlayer();
                mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mp.prepare();
                mMp3List.add(mp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // split string into word.
    private ArrayList<String> getArrayStringOfSpeakString(String speakString) {
        ArrayList<String> arrayStringOfSpeakString = new ArrayList<>();
        String[] words = speakString.split(" ");
        for ( String word : words) {
            arrayStringOfSpeakString.add(word);
        }
        return arrayStringOfSpeakString;
    }

    // doc tung chu trong chuoi
    public void speak() {
        this.cdTimerList = new ArrayList();
        for (int i = this.mMp3List.size() - 1; i >= 0; i--) {
            int j = i;
            this.cdTimerList.add(new C00551((long) ((MediaPlayer) this.mMp3List.get(j)).getDuration(), 10, j));
        }
        ((CountDownTimer) this.cdTimerList.get(this.cdTimerList.size() - 1)).start();
    }

    class C00551 extends CountDownTimer {
        private final /* synthetic */ int val$j;

        C00551(long $anonymous0, long $anonymous1, int i) {
            super($anonymous0, $anonymous1);
            this.val$j = i;
        }

        public void onTick(long millisUntilFinished) {
            ((MediaPlayer) VIetnameseSpeak.this.mMp3List.get(this.val$j)).start();
        }

        public void onFinish() {
            ((MediaPlayer) VIetnameseSpeak.this.mMp3List.get(this.val$j)).stop();
            if (this.val$j < VIetnameseSpeak.this.mMp3List.size() - 1) {
                ((CountDownTimer) VIetnameseSpeak.this.cdTimerList.get((VIetnameseSpeak.this.mMp3List.size() - 2) - this.val$j)).start();
            }
        }
    }


}

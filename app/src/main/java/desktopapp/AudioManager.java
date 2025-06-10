package desktopapp;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AudioManager {

    private static MediaPlayer mainMusicPlayer;
    private static AudioClip clickSfx;
    private static List<AudioClip> typingSfxList;
    private static AudioClip wrongSfx;
    private static AudioClip correctSfx;
    private static AudioClip completeSfx;

    private static boolean isMusicGloballyEnabled = true;
    private static boolean isSoundEffectsGloballyEnabled = true;

    private static final double DEFAULT_MUSIC_VOLUME = 0.8;
    private static final Random random = new Random();
    private static int typingSoundIndex = 0;

    public static void loadAudio() {
        System.out.println("AudioManager: Loading audio assets...");
        try {
            URL musicResource = AudioManager.class.getResource("/musik_sfx/main-music.wav");
            if (musicResource != null) {
                Media media = new Media(musicResource.toExternalForm());
                mainMusicPlayer = new MediaPlayer(media);
                mainMusicPlayer.setVolume(DEFAULT_MUSIC_VOLUME);
                mainMusicPlayer.setOnEndOfMedia(() -> mainMusicPlayer.seek(Duration.ZERO));
                System.out.println("AudioManager: main-music.wav loaded.");
            } else {
                System.err.println("AudioManager Error: main-music.wav not found in /musik_sfx/");
            }
            clickSfx = loadAudioClip("/musik_sfx/click.mp3", "click.mp3");

            typingSfxList = new ArrayList<>();
            for (int i = 1; i <= 6; i++) {
                AudioClip clip = loadAudioClip("/musik_sfx/sound-" + i + ".mp3", "sound-" + i + ".mp3");
                if (clip != null) {
                    typingSfxList.add(clip);
                }
            }
            if (typingSfxList.isEmpty()) {
                 System.err.println("AudioManager Error: No typing sounds (sound-1.mp3 to sound-6.mp3) were loaded.");
            } else {
                System.out.println("AudioManager: " + typingSfxList.size() + " typing SFX loaded.");
            }


            wrongSfx = loadAudioClip("/musik_sfx/wrong-sfx.mp3", "wrong-sfx.mp3");
            correctSfx = loadAudioClip("/musik_sfx/correct-sfx.mp3", "correct-sfx.mp3");
            completeSfx = loadAudioClip("/musik_sfx/complete_typing.mp3", "complete_typing.mp3");

        } catch (Exception e) {
            System.err.println("AudioManager Error: Exception during audio loading: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("AudioManager: Audio asset loading complete.");
    }

    private static AudioClip loadAudioClip(String path, String fileName) {
        try {
            URL resource = AudioManager.class.getResource(path);
            if (resource != null) {
                System.out.println("AudioManager: Loading " + fileName);
                return new AudioClip(resource.toExternalForm());
            } else {
                System.err.println("AudioManager Error: " + fileName + " not found at " + path);
            }
        } catch (Exception e) {
            System.err.println("AudioManager Error: Could not load " + fileName + ": " + e.getMessage());
        }
        return null;
    }

    // --- Kontrol Musik Utama ---
    public static void playMainMusic() {
        if (mainMusicPlayer != null && isMusicGloballyEnabled) {
            if (mainMusicPlayer.getStatus() == MediaPlayer.Status.PLAYING) { 
            } else {
                 mainMusicPlayer.play();
            }
            System.out.println("AudioManager: Main music playing (Volume: " + mainMusicPlayer.getVolume() + ")");
        } else if (mainMusicPlayer == null) {
            System.err.println("AudioManager Error: Main music player not loaded, cannot play.");
        }
    }

    public static void pauseMainMusic() {
        if (mainMusicPlayer != null && mainMusicPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mainMusicPlayer.pause();
            System.out.println("AudioManager: Main music paused.");
        }
    }
    
    public static void stopMainMusic() {
        if (mainMusicPlayer != null) {
            mainMusicPlayer.stop();
            System.out.println("AudioManager: Main music stopped.");
        }
    }

    public static void setMusicEnabled(boolean enabled) {
        isMusicGloballyEnabled = enabled;
        if (mainMusicPlayer != null) {
            if (enabled) {
                playMainMusic();
            } else {
                pauseMainMusic();
            }
        }
        System.out.println("AudioManager: Music globally " + (enabled ? "enabled" : "disabled"));
    }

    public static boolean isMusicEnabled() {
        return isMusicGloballyEnabled;
    }

    // --- Kontrol Efek Suara ---
    public static void setSoundEffectsEnabled(boolean enabled) {
        isSoundEffectsGloballyEnabled = enabled;
        System.out.println("AudioManager: Sound effects globally " + (enabled ? "enabled" : "disabled"));
    }
     public static boolean isSoundEffectsEnabled() {
        return isSoundEffectsGloballyEnabled;
    }

    public static void playClickSound() {
        if (clickSfx != null && isSoundEffectsGloballyEnabled) {
            clickSfx.play();
        }
    }

    public static void playTypingSound() {
        if (typingSfxList != null && !typingSfxList.isEmpty() && isSoundEffectsGloballyEnabled) {
            // Putar secara berurutan atau acak
            AudioClip clipToPlay = typingSfxList.get(random.nextInt(typingSfxList.size()));
            // AudioClip clipToPlay = typingSfxList.get(typingSoundIndex % typingSfxList.size()); // Berurutan
            clipToPlay.play();
            typingSoundIndex++;
        }
    }

    public static void playWrongSound() {
        if (wrongSfx != null && isSoundEffectsGloballyEnabled) {
            wrongSfx.play();
        }
    }
    
    public static void playCorrectSound() {
        if (correctSfx != null && isSoundEffectsGloballyEnabled) {
            correctSfx.play();
        }
    }

    public static void playCompleteSound() {
        if (completeSfx != null && isSoundEffectsGloballyEnabled) {
            completeSfx.play();
        }
    }
}

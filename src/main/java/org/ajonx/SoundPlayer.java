package org.ajonx;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.InputStream;

public class SoundPlayer {
	public static void move() {
		play("/sounds/move.wav");
	}

	public static void take() {
		play("/sounds/capture.wav");
	}

	public static void check() {
		play("/sounds/check.wav");
	}

	public static void castle() {
		play("/sounds/castle.wav");
	}

	public static void gameEnd() {
		play("/sounds/game-end.wav");
	}

	public static void play(String filepath) {
		new Thread(() -> {
			try (InputStream is = SoundPlayer.class.getResourceAsStream(filepath)) {
				if (is != null) {
					AudioInputStream audioIn = AudioSystem.getAudioInputStream(is);
					Clip clip = AudioSystem.getClip();
					clip.open(audioIn);
					clip.start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
}
package net.namekdev.theconsole.utils;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.SourceDataLine;

/**
 * Usage: <pre><code>
 * final AudioFilePlayer player = new AudioFilePlayer();
 * player.play("something.mp3");
 * player.play("something.ogg");
 * </code>
 * </pre>
 */
public class AudioFilePlayer {
	private final byte[] buffer = new byte[65536];
	private ExecutorService executor = Executors.newSingleThreadExecutor();


	public void playSync(String filePath) {
		final File file = new File(filePath);

		try (final AudioInputStream in = getAudioInputStream(file)) {
			final AudioFormat outFormat = getOutFormat(in.getFormat());
			final Info info = new Info(SourceDataLine.class, outFormat);

			try (final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info)) {
				if (line != null) {
					line.open(outFormat);
					line.start();
					stream(getAudioInputStream(outFormat, in), line);
					line.drain();
					line.stop();
				}
			}
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public void play(String filePath) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				playSync(filePath);
			}
		});
	}

	private AudioFormat getOutFormat(AudioFormat inFormat) {
		final int ch = inFormat.getChannels();
		final float rate = inFormat.getSampleRate();
		return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
	}

	private void stream(AudioInputStream in, SourceDataLine line) throws IOException {
		for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
			line.write(buffer, 0, n);
		}
	}
}
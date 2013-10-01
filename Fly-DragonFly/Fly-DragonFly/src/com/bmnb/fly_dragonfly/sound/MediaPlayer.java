package com.bmnb.fly_dragonfly.sound;



import java.util.Hashtable;
import java.util.Vector;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.bmnb.fly_dragonfly.tools.Pair;

/**
 * Media player class
 * @author benjamin
 *
 */
public class MediaPlayer {
	private static boolean soundOn = true;
	private static Hashtable<String,Pair<Music,Float>> allMusic = new Hashtable<String,Pair<Music,Float>>();
	private static Hashtable<String,Sound> allSound = new Hashtable<String,Sound>();
	private static Hashtable<Sound,Vector<Pair<Long,Float>>> allSndInstances = new Hashtable<Sound,Vector<Pair<Long,Float>>>(); 
	/**
	 * Loads a sound given a file name (if it has been loaded already the call is ignored)
	 * @param filename
	 */
	public static void loadSound(String filename){
		if (!allSound.containsKey(filename)){
			Sound snd = Gdx.audio.newSound(Gdx.files.internal(filename));
			allSound.put(filename, snd);
			allSndInstances.put(snd, new Vector<Pair<Long,Float>>());
		}
			
	}
	/**
	 * Loads music given a file name (if it has been loaded already the call is ignored)
	 * @param filename
	 */
	public static void loadMusic(String filename){
		if (!allMusic.containsKey(filename))
			allMusic.put(filename,new Pair<Music,Float>(Gdx.audio.newMusic(Gdx.files.internal(filename)),1.0f));
	}
	/**
	 * Plays preloaded music (if the file has not been loaded the call is ignored)
	 * @param filename
	 * @param loop should the file be looped
	 */
	public static void playMusic(String filename, boolean loop){
		if (allMusic.containsKey(filename)){
			allMusic.get(filename).getVal1().play();
			allMusic.get(filename).getVal1().setLooping(loop);
			if (!soundOn) setMusicVolume(filename, 0);
		}
	}
	/**
	 * Plays preloaded sound fx (if the sound is not on or the file has not been loaded the call is ignored)
	 * @param filename 
	 * @return handle to the instance of the sound fx. -1 if the call did not complete
	 */
	public static long playSound(String filename){
		if (allSound.containsKey(filename)){
			long hnd = 0;
			hnd = allSound.get(filename).play(1.0f);
			allSndInstances.get(allSound.get(filename)).add(new Pair<Long,Float>(hnd,1.0f));
			if (!soundOn) setSoundVolume(filename, 0, hnd);
			return hnd;
		}
		return -1;
	}
	/**
	 * stops playing music. if the music is not playing or not loaded then the call is ignored
	 * @param filename
	 */
	public static void stopMusic(String filename){
		if (allMusic.containsKey(filename)){
			if (allMusic.get(filename).getVal1().isPlaying())
				allMusic.get(filename).getVal1().stop();
		}
	}
	/**
	 * stops a sound instance. if the sound instance has stopped already or does not exist the call is ignored
	 * @param filename
	 * @param instanceHandle handle to the sound fx instance
	 */
	public static void stopSound(String filename, long instanceHandle){
		if (allSound.containsKey(filename)){
			allSound.get(filename).stop(instanceHandle);
		}
	}
	/**
	 * Stops all sound instances
	 */
	public static void stopAllSoundInstances(){
		for (String s:allSound.keySet())
			allSound.get(s).stop();
	}
	public static void disposeInstances(){
		for (String s:allSound.keySet())
			allSound.get(s).dispose();
		for (String s:allMusic.keySet()){
			allMusic.get(s).getVal1().dispose();
		}
		allSound.clear();
		allMusic.clear();
		allSndInstances.clear();
	}
	/**
	 * pauses the music file if it is playing. If not the call is ignored
	 * @param filename
	 */
	public static void pauseMusic(String filename){
		if (allMusic.containsKey(filename)){
			if (allMusic.get(filename).getVal1().isPlaying())
				allMusic.get(filename).getVal1().pause();
		}
	}
	/**
	 * resumes playing music. if the music was not paused the track will resume from the start
	 * @param filename
	 */
	public static void resumeMusic(String filename){
		if (allMusic.containsKey(filename)){
			allMusic.get(filename).getVal1().play();
			if (!soundOn) setMusicVolume(filename, 0);
		}
	}
	/**
	 * sets sound fx instance volume. if the sound is not loaded the call is ignored
	 * @param filename
	 * @param volume
	 * @param sndHandle instance of the sound
	 */
	public static void setSoundVolume(String filename, float volume, long sndHandle){
		if (allSound.containsKey(filename)){
			for (Pair<Long,Float> p : allSndInstances.get(allSound.get(filename)))
				if(p.getVal1().longValue() == sndHandle){
					p.setVal2(volume);
					break;
				}
			allSound.get(filename).setVolume(sndHandle, soundOn ? volume : 0);
		}
	}
	/**
	 * sets music volume. if the music is not loaded the call is ignored
	 * @param filename
	 * @param volume
	 */
	public static void setMusicVolume(String filename, float volume){
		if (allMusic.containsKey(filename)){
			allMusic.get(filename).setVal2(volume);
			allMusic.get(filename).getVal1().setVolume(soundOn ? volume : 0);
		}
	}
	/**
	 * true if the sound is on, false otherwise
	 * @return
	 */
	public static boolean isSoundOn() {
		return soundOn;
	}
	/**
	 * Activates/deactivates the sound and music within the game
	 * @param soundOn
	 */
	public static void setSoundOn(boolean soundOn) {
		MediaPlayer.soundOn = soundOn;
		if (!soundOn){ //mute volume (but do not save to volume stores
			for (String s: allSound.keySet()){
				for (Pair<Long,Float> p : allSndInstances.get(allSound.get(s))){
					allSound.get(s).setVolume(p.getVal1().longValue(), 0);
				}
			}
			for (String m: allMusic.keySet())
				allMusic.get(m).getVal1().setVolume(0);
		} else { //restore to previous volume
			for (String s: allSound.keySet()){
				for (Pair<Long,Float> p : allSndInstances.get(allSound.get(s))){
					allSound.get(s).setVolume(p.getVal1().longValue(), p.getVal2().floatValue());
				}
			}
			for (String m: allMusic.keySet())
				allMusic.get(m).getVal1().setVolume(allMusic.get(m).getVal2());
		}
	}
}

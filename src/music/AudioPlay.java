package music;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

import org.tritonus.share.sampled.file.TAudioFileFormat;

import javazoom.jl.player.Player;

class AudioPlay{
	private Player player;
	private FileInputStream FIS;
	private BufferedInputStream BIS;
	private boolean canResume;
	private static String path;
	private int total;
	private int stopped;
	private boolean valid;
	private int miliDuration = 0;

	public AudioPlay(){
	    player = null;
	    FIS = null;
	    valid = false;
	    BIS = null;
	    path = null;
	    total = 0;
	    stopped = 0;
	    canResume = false;
	}

	public boolean canResume(){
	    return canResume;
	}
	
	public void setPath(String path){
	    AudioPlay.path = path;
	}
	
	public String getDuration() throws Exception {
		int min = 0;
		int sec = 0;
		File file = new File(path);
		AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
	    if (fileFormat instanceof TAudioFileFormat) {
	        Map<?, ?> properties = ((TAudioFileFormat) fileFormat).properties();
	        String key = "duration";
	        Long microseconds = (Long) properties.get(key);
	        miliDuration = (int) (microseconds / 1000);
	        sec = (miliDuration / 1000) % 60;
	        min = (miliDuration / 1000) / 60;
	    } else {
	        throw new UnsupportedAudioFileException();
	    }
	    
	    return ((min <= 9)?"0"+min:min) + ":" + ((sec <= 9)?"0"+sec:sec);
	}
	
	public int getDurationSecon(){
		return (miliDuration / 1000);
	}
	
	public String getDurationDynamic(int position){
		int mili = 0, sec = 0, min = 0;
		mili = (miliDuration / 1000) - position;
		sec = mili % 60;
        min = mili / 60;
        if(mili < 0){
        	return "00:00";
        }else{
        	return ((min <= 9)?"0"+min:min) + ":" + ((sec <= 9)?"0"+sec:sec);
        }
	}
	
	public String getTitle(){
		String path = AudioPlay.path;
		String[] titles = path.split("/");
		int jumlah = titles.length;
		return titles[jumlah-1];
	}

	public void pause(){
	    try{
	    stopped = FIS.available();
	    player.close();
	    FIS = null;
	    BIS = null;
	    player = null;
	    if(valid) canResume = true;
	    }catch(Exception e){

	    }
	}

	public void resume(){
	    if(!canResume) return;
	    if(play(total-stopped)) canResume = false;
	}
	
	public void stop(){
		player.close();
	}

	public boolean play(int pos){
	    valid = true;
	    canResume = false;
	    try{
	    FIS = new FileInputStream(path);
	    total = FIS.available();
	    if(pos > -1) FIS.skip(pos);
	    BIS = new BufferedInputStream(FIS);
	    player = new Player(BIS);
	    new Thread(
	            new Runnable(){
	                public void run(){
	                    try{
	                        player.play();
	                    }catch(Exception e){
	                        JOptionPane.showMessageDialog(null, "Error playing mp3 file");
	                        valid = false;
	                    }
	                }
	            }
	    ).start();
	    }catch(Exception e){
	        JOptionPane.showMessageDialog(null, "Error playing mp3 file");
	        valid = false;
	    }
	    return valid;
	}
}
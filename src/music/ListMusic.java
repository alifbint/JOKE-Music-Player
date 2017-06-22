package music;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

class ListMusic {
	
	private static String listMusic;
	
	ListMusic(String nama) {
		ListMusic.listMusic = nama;
		
		File fileLagu = new File(nama);
		try {
			if(!fileLagu.exists()){
				fileLagu.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> read() throws IOException{
	    ArrayList<String> daftar = new ArrayList<String>();
	    String line = null;
	    File music = new File(listMusic);
	    FileInputStream listStream = new FileInputStream(music);
	    BufferedReader readLagu = new BufferedReader(new InputStreamReader(listStream));
	    
	    while((line = readLagu.readLine()) != null){
	    	daftar.add(line);
	    }
	    
	    readLagu.close();
	    return daftar;
	}
	
	public void write(String str) throws IOException{
	        File fileLagu = new File(listMusic);
	        FileWriter fileWritter = new FileWriter(fileLagu.getName(),true);
	        BufferedWriter write = new BufferedWriter(fileWritter);
	        
	        write.write(str + "\n");
	        write.close();
	}
	
	public void clear(){
		File fileLagu = new File(listMusic);
		try {
			PrintWriter writer = new PrintWriter(fileLagu);
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}

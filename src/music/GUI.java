package music;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

class GUI {
	private JFrame frame;
	private static AudioPlay player; 
	private int valuePositionSlider = 0;
	private static Timer animateJudul = null;
	private static Timer sliderAnimate = null;
	private static ListMusic listMusic = new ListMusic("laguku.aku");
	private static Map<Integer, String> daftarTmpLagu = new HashMap<Integer, String>();
	private static DefaultListModel<String> listModel = new DefaultListModel<String>();
	
	public GUI() {
		initialize();
		updateListLagu();
	}
	
	public static void runGUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void updateListLagu(){
		daftarTmpLagu.clear();
		listModel.clear();
		
		try {
			int i = 0;
			for(String lagu : listMusic.read()){
				String[] laguArr = lagu.split("/");
				int jumlah = laguArr.length;
				listModel.add(i, laguArr[jumlah-1]);
				daftarTmpLagu.put(i, lagu);
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initialize() {
		frame = new JFrame("JOKE Music Player");
		frame.setBounds(100, 100, 600, 320);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		final JLabel lblJudulLagu = new JLabel("Artist - Song Title");
		lblJudulLagu.setBounds(147, 11, 400, 23);
		frame.getContentPane().add(lblJudulLagu);
		
		final JLabel lblPlayList = new JLabel("Play List");
		lblPlayList.setBounds(10, 75, 400, 23);
		frame.getContentPane().add(lblPlayList);
		
		final JLabel lblDurasiLagu = new JLabel("00:00");
		lblDurasiLagu.setBounds(528, 38, 46, 26);
		frame.getContentPane().add(lblDurasiLagu);
		
		JScrollPane listScrollPane = new JScrollPane();
		final JList<String> list = new JList<String>(listModel);
		listScrollPane.setViewportView(list);
		listScrollPane.setBounds(10, 100, 560, 140);
		frame.getContentPane().add(listScrollPane);
		
		final JSlider slider = new JSlider();
		slider.setBounds(185, 40, 340, 26);
		slider.setEnabled(false);
		slider.setMaximum(100);
		slider.setValue(0);
		frame.getContentPane().add(slider);
		
		final JLabel lblRunningLagu = new JLabel("00:00");
		lblRunningLagu.setBounds(147, 38, 46, 26);
		frame.getContentPane().add(lblRunningLagu);
		
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Browse File Music");
		fc.setCurrentDirectory(new java.io.File("."));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("File Music", "mp3","wav");
		fc.setFileFilter(filter);
		
		//iki button pause
		final JButton pauseBtn = new JButton();
		Image imgpause = new ImageIcon(this.getClass().getResource("/Pause Icon.png")).getImage();
		pauseBtn.setIcon(new ImageIcon(imgpause));
		pauseBtn.setBounds(10, 11, 53, 53);
		pauseBtn.setVisible(false);
		frame.getContentPane().add(pauseBtn);
		
		//iki button play
		final JButton playBtn = new JButton();
		Image imgplay = new ImageIcon(this.getClass().getResource("/Play Icon.png")).getImage();
		playBtn.setIcon(new ImageIcon(imgplay));
		playBtn.setBounds(10, 11, 53, 53);
		frame.getContentPane().add(playBtn);
		
		//iki button resume
		final JButton resumeBtn = new JButton();
		resumeBtn.setIcon(new ImageIcon(imgplay));
		resumeBtn.setBounds(10, 11, 53, 53);
		resumeBtn.setVisible(false);
		frame.getContentPane().add(resumeBtn);
		
		//iki button stop
		final JButton stopBtn = new JButton();
		Image imgstop = new ImageIcon(this.getClass().getResource("/Stop Icon.png")).getImage();
		stopBtn.setIcon(new ImageIcon(imgstop));
		stopBtn.setBounds(68, 11, 53, 53);
		stopBtn.setEnabled(false);
		frame.getContentPane().add(stopBtn);
		
		//iki Aksi Button Pause
		pauseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.pause();
				sliderAnimate.stop();
				pauseBtn.setVisible(false);
				resumeBtn.setVisible(true);
			}
		});
		
		//iki Aksi Button resume
		resumeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				player.resume();
				pauseBtn.setVisible(true);
				sliderAnimate.start();
			}
		});
		
		//iki Aksi Button Stop
		stopBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resumeBtn.setVisible(false);
				pauseBtn.setVisible(false);
				playBtn.setVisible(true);
				sliderAnimate.stop();
				slider.setMaximum(100);
				slider.setValue(0);
				lblRunningLagu.setText("00:00");
				player.resume();
				player.stop();
				stopBtn.setEnabled(false);
			}
		});
		
		//iki Aksi Button Play
		playBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(daftarTmpLagu.get(listModel.indexOf(list.getSelectedValue())) != null){
					lblJudulLagu.setText("");
					pauseBtn.setVisible(true);
					player = new AudioPlay();
					player.setPath(daftarTmpLagu.get(listModel.indexOf(list.getSelectedValue())));
					lblJudulLagu.setText(player.getTitle());
					try {
						lblDurasiLagu.setText(player.getDuration());
					} catch (Exception e) {
						e.printStackTrace();
					}
					final int durationSecon = player.getDurationSecon();
					slider.setMaximum(durationSecon);
					player.play(-1);
					stopBtn.setEnabled(true);
					
					valuePositionSlider = 0;
					sliderAnimate = new Timer(1000, new ActionListener(){
						public void actionPerformed(ActionEvent e) {
							slider.setValue(valuePositionSlider);
							lblRunningLagu.setText(player.getDurationDynamic(valuePositionSlider));
							valuePositionSlider += 1;
						}
					});
					
					final int labelWidth = 400;
					final AtomicInteger labelPadding = new AtomicInteger();
					animateJudul = new Timer(20, new ActionListener() {
					    public void actionPerformed(ActionEvent e) {
					    	if(valuePositionSlider == (durationSecon+1)){
					    		resumeBtn.setVisible(false);
								pauseBtn.setVisible(false);
								playBtn.setVisible(true);
								sliderAnimate.stop();
								slider.setMaximum(100);
								slider.setValue(0);
								lblRunningLagu.setText("00:00");
								player.stop();
								stopBtn.setEnabled(false);							
							}
					    	lblJudulLagu.setBorder(new EmptyBorder(0, labelPadding.getAndIncrement() % labelWidth, 0, 0));
					    }
					});
					
					animateJudul.start();
					sliderAnimate.start();
					playBtn.setVisible(false);
				}
				else{
					JOptionPane.showMessageDialog(null, "Pilih Lagu dahulu!!!");
				}
			}
		});
		
		//Open Button
		JButton open = new JButton("Open");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String namaFile = "";
				
				int returnVal = fc.showSaveDialog(fc);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
				    File yourFolder = fc.getSelectedFile();
				    namaFile = yourFolder.toString();
				    try {
						listMusic.write(namaFile.replace("\\", "/"));
						JOptionPane.showMessageDialog(null, "Berhasil menyimpan lagu");
						updateListLagu();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		Image imgopen = new ImageIcon(this.getClass().getResource("/open icon.png")).getImage();
		open.setIcon(new ImageIcon(imgopen));
		open.setBounds(430, 250, 140, 23);
		frame.getContentPane().add(open);
		
		//hapus Button
		JButton hapusBtn = new JButton("Delete");
		hapusBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(daftarTmpLagu.get(listModel.indexOf(list.getSelectedValue())) != null){
					daftarTmpLagu.remove(listModel.indexOf(list.getSelectedValue()));
					listMusic.clear();
					for(Map.Entry m:daftarTmpLagu.entrySet()){
						try {
							listMusic.write(m.getValue().toString());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					JOptionPane.showMessageDialog(null, "Berhasil menghapus lagu");
					updateListLagu();
				}
				else{
					JOptionPane.showMessageDialog(null, "Pilih Lagu untuk dihapus!!!");
				}
			}
		});
		Image imghapus = new ImageIcon(this.getClass().getResource("/delete icon.png")).getImage();
		hapusBtn.setIcon(new ImageIcon(imghapus));
		hapusBtn.setBounds(280, 250, 140, 23);
		frame.getContentPane().add(hapusBtn);
		
	}
}
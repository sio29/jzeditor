/******************************************************************************
;	音声プレビュー
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler.preview.ext;

import java.awt.Component;
import javax.swing.JLabel;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

import sio29.ulib.ufile.*;

import sio29.ulib.usound.*;
import sio29.ulib.usound.backends.jsound.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.*;

import sio29.jzeditor.backends.j2d.jzfiler.preview.*;

public class JzFilerPreviewCreaterAudio implements JzFilerPreviewViwerCreater{
	private USound_JSND usound;
	public JzFilerPreviewCreaterAudio(){
		usound=new USound_JSND(false);
		//usound=new USound_JSND(true);	//MDZ
	}
	public boolean isSupport(UFile filename){
		if(!filename.isFile())return false;
		String ext=filename.getFileExt();
		if(isAudioFileExt(ext))return true;
		if(isEffectFileExt(ext))return true;
		return false;
	}
	public JzFilerPreviewViwerDispoeable createViewer(){
		return new JzFilerPreviewViwer_Audio(this);
	}
	public JzFilerPreviewDataLoader createDataLoader(UFile filename){
		String ext=filename.getFileExt();
		if(isAudioFileExt(ext)){
			return new JzFilerPreviewDataLoader_Audio(usound,this,filename);
		}
		if(isEffectFileExt(ext)){
			return new JzFilerPreviewDataLoader_Effect(usound,this,filename);
		}
		return null;
	}
	public void disposeData(UFile filename,Object data){
		//System.out.println("disposeData");
		if(data!=null){
			if(data instanceof MusicPlayerBase ){
				MusicPlayerBase player=(MusicPlayerBase)data;
				//player.stop();
			}
		}
	}
	//拡張子が音声か?
	private boolean isAudioFileExt(String ext){
		if(ext==null)return false;
		if(ext.length()==0)return false;
		ext=ext.toLowerCase();
		return usound.getMusicPlayer().isSupport(ext);
	}
	private boolean isEffectFileExt(String ext){
		if(ext==null)return false;
		if(ext.length()==0)return false;
		ext=ext.toLowerCase();
		return usound.getEffectPlayer().isSupport(ext);
	}
}

class JzFilerPreviewDataLoader_Audio implements JzFilerPreviewDataLoader{
	private USound_JSND usound;
	private MusicPlayerBase player;
	private JzFilerPreviewViwerCreater creator;
	private UFile filename;
	//
	JzFilerPreviewDataLoader_Audio(USound_JSND _usound,JzFilerPreviewViwerCreater _creator,UFile _filename){
		usound=_usound;
		creator=_creator;
		filename=_filename;
	}
	public boolean load(UFile _filename){
		filename=_filename;
		player=usound.getMusicPlayer();
		return true;
	}
	public Object getData(){
		return player;
	}
	public JzFilerPreviewViwerCreater getCreator(){
		return creator;
	}
	public void dispose(){
	}
}

class JzFilerPreviewDataLoader_Effect implements JzFilerPreviewDataLoader{
	private USound_JSND usound;
	private EffectPlayerBase player;
	private JzFilerPreviewViwerCreater creator;
	private UFile filename;
	//
	JzFilerPreviewDataLoader_Effect(USound_JSND _usound,JzFilerPreviewViwerCreater _creator,UFile _filename){
		usound=_usound;
		creator=_creator;
		filename=_filename;
	}
	public boolean load(UFile _filename){
		filename=_filename;
		player=usound.getEffectPlayer();
		return true;
	}
	public Object getData(){
		return player;
	}
	public JzFilerPreviewViwerCreater getCreator(){
		return creator;
	}
	public void dispose(){
	}
}


class JzFilerPreviewViwer_Audio extends Container implements JzFilerPreviewViwerDispoeable{
	private volatile Object player;
	private volatile boolean dispose_flg=false;
	private UFile filename;
	private Thread thread;
	private JzFilerPreviewViwerCreater creator;
	JLabel dl_filename;
	JSlider dl_slider;
	JSlider dl_volume;
	JLabel dl_now;
	JLabel dl_total;
	JButton dl_play;
	JButton dl_stop;
	JLabel dl_info;
	IntValField dl_index;
	//float volume=0.5f;
	float volume=0.3f;
	boolean set_param_flg=false;
	//
	JzFilerPreviewViwer_Audio(JzFilerPreviewViwerCreater _creator){
		player=null;
		creator=_creator;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel dl_panel1=new JPanel();
		JPanel dl_panel2=new JPanel();
		JPanel dl_panel3=new JPanel();
		JPanel dl_panel4=new JPanel();
		dl_panel1.setLayout(new BoxLayout(dl_panel1, BoxLayout.X_AXIS));
		dl_panel2.setLayout(new BoxLayout(dl_panel2, BoxLayout.X_AXIS));
		dl_panel3.setLayout(new BoxLayout(dl_panel3, BoxLayout.X_AXIS));
		dl_panel4.setLayout(new BoxLayout(dl_panel4, BoxLayout.X_AXIS));
		add(dl_panel1);
		add(dl_panel2);
		add(dl_panel3);
		add(dl_panel4);
		
		
		dl_filename=new JLabel("");
		dl_slider=new JSlider();
		dl_volume=new JSlider();
		dl_now=new JLabel("now");
		dl_total=new JLabel("total");
		dl_play=new JButton("play");
		dl_stop=new JButton("stop");
		dl_info=new JLabel("info");
		dl_index=new IntValField(0,255,1);
		dl_index.SetItem(0);
		dl_index.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent ev){
				//float n=dl_index.getItem();
				//setVolume(n);
				play();
			}
		});
		
		dl_panel1.add(dl_filename);
		dl_panel2.add(dl_play);
		dl_panel2.add(dl_stop);
		dl_panel3.add(dl_now);
		dl_panel3.add(dl_total);
		dl_panel3.add(dl_slider);
		dl_panel4.add(dl_volume);
		dl_panel4.add(dl_info);
		dl_panel4.add(dl_index);
		dl_volume.setMinimum(0);
		dl_volume.setMaximum(100);
		dl_play.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				play();
			}
		});
		dl_stop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				stopOnlyAndFeedback();
			}
		});
		addComponentListener(new ComponentAdapter(){
			public void componentHidden(ComponentEvent e){
				stopOnlyAndFeedback();
			}
		});
		super.addComponentListener(new ComponentAdapter(){
			public void componentHidden(ComponentEvent e){
				stopOnlyAndFeedback();
			}
		});
		dl_volume.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent ev){
				float n=dl_volume.getValue()/100.0f;
				setVolume(n);
			}
		});
		dl_volume.setValue((int)(volume*100.0f));
		thread=new Thread(new Runnable(){
			public void run(){
				while(true){
					if(dispose_flg)break;
					setParam();
					//setRealtimeParam();
					try{
						Thread.sleep(33);
					}catch(Exception ex){}
				}
				//System.out.println("Thread end:"+((player!=null)?player.getFilename():"null"));
			}
		});
		thread.start();
	}
	void resetParam(){
		dl_total.setText("");
		dl_slider.setMaximum(0);
		dl_info.setText("");
		dl_now.setText("");
		dl_play.setEnabled(false);
		dl_stop.setEnabled(false);
	}
	void setParam(){
		if(filename!=null){
			dl_filename.setText(filename.toString());
		}
		/*
		if(player instanceof MusicPlayerBaseStream){
			MusicPlayerBaseStream streamplayer=(MusicPlayerBaseStream)player;
			if(streamplayer.isReady()){
				if(!set_param_flg){
					set_param_flg=true;
					float total_time=streamplayer.getTotalTime()/1000.0f;
					dl_total.setText(String.format("total(%.2f)",total_time));
					dl_slider.setMaximum((int)total_time);
					//
					int rate=streamplayer.getRate();
					int cnl=streamplayer.getChannel();
					String info_m=String.format("rate(%d),cnl(%d)",rate,cnl);
					if(streamplayer.hasLoop()){
						long loop_start=streamplayer.getLoopStart();
						long loop_end  =streamplayer.getLoopEnd();
						info_m+=String.format(",loop(%08x-%08x)",loop_start,loop_end);
					}
					dl_info.setText(info_m);
				}
			}
		}
		*/
		if(player instanceof EffectPlayerBase){
			EffectPlayerBase effplayer=(EffectPlayerBase)player;
			if(effplayer.isLoaded(filename)){
				//dl_total.setText("");
				//int bank_id=0;
				//int[] bounds=effplayer.getIndexBounds(bank_id);
				//dl_info.setText(String.format("index(%d-%d)",bounds[0],bounds[1]));
				//dl_play.setEnabled(true);
				//dl_stop.setEnabled(true);
			}
			
		}
		setRealtimeParam();
	}
	void setRealtimeParam(){
		if(player!=null){
			/*
			if(player instanceof MusicPlayerBaseStream){
				MusicPlayerBaseStream streamplayer=(MusicPlayerBaseStream)player;
				if(streamplayer.isReady()){
					float now_time=streamplayer.getNowTime()/1000.0f;
					if(dl_now!=null){
						dl_now.setText(String.format("now(%.2f)",now_time));
					}
					if(streamplayer.isPlay()){
						dl_play.setEnabled(false);
						dl_stop.setEnabled(true);
					}else{
						dl_play.setEnabled(true);
						dl_stop.setEnabled(false);
					}
					dl_slider.setValue((int)now_time);
				}
			}
			*/
			if(player instanceof EffectPlayerBase){
				EffectPlayerBase effplayer=(EffectPlayerBase)player;
				if(effplayer.isLoaded(filename)){
					dl_total.setText("");
					int bank_id=0;
					int[] bounds=effplayer.getIndexBounds(bank_id);
					dl_info.setText(String.format("index(%d-%d)",bounds[0],bounds[1]));
					dl_play.setEnabled(true);
					dl_stop.setEnabled(true);
				}
			}
		}
	}
	public void dispose(){
		dispose_flg=true;
		stop();
	}
	public Component getComponent(){
		return this;
	}
	public JzFilerPreviewViwerCreater getCreator(){
		return creator;
	}
	public void setVolume(float n){
		if(player!=null){
			/*
			if(player instanceof MusicPlayerBaseStream){
				MusicPlayerBaseStream streamplayer=(MusicPlayerBaseStream)player;
				//streamplayer.setVolume(volume);
			}
			*/
			if(player instanceof EffectPlayerBase){
				EffectPlayerBase _player=(EffectPlayerBase)player;
				//_player.setVolume(volume);
			}
		}
	}
	public void play(){
		if(player instanceof MusicPlayerBase){
			MusicPlayerBase _player=(MusicPlayerBase)player;
			MusicPlayerParam param=new MusicPlayerParam();
			param.filename=filename;
			param.vol=volume;
			_player.play(param);
			//int handle=_player.play(param);
			//_player.setVolume(handle,volume);
		}else if(player instanceof EffectPlayerBase){
			EffectPlayerBase _player=(EffectPlayerBase)player;
			int bank_id=0;
			if(!_player.isLoaded(filename)){
				_player.load(bank_id,filename);
			}
			int index=dl_index.GetItem();
			_player.play(bank_id,index,volume);
		}
	}
	public void stopOnlyAndFeedback(){
		stopOnly();
		dl_play.setEnabled(true);
		dl_stop.setEnabled(false);
	}
	public void stopOnly(){
		if(player instanceof MusicPlayerBase){
			MusicPlayerBase _player=(MusicPlayerBase)player;
			//_player.stop();
		}else if(player instanceof EffectPlayerBase){
			EffectPlayerBase _player=(EffectPlayerBase)player;
			_player.stop();
		}
	}
	public void stop(){
		stopOnly();
		player=null;
	}
	public void setData(UFile _filename,Object data){
		resetParam();
		filename=_filename;
		set_param_flg=false;
		if(data instanceof MusicPlayerBase){
System.out.println("MusicPlayerBase:"+filename);
			player=data;
			MusicPlayerBase _player=(MusicPlayerBase)player;
			play();
		}else if(data instanceof EffectPlayerBase){
System.out.println("EffectPlayerBase:"+filename);
			player=data;
			EffectPlayerBase _player=(EffectPlayerBase)player;
			dl_index.SetItem(0);
			play();
		}
		setParam();
	}
	public void clearData(){
		//System.out.println("JzFilerPreviewViwer_Audio::clearData:"+filename);
		stop();
		filename=null;
		setParam();
	}
	public UFile getFilename(){
		return filename;
	}
}

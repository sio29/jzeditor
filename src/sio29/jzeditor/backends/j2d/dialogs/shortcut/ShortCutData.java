/******************************************************************************
;	ショートカットダイアログ
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.shortcut;

import java.util.HashMap;
import java.util.Set;
import javax.swing.KeyStroke;
import javax.swing.InputMap;
import javax.swing.JComponent;

public class ShortCutData{
	public HashMap<KeyStroke,String> map=new HashMap<KeyStroke,String>();
	/*
	public void setComponent(JComponent c){
		InputMap inputmap=c.getInputMap();
		//KeyStroke[] keys=inputmap.keys();
		KeyStroke[] keys=inputmap.allKeys();
		for(int i=0;i<keys.length;i++){
			Object n=inputmap.get(keys[i]);
			map.put(keys[i].toString(),(String)n);
		}
	}
	*/
	public KeyStroke[] getKeyStrokes(){
		Set<KeyStroke> keyset=map.keySet();
		return (KeyStroke[])keyset.toArray(new KeyStroke[]{});
	}
	public Object[][] toArray(){
		KeyStroke[] keys=getKeyStrokes();
		int key_num=keys.length;
		Object[][] ret=new Object[key_num][2];
		for(int i=0;i<key_num;i++){
			KeyStroke key=keys[i];
			ret[i][0]=key;
			ret[i][1]=map.get(key);
		}
		return ret;
	}
	public boolean equls(Object o){
		if(o==null)return false;
		if(!(o instanceof ShortCutData))return false;
		ShortCutData d=(ShortCutData)o;
		if(!map.equals(d.map))return false;
		return true;
	}
	
	
	/*
	public String[][] toArray(){
		Set<String> keyset=map.keySet();
		String[][] ret=new String[keyset.size()][2];
		int i=0;
		for(String key : keyset){
			ret[i][0]=key;
			ret[i][1]=map.get(key);
			i++;
		}
		return ret;
	}
	*/
	
}

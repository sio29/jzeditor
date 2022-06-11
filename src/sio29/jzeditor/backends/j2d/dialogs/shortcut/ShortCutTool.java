/******************************************************************************
;	ショートカットダイアログ
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.shortcut;

import java.util.HashMap;
import java.util.Set;
import javax.swing.KeyStroke;
import javax.swing.InputMap;
import javax.swing.JComponent;

import sio29.ulib.ufile.*;


public class ShortCutTool{
	//コンポーネントからショートカット獲得
	public static void getShortCutDataFromComponent(ShortCutData data,JComponent c){
		InputMap inputmap=c.getInputMap();
		KeyStroke[] keys=inputmap.allKeys();
		for(int i=0;i<keys.length;i++){
			KeyStroke key=keys[i];
			String cmd=(String )inputmap.get(key);
			data.map.put(key,cmd);
		}
	}
	//コンポーネントへショートカット設定
	public static void setShortCutDataToComponent(ShortCutData data,JComponent c){
		InputMap inputmap=c.getInputMap();
		KeyStroke[] keys=data.getKeyStrokes();
		for(int i=0;i<keys.length;i++){
			KeyStroke key=keys[i];
			String cmd=data.map.get(key);
			inputmap.put(key,cmd);
		}
	}
	//
	public static String toStringShortCutData(ShortCutData data){
		StringBuilder sb=new StringBuilder();
		KeyStroke[] keys=data.getKeyStrokes();
		for(int i=0;i<keys.length;i++){
			KeyStroke key=keys[i];
			String cmd=data.map.get(key);
			String m=String.format("%s = %s",key.toString(),cmd);
			sb.append(m+"\n");
		}
		return sb.toString();
	}
	//
	static String deleteSpaceString(String m){
		while(true){
			char mm=m.charAt(0);
			if(mm==0x20 || mm==0x09){
				m=m.substring(1);
			}else{
				break;
			}
		}
		while(true){
			int bottom=m.length()-1;
			if(bottom<0)break;
			char mm=m.charAt(bottom);
			if(mm==0x20 || mm==0x09){
				m=m.substring(0,bottom);
			}else{
				break;
			}
		}
		return m;
	}
	//
	public static ShortCutData getShortCutDataFromString(String text){
		ShortCutData data=new ShortCutData();
		String[] list=text.split("\n");
//System.out.println("list="+list.length);
		for(int i=0;i<list.length;i++){
			String line=list[i];
			String[] pair=line.split("=");
			String key_m=deleteSpaceString(pair[0]);
			String cmd  =deleteSpaceString(pair[1]);
			KeyStroke key=KeyStroke.getKeyStroke(key_m);
//System.out.println("list["+i+"]=("+pair[0]+"),("+pair[1]+")");
//System.out.println("list["+i+"]=("+key_m+"),("+cmd+")");
			data.map.put(key,cmd);
		}
		return data;
	}
	
	public static boolean saveShortCutData(UFile file,ShortCutData data){
		String text=toStringShortCutData(data);
		if(text==null)return false;
		byte[] blob=null;
		try{
			blob=text.getBytes("SHIFT_JIS");
		}catch(Exception ex){
			return false;
		}
		if(blob==null)return false;
System.out.println("save:"+file);
		return file.bsave(blob);
	}
	public static ShortCutData loadShortCutData(UFile file){
		byte[] blob=file.bload_malloc();
		if(blob==null)return null;
System.out.println("load:"+file);
		String text=null;
		try{
			text=new String(blob,"SHIFT_JIS");
		}catch(Exception ex){
			return null;
		}
		if(text==null)return null;
		ShortCutData data=getShortCutDataFromString(text);
		return data;
	}
}

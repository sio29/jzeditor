/******************************************************************************
;	改行コード
******************************************************************************/
package sio29.jzeditor.backends.j2d.texttool;

public class CRType{
	public final static String CR_LF="CR_LF";
	public final static String CR="CR";
	public final static String LF="LF";
	private final static String[] list={CR_LF,CR,LF};
	public final static String[] GetNameList(){return list;}
	public static int GetIndex(String name){
		for(int i=0;i<list.length;i++){
			if(list[i].equals(name))return i;
		}
		return -1;
	}
	
}

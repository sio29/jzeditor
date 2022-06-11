/******************************************************************************
;	Vzファイラー、ファイルフィルター
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import java.util.ArrayList;

import sio29.ulib.ufile.*;

public class JzFilerMaskFileFilter implements UFilenameFilter{
	public final static int TYPE_ALL      =0;		//全て      "*"
	public final static int TYPE_NONE     =1;		//禁止      ""
	public final static int TYPE_FRONT    =2;		//開始文字  "abcd*"
	public final static int TYPE_PART     =3;		//?以外一致 "abcd???"
	public final static int TYPE_COMP     =4;		//完全一致  "abcd"
	public final static int TYPE_FRONTPART=5;		//開始文字  "a???*"
	public final static EnableType ENABLE_ALL =new EnableType(TYPE_ALL,null);
	public final static EnableType ENABLE_NONE=new EnableType(TYPE_NONE,null);
	//
	public boolean file_only;
	private ArrayList<MaskParam> masks=new ArrayList<MaskParam>();
	//
	public static String getTypeName(int type){
		switch(type){
			case TYPE_ALL      :return "ALL";
			case TYPE_NONE     :return "NONE";
			case TYPE_FRONT    :return "FRONT";
			case TYPE_PART     :return "PART";
			case TYPE_COMP     :return "COMP";
			case TYPE_FRONTPART:return "FRONTPART";
		}
		return "---";
	}
	
	static class EnableType{
		int type;
		String name;
		public EnableType(int type,String name){
			this.type=type;
			this.name=name;
		}
		public String toString(){
			if(name==null){
				return String.format("type(%s)",getTypeName(type));
			}else{
				return String.format("type(%s),name(%s)",getTypeName(type),name);
			}
		}
		public static final char char_que="?".charAt(0);
		public static boolean comparePart(String part,String m){
			int len=part.length();
			if(m.length()!=len)return false;
			for(int i=0;i<len;i++){
				char mm=part.charAt(i);
				if(mm==char_que)continue;
				if(mm!=m.charAt(i))return false;
			}
			return true;
		}
		public boolean compare(String m){
			switch(type){
				case TYPE_ALL :
					return true;
				case TYPE_NONE:
					return false;
				case TYPE_FRONT:
					return m.startsWith(name);
				case TYPE_COMP:
					return m.equals(name);
				case TYPE_PART:
					return comparePart(name,m);
				case TYPE_FRONTPART:
					if(m.length()<name.length())return false;
					return comparePart(name,m.substring(0,name.length()));
			}
			return false;
		}
	}
	static class MaskParam{
		EnableType body;
		EnableType ext;
		public MaskParam(EnableType body,EnableType ext){
			this.body=body;
			this.ext =ext;
		}
		public String toString(){
			return String.format("body:[%s],ext:[%s]",body.toString(),ext.toString());
		}
		public boolean compare(String body_m,String ext_m){
			if(!body.compare(body_m))return false;
			if(!ext.compare(ext_m))return false;
			return true;
		}
	}
	public static UFilenameFilter createMaskFileFilter(String mask_str,boolean file_only){
		if(mask_str==null)return null;
		if(mask_str.length()==0)return null;
		String[] masks=mask_str.split(",");
		if(masks.length==0)return null;
		JzFilerMaskFileFilter file_filter=new JzFilerMaskFileFilter();
		file_filter.file_only=file_only;
		for(int i=0;i<masks.length;i++){
			String mask=masks[i];
			if(mask==null)continue;
			if(mask.length()==0)continue;
//System.out.println("["+i+"]:"+mask);
			if(mask.startsWith("*.*")){
				file_filter=null;
				return null;
			}else{
				file_filter.addMask(mask);
			}
		}
		if(file_filter.getMaskCount()==0)return null;
		return file_filter;
	}
	public int getMaskCount(){
		return masks.size();
	}
	public MaskParam getMask(int i){
		return masks.get(i);
	}
	public EnableType GetEnableType(String m){
		if(m==null)return ENABLE_NONE;
		if(m.length()==0)return ENABLE_NONE;
		if(m.startsWith("*"))return ENABLE_ALL;
		int asta_index=m.indexOf("*");
		if(asta_index>=0){
			m=m.substring(0,asta_index);
//System.out.println("front:"+m);
			int que_index=m.indexOf("?");
			if(que_index>=0){
				return new EnableType(TYPE_FRONTPART,m);
			}else{
				return new EnableType(TYPE_FRONT,m);
			}
		}
		int que_index=m.indexOf("?");
		if(que_index>=0){
			return new EnableType(TYPE_PART,m);
		}
		return new EnableType(TYPE_COMP,m);
	}
	public String[] getBodyAndExt(String mask){
		String ext=null;
		String body=null;
		int ext_index=mask.lastIndexOf(".");
		if(ext_index>=0){
			body=mask.substring(0,ext_index);
			ext =mask.substring(ext_index+1);
		}else{
			body=mask;
			ext =null;
		}
		int body_index=body.lastIndexOf("\\");
		if(body_index>=0){
			body=body.substring(body_index+1);
		}
		return new String[]{body,ext};
	}
	public void addMask(String mask){
		String[] body_ext=getBodyAndExt(mask);
		String body=body_ext[0];
		String ext =body_ext[1];
		if(body==null)body="*";
		if(ext ==null)ext ="*";
		EnableType enable_body=GetEnableType(body);
		EnableType enable_ext =GetEnableType(ext);
		MaskParam param=new MaskParam(enable_body,enable_ext);
//System.out.println(String.format("body(%s),ext(%s)",body,ext));
//System.out.println(""+param);
		masks.add(param);
	}
	@Override
	public boolean accept(UFile file){
		if(file_only){
			if(file.isDirectory())return true;
		}
		//String name=file.getName();
		String name=file.getFileBody();
		String[] body_ext=getBodyAndExt(name);
		for(int i=0;i<getMaskCount();i++){
			MaskParam param=getMask(i);
			if(param.compare(body_ext[0],body_ext[1]))return true;
		}
		return false;
	}
}

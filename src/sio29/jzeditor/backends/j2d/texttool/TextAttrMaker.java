/******************************************************************************
;	アトリュビュート作成
******************************************************************************/
package sio29.jzeditor.backends.j2d.texttool;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TextAttrMaker{
	//指定範囲のアトリュビュート設定
	static void SetCharacterAttributesSub(int offset, int length,TextAttrSetterFunc setter){
		if(setter==null)return;
		setter.set(offset,length);
	}
	//指定範囲のアトリュビュート設定
	static void SetAttrSub(int start,int end,TextAttrSetterFunc setter){
		if(setter==null)return;
		int len=end-start+1;
		SetCharacterAttributesSub(start,len,setter);
	}
	//パターンにマッチする部分にアトリュビート設定
	static void MakeAttrColorSub(CharSequence text,Pattern pattern,TextAttrSetterFunc setter){
		if(setter==null)return;
		try{
			Matcher m = pattern.matcher(text);
			if(m==null)return;
			while(m.find()) {
				SetCharacterAttributesSub(m.start(),m.end()-m.start(),setter);
			}
		}catch(Exception ex){}
	}
	//C++コメントアトリュビュート作成
	static void MakeAttrComment(CharSequence _text,TextAttrSetterFunc setter){
		if(setter==null)return;
		if(_text==null)return;
		int len=_text.length();
		if(len==0)return;
		if(len<=1)return;
		StringBuilder text=new StringBuilder(_text);
		int now_index=0;
		boolean eof=false;
		while(true){
			if(now_index>=len)break;
			int start_index = text.indexOf("/",now_index);
			if(start_index==-1)break;
			if(start_index>=(len-1))break;
			char m=text.charAt(start_index+1);
			if(m=='/'){
				int end_index = text.indexOf("\n",start_index+2);
				if(end_index==-1){
					end_index=len-1;
					eof=true;
				}
				SetAttrSub(start_index,end_index,setter);
				now_index=end_index+1;
			
			}else if(m=='*'){
				int end_index = text.indexOf("*/",start_index+2);
				if(end_index==-1){
					end_index=len-1;
					eof=true;
				}else{
					end_index++;
				}
				SetAttrSub(start_index,end_index,setter);
				now_index=end_index+1;
			
			}else{
				now_index=start_index+1;
			}
			if(eof)break;
		}
	}
	//文字列アトリュビュート作成
	static void MakeAttrString(CharSequence text,TextAttrSetterFunc setter){
		Pattern dq_pattern= Pattern.compile("\"(.*?)\"");		//文字列"
		Pattern sq_pattern= Pattern.compile("\'(.*?)\'");		//文字列'
		MakeAttrColorSub(text,dq_pattern,setter);
		MakeAttrColorSub(text,sq_pattern,setter);
	}
	//マクロアトリュビュート作成
	static void MakeAttrMacro(CharSequence text,TextAttrSetterFunc setter){
		Pattern sharp_pattern= Pattern.compile("\\#.*");		//マクロ#
		MakeAttrColorSub(text,sharp_pattern,setter);
	}
	//
	public static void makeAttr(CharSequence text,TextAttrSetter setter){
		if(text==null)return;
		int len=text.length();
		//一度テキスト全体をattr_normalでフィル
		if(setter.normal!=null){
			setter.normal.set(0,len);
		}
		//コメント
		MakeAttrComment(text,setter.comment);
		//文字列
		MakeAttrString(text,setter.str);
		//マクロアトリュビュート作成
		MakeAttrMacro(text,setter.macro);
	}
}




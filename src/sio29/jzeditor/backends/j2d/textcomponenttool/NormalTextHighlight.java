/******************************************************************************
;	ハイライト
******************************************************************************/
package sio29.jzeditor.backends.j2d.textcomponenttool;
//package sio29.jzeditor.backends.j2d.normaltext;

import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.Element;
import javax.swing.text.DefaultHighlighter;

import sio29.jzeditor.backends.j2d.texttool.*;

//ハイライト
public class NormalTextHighlight implements Highlighter.Highlight{
	int start;
	int end;
	Highlighter.HighlightPainter painter;
	public NormalTextHighlight(){}
	public NormalTextHighlight(int start,int end,Highlighter.HighlightPainter painter){
		this.start  =start;
		this.end    =end;
		this.painter=painter;
	}
	public int getStartOffset(){
	 	return start;
	}
	public int getEndOffset(){
	 	return end;
	}
	public Highlighter.HighlightPainter getPainter(){
	 	return painter;
	}
	public static Highlighter.Highlight[] ShortHilighter(Highlighter.Highlight[] hlist){
		//ソート
		for(int i=0;i<hlist.length-1;i++){
			for(int j=i+1;j<hlist.length;j++){
				Highlighter.Highlight h0=hlist[i];
				Highlighter.Highlight h1=hlist[j];
				if(h0.getStartOffset()>h1.getStartOffset()){
					hlist[i]=h1;
					hlist[j]=h0;
				}
			}
		}
		//重複する場所を削る
		ArrayList<NormalTextHighlight> dst=new ArrayList<NormalTextHighlight>();
		for(int i=0;i<hlist.length;i++){
			Highlighter.Highlight h0=hlist[i];
			if(h0==null)continue;
			int s0=h0.getStartOffset();
			int e0=h0.getEndOffset();
			for(int j=i+1;j<hlist.length;j++){
				Highlighter.Highlight h1=hlist[j];
				if(h0==h1)continue;
				if(h1==null)continue;
				int s1=h1.getStartOffset();
				int e1=h1.getEndOffset();
				if(s1>=s0 && s1<=e0){
					if(e1>e0){
						e0=e1;
					}
					hlist[j]=null;
				}
			}
			dst.add(new NormalTextHighlight(s0,e0,h0.getPainter()));
		}
		return (Highlighter.Highlight[])dst.toArray(new Highlighter.Highlight[]{});
		//return hlist;
	}
	public static Highlighter.Highlight[] GetHighlightList(JComponent _textArea){
		if(!(_textArea instanceof JTextComponent))return null;
		JTextComponent textArea=(JTextComponent)_textArea;
		try{
			Highlighter hl=textArea.getHighlighter();
			Highlighter.Highlight[] hlist=hl.getHighlights();
			return hlist;
		}catch(Exception ex){
			return null;
		}
	}
	public static String toStringHilighter(JComponent _textArea){
		if(!(_textArea instanceof JTextComponent))return null;
		JTextComponent textArea=(JTextComponent)_textArea;
		String mm="";
		try{
		Highlighter.Highlight[] hlist=GetHighlightList(textArea);
		if(hlist==null)return mm;
//hlist=ShortHilighter(hlist);
		mm="Highlight("+hlist.length+")";
		for(int i=0;i<hlist.length;i++){
			Highlighter.Highlight h=hlist[i];
			if(h==null)continue;
			int s=h.getStartOffset();
			int e=h.getEndOffset();
			String m=textArea.getText(s,e-s);
			mm+="["+i+"]:("+s+"-"+e+"):"+m;
		}
		}catch(Exception ex){}
		return mm;
	}
	public static void DeleteHilighterText(JComponent _textArea){
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		try{
			Document doc=textArea.getDocument();
			Highlighter.Highlight[] hlist=GetHighlightList(textArea);
			if(hlist==null)return;
			hlist=NormalTextHighlight.ShortHilighter(hlist);
			
			for(int i=hlist.length-1;i>=0;i--){
				Highlighter.Highlight h=hlist[i];
				if(h==null)continue;
				int s=h.getStartOffset();
				int e=h.getEndOffset();
				//textArea.removeText(s,e-s);
				doc.remove(s,e-s);
			}
			Highlighter hl=textArea.getHighlighter();
			hl.removeAllHighlights() ;
		}catch(Exception ex){}
	}
	public static void CopyClipBoardHilighter(JComponent _textArea){
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		try{
		Highlighter.Highlight[] hlist=GetHighlightList(textArea);
		if(hlist==null)return;
		String mm="";
		if(hlist.length==1){
			Highlighter.Highlight h=hlist[0];
			if(h!=null){
				int s=h.getStartOffset();
				int e=h.getEndOffset();
				mm=textArea.getText(s,e-s);
			}
		}else{
			for(int i=0;i<hlist.length;i++){
				Highlighter.Highlight h=hlist[i];
				if(h==null)continue;
				int s=h.getStartOffset();
				int e=h.getEndOffset();
				String m=textArea.getText(s,e-s);
				//System.out.println("["+i+"]:("+s+"-"+e+"):"+m);
				mm+=m;
				int l=m.length();
				if(l>0){
					if(m.charAt(l-1)!='\n'){
						mm+="\n";
					}
				}
			}
		}
		CopyClipBoardString(mm);
		}catch(Exception ex){}
	}
	public static void CopyClipBoardString(String m){
		Toolkit kit = Toolkit.getDefaultToolkit();
		Clipboard clip = kit.getSystemClipboard();
		StringSelection ss = new StringSelection(m);
		clip.setContents(ss, ss);
	}
	public static void CutClipBoardHilighter(JComponent _textArea){
		NormalTextHighlight.CopyClipBoardHilighter(_textArea);
		NormalTextHighlight.DeleteHilighterText(_textArea);
	}
	//ペイント
	public static void paintTextHilighter(Graphics _g,JComponent _textArea,Color col,boolean block_select){
		if(!(_textArea instanceof JTextComponent))return;
		JTextComponent textArea=(JTextComponent)_textArea;
		Document doc=textArea.getDocument();
		Highlighter hl=textArea.getHighlighter();
		Highlighter.Highlight[] hlist=hl.getHighlights();
		
		if(!block_select){
			//選択テキストがないのにハイライトがある場合
			if(textArea.getSelectedText()==null && hlist.length>0){
				hl.removeAllHighlights();
			}
			return;
		}
		
		Graphics2D g = (Graphics2D) _g;
		g.setColor(col);
		if(hlist.length!=0){
			hl.removeAllHighlights() ;
			try{
				int s=textArea.getSelectionStart();
				int e=textArea.getSelectionEnd();
				Element root=doc.getDefaultRootElement();
				int si=root.getElementIndex(s);
				int ei=root.getElementIndex(e);
				Element s_e=root.getElement(si);
				Element e_e=root.getElement(ei);
				int s_e_o=s_e.getStartOffset();
				int e_e_o=e_e.getStartOffset();
				int so=s-s_e_o;
				int eo=e-e_e_o;
				int tab_len=4;
				so=TextTool.StrlenFromTab(textArea.getText(s_e_o,so),tab_len);
				eo=TextTool.StrlenFromTab(textArea.getText(e_e_o,eo),tab_len);
				if(so>eo){
					int t=so;
					so=eo;
					eo=t;
				}
				int top   =si;
				int bottom=ei;
				if(top>bottom){
					int t=top;
					top=bottom;
					bottom=t;
				}
				//
				for(int i=top;i<=bottom;i++){
					Element ee=root.getElement(i);
					int ts=ee.getStartOffset();
					int te=ee.getEndOffset() ;
					//
					String m=textArea.getText(ts,te-ts+1);
					int t0=TextTool.StrlenToTab(m,tab_len,so);
					int t1=TextTool.StrlenToTab(m,tab_len,eo);
					t0+=ts;
					t1+=ts;
					if(t0>=te)t0=te;
					if(t1>=te)t1=te;
					if(t0==t1)continue;
					hl.addHighlight(t0,t1,new DefaultHighlighter.DefaultHighlightPainter(Color.RED));
				}
			}catch(Exception ex){}
		}
	}
	
};



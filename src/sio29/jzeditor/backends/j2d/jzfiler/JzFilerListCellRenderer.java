/******************************************************************************
;	Vzファイラー、ファイルリスト一セル分のレンダラ
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.net.URLDecoder;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.ureg.*;
import sio29.ulib.ufile.*;
import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;


class JzFilerListCellRendererParam {
	public CVECTOR select_front_col=CVECTOR.BLACK;		//選択前景色
	public CVECTOR select_back_col =CVECTOR.CYAN;		//選択背景色
	public CVECTOR select_back2_col=CVECTOR.GRAY;		//選択背景色(フォーカスがない場合)
	public CVECTOR[] base_back_col=new CVECTOR[]{CVECTOR.BLACK,new CVECTOR(32,32,32)};	//奇数、偶数用
	public CVECTOR normal_col=CVECTOR.WHITE;				//通常色
	public CVECTOR dir_col=CVECTOR.CYAN;					//ディレクトリ色
	public CVECTOR hidden_col=new CVECTOR(64,64,255);	//隠しファイル
	public CVECTOR readonly_col=CVECTOR.GREEN;			//リードオンリー
	public boolean use_back_col2=false;
	//
	public JzFilerListCellRendererParam(){
	}
	public JzFilerListCellRendererParam(JzFilerListCellRendererParam src){
	}
	public void ReadReg(RegOut regout,String node_name){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		select_front_col=regout.ReadRegColor(node,"select_front_col",select_front_col);
		select_back_col =regout.ReadRegColor(node,"select_back_col" ,select_back_col);
		select_back2_col=regout.ReadRegColor(node,"select_back2_col",select_back2_col);
		base_back_col[0]=regout.ReadRegColor(node,"base_back_col0"  ,base_back_col[0]);
		base_back_col[1]=regout.ReadRegColor(node,"base_back_col1"  ,base_back_col[1]);
		normal_col      =regout.ReadRegColor(node,"normal_col"      ,normal_col);
		dir_col         =regout.ReadRegColor(node,"dir_col"         ,dir_col);
		hidden_col      =regout.ReadRegColor(node,"hidden_col"      ,hidden_col);
		readonly_col    =regout.ReadRegColor(node,"readonly_col"    ,readonly_col);
		use_back_col2   =regout.ReadRegBool (node,"use_back_col2"   ,use_back_col2);
//System.out.println("ReadReg:"+normal_col);
	}
	public void OutReg(RegOut regout,String node_name){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		regout.OutRegColor(node,"select_front_col",select_front_col);
		regout.OutRegColor(node,"select_back_col" ,select_back_col);
		regout.OutRegColor(node,"select_back2_col",select_back2_col);
		regout.OutRegColor(node,"base_back_col0"  ,base_back_col[0]);
		regout.OutRegColor(node,"base_back_col1"  ,base_back_col[1]);
		regout.OutRegColor(node,"normal_col"      ,normal_col);
		regout.OutRegColor(node,"dir_col"         ,dir_col);
		regout.OutRegColor(node,"hidden_col"      ,hidden_col);
		regout.OutRegColor(node,"readonly_col"    ,readonly_col);
		regout.OutRegBool (node,"use_back_col2"   ,use_back_col2);
//System.out.println("OutReg:"+normal_col);
	}
}

//ファイルリストセルレンダラー
public class JzFilerListCellRenderer extends JLabel implements ListCellRenderer<Object> {
	private JzFilerListCellRendererParam param;
	private String g_text_name;
	private String g_text_date;
	private String g_text_size;
	private CVECTOR g_back_col;
	private CVECTOR g_front_col;
	private UFile current_dir=null;
	//
	public JzFilerListCellRenderer(JzFilerListCellRendererParam param){
		this.param=param;
		setOpaque(true);
	}
	public void setParam(JzFilerListCellRendererParam param){
		this.param=param;
	}
	public void setCurrentDir(UFile _current_dir){
		current_dir=_current_dir;
	}
	public void paint(Graphics g){
		Dimension size=getSize();
		g.setColor(ConvAWT.CVEC2Color(g_back_col));
		g.fillRect(0,0,size.width,size.height);
		g.setColor(ConvAWT.CVEC2Color(g_front_col));
		//
		FontMetrics fm=g.getFontMetrics();
		int gap1=4;
		int gap2=8;
		int w_date =fm.stringWidth(g_text_date)+gap1;
		int w_date2=fm.stringWidth("0000/00/00 00:00:00")+(gap1+gap2);
		int w_size =fm.stringWidth(g_text_size);
		int w_size2=fm.stringWidth("000,000,000");
		
		int x_name=2;
		int x_date=size.width-w_date;
		int x_size=size.width-w_date2-w_size;
		int y_pos=size.height-2;
		
		g.drawString(g_text_name,x_name,y_pos);
		g.drawString(g_text_size,x_size,y_pos);
		g.drawString(g_text_date,x_date,y_pos);
	}
	public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus){
		int width =256;
		int height=15;
		//UFile current_dir=null;
		if(list!=null){
			Dimension p_size1=list.getSize();
			if(p_size1.width!=0){
				width=p_size1.width;
			}
			/*
			if(list instanceof JzFilerFileList){
				current_dir=((JzFilerFileList)list).GetCurrentDir();
			}
			*/
		}
		Dimension size=getPreferredSize();
		size.width =width;
		size.height=height;
		setPreferredSize(size);
		String data ;
		CVECTOR select_front_col=param.select_front_col;
		CVECTOR select_back_col =param.select_back_col;
		CVECTOR select_back2_col=param.select_back2_col;
		CVECTOR[] base_back_col =param.base_back_col;
		CVECTOR normal_col      =param.normal_col;
		CVECTOR dir_col         =param.dir_col;
		CVECTOR hidden_col      =param.hidden_col;
		CVECTOR readonly_col    =param.readonly_col;
		boolean use_back_col2   =param.use_back_col2;
		CVECTOR front_col=normal_col;
		CVECTOR back_col;
//System.out.println(""+param.uid);
		if(value instanceof UFile){
			UFile filename=(UFile)value;
			boolean parent_dir_flg=false;
			if(current_dir!=null){
				UFile parent_dir=current_dir.getParentFile();
				if(parent_dir!=null){
					if(parent_dir.equals(filename)){
						parent_dir_flg=true;
					}
				}
			}
			String name="";
			if(parent_dir_flg){
				name="..";
			}else{
				name=filename.getFileBody();
				try{
					name=URLDecoder.decode(name,"UTF-8");
				}catch(Exception ex){
				}
			}
			/*
			String name="";
			try{
				//name=((UFileURL)filename).getFile().getName();
				//name=((UFileURL)filename).getFile().getCanonicalFile().toString();
				name=((UFileURL)filename).getFile().getAbsoluteFile().toString();
				
				 URLDecoder.decode( url.getFile(), "UTF-8" ) 
			}catch(Exception ex){
			}
			*/
			if(filename.isDirectory()){
			//ディレクトリ
				data ="["+name+"]";
				g_text_name=name;
				g_text_size="";
				g_text_date="<DIR>";
				front_col=dir_col;
				
				if(filename.isHidden()){
					front_col=hidden_col;
				}else if(!filename.canWrite()){
					front_col=readonly_col;
				}
			}else{
			//ファイル
				long filesize=filename.getSize();
				Date date=new Date(filename.lastModified());
				DateFormat df=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				data=name+"   ["+filesize+"byte]    ("+df.format(date)+")";
				front_col=normal_col;
				g_text_name=name;
				g_text_size=String.format("%,d",filesize);
				g_text_date=df.format(date);
				if(filename.isHidden()){
					front_col=hidden_col;
				}else if(!filename.canWrite()){
					front_col=readonly_col;
				}
			}
		}else{
			data = value.toString();
		}
		if(!isSelected){
		//選択されていない
			if(!use_back_col2){
				back_col=base_back_col[0];
			}else{
				//※奇数偶数行で色分けする?
				if ((index &1) == 0){
				//偶数
					back_col=base_back_col[0];
				}else{
				//奇数
					back_col=base_back_col[1];
				}
			}
		}else{
		//選択されている
			front_col=select_front_col;
			back_col =select_back_col;
			if(!list.isFocusOwner()){
				back_col=select_back2_col;
			}
		}
		g_front_col=front_col;
		g_back_col=back_col;
		return this;
	}
}

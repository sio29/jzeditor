/******************************************************************************
;	Vzファイラー、オプションデータ
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.ureg.*;

public class JzFilerOptionData{
	public JzFilerListCellRendererParam cellrenderer;
	public String[] current_dir;
	public String[] current_drive;
	//
	public JzFilerOptionData(){
		cellrenderer=new JzFilerListCellRendererParam();
	}
	public JzFilerOptionData(JzFilerOptionData src){
		cellrenderer=new JzFilerListCellRendererParam(src.cellrenderer);
	}
	public String GetCellRendererNode(String node_name){
		return node_name+="/cellrenderer";
	}
	public void ReadReg(RegOut regout,String node_name){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null){
System.out.println("ReadReg:"+node_name+"がない");
			return;
		}
		String cellrenderer_node=GetCellRendererNode(node_name);
		cellrenderer.ReadReg(regout,cellrenderer_node);
		
		
		//select_front_col=regout.ReadRegColor(node,"select_front_col"       ,select_front_col);
		//editor_filename=regout.ReadRegStr  (node,"select_front_col",select_front_col);
		//editor_opt_line=regout.ReadRegBool (node,"editor_opt_line",editor_opt_line);
		//doubleboot_port=regout.ReadRegInt  (node,"doubleboot_port",doubleboot_port);
		//col_back       =regout.ReadRegColor(node,"col_back"       ,col_back);
	}
	public void OutReg(RegOut regout,String node_name){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null){
System.out.println("OutReg:"+node_name+"がない");
			return;
		}
		String cellrenderer_node=GetCellRendererNode(node_name);
		cellrenderer.OutReg(regout,cellrenderer_node);
		
		//regout.OutRegColor(node,"select_front_col"       ,select_front_col);
		//regout.OutRegStr  (node,"editor_filename",editor_filename);
		//regout.OutRegBool (node,"editor_opt_line",editor_opt_line);
		//regout.OutRegInt  (node,"doubleboot_port",doubleboot_port);
		//regout.OutRegColor(node,"col_back"       ,col_back);
	}
	
}

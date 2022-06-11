/******************************************************************************
;	ÉåÉWÉXÉgÉä
******************************************************************************/
package sio29.jzeditor.backends.j2d.reg;

import sio29.ulib.ureg.*;
import sio29.ulib.ureg.backends.j2d.*;
import sio29.ulib.umat.*;
import sio29.ulib.umat.backends.j2d.*;

public class TextEditRegOut{
	//ì«Ç›çûÇ›
	public static boolean ReadReg(RegOut regout,String node_name,RegPara _regpara){
		JzEditorRegPara regpara=(JzEditorRegPara)_regpara;
		RegNode node=regout.GetRegNode(node_name);
		//regpara.window=ReadRegRect(node,"window",regpara.window);
		regpara.window=regout.ReadRegRect(node,"window",regpara.window);
//System.out.println("OutWindow("+regpara.window.x+","+regpara.window.y+")-("+regpara.window.width+","+regpara.window.height+")");
		return true;
		
	}
	//èëÇ´çûÇ›
	public static boolean OutReg(RegOut regout,String node_name,RegPara _regpara){
		JzEditorRegPara regpara=(JzEditorRegPara)_regpara;
//System.out.println("OutWindow("+regpara.window.x+","+regpara.window.y+")-("+regpara.window.width+","+regpara.window.height+")");
		RegNode node=regout.GetRegNode(node_name);
		regout.OutRegRect(node,"window",regpara.window);
		return true;
	}
}


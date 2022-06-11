/******************************************************************************
;	ƒŒƒWƒXƒgƒŠ
******************************************************************************/
package sio29.jzeditor.backends.j2d.reg;

import sio29.ulib.ureg.*;
import sio29.ulib.umat.*;

public class JzEditorRegPara extends RegPara{
	public IRECT window=new IRECT(0,0,640,480);
	public JzEditorRegPara(String _name){
		node_name=_name;
//		node_name="/JzEditor/preferences";
	}
}


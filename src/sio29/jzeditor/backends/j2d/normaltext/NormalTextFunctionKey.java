/******************************************************************************
;	�t�@���N�V�����L�[�y�C��
******************************************************************************/
package sio29.jzeditor.backends.j2d.normaltext;

import sio29.ulib.udlgbase.backends.j2d.udlg.*;
import sio29.ulib.udlgbase.backends.j2d.udlg.functionkey.*;

import sio29.jzeditor.backends.j2d.*;

public class NormalTextFunctionKey{
	final static FunctionKeyPane.Function[][] def_func1=
	{{
		new FunctionKeyPane.Function("̧��",JzEditorAction.Open) ,new FunctionKeyPane.Function("����",JzEditorAction.SelectNextTab) ,new FunctionKeyPane.Function("����",JzEditorAction.SelectDoc) ,new FunctionKeyPane.Function("����"),
		new FunctionKeyPane.Function("�L��") ,new FunctionKeyPane.Function("����") ,new FunctionKeyPane.Function("�u��") ,new FunctionKeyPane.Function("���"),
		new FunctionKeyPane.Function("�ݻ��"),new FunctionKeyPane.Function("��ۯ�"),new FunctionKeyPane.Function("�߰��"),null,//new FunctionKeyPane.Function("")
	},{
		new FunctionKeyPane.Function("�ݒ�") ,new FunctionKeyPane.Function("")   ,new FunctionKeyPane.Function("��r") ,null,//new FunctionKeyPane.Function(""),
		new FunctionKeyPane.Function("����") ,new FunctionKeyPane.Function("")   ,new FunctionKeyPane.Function("����2"),new FunctionKeyPane.Function("��߰"),
		new FunctionKeyPane.Function("�߰��"),new FunctionKeyPane.Function("���"),new FunctionKeyPane.Function("")     ,null,//new FunctionKeyPane.Function(""),
	}};
	public static FunctionKeyPane.Function[][] getFunctions(){
		return def_func1;
	}
}

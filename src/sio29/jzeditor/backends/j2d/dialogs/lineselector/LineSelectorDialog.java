/******************************************************************************
;	�s�ԍ��I��
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.lineselector;

import java.awt.Component;

import sio29.ulib.udlgbase.backends.j2d.udlg.*;

public class LineSelectorDialog{
	//�s�ԍ��I��
	public static int Open(Component parent,String title,int line_num,final LineSelectOption opt){
		//���X�g�ɍs�ԍ��ꗗ�ݒ�
		IntValField list=new IntValField(1,line_num);
		//�_�C�A���O�I�[�v��
		if(!OptionDialogBase.Open(parent,title,list,list)){
			return -1;
		}else{
		//�I�����ꂽ
			return list.GetVal();
		}
	}
}

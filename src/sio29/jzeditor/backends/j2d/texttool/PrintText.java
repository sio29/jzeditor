/******************************************************************************
;	�e�L�X�g�̈��
******************************************************************************/
package sio29.jzeditor.backends.j2d.texttool;

import java.text.MessageFormat;
import javax.swing.JTextArea;

import sio29.ulib.ufile.*;
import sio29.ulib.udlgbase.*;
import sio29.ulib.udlgbase.backends.j2d.*;

public class PrintText{
	public static void print(String text,UFile filename) throws Exception {
		//try{
System.out.println("OnPrint");
			//textArea.print();
			// �e�L�X�g�E�R���|�[�l���g�̈��
			// �w�b�_�[�C�t�b�^�[�t��
			//textArea.print(new MessageFormat(filename),new MessageFormat("Page {0}"));
			
			JTextArea print_text=new JTextArea();
			//textArea.print();
			print_text.setTabSize(4);
			print_text.setText(text);
			//print_text.print();
			print_text.print(new MessageFormat(filename.getLocalFilename()),new MessageFormat("Page {0}"));
			
			
			/*
			// doc�t���[�o�ƈ���v�������ɑΉ����Ă������T�[�r�X�ꗗ�擾
			PrintService[] printServices = PrintServiceLookup.lookupPrintServices(docFlavor, printRequestAttributeSet);
			// ����_�C�A���O��\�����đI�������o�͐�𓾂�
			PrintService prin
			tService = ServiceUI.printDialog(null, 100, 100, printServices, printServices[0], docFlavor, printRequestAttributeSet);
			*/
	}
}

/******************************************************************************
;	Vz�t�@�C���[�A�v���r���[�E�B���h�E
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler.preview;

import java.awt.Component;

import sio29.ulib.ufile.*;

//====================
//�r�����[�C���^�[�t�F�[�X
public interface JzFilerPreviewViwerDispoeable{
	public void dispose();
	public JzFilerPreviewViwerCreater getCreator();
//	public void reload(UFile filename);
	public Component getComponent();
	public void setData(UFile filename,Object data);
	public void clearData();
	public UFile getFilename();
}

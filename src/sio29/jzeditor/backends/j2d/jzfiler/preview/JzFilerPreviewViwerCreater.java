/******************************************************************************
;	Vz�t�@�C���[�A�v���r���[�E�B���h�E
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler.preview;

import java.awt.Component;

import sio29.ulib.ufile.*;

//====================
//�r�����[�쐬�C���^�[�t�F�[�X
public interface JzFilerPreviewViwerCreater{
	public boolean isSupport(UFile filename);
//	public JzFilerPreviewViwer createViewer();
//	public Component createViewer(UFile filename);
//	public JzFilerPreviewViwerDispoeable createViewer(UFile filename);
	public JzFilerPreviewViwerDispoeable createViewer();
	public JzFilerPreviewDataLoader createDataLoader(UFile filename);
	public void disposeData(UFile filename,Object data);
}

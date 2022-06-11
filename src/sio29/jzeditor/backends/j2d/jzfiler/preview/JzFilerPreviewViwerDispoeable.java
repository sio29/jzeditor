/******************************************************************************
;	Vzファイラー、プレビューウィンドウ
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler.preview;

import java.awt.Component;

import sio29.ulib.ufile.*;

//====================
//ビュワーインターフェース
public interface JzFilerPreviewViwerDispoeable{
	public void dispose();
	public JzFilerPreviewViwerCreater getCreator();
//	public void reload(UFile filename);
	public Component getComponent();
	public void setData(UFile filename,Object data);
	public void clearData();
	public UFile getFilename();
}

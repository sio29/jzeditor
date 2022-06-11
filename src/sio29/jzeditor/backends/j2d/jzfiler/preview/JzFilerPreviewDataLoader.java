/******************************************************************************
;	Vzファイラー、プレビューウィンドウ
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler.preview;

import java.awt.Component;

import sio29.ulib.ufile.*;

/*
public interface JzFilerPreviewViwer{
	public boolean load(UFile filename);
	public Component getComponent();
//	public void dispose();
}
*/
public interface JzFilerPreviewDataLoader{
	public boolean load(UFile filename);
	public Object getData();
	public JzFilerPreviewViwerCreater getCreator();
	public void dispose();
}


/******************************************************************************
;	GrepƒIƒvƒVƒ‡ƒ“
******************************************************************************/
package sio29.jzeditor.backends.j2d.dialogs.grep;

import sio29.ulib.ufile.*;

public interface JzGrepFunc{
	public void addFile(UFile file);
	public void addString(UFile file,int line,String m);
}

/******************************************************************************
;	Jz�t�@�C���[�R�}���h���
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler;

import sio29.ulib.udlgbase.backends.j2d.udlg.actiontool.*;

public class JzFilerActionInfo{
	public final static ActionInfo[] g_actioninfo_tbl={
		new ActionInfo(JzFilerAction.OpenEditor			,"�G�f�B�^�[�ŊJ��"),
		new ActionInfo(JzFilerAction.OpenOther			,"�֘A�A�v���ŊJ��"),
		new ActionInfo(JzFilerAction.OpenReadOnly		,"�{��"),
		new ActionInfo(JzFilerAction.Prop				,"�v���p�e�B"),
		new ActionInfo(JzFilerAction.Rename				,"���O�̕ύX"),
		new ActionInfo(JzFilerAction.Attr				,"�A�g�����r�[�g�̕ύX"),
		new ActionInfo(JzFilerAction.Date				,"���t�̕ύX"),
		new ActionInfo(JzFilerAction.FileShortcut		,"�V���[�g�J�b�g�̍쐬"),
		new ActionInfo(JzFilerAction.Close				,"�I��"),
		new ActionInfo(JzFilerAction.Copy				,"�R�s�["),
		new ActionInfo(JzFilerAction.Move				,"�ړ�"),
		new ActionInfo(JzFilerAction.Delete				,"�폜"),
		new ActionInfo(JzFilerAction.RenameCopy			,"���O��ς��ăR�s�["),
		new ActionInfo(JzFilerAction.SelectAll			,"���ׂđI��"),
		new ActionInfo(JzFilerAction.SelectAllFile		,"���ׂẴt�@�C����I��"),
		new ActionInfo(JzFilerAction.SelectFlip			,"�I���̐؂�ւ�"),
		new ActionInfo(JzFilerAction.UnSelect			,"�I���̉���"),
		new ActionInfo(JzFilerAction.CopyFilename		,"�t�@�C�������N���b�v�{�[�h�ɃR�s�["),
		new ActionInfo(JzFilerAction.SortFilename		,"�t�@�C������"),
		new ActionInfo(JzFilerAction.SortExt			,"�g���q��"),
		new ActionInfo(JzFilerAction.SortDate			,"���t��"),
		new ActionInfo(JzFilerAction.SortSize			,"�T�C�Y��"),
		new ActionInfo(JzFilerAction.SortDescending		,"�~��"),
		new ActionInfo(JzFilerAction.SortAscending		,"����"),
		new ActionInfo(JzFilerAction.NewFile			,"�V�K�t�@�C���쐬"),
		new ActionInfo(JzFilerAction.NewDir				,"�V�K�t�H���_�쐬"),
		new ActionInfo(JzFilerAction.ChangeDir			,"�h���C�u�̕ύX"),
		new ActionInfo(JzFilerAction.ChangeDirParent	,"�e�f�B���N�g���ֈړ�"),
		new ActionInfo(JzFilerAction.OpenDir			,"�t�H���_���J��"),
		new ActionInfo(JzFilerAction.OpenDirHist		,"�ŋߎg�����t�H���_"),
		new ActionInfo(JzFilerAction.OpenDirList		,"�t�H���_���X�g"),
		new ActionInfo(JzFilerAction.CompareDir			,"�t�H���_�̔�r"),
		new ActionInfo(JzFilerAction.MaskList			,"�}�X�N���X�g"),
		new ActionInfo(JzFilerAction.OpenNewFile		,"�V�K�e�L�X�g���J��"),
		new ActionInfo(JzFilerAction.FileHist			,"�ŋߎg�����t�@�C��"),
		new ActionInfo(JzFilerAction.Grep				,"Grep"),
		new ActionInfo(JzFilerAction.Setting			,"�ݒ�"),
		new ActionInfo(JzFilerAction.KeyShortcut		,"�L�[�̃V���[�g�J�b�g"),
		new ActionInfo(JzFilerAction.SetReadOnly		,"�{���ŊJ��"),
		new ActionInfo(JzFilerAction.SetOpenOther		,"�֘A�A�v���P�[�V�����ŊJ��"),
		new ActionInfo(JzFilerAction.Set2Window			,"2�E�B���h�E"),
		new ActionInfo(JzFilerAction.Update				,"�ŐV���ɍX�V"),
		new ActionInfo(JzFilerAction.SetPreview			,"�v���r���["),
		new ActionInfo(JzFilerAction.NewWindow			,"�V�����E�B���h�E"),
		new ActionInfo(JzFilerAction.Help				,"�w���v"),
		new ActionInfo(JzFilerAction.Version			,"�o�[�W�������"),
		new ActionInfo(JzFilerAction.FileFTPInfo		,"FTP�o�^"),
		
	};
	public static ActionInfo[] getActionInfoTbl(){
		return g_actioninfo_tbl;
	}
}

/******************************************************************************
;	�X�e�[�^�X�o�[
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline;

import sio29.ulib.udlgbase.backends.j2d.udlg.statusbar.*;

public class CommandLineStatusBar{
	public final static String POS="pos";
	public final static String INS="ins";
	public final static String UPDATE="update";
	public final static String SELECT="select";
	public final static String SEARCHF="searchf";
	public final static String MACRO="macro";
	public final static String STR="str";
	
	private final static StatusBarPane.StateLabel[] labels=new StatusBarPane.StateLabel[]{
		new StateLabelPos(),
//		new StateLabelIns(),
//		new StateLabelUpdate(),
//		new StateLabelSelect(),
//		new StateLabelSearchF(),
//		new StateLabelMacro(),
//		new StateLabelStr()
	};
	public final static StatusBarPane.StateLabel[] getLabels(){
//System.out.println("CommandLineStatusBar.getLabels()");
//		return labels;
		return new StatusBarPane.StateLabel[]{
			new StateLabelPos(),
//			new StateLabelIns(),
//			new StateLabelUpdate(),
//			new StateLabelSelect(),
//			new StateLabelSearchF(),
//			new StateLabelMacro(),
//			new StateLabelStr()
		};
	}
	//Pos
	private static class StateLabelPos extends StatusBarPane.StateLabel{
		public final static String TYPE="pos";
		public StateLabelPos(){
			super();
		}
		public String getLabelType(){
			return TYPE;
		}
		public void printState(Object n){
			setText((String)n);
		}
		public int getLabelWidth(){
			return StatusBarPane.LWIDTH*2;
		}
	}
	/*
	//Ins
	private static class StateLabelIns extends StatusBarPane.StateLabel{
		public final static String TYPE="ins";
		public String getLabelType(){
			return TYPE;
		}
		private static final String[] ins_m={"�}��","�㏑��"};
		public void printState(Object n){
			String m=ins_m[(Boolean)n?1:0];
			setText(m);
		}
		public int getLabelWidth(){
			return StatusBarPane.SWIDTH;
		}
	}
	//Update
	private static class StateLabelUpdate extends StatusBarPane.StateLabel{
		public final static String TYPE="update";
		public String getLabelType(){
			return TYPE;
		}
		public void printState(Object n){
			setText((String)n);
		}
		public int getLabelWidth(){
			return StatusBarPane.SWIDTH;
		}
	}
	//Select
	private static class StateLabelSelect extends StatusBarPane.StateLabel{
		public final static String TYPE="select";
		public String getLabelType(){
			return TYPE;
		}
		private static final String[] select_m={"","�I��"};
		public void printState(Object n){
			String m=select_m[(Boolean)n?1:0];
			setText(m);
		}
		public int getLabelWidth(){
			return StatusBarPane.SWIDTH;
		}
	}
	//SearchF
	private static class StateLabelSearchF extends StatusBarPane.StateLabel{
		public final static String TYPE="searchf";
		public String getLabelType(){
			return TYPE;
		}
		private static final String[] search_m={"","����"};
		public void printState(Object n){
			String m=search_m[(Boolean)n?1:0];
			setText(m);
		}
		public int getLabelWidth(){
			return StatusBarPane.SWIDTH;
		}
	}
	//Macro
	private static class StateLabelMacro extends StatusBarPane.StateLabel{
		public final static String TYPE="macro";
		public String getLabelType(){
			return TYPE;
		}
		private static final String[] macro_m={"�}�N���L�^�J�n","�}�N���L�^�I��","�}�N���Đ�"};
		public void printState(Object n){
			String m=macro_m[(Integer)n];
			setText(m);
		}
		public int getLabelWidth(){
			return StatusBarPane.MWIDTH;
		}
	}
	//Str
	private static class StateLabelStr extends StatusBarPane.StateLabel{
		public final static String TYPE="str";
		public String getLabelType(){
			return TYPE;
		}
		public void printState(Object n){
			setText((String)n);
		}
		public int getLabelWidth(){
			return StatusBarPane.AWIDTH;
		}
	}
	*/
}

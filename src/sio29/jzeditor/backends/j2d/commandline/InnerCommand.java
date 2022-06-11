/******************************************************************************
;	内部コマンド
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline;

public class InnerCommand{
	public String name;				//コマンド名
	public Object class_p;
	public String proc_name;			//関数名
	public String new_opt;				//新規オプションの文字列、ない場合はnull
	public InnerCommand(String name,Object class_p,String proc_name){
		this.name=name;
		this.class_p=class_p;
		this.proc_name=proc_name;
		this.new_opt=null;
	}
	public InnerCommand(String name,Object class_p,String proc_name,String new_opt){
		this.name=name;
		this.class_p=class_p;
		this.proc_name=proc_name;
		this.new_opt=new_opt;
	}
}

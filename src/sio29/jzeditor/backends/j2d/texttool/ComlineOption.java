/******************************************************************************
;	�R�}���h���C���I�v�V����
******************************************************************************/
package sio29.jzeditor.backends.j2d.texttool;

import java.util.ArrayList;

import sio29.ulib.ufile.*;
import sio29.ulib.ufile.url.*;

public class ComlineOption{
	public boolean shell_flg=false;
	public boolean help_flg=false;
	public boolean filer_flg=false;
	public boolean reset_reg=false;
	public ArrayList<UFile> start_filenames=new ArrayList<UFile>();
	//
	public static ComlineOption parseArgs(String[] args){
		ComlineOption comline_option=new ComlineOption();
		for(int i=0;i<args.length;i++){
			String m=args[i];
			if(m==null)continue;
			if(m.length()==0)continue;
			char m0=m.charAt(0);
			if(m0=='-' || m0=='/'){
				if(m.length()>=2){
					String m1=m.substring(1).toLowerCase();
					if(m1.equals("h") || m1.equals("help") || m1.equals("?")){
						comline_option.help_flg=true;
						System.out.println("JzEditor ver 1.00 by sio29 since 2015-");
						System.out.println("usage)-,/option filename1 filename2 ...");
						System.out.println(" -help �w���v�̕\��");
						System.out.println(" -shell �V�F���̗����グ");
						System.out.println(" -filer �t�@�C���[�̗����グ");
						System.out.println(" -reset_reg ���W�X�g��������");
						System.exit(0);
					}else if(m1.equals("shell")){
						comline_option.shell_flg=true;
					}else if(m1.equals("filer")){
						comline_option.filer_flg=true;
					}else if(m1.equals("reset_reg")){
						comline_option.reset_reg=true;
					}else{
						System.out.println(m+"�͖��Ή��̃I�v�V�����ł�");
						System.exit(1);
					}
				}
			}else{
				try{
					UFile ufilename=UFileBuilder.createLocal(m);
					comline_option.start_filenames.add(ufilename);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		return comline_option;
	}
}

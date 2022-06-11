/******************************************************************************
;	FTPŠÖŒW
******************************************************************************/
package sio29.jzeditor.backends.j2d.jzfiler.ftp;

import java.util.ArrayList;

import sio29.ulib.ureg.*;
import sio29.ulib.ufile.*;
import sio29.ulib.ufile.backends.j2d.ftp.*;

public class FTPInfoList{
	public final static int INFO_MAX=100;
	private ArrayList<UFileFTPInfo> list=new ArrayList<UFileFTPInfo>();
	//
	public FTPInfoList(){}
	public int size(){
		return list.size();
	}
	public UFileFTPInfo get(int i){
		if(i<0 || i>=list.size())return null;
		return list.get(i);
	}
	public void add(UFileFTPInfo n){
		list.add(n);
	}
	public void remove(UFileFTPInfo n){
		list.remove(n);
	}
	public UFileFTPInfo[] toArray(){
		return list.toArray(new UFileFTPInfo[]{});
	}
	public String GetChildNodeName(String node_name,int i){
		return node_name+String.format("/FTPInfo%03d",i);
	}
	public void ReadReg(RegOut regout,String node_name){
		for(int i=0;i<INFO_MAX;i++){
			String name=GetChildNodeName(node_name,i);
			RegNode node=regout.GetRegNode(name);
			if(node==null)continue;
			UFileFTPInfo infoone=new UFileFTPInfo();
			ReadRegUFileFTPInfo(regout,name,infoone);
			if(!infoone.isUsing()){
				try{
					node.removeNode();
				}catch(Exception ex){
					System.out.println("íœŽ¸”s:"+node);
				}
			}else{
				add(infoone);
			}
		}
	}
	public void OutReg(RegOut regout,String node_name){
		for(int i=0;i<INFO_MAX;i++){
			String name=GetChildNodeName(node_name,i);
			UFileFTPInfo infoone=get(i);
			if(infoone!=null){
				if(!infoone.isUsing()){
					infoone=null;
				}
			}
			if(infoone==null){
				RegNode node=regout.GetRegNode(name);
				if(node!=null){
					try{
						node.removeNode();
					}catch(Exception ex){
						System.out.println("íœŽ¸”s:"+node);
					}
				}
			}else{
				OutRegUFileFTPInfo(regout,name,infoone);
			}
		}
	}
	public static void ReadRegUFileFTPInfo(RegOut regout,String node_name,UFileFTPInfo infoone){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		infoone.setTitle   (regout.ReadRegStr(node,"name"    ,infoone.getTitle()));
		infoone.setServer  (regout.ReadRegStr(node,"server"  ,infoone.getServer()));
		infoone.setUser    (regout.ReadRegStr(node,"user"    ,infoone.getUser()));
		infoone.setPassword(regout.ReadRegStr(node,"password",infoone.getPassword()));
	}
	public static boolean isNull(String n){
		if(n==null)return true;
		if(n.length()==0)return true;
		return false;
	}
	public static void OutRegStrSub(RegOut regout,RegNode node,String param_name,String param){
		if(!isNull(param)){
			regout.OutRegStr(node,param_name,param);
		}else{
			node.remove(param_name);
		}
	}
	public static void OutRegUFileFTPInfo(RegOut regout,String node_name,UFileFTPInfo infoone){
		RegNode node=regout.GetRegNode(node_name);
		if(node==null)return;
		OutRegStrSub(regout,node,"name"    ,infoone.getTitle());
		OutRegStrSub(regout,node,"server"  ,infoone.getServer());
		OutRegStrSub(regout,node,"user"    ,infoone.getUser());
		OutRegStrSub(regout,node,"password",infoone.getPassword());
	}
}


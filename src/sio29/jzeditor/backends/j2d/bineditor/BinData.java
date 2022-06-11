/******************************************************************************
;	バイナリデータ
******************************************************************************/
package sio29.jzeditor.backends.j2d.bineditor;

import sio29.ulib.umat.*;

public class BinData{
	private byte[] bindata=new byte[0];
	
	public byte get(int i){
		return bindata[i];
	}
	public void set(int i,byte n){
		bindata[i]=n;
	}
	public int getSize(){
		return bindata.length;
	}
	public void setBytes(byte[] n){
		bindata=n;
	}
	public byte[] getBytes(){
		return bindata;
	}
	public byte[] getBytes(int offset,int length){
		return UArrays.copyOfRange(bindata,offset,offset+length);
	}
	public byte[] RemoveDataRange(int pos,int size){
		if(pos>=bindata.length)return null;
		byte[] data=getBytes(pos,size);
		for(int i=0;i<size;i++){
			bindata[pos+i]=bindata[pos+size+i];
		}
		bindata=UArrays.copyOf(bindata,bindata.length-size);
		return data;
	}
	public void AddDataRange(int pos,byte[] data){
		int size=data.length;
		bindata=UArrays.copyOf(bindata,bindata.length+size);
		for(int i=(bindata.length-1);i>=(bindata.length-size);i--){
			bindata[i]=bindata[i-size];
		}
		for(int i=0;i<size;i++){
			bindata[i+pos]=data[i];
		}
	}
	public void resize(int size){
		bindata=UArrays.copyOf(bindata,size);
	}
	public void deleteBytes(int top,int cut_size){
		int size=getSize();
		if(size==0)return;
		if(cut_size<=0)return;
		int new_size=size-cut_size;
		byte[] newdata=new byte[new_size];
		for(int i=0;i<top;i++){
			newdata[i]=get(i);
		}
		int bottom=top+cut_size;
		int left_size=size-bottom;
		for(int i=0;i<left_size;i++){
			int src=i+bottom;
			int dst=i+top;
			newdata[dst]=get(src);
		}
		setBytes(newdata);
	}
	void insertBytes(int top,byte[] ins_data){
		int size=getSize();
		int ins_size=ins_data.length;
		int new_size=size+ins_size;
		byte[] new_data=new byte[new_size];
		for(int i=0;i<top;i++){
			new_data[i]=get(i);
		}
		for(int i=0;i<ins_size;i++){
			int dst=top+i;
			int src=i;
			new_data[dst]=ins_data[src];
		}
		int left_size=size-top;
		for(int i=0;i<left_size;i++){
			int dst=top+ins_size+i;
			int src=top+i;
			new_data[dst]=get(src);
		}
		setBytes(new_data);
	}
	public void overwriteBytes(int top,byte[] ins_data){
		int size=getSize();
		int ins_size=ins_data.length;
		int new_size=top+ins_size;
		byte[] new_data=bindata;
		if(new_size>size){
			new_data=new byte[new_size];
			for(int i=0;i<top;i++){
				new_data[i]=get(i);
			}
		}
		for(int i=0;i<ins_size;i++){
			int dst=top+i;
			int src=i;
			new_data[dst]=ins_data[src];
		}
		setBytes(new_data);
	}
}


/******************************************************************************
;	“à•”ƒRƒ}ƒ“ƒh
******************************************************************************/
package sio29.jzeditor.backends.j2d.commandline;

import java.util.HashMap;

public interface CommandAlias{
	public String getName();
}

class CommandAliasList{
	private HashMap<String,CommandAlias> map=new HashMap<String,CommandAlias>();
	public void addCommandAlias(CommandAlias n){
		map.put(n.getName(),n);
	}
}

class CommandAlias_Vz implements CommandAlias{
	public String getName(){
		return "vz";
	}
	
}
class CommandAlias_FD implements CommandAlias{
	public String getName(){
		return "fd";
	}
	
}


package com.code.raker.cmd;

import us.codecraft.webmagic.Page;

import com.code.metadata.raker.RakerMotion;
import com.code.metadata.raker.RakerTextRule;
import com.code.metadata.raker.motion.RakerMotionLogin;
import com.code.raker.RakerCmd;

public class CmdLogin  extends AbstractCmd implements RakerCmd{

	
	@Override
	public void execute(final String url,final Page page) {
		if(rakerSchema.getMotionLogin()!= null ){
			return;
		}
		
		
		
	}

	@Override
	public Page submit(final String url,final Page page) {
		
		return null;
	}
	


}

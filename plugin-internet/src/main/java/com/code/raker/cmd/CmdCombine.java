package com.code.raker.cmd;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import us.codecraft.webmagic.Page;

import com.code.raker.RakerActor;
import com.code.raker.RakerCmd;
import com.code.raker.RakerCmdFactory;
/**
 * 使用责任链模式提供，可传递的命令
 * @author feizaizheli9203
 *
 */
public class CmdCombine extends AbstractCmd implements RakerCmd{

	private Set<RakerCmd> rakerCmds = new HashSet<RakerCmd>();
	
	private static Logger _LOG = Logger.getLogger(CmdCombine.class);

	public CmdCombine() {

	}
	
	public CmdCombine(RakerActor rakerActor) {
		super(rakerActor);
	}
	
	@Override
	public void execute(final String url,final Page page) {

		if(rakerCmds.size() == 0){
			return;
		}
		for(RakerCmd rakerCmd:rakerCmds){
			_LOG.trace("命令【"+rakerCmd.getName()+"】:开始执行");
			rakerCmd.execute(url,page);
		}

	}

	@Override
	public Page submit(final String url,final Page page) {
		execute(url,page);
		return page;
	}
	
	@Override
	public void putRakerCmd(RakerCmd rakerCmd) {
		rakerCmd.setRakerActor(this.rakerActor);
		rakerCmds.add(rakerCmd);
	}
	
	public void putRakerCmd(RakerCmd.CMD key){
		RakerCmd cmd = RakerCmdFactory.create(key);
		putRakerCmd(cmd);
	}


	
}

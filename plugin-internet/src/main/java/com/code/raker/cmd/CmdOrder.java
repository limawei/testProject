package com.code.raker.cmd;

import us.codecraft.webmagic.Page;

import com.code.raker.RakerCmd;
/**
 * 使用责任链模式提供，可传递的命令
 * @author feizaizheli9203
 *
 */
public class CmdOrder extends AbstractCmd implements RakerCmd{

	@Override
	public void execute(final String url,final Page page) {
		if(this.getNext() == null){
			return;
		}
		RakerCmd nextCmd = this.getNext();
		if(nextCmd!=null){
			nextCmd.execute(url,page);
			this.setNext(nextCmd.getNext());
		}
		execute(url,page);
	}

	@Override
	public Page submit(final String url,final Page page) {
		// TODO Auto-generated method stub
		return null;
	}


	public static void main(String[] args) {
		RakerCmd order = new CmdOrder();
		
		
	}
	
}

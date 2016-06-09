package com.code.raker.cmd;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.utils.UrlUtils;

import com.code.metadata.raker.RakerClue;
import com.code.raker.RakerActor;
import com.code.raker.RakerCmd;
import com.code.raker.RakerRedis;

public class CmdFindClue extends AbstractCmd implements RakerCmd{

	private static Logger LOG = Logger.getLogger(CmdFindClue.class);
	
	public CmdFindClue() {
	}
	

	public CmdFindClue(RakerActor rakerActor, Page page) {
		super(rakerActor);

	}
	

	@Override
	public void execute(final String url,final Page page) {
		List<String> links = page.getHtml().links().all();
		if(links == null || links.size() == 0 ){
			return;
		}
		if(rakerSchema.getClues() == null ||rakerSchema.getClues().size() < 0){
			for(RakerClue clue:rakerSchema.getClues()){
				for(String link:links){
					if(isMatchClue(link,clue)){
						rakerRedis.pushQuene(RakerRedis.Key.URLS_WAIT, link);
					}	
				}
			}
		}else{
			
			for(String link:links){
				if(UrlUtils.isMatch(link, rakerActor.getDomain())){
					//LOG.info("待爬队列加入URL:"+link);
					rakerRedis.pushQuene(RakerRedis.Key.URLS_WAIT, link);
				}	
			}
		}
		

	}
	
	public boolean isMatchClue(String url,RakerClue clue) {
		if(StringUtils.isNotBlank(url) && url.contains(rakerActor.getDomain())){
			return true;
		}
		return false;
	}
	@Override
	public Page submit(final String url,final Page page) {
		execute(url,page);
		return page;
	}
	

}

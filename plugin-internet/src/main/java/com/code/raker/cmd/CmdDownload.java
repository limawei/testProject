package com.code.raker.cmd;

import org.apache.log4j.Logger;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;

import com.code.raker.RakerActor;
import com.code.raker.RakerCmd;
import com.code.raker.RakerRedis;

public class CmdDownload extends AbstractCmd implements RakerCmd{

	

//	public static final String chromeDriverPath = "D:\\spider\\chormedriver\\chromedriver.exe";
	
	private static final Logger _LOG = Logger.getLogger(CmdDownload.class);
	
	private String url ;
	
	
	public CmdDownload(){
	}

	public CmdDownload(RakerActor rakerActor) {
		super(rakerActor);
	}
	
	
	@Override
	public void execute(final String url,final Page page) {
		submit(url,page);
		
	}
	@Override
	public Page submit(final String url,final Page page) {
		rakerRedis.addSet(RakerRedis.Key.URLS_RUN, url);
		Request request = new Request(url);
		Page newPage = null;
		try {
			newPage = rakerActor.getDownLoader().download(request,rakerActor.getTask());
		} catch (Exception e) {
			e.printStackTrace();
			_LOG.error("下载[url:"+url+"]异常,从再爬集合移除,并加入异常队列."+e.toString());	
			rakerActor.getRakerRedis().addErrorAndRemoveRun(url);
		}
		return newPage;
		
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	


}

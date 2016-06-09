package com.code.raker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpHost;
import org.apache.log4j.Logger;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;

import com.code.metadata.raker.RakerSchema;
import com.code.metaservice.core.GlobalCfg;
import com.code.raker.cmd.CmdCombine;
import com.code.raker.cmd.CmdDownload;

public class RakerActor {
	
	private static final Logger _LOG = Logger.getLogger(RakerActor.class);

	private RakerSchema rakerSchema ;
	
	private RakerRedis rakerRedis;
	
	private String domain;
	
	private Downloader downLoader = null;
	
	private Task task; 
	
	public static String chromeDriverPath = "D:\\spider\\chormedriver\\chromedriver.exe";

	protected GlobalCfg globalCfg;
	
	private Site site = null;
	
	private RakerCmd cmdDownload = null;
	
	private CmdCombine cmdPage = null;
	
	public RakerActor(RakerSchema rakerSchema ,String host){
		
		this.rakerSchema = rakerSchema;
		this.domain = rakerSchema.getDomain();
		rakerRedis = new RakerRedis(host,domain);
		downLoader = DownloadBuilder.build(rakerSchema.getDynamic());
		init();
	}

	public void init() {
	//	chromeDriverPath = globalCfg.getChromeDriverPath();
		
		/**初始化Site*/
		
		this.site = Site.me().setDomain(domain);
		/*if(rakerSchema.getShield()){
			List<String[]>  httpProxyList = new ArrayList<String[]>();
			String host = "192.168.4.26:808";
			httpProxyList.add(host.split(":"));
			site.setHttpProxyPool(httpProxyList);
		}*/
		site.setHttpProxy(new HttpHost("192.168.4.26",808));
		task = new RakerTask(site);
		
		/**初始化URL队列*/
		for(String url:rakerSchema.getStartUrls()){
			if(rakerRedis.isDuplicate(RakerRedis.Key.URLS_REMOVE,url)){
				rakerRedis.pushQuene(RakerRedis.Key.URLS_WAIT, url);
			}
		}
		
		cmdDownload = new CmdDownload(this);
		cmdPage = new CmdCombine(this);
		cmdPage.putRakerCmd(RakerCmd.CMD.FIND_CLUE);
		if(rakerSchema.getMotionLogin()!=null){
			cmdPage.putRakerCmd(RakerCmd.CMD.LOGIN);
		}
		
	}

	
	public Page downLoad(final String url){
		return cmdDownload.submit(url, null);
	}
	
	public void handlePage(final String url,final Page page){
		cmdPage.execute(url, page);
	}

	public void execute(RakerCmd cmd,final String url,final Page page){
		cmd.setRakerActor(this);
		cmd.execute(url,page);
	}
	
	public static class RakerTask implements Task{

		
		private Site site;
		
		public RakerTask(Site site) {
			this.site = site;
		}

		@Override
		public String getUUID() {
			return site.getDomain()+"_"+UUID.randomUUID().toString().replace("-", "");
		}

		@Override
		public Site getSite() {
		
			return site;
		}

	}
	
	public static void main(String[] args) {
		System.out.println(UUID.randomUUID().toString().replace("-", ""));
	}
	
	public static class DownloadBuilder{
		
		public static Downloader build(boolean dynamic){
			
			if(dynamic){
				
				//chromeDriverPath = globalCfg.getChromeDriverPath();
				SeleniumDownloader seleniumDownloader = new SeleniumDownloader(chromeDriverPath);
				seleniumDownloader.setThread(5);
				return seleniumDownloader;
			}else{
				return new HttpClientDownloader();
			}
		}
	}

	
	
	
	
	public RakerSchema getRakerSchema() {
		return rakerSchema;
	}

	public void setRakerSchema(RakerSchema rakerSchema) {
		this.rakerSchema = rakerSchema;
	}

	public RakerRedis getRakerRedis() {
		return rakerRedis;
	}

	public void setRakerRedis(RakerRedis rakerRedis) {
		this.rakerRedis = rakerRedis;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Downloader getDownLoader() {
		return downLoader;
	}

	public void setDownLoader(Downloader downLoader) {
		this.downLoader = downLoader;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
	
	public GlobalCfg getGlobalCfg() {
		return globalCfg;
	}

	public void setGlobalCfg(GlobalCfg globalCfg) {
		this.globalCfg = globalCfg;
	}

	public RakerCmd getCmdDownload() {
		return cmdDownload;
	}

	public void setCmdDownload(RakerCmd cmdDownload) {
		this.cmdDownload = cmdDownload;
	}

	public CmdCombine getCmdPage() {
		return cmdPage;
	}

	public void setCmdPage(CmdCombine cmdPage) {
		this.cmdPage = cmdPage;
	}
	
	
	
	
}

package com.code.raker;

import us.codecraft.webmagic.Page;



public interface RakerCmd {

	
	
	public void execute(final String url,final Page page);
	
	public Page submit(final String url,final Page page);
	
	public void setRakerActor(RakerActor rakerActor);
	
	public void setNext(RakerCmd cmd);
	
	public RakerCmd getNext();
	
	public void setName(String cmdName);
	
	public String getName();
	
	public static enum CMD{
		DOWNLOAD("downlaod","下载"),
		FIND_CLUE("find_clue","寻找线索"),
		LOGIN("login","模拟登陆");
		
		private String code;
		
		private String name;
		
		CMD(String code,String name){
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		
	}
}

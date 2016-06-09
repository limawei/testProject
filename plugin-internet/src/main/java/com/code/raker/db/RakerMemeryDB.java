package com.code.raker.db;

import java.util.HashMap;
import java.util.Map;

import com.code.metadata.raker.EnumDataRule;
import com.code.metadata.raker.RakerClue;
import com.code.metadata.raker.RakerSchema;
import com.code.metadata.raker.RakerTextRule;

/**
 * 爬虫内存数据库
 * @author feizaizheli9203
 *
 */
public class RakerMemeryDB {

	public final static String CODE = "SCHEMA_CODE";
	
	private static Map<String,RakerSchema> schemaBox = new HashMap<String,RakerSchema>();
	
	public static String TAOBAO_PL = "taobaoPL";
	public static String BAIDU_TB = "baiduTB";
	public static String LUNTAN_NJ = "luntanNJ";
	
	
	
	static {
		
		/** 百度贴吧*/
		RakerSchema_BaiduTB();
		
		/** 南京论坛*/
		RakerSchema_luntanNJ();
		
		/** 淘宝评论*/
		RakerSchema_taobaoPL();
		
	}
	
	public static RakerSchema get(String schemaCode){
		return schemaBox.get(schemaCode);
	}
	
	/** 百度贴吧*/
	public static void RakerSchema_BaiduTB(){
		
		RakerSchema baiduTB = new RakerSchema();
		baiduTB.addStartUrl("http://tieba.baidu.com/");
		baiduTB.setCode("baiduTB");
		baiduTB.setName("百度贴吧");
		baiduTB.setDomain("tieba.baidu.com");
		RakerTextRule topTheme = RakerTextRule.build("theme", "主题","//*[@id='j_core_title_wrap']/h3/text()");
		RakerTextRule pDiv = RakerTextRule.buildFunc("//*[@id='j_p_postlist']/div");
		
		//*[@id="j_p_postlist"]/div[2]/div[1]/ul/li[2]/a
		RakerTextRule authorName = RakerTextRule.build("author","作者", "//div[@class='d_author']/ul/li[3]/a");
		RakerTextRule content = RakerTextRule.build("content","正文内容", 
				"//div[@class='p_content']");
		RakerTextRule reply = RakerTextRule.build("content","正文内容", 
				"//div[@class='j_lzl_c_b_a core_reply_content']");

		pDiv.addChlid(authorName);
		pDiv.addChlid(content);
		pDiv.addChlid(reply);
		
		
		baiduTB.addTextRule(topTheme);
		baiduTB.addTextRule(pDiv);
		
		
		RakerClue indexClue = new RakerClue();
		indexClue.setClueType("nextIndex");
		RakerTextRule indexRule = new RakerTextRule();
		indexRule.setDataRule(EnumDataRule.INCLUDE.getCode());
		indexRule.setDataContent("tieba.baidu.com");
		
		baiduTB.addRakerClue(indexClue);
		schemaBox.put(baiduTB.getCode(), baiduTB);
	}
	
	/** 南京论坛*/
	public static void RakerSchema_luntanNJ(){
		
		RakerSchema luntanNJ = new RakerSchema();
		luntanNJ.addStartUrl("http://bbs.njrx.cc/thread-307906-1-1.html");
		luntanNJ.setCode("luntanNJ");
		luntanNJ.setName("南京论坛");
		luntanNJ.setDomain("bbs.njrx.cc");
		
		RakerTextRule topTheme = RakerTextRule.build("theme", "主题","//*[@id='thread_subject']");
		RakerTextRule pDiv = RakerTextRule.buildFunc("//div[@id='postlist']/div");
		
		//*[@id="j_p_postlist"]/div[2]/div[1]/ul/li[2]/a
		RakerTextRule authorName = RakerTextRule.build("author","作者", "//div[@class='authi']/a");
		RakerTextRule content = RakerTextRule.build("content","正文内容","//div[@class='t_fsz']");
		RakerTextRule date = RakerTextRule.build("date","时间", "//div[@class='authi']/em/text()");

		pDiv.addChlid(authorName);
		pDiv.addChlid(content);
		pDiv.addChlid(date);
		
		
		luntanNJ.addTextRule(topTheme);
		luntanNJ.addTextRule(pDiv);

		schemaBox.put(luntanNJ.getCode(), luntanNJ);
	}
	/** 淘宝评论*/
	public static void RakerSchema_taobaoPL(){
		
		RakerSchema taobaoPL = new RakerSchema();
		//https://item.taobao.com/item.htm?spm=a230r.1.14.182.Q1nblw&id=531131129473&ns=1&abbucket=4#detail
		
		taobaoPL.addStartUrl("https://s.taobao.com/search?q=%E6%89%8B%E6%9C%BA&imgfile=&commend=all&ssid=s5-e&search_type=item&sourceId=tb.index&spm=a21bo.50862.201856-taobao-item.2&ie=utf8&initiative_id=tbindexz_20160609");
		taobaoPL.setCode("taobaoPL");
		taobaoPL.setName("淘宝评论");
		taobaoPL.setDomain("www.taobao.com");
		taobaoPL.setDynamic(true);
		
		RakerTextRule shopName = RakerTextRule.build("shopName", "商品名称","//*[@id='J_Title']/h3");
		RakerTextRule shopPrice = RakerTextRule.build("shopPrice", "商品价格","//*[@id='J_StrPrice']/em[2]");
		RakerTextRule taobaoPrice = RakerTextRule.build("taobaoPrice", "淘宝价格","//*[@id='J_PromoPriceNum']");
		
		RakerTextRule pinDiv = RakerTextRule.buildFunc("//*[@id='reviews']/div/div/div/div[2]/ul/li");
		RakerTextRule pin = RakerTextRule.build("content","评论内容", "//div[1]/div[0]");

		pinDiv.addChlid(pin);
		
		
		taobaoPL.addTextRule(shopName);
		taobaoPL.addTextRule(shopPrice);
		taobaoPL.addTextRule(taobaoPrice);
		taobaoPL.addTextRule(pinDiv);
		schemaBox.put(taobaoPL.getCode(), taobaoPL);
	}

}	
	

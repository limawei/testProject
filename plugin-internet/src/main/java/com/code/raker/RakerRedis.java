package com.code.raker;

import java.util.Set;

import com.code.common.utils.assertion.Assert;
import com.code.memery.cache.ICacheAdmin;
import com.code.memery.cache.redis.CacheAdminRedis;
import com.code.metadata.raker.RakerSchema;

public class RakerRedis {

	private String host;
	/**key前缀*/
	private String keyPrefix;
	
	private ICacheAdmin scheduler = null;
	
	public RakerRedis(String host,String keyPrefix){
		init(host,keyPrefix);
	}
	 

	public void init(String host,String keyPrefix){
		Assert.hasText(host,"host不能为空");
		Assert.hasText(keyPrefix,"redis前缀不能为空");
		this.host = host;
		scheduler = new CacheAdminRedis(host);
		this.keyPrefix = keyPrefix;
	}
	
	public boolean isOver(){
		if(!isEmptySet(RakerRedis.Key.URLS_RUN) || 
		   !isEmptyQuene(RakerRedis.Key.URLS_WAIT)){
			return true;
		}
		return false;
	}

	
	public void addErrorAndRemoveRun(String url) {
		scheduler.removeSet(redisKey(Key.URLS_RUN),url);
		scheduler.addSet(redisKey(Key.URLS_ERROR),url);
	}
	/**
	 * redis(key=setKey ，value=set)删除set集合中制定url
	 * @param url
	 * @param setKey
	 */
	public void removeSet(Key key,String url){
		scheduler.removeSet(redisKey(key), url);
	}
		
	public boolean isDuplicate(Key key,String url){
		return scheduler.isDuplicate(redisKey(key), url);
	
	}

	/**
	 * redis(key=setKey ，value=set)弹出list链表中的数据
	 * @param url
	 * @param queueKey
	 */
	public String pollQuene(Key key){
		return scheduler.pollQuene(redisKey(key));
	}

	/**
	 * redis(key=setKey ，value=list)list中的插入url
	 * @param url
	 * @param queueKey
	 */
	public void pushQuene(Key key, String url){
		scheduler.pushQuene(redisKey(key), url);
	}
	
	/**
	 *  redis(key=setKey ，value=list)set集合插入数据url
	 * @param url
	 * @param setKey
	 */
	public void addSet(Key key,String url){
		scheduler.addSet(redisKey(key), url);
	}
	/**
	 * redis(key=setKey ，value=set)set集合中插入数据urls
	 * @param url
	 * @param setKey
	 */
    public void addSet(Key key,Set<String> urls){
    	scheduler.addSet(redisKey(key), urls);
    }
    
    /**
	 * redis(key=setKey ，value=list)，判断list是否为空
	 * @param url
	 * @param queneKey
	 */
    public boolean isEmptyQuene(Key key){
    	return scheduler.isEmptyQuene(redisKey(key));
    }
    
    /**
	 * redis(key=setKey ，value=set)，判断set是否为空
	 * @param url
	 * @param queneKey
	 */
    public boolean isEmptySet(Key key){
    	return scheduler.isEmptySet(redisKey(key));
    }
    /**
	 * redis(key=setKey ，value=set)，判断set是否存在
	 * @param url
	 * @param setKey
	 */
    public boolean isExsitSetKey(Key key){
    	return scheduler.isExsitSetKey(redisKey(key));
	}
    
	public String redisKey(Key key){
		return this.keyPrefix+"_"+key.getCode();
		
	}
	public enum Key{
		 
	    	URLS_WAIT("quene_wait","带爬队列"),
			URLS_RUN("set_run","再爬集合"),
			URLS_REMOVE("set_remove","去重集合"),
			URLS_ERROR("set_error","错误集合"),
			
			PROXY_POOL("proxy_pool","IP代理池");
			
			private String code;
			
			private String name;
			
			Key(String code,String name){
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
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}



	public String getKeyPrefix() {
		return keyPrefix;
	}


	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}


	public ICacheAdmin getScheduler() {
		return scheduler;
	}

	public void setScheduler(ICacheAdmin scheduler) {
		this.scheduler = scheduler;
	}
	
	 
	


}

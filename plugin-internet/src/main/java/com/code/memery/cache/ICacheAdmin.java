package com.code.memery.cache;

import java.util.Set;

public interface ICacheAdmin {

	/**
	 * redis(key=setKey ，value=set)删除set集合中制定url
	 * 
	 * @param url
	 * @param setKey
	 */
	public abstract void removeSet(String setKey, String url);

	/**
	 * redis(key=setKey ，value=set)判断set集合中的某个url是否存在
	 * 
	 * @param url
	 * @param setKey
	 */
	public abstract boolean isDuplicate(String setKey, String url);

	/**
	 * redis(key=setKey ，value=set)弹出list链表中的数据
	 * 
	 * @param url
	 * @param queueKey
	 */
	public abstract String pollQuene(String queueKey);

	/**
	 * redis(key=setKey ，value=list)list中的插入url
	 * 
	 * @param url
	 * @param queueKey
	 */
	public abstract void pushQuene(String queneKey, String url);

	/**
	 * redis(key=setKey ，value=list)set集合插入数据url
	 * 
	 * @param url
	 * @param setKey
	 */
	public abstract void addSet(String setKey, String url);

	/**
	 * redis(key=setKey ，value=set)set集合中插入数据urls
	 * 
	 * @param url
	 * @param setKey
	 */
	public abstract void addSet(String setKey, Set<String> urls);

	/**
	 * redis(key=setKey ，value=list)，判断list是否为空
	 * 
	 * @param url
	 * @param queneKey
	 */
	public abstract boolean isEmptyQuene(String queneKey);

	/**
	 * redis(key=setKey ，value=set)，判断set是否为空
	 * 
	 * @param url
	 * @param queneKey
	 */
	public abstract boolean isEmptySet(String setKey);

	/**
	 * redis(key=setKey ，value=set)，判断set是否存在
	 * 
	 * @param url
	 * @param setKey
	 */
	public boolean isExsitSetKey(String setKey);
}

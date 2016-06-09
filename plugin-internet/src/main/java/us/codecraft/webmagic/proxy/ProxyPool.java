package us.codecraft.webmagic.proxy;

import org.apache.http.HttpHost;

public interface ProxyPool {

	public abstract void addProxy(String[]... httpProxyList);

	public abstract HttpHost getProxy();

	public abstract void returnProxy(HttpHost host, int statusCode);

	public abstract boolean isEnable();
	
	public void setReuseInterval(int reuseInterval);




}
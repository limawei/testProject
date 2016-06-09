package us.codecraft.webmagic.proxy.pool;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpHost;
import org.apache.log4j.Logger;

import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyPool;
import us.codecraft.webmagic.utils.ProxyUtils;

import com.code.common.utils.assertion.Assert;

public class RandomProxyPool implements ProxyPool {

	private static Logger LOG = Logger.getLogger(RandomProxyPool.class);

	private Map<String, Proxy> allProxy = new ConcurrentHashMap<String, Proxy>();

	private Map<Integer,String> proxyIndex = new HashMap<Integer,String>();
	
	private int reviveTime = 2 * 60 * 60 * 1000;// ms

	private boolean isEnable = false;

	private int reuseInterval = 1500;// ms
	
	private List<Integer> usedNums = new LinkedList<Integer>();

	public RandomProxyPool() {
		this(null, true);
	}

	public RandomProxyPool(List<String[]> httpProxyList) {
		this(httpProxyList, true);
	}

	public RandomProxyPool(List<String[]> httpProxyList, boolean isUseLastProxy) {
		if (httpProxyList != null) {
			addProxy(httpProxyList.toArray(new String[httpProxyList.size()][]));
		}
	}

	@Override
	public void addProxy(String[]... httpProxyList) {
		isEnable = true;
		int index = 0;
		for (String[] s : httpProxyList) {
			try {
				if (allProxy.containsKey(s[0])) {
					continue;
				}
				HttpHost item = new HttpHost(InetAddress.getByName(s[0]),
						Integer.valueOf(s[1]));
				/*Proxy p = new Proxy(item);
				p.setFailedNum(0);
                p.setReuseTimeInterval(reuseInterval);
                proxyIndex.put(index, s[0]);
				allProxy.put(s[0], p);*/
				if (ProxyUtils.validateProxy(item)) {
					Proxy p = new Proxy(item);
					p.setFailedNum(0);
	                p.setReuseTimeInterval(reuseInterval);
	                proxyIndex.put(index, s[0]);
					allProxy.put(s[0], p);
				}
			} catch (NumberFormatException e) {
				LOG.error("HttpHost init error:", e);
			} catch (UnknownHostException e) {
				LOG.error("HttpHost init error:", e);
			}
			index++;
		}
		LOG.info("代理池大小:" + allProxy.size());
	}
	
	public int size(){
		return allProxy.size();
	}
	
	

	@Override
	public HttpHost getProxy() {
		Proxy proxy = null;
		int size = allProxy.size();
		if(size == 0){
			throw new NullPointerException("代理池为空，未注册代理IP");
		}
		try {
			while(proxy == null){
				int random = new Random().nextInt(size);
				if(size == usedNums.size()){
					LOG.info("所有代理已使用，全部清理，重新使用");
					usedNums.clear();
				}
				if(usedNums.contains(random)){
					continue;
				}
				usedNums.add(random);
				proxy = allProxy.get(proxyIndex.get(random));
				//LOG.info("获取代理");
			}
			return proxy.getHttpHost();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("获取代理失败", e);
		}

		return null;
	}

	@Override
	public void returnProxy(HttpHost host, int statusCode) {
		Proxy p = allProxy.get(host.getAddress().getHostAddress());
		if (p == null) {
			return;
		}
		switch (statusCode) {
		case Proxy.SUCCESS:
			p.setReuseTimeInterval(reuseInterval);
			p.setFailedNum(0);
			p.setFailedErrorType(new ArrayList<Integer>());
			p.recordResponse();
			p.successNumIncrement(1);
			break;
		case Proxy.ERROR_403:
			// banned,try longer interval
			p.fail(Proxy.ERROR_403);
			p.setReuseTimeInterval(reuseInterval * p.getFailedNum());
			LOG.info(host + " >>>> reuseTimeInterval is >>>> "
					+ p.getReuseTimeInterval() / 1000.0);
			break;
		case Proxy.ERROR_BANNED:
			p.fail(Proxy.ERROR_BANNED);
			p.setReuseTimeInterval(10 * 60 * 1000 * p.getFailedNum());
			LOG.warn("this proxy is banned >>>> " + p.getHttpHost());
			LOG.info(host + " >>>> reuseTimeInterval is >>>> "
					+ p.getReuseTimeInterval() / 1000.0);
			break;
		case Proxy.ERROR_404:
			// p.fail(Proxy.ERROR_404);
			// p.setReuseTimeInterval(reuseInterval * p.getFailedNum());
			break;
		default:
			p.fail(statusCode);
			break;
		}
		if (p.getFailedNum() > 20) {
			p.setReuseTimeInterval(reviveTime);
			LOG.error("remove proxy >>>> " + host + ">>>>" + p.getFailedType());
			return;
		}
		if (p.getFailedNum() > 0 && p.getFailedNum() % 5 == 0) {
			if (!ProxyUtils.validateProxy(host)) {
				p.setReuseTimeInterval(reviveTime);
				LOG.error("删除代理： " + host + ">>>>"
						+ p.getFailedType() + " >>>> remain proxy >>>> ");
				return;
			}
		}

	}

	@Override
	public boolean isEnable() {
		return true;
	}

	@Override
	public void setReuseInterval(int reuseInterval) {
		this.reuseInterval = reuseInterval;
	}

	
	public static void main(String[] args) {
		List<String[]> httpProxyList = new ArrayList<String[]>();
	
		String proxy = "";
		int netRange = 1;
		int netIp = 9;
		String ip = "";
	/*	for(int i=0;i<netRange;i++){
			proxy = "192.168."+i;
			for(int j=0;j<netIp;j++){
				ip = proxy+"."+j+":808";
				System.out.println("生成代理IP:"+ip);
				httpProxyList.add(ip.split(":"));
			}
			
		}*/
		String ip1 = "218.92.220.64:8080";
		/*String ip2 = "192.168.121.121:8080";
		String ip3 = "192.168.11.6:8080";
		String ip4 = "192.168.4.26:808";*/
		httpProxyList.add(ip1.split(":"));
/*		httpProxyList.add(ip2.split(":"));
		httpProxyList.add(ip3.split(":"));*/
		ProxyPool pool = new RandomProxyPool(httpProxyList);
		for(int z = 0 ;z<23;z++){
			HttpHost host = pool.getProxy();
			System.out.println(host.getAddress().getHostAddress());
		}
		
	
	}
}

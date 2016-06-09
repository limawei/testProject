package us.codecraft.webmagic.proxy.pool;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;

import org.apache.http.HttpHost;
import org.apache.log4j.Logger;

import com.code.common.utils.assertion.Assert;

import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyPool;
import us.codecraft.webmagic.utils.ProxyUtils;

public class SimpleProxyPool implements ProxyPool {

	private static Logger LOG = Logger.getLogger(SimpleProxyPool.class);

	private Map<String, Proxy> allProxy = new ConcurrentHashMap<String, Proxy>();

	private BlockingQueue<Proxy> proxyQueue = new DelayQueue<Proxy>();

	private int reviveTime = 2 * 60 * 60 * 1000;// ms

	private boolean isEnable = false;

	private int reuseInterval = 1500;// ms

	public SimpleProxyPool() {
		this(null, true);
	}

	public SimpleProxyPool(List<String[]> httpProxyList) {
		this(httpProxyList, true);
	}

	public SimpleProxyPool(List<String[]> httpProxyList, boolean isUseLastProxy) {
		if (httpProxyList != null) {
			addProxy(httpProxyList.toArray(new String[httpProxyList.size()][]));
		}
	}

	@Override
	public void addProxy(String[]... httpProxyList) {
		isEnable = true;
		for (String[] s : httpProxyList) {
			try {
				if (allProxy.containsKey(s[0])) {
					continue;
				}
				HttpHost item = new HttpHost(InetAddress.getByName(s[0]),
						Integer.valueOf(s[1]));
				if (ProxyUtils.validateProxy(item)) {
					Proxy p = new Proxy(item);
					p.setFailedNum(0);
	                p.setReuseTimeInterval(reuseInterval);
	                proxyQueue.add(p);
					allProxy.put(s[0], p);
				}
			} catch (NumberFormatException e) {
				LOG.error("HttpHost init error:", e);
			} catch (UnknownHostException e) {
				LOG.error("HttpHost init error:", e);
			}
		}
		LOG.info("代理池大小:" + allProxy.size());
	}
	
	
	public void addProxy(String FLAG,String... httpProxyList) {
		Assert.notNull(FLAG);
		isEnable = true;
		for (String httpProxy : httpProxyList) {
			try {
				String[] s = httpProxy.split(FLAG);
				if (allProxy.containsKey(s[0])) {
					continue;
				}
				HttpHost item = new HttpHost(InetAddress.getByName(s[0]),
						Integer.valueOf(s[1]));
				if (ProxyUtils.validateProxy(item)) {
					Proxy p = new Proxy(item);
					allProxy.put(s[0], p);
				}
			} catch (NumberFormatException e) {
				LOG.error("HttpHost init error:", e);
			} catch (UnknownHostException e) {
				LOG.error("HttpHost init error:", e);
			}
		}
		LOG.info("代理池大小:" + allProxy.size());
	}


	@Override
	public HttpHost getProxy() {
		Proxy proxy = null;
		try {
			Long time = System.currentTimeMillis();
			proxy = proxyQueue.take();
			double costTime = (System.currentTimeMillis() - time) / 1000.0;
			if (costTime > reuseInterval) {
				LOG.info("获取代理时间： " + costTime);
			}
			Proxy p = allProxy.get(proxy.getHttpHost().getAddress()
					.getHostAddress());
			p.setLastBorrowTime(System.currentTimeMillis());
			p.borrowNumIncrement(1);
		} catch (InterruptedException e) {
			LOG.error("获取代理失败", e);
		}
		if (proxy == null) {
			throw new NoSuchElementException();
		}
		return proxy.getHttpHost();
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
			LOG.error("remove proxy >>>> " + host + ">>>>" + p.getFailedType()
					+ " >>>> remain proxy >>>> " + proxyQueue.size());
			return;
		}
		if (p.getFailedNum() > 0 && p.getFailedNum() % 5 == 0) {
			if (!ProxyUtils.validateProxy(host)) {
				p.setReuseTimeInterval(reviveTime);
				LOG.error("remove proxy >>>> " + host + ">>>>"
						+ p.getFailedType() + " >>>> remain proxy >>>> "
						+ proxyQueue.size());
				return;
			}
		}
		try {
			proxyQueue.put(p);
		} catch (InterruptedException e) {
			LOG.warn("proxyQueue return proxy error", e);
		}
	}

	@Override
	public boolean isEnable() {
		return isEnable;
	}

	@Override
	public void setReuseInterval(int reuseInterval) {
		this.reuseInterval = reuseInterval;
	}

	public static void main(String[] args) {
		ProxyPool pool = new SimpleProxyPool();
		List<String[]> httpProxyList = new ArrayList<String[]>();
		
		String[] proxy = new String[2];
		proxy[1] = "127.0.0.1";
		proxy[2] = "2132";
		httpProxyList.add(proxy);
		
		String host = "127.0.0.1:808";
		host.split(",");
	
	}
}

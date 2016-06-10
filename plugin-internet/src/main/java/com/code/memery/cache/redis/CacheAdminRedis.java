package com.code.memery.cache.redis;

import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.code.memery.cache.ICacheAdmin;
/**
*缓存Redis管理
*/
public class CacheAdminRedis implements ICacheAdmin{

	private static Logger _LOG = Logger.getLogger(CacheAdminRedis.class);
	
    private JedisPool pool;
    
    private final ReadLock _readLock;
	
	private final WriteLock _writeLock;
	
    public CacheAdminRedis(String host) {
    	
        this(new JedisPool(new JedisPoolConfig(), host));
        
    }

    public CacheAdminRedis(JedisPool pool) {
        this.pool = pool;
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		this._readLock = lock.readLock();
	    this._writeLock = lock.writeLock();
        initTest();
        
    }

    private void initTest() {
    	Jedis jedis = null;
        try {
			jedis = pool.getResource();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			_LOG.error("Redis启动异常,请确认Redis是否启动："+e.toString());
			throw new JedisConnectionException("Redis启动异常,请确认Redis是否启动："+e.toString());
		}finally{
			if(jedis!=null){
				pool.returnResource(jedis);
			}
			 
		}
	}

	/**
	 * redis(key=setKey ，value=set)删除set集合中制定url
	 * @param url
	 * @param setKey
	 */
    @Override
	public void removeSet(String setKey,String url){
    	
    	 Jedis jedis = pool.getResource();
         try {
             jedis.srem(setKey, url);
         } finally {
             pool.returnResource(jedis);
         }
    }
    
    /**
	 * redis(key=setKey ，value=set)判断set集合中的某个url是否存在
	 * @param url
	 * @param setKey
	 */
    @Override
	public boolean isDuplicate( String setKey,String url) {
        Jedis jedis = pool.getResource();
        try {
        	
            boolean isDuplicate = jedis.sismember(setKey, url);
            if (!isDuplicate) {
                jedis.sadd(setKey, url);
            }
            return isDuplicate;
        } finally {
            pool.returnResource(jedis);
        }

    }
    
    public boolean isExsitSetKey( String setKey) {
        Jedis jedis = pool.getResource();
        try {
           return jedis.exists(setKey);
        } finally {
            pool.returnResource(jedis);
        }

    }
 
    public String getSetKey(String domain,String type){
    	return domain+"_"+type;
    }
    
    



    /**
	 * redis(key=setKey ，value=set)弹出list链表中的数据
	 * @param url
	 * @param queueKey
	 */
    @Override
	public synchronized String pollQuene(String queueKey) {
        Jedis jedis = pool.getResource();
        try {
        	_readLock.lock();
            return jedis.lpop(queueKey);
        } finally {
        	_readLock.unlock();
            pool.returnResource(jedis);
        }
    }
 
    /**
	 * redis(key=setKey ，value=list)list中的插入url
	 * @param url
	 * @param queueKey
	 */
    @Override
	public void pushQuene(String queueKey,String url) {
        Jedis jedis = pool.getResource();
        try {
        	_writeLock.lock();
            jedis.rpush(queueKey, url);
        } finally {
        	_writeLock.unlock();
            pool.returnResource(jedis);
        }
    }

    /**
	 *  redis(key=setKey ，value=list)set集合插入数据url
	 * @param url
	 * @param setKey
	 */
    public void addSet(String setKey,String url){
    	Jedis jedis = pool.getResource();
        try {
            jedis.sadd(setKey, url);
        } finally {
            pool.returnResource(jedis);
        }
    }

    /**
	 * redis(key=setKey ，value=set)set集合中插入数据urls
	 * @param url
	 * @param setKey
	 */
    public void addSet(String setKey,Set<String> urls){
    	Jedis jedis = pool.getResource();
        try {
        	for(String url:urls){
        		jedis.sadd(setKey, url);
        	}
        } finally {
            pool.returnResource(jedis);
        }
    }

    /**
   	 * redis(key=setKey ，value=set)，判断set是否为空
   	 * @param url
   	 * @param queneKey
   	 */
    public boolean isEmptySet(String setKey){
    	Jedis jedis = pool.getResource();
        try {
        	long num = jedis.scard(setKey);
        	if(num <= 0){
        		return true;
        	}
        	return false;
        	
        } finally {
            pool.returnResource(jedis);
        }
    }
    
    /**
	 * redis(key=setKey ，value=list)，判断list是否为空
	 * @param url
	 * @param queneKey
	 */
    public boolean isEmptyQuene(String queneKey){
    	Jedis jedis = pool.getResource();
        try {
        	/*List<String> sets = jedis.mget(setKey);
        	if(sets == null || sets.size() == 0){
        		return true;
        	}*/
        	long num = jedis.llen(queneKey);
        	if(num <= 0){
        		return true;
        	}
        	return false;
        	
        } finally {
            pool.returnResource(jedis);
        }
    }
    public static void main(String[] args) {
    	String host = "127.0.0.1";
    	String setKey = "myset1";
    	String url = "http://www.sina.com.cn/";
    	
    	/*redis.addSet(setKey, url);
    	System.out.println("新增成功");
    	System.out.println("结果:"+redis.isEmptySet(setKey));
    	//System.out.println("结果:"+redis.poll(task));
    	System.out.println(redis.isExsitSetKey(setKey));
    	System.out.println(redis.isDuplicate( setKey,url));*/

    	System.out.println("a".getBytes().length);

	}
    
}

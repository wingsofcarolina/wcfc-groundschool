package org.wingsofcarolina.gs.email;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerificationCodeCache {
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(VerificationCodeCache.class);

	Random random = new Random();
	private Map<String, Entry> cache = new HashMap<String, Entry>();

	private static VerificationCodeCache instance;
	
	private VerificationCodeCache() {}
	
	public static VerificationCodeCache instance() {
		if (instance == null) {
			instance = new VerificationCodeCache();
		}
		return instance;
	}
	
	public Integer getVerificationCode(String email) {
		Integer code = random.nextInt(999999 - 100000) + 100000;
		cache.put(email, new Entry(code));
		
		return code;
	}
	
	public Boolean verifyCode(String email, Integer code) {
		Entry query = cache.remove(email);
		if (query != null && code.equals(query.getCode())) {
			return true;
		} else {
			return false;
		}
	}
	
	public void cleanCache() {
		Date now = new Date();
		
		 // Get the iterator over the HashMap
        Iterator<Map.Entry<String, Entry> >
            iterator = cache.entrySet().iterator();
  
        // Iterate over the HashMap
        while (iterator.hasNext()) {
  
            // Get the entry at this iteration
            Map.Entry<String, Entry> mapEntry = iterator.next();
  
            // Check if this entry has expired
            Date expire = mapEntry.getValue().getExpire();
            if (now.compareTo(expire) > 0) {
                // Remove this entry from HashMap
            	LOG.info("Removed {} from verification cache.", mapEntry.getKey());
                iterator.remove();
            }
        }
	}
	
	class Entry {
		private Integer code;
		private Date expire;
		
		public Entry(Integer code) {
			this.code = code;
		    Calendar calendar = Calendar.getInstance();
		    calendar.setTime(new Date());
		    calendar.add(Calendar.HOUR_OF_DAY, 3);
		    this.expire = calendar.getTime();
		}

		public Integer getCode() {
			return code;
		}

		public Date getExpire() {
			return expire;
		}
	}
}

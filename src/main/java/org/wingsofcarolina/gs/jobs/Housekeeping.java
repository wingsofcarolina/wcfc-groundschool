package org.wingsofcarolina.gs.jobs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.knowm.sundial.Job;
import org.knowm.sundial.annotations.CronTrigger;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wingsofcarolina.gs.email.VerificationCodeCache;

//@CronTrigger(cron = "0/100 * * * * ?")  // Fire every minute, for testing
@CronTrigger(cron = "0 0 * * * ?")  // Fire at the top of every hour
public class Housekeeping extends Job {
	private static final Logger LOG = LoggerFactory.getLogger(Housekeeping.class);

	SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
	
	@Override
	public void doRun() throws JobInterruptException {
		LOG.info("Housekeeping triggered : {}", new Date().toString());
		
		// Expunge all ancient/expired verification codes
		VerificationCodeCache.instance().cleanCache();
		
		LOG.info("Housekeeping completed.");
	}
}

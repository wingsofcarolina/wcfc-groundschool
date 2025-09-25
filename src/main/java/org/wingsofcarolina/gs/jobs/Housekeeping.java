package org.wingsofcarolina.gs.jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wingsofcarolina.gs.domain.VerificationCode;

/**
 * Legacy housekeeping class - now replaced by HousekeepingService
 * This class is kept for reference but is no longer used.
 * The actual housekeeping is now performed by HousekeepingService
 * which runs opportunistically when there is activity.
 */
@Deprecated
public class Housekeeping {

  private static final Logger LOG = LoggerFactory.getLogger(Housekeeping.class);

  SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

  /**
   * Legacy method - now replaced by HousekeepingService.performHousekeepingIfNeeded()
   */
  @Deprecated
  public void doRun() {
    LOG.debug("Housekeeping triggered : {}", new Date().toString());

    // Expunge all ancient/expired verification codes
    VerificationCode.cleanCache();

    LOG.debug("Housekeeping completed.");
  }
}

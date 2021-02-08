######

# Operational mode, DEV or PROD
mode: ${MODE!'PROD'}

# Slack target channel (default is Planez #notify channel)
slackTarget: ${SLACK!'TAYTPJJF5/B01LH95DHRT/lo9pphg7Rs80fmCHatAGmPMR'}

# Configure ports used by DropWizard
server:
    type: simple
    connector:
        type: http
        port: ${SERVER_PORT!'9300'}
    applicationContextPath: /
    adminContextPath: /admin
    requestLog:
        appenders:
          - type: file
            currentLogFilename: log/planez-server-http.log
            threshold: ALL
            archive: true
            archivedLogFilenamePattern: log/planez-server-%i-%d-http.log
            maxFileSize: 500MB
            archivedFileCount: 5
            timeZone: UTC

# SLF4j Logging settings.
logging:
  # The default level of all loggers. Can be OFF, ERROR, WARN, INFO, DEBUG, TRACE, or ALL.
  level: ${DEFAULT_LOGLEVEL!'INFO'}

  # Logger-specific levels.
  loggers:
    "org.wingsofcarolina.gs": ${LOGLEVEL!'INFO'}
    
  appenders:
      - type: console
        threshold: ALL
        timeZone: UTC
        target: stdout
      - type: file
        currentLogFilename: ./log/planez-server.log
        threshold: ALL
        archive: true
        archivedLogFilenamePattern: ./log/planez-server-%i-%d.log
        maxFileSize: 500MB
        archivedFileCount: 5
        timeZone: UTC

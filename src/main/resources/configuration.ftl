######

gsroot: ${GSROOT!'/gs_root'}

# Location of the MongoDB server
# If on a Docker network, just give the name of the MongoDB
# container. Otherwise give the IP:PORT for the MongoDB process.
mongodb: ${MONGODB!'mongodb'}

# Operational mode, DEV or PROD
mode: ${MODE!'PROD'}

# Authentication, ON or OFF
auth: ${AUTH!'ON'}
mockAdmin: ${MOCKADMIN!'OFF'}
mockUser: ${MOCKUSER!"NONE"}

# Slack channels
slackNotify: ${SLACK!'REDACTED/REDACTED'}     # Targets #notification
slackContact: ${SLACK!'REDACTED/REDACTED'}    # Targets #contact

# Configure the Sundial job management system    
sundial:
  thread-pool-size: 10
  shutdown-on-unload: true
  start-delay-seconds: 0
  start-scheduler-on-load: true
  global-lock-on-load: false
  annotated-jobs-package-name: org.wingsofcarolina.gs.jobs
  tasks: [startjob, stopjob]
  
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
            currentLogFilename: log/server-http.log
            threshold: ALL
            archive: true
            archivedLogFilenamePattern: log/server-%i-%d-http.log
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
        currentLogFilename: ./log/server.log
        threshold: ALL
        archive: true
        archivedLogFilenamePattern: ./log/server-%i-%d.log
        maxFileSize: 500MB
        archivedFileCount: 5
        timeZone: UTC

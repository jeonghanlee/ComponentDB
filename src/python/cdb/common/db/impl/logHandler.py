#!/usr/bin/env python

"""
Copyright (c) UChicago Argonne, LLC. All rights reserved.
See LICENSE file.
"""
from cdb.common.exceptions.invalidArgument import InvalidArgument
from cdb.common.exceptions.invalidSession import InvalidSession
from sqlalchemy import and_
from sqlalchemy.orm.exc import NoResultFound

from datetime import datetime

from cdb.common.exceptions.objectNotFound import ObjectNotFound
from cdb.common.db.entities.attachment import Attachment
from cdb.common.db.entities.logAttachment import LogAttachment
from cdb.common.db.entities.log import Log
from cdb.common.db.entities.logTopic import LogTopic
from cdb.common.db.entities.logLevel import LogLevel
from cdb.common.db.entities.systemLog import SystemLog
from cdb.common.db.entities.itemElementLog import ItemElementLog
from cdb.common.db.impl.cdbDbEntityHandler import CdbDbEntityHandler
from cdb.common.db.impl.userInfoHandler import UserInfoHandler


class LogHandler(CdbDbEntityHandler):
    def __init__(self):
        CdbDbEntityHandler.__init__(self)
        self.userInfoHandler = UserInfoHandler()

    def getLogs(self, session):
        dbLogs = session.query(Log).all()
        return dbLogs

    def findLogById(self, session, id):
        return self._findDbObjById(session, Log, id)

    def getAttachments(self, session):
        dbAttachments = session.query(Attachment).all()
        return dbAttachments

    def getLogLevelByName(self, session, logLevelName):
        return self._findDbObjByName(session, LogLevel, logLevelName)

    def getLogAttachments(self, session):
        dbLogAttachments = session.query(LogAttachment).all()
        return dbLogAttachments

    def addLog(self, session, text, enteredByUserId, effectiveFromDateTime, effectiveToDateTime, logTopicName,
               enteredOnDateTime=None):
        if not enteredOnDateTime:
            enteredOnDateTime = datetime.now()

        dbLog = Log(text=text)
        dbLog.entered_on_date_time = enteredOnDateTime
        dbLog.entered_by_user_id = enteredByUserId
        if effectiveToDateTime:
            dbLog.effective_from_date_time = effectiveFromDateTime
        if effectiveToDateTime:
            dbLog.effective_to_date_time = effectiveToDateTime

        if logTopicName:
            dbLogTopic = self.findLogTopicByName(session, logTopicName)
            dbLog.logTopic = dbLogTopic

        session.add(dbLog)
        session.flush()

        self.logger.debug('Added Log id %s' % dbLog.id)
        return dbLog

    def updateLog(self, session, logId, enteredByUserId, text=None, effectiveFromDateTime=None,
                  effectiveToDateTime=None, logTopicName=None):
        if text is None and effectiveFromDateTime is None and effectiveToDateTime is None and logTopicName is None:
            raise InvalidArgument("No argument was provided to update log %s" % logId)

        dbLogEntry = self.findLogById(session, logId)
        self.verifyUserCreatedLogEntry(session, enteredByUserId, dbLogObject=dbLogEntry)

        if text is not None:
            dbLogEntry.text = text

        if effectiveFromDateTime is not None:
            dbLogEntry.effective_from_date_time = effectiveFromDateTime

        if effectiveToDateTime is not None:
            dbLogEntry.effective_to_date_time = effectiveToDateTime

        if logTopicName is not None:
            dbLogTopic = self.findLogTopicByName(session, logTopicName)
            dbLogEntry.logTopic = dbLogTopic

        session.add(dbLogEntry)
        session.flush()

        self.logger.debug('Updated Log id %s' % logId)
        return dbLogEntry

    def deleteLog(self, session, logId, userId):
        dbLog = self.findLogById(session, logId)
        self.verifyUserCreatedLogEntry(session, userId, dbLogObject=dbLog)

        itemElementLogList = dbLog.itemElementLogList
        for itemElementLog in itemElementLogList:
            session.delete(itemElementLog)

        session.delete(dbLog)

        session.flush()

        self.logger.debug("Removed log %s" % logId)

    def addSystemLog(self, session, logEntry, logLevelName):
        logLevel = self.getLogLevelByName(session, logLevelName)

        dbSystemLog = SystemLog()
        dbSystemLog.log = logEntry
        dbSystemLog.logLevel = logLevel

        session.add(dbSystemLog)
        session.flush()

        self.logger.debug('Added System Log of log level %s to log id %s' % (logLevelName, logEntry.id))
        return dbSystemLog

    def getLogEntriesForItemElementId(self, session, itemElementId, logLevelName=None):
        entityDisplayName = self._getEntityDisplayName(Log)

        try:
            query = session.query(Log).join(ItemElementLog)

            if logLevelName is not None:
                query = query.join(SystemLog).join(LogLevel).filter(LogLevel.name == logLevelName)

            query = query.filter(ItemElementLog.item_element_id == itemElementId)

            dbLogs = query.all()
            return dbLogs
        except NoResultFound, ex:
            raise ObjectNotFound('No %s with item element id: %s found.'
                                 % (entityDisplayName, itemElementId))

    def addLogTopic(self, session, logTopicName, description):
        return self._addSimpleNameDescriptionTable(session, LogTopic, logTopicName, description)

    def getLogTopics(self, session):
        return self._getAllDbObjects(session, LogTopic)

    def findLogTopicByName(self, session, logTopicName):
        return self._findDbObjByName(session, LogTopic, logTopicName)

    def addAttachment(self, session, attachmentName, tag, description):
        self._prepareAddUniqueNameObj(session, Attachment, attachmentName)

        dbAttachment = Attachment(name=attachmentName)
        if description:
            dbAttachment.description = description
        if tag:
            dbAttachment.tag = tag

        session.add(dbAttachment)
        session.flush()

        self.logger.debug('Inserted attachment id %s' % dbAttachment.id)

        return dbAttachment

    def verifyUserCreatedLogEntry(self, session, userId, logId=None, dbLogObject=None):
        if logId is None and dbLogObject is None:
            raise InvalidArgument("At least log id or db log object must be provided.")
        if dbLogObject is None:
            dbLogObject = self.findLogById(session, logId)
        else:
            logId = dbLogObject.id

        if userId == dbLogObject.entered_by_user_id:
            return True

        raise InvalidSession("The log entry %s was created by another user." % logId)

    def addLogAttachment(self, session, logId, attachmentName, attachmentTag, attachmentDescription, userAddingId):
        dbLog = self.findLogById(session, logId)
        self.verifyUserCreatedLogEntry(session, userAddingId, dbLogObject=dbLog)
        dbAttachment = self.addAttachment(session, attachmentName, attachmentTag, attachmentDescription)

        dbLogAttachment = LogAttachment()
        dbLogAttachment.log = dbLog
        dbLogAttachment.attachment = dbAttachment

        session.add(dbLogAttachment)
        session.flush()

        self.logger.debug('Inserted log attachment for log id %s' % logId)

        return dbLogAttachment

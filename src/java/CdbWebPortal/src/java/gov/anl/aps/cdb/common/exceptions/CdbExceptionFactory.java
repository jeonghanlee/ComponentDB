/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/common/exceptions/CdbExceptionFactory.java $
 *   $Date: 2015-05-04 13:48:28 -0500 (Mon, 04 May 2015) $
 *   $Revision: 616 $
 *   $Author: sveseli $
 */
package gov.anl.aps.cdb.common.exceptions;

import gov.anl.aps.cdb.common.constants.CdbStatus;
import org.apache.log4j.Logger;

/**
 * CDB exception factory class.
 *
 */
public class CdbExceptionFactory {

    private static final Logger logger
            = Logger.getLogger(CdbExceptionFactory.class.getName());

    /**
     * Generate CDB exception.
     *
     * @param type exception type
     * @param code exception code
     * @param error exception error message
     * @return generated CDB exception
     */
    public static CdbException generateCdbException(String type, int code, String error) {
        CdbException exc = new CdbException();
        try {
            String fullType = "gov.anl.aps.cdb.common.exceptions." + type;
            // Having trouble getting code below to work in all cases, so
            // use code for now.
            // exc = (CdbException) Class.forName(fullType).newInstance();
            switch (code) {

                case CdbStatus.CDB_AUTHORIZATION_ERROR:
                    exc = new AuthorizationError();
                    break;
                case CdbStatus.CDB_COMMUNICATION_ERROR:
                    exc = new CommunicationError();
                    break;
                case CdbStatus.CDB_INTERNAL_ERROR:
                    exc = new InternalError();
                    break;
                case CdbStatus.CDB_INVALID_ARGUMENT:
                    exc = new InvalidArgument();
                    break;
                case CdbStatus.CDB_INVALID_SESSION:
                    exc = new InvalidSession();
                    break;
                case CdbStatus.CDB_OBJECT_ALREADY_EXISTS:
                    exc = new ObjectAlreadyExists();
                    break;
                case CdbStatus.CDB_OBJECT_NOT_FOUND:
                    exc = new ObjectNotFound();
                    break;
                default:
                    exc = (CdbException) Class.forName(fullType).newInstance();
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            String err = "Cannot generate exception of type " + type + ": " + ex;
            logger.error(err);
        }
        exc.setErrorMessage(error);
        return exc;
    }

    /**
     * Throw CDB exception.
     *
     * @param type exception type
     * @param code exception code
     * @param error exception error message
     * @throws CdbException generated CDB exception whenever this method is
     * called
     */
    public static void throwCdbException(String type, int code, String error) throws CdbException {
        CdbException exc = generateCdbException(type, code, error);
        throw exc;
    }
}

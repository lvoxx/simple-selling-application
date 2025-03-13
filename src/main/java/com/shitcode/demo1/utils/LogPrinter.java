package com.shitcode.demo1.utils;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogPrinter {
        private static final Logger logger = LoggerFactory.getLogger(LogPrinter.class);
        private static final String PREFIX = "[%s] - ";

        public static abstract class Flag {
                public static final String MISSED_CONTROLLER_FLAG = "MISSED_CONTROLLER";
                public static final String CONTROLLER_FLAG = "CONTROLLER";
                public static final String SERVICE_FLAG = "SERVICE";
                public static final String REPOSITORY_FLAG = "REPOSITORY";
                public static final String ASPECT_FLAG = "ASPECT";
                public static final String UTILS_FLAG = "UTILS";
                public static final String SCHEDULER_FLAG = "SCHEDULER";
                public static final String START_UP = "START-UP";
        }

        private static abstract class Log {
                private static final String MISSED_CONTROLLER_LOG = "Handling request via %s.%s() at %s. %s.";
                private static final String CONTROLLER_LOG = "Handling request %s via %s.%s() at %s. %s.";
                private static final String SERVICE_LOG = "Executing %s.%s() at %s. %s.";
                private static final String REPOSITORY_LOG = "Accessing %s.%s() at %s. %s";
                private static final String ASPECT_LOG = "Triggering %s.%s() at %s. %s";
                private static final String UTILS_LOG = "Utilizing %s.%s() at %s. %s";
                private static final String SCHEDULER_LOG = "Utilizing %s.%s() at %s. %s";
        }

        public static enum Type {
                INFO, DEBUG, WARM, ERROR, SCHEDULER
        }

        public static void printControllerLog(Type type, String requestPath, String className, String methodName,
                        String time,
                        String extraMessage) {
                if (requestPath != null) {
                        printLog(type, Flag.CONTROLLER_FLAG,
                                        String.format(Log.CONTROLLER_LOG, requestPath, className,
                                                        methodName, time, extraMessage));
                        return;
                }
                printLog(Type.WARM, Flag.MISSED_CONTROLLER_FLAG,
                                String.format(Log.MISSED_CONTROLLER_LOG, className,
                                                methodName, time, extraMessage));
        }

        public static void printServiceLog(Type type, String className, String methodName, String time,
                        String extraMessage) {
                printLog(type, Flag.SERVICE_FLAG,
                                String.format(Log.SERVICE_LOG, className,
                                                methodName, time, extraMessage));
        }

        public static void printRepositoryLog(Type type, String className, String methodName, String time,
                        String extraMessage) {
                printLog(type, Flag.REPOSITORY_FLAG,
                                String.format(Log.REPOSITORY_LOG, className,
                                                methodName, time, extraMessage));
        }

        public static void printAspectLog(Type type, String className, String methodName, String time,
                        String extraMessage) {
                printLog(type, Flag.ASPECT_FLAG,
                                String.format(Log.ASPECT_LOG, className,
                                                methodName, time, extraMessage));
        }

        public static void printUtilsLog(Type type, String className, String methodName, String time,
                        String extraMessage) {
                printLog(type, Flag.UTILS_FLAG,
                                String.format(Log.UTILS_LOG, className,
                                                methodName, time, extraMessage));
        }

        public static void printSchedulerLog(Type type, String className, String methodName, String time,
                        String extraMessage) {
                printLog(type, Flag.SCHEDULER_FLAG,
                                String.format(Log.SCHEDULER_LOG, className,
                                                methodName, time, extraMessage));
        }

        public static void printLog(Type type, String flag, String message) {
                flag = Optional.ofNullable(flag).orElse("INFO");
                type = Optional.ofNullable(type).orElse(LogPrinter.Type.INFO);

                String logMessage = new StringBuilder()
                                .append(String.format(PREFIX, flag.toUpperCase()))
                                .append(message)
                                .toString();
                switch (type) {
                        case LogPrinter.Type.ERROR:
                                logger.error(logMessage);
                                return;
                        case LogPrinter.Type.DEBUG:
                                logger.debug(logMessage);
                                return;
                        case LogPrinter.Type.WARM:
                                logger.warn(logMessage);
                                return;
                        case LogPrinter.Type.INFO:
                        case LogPrinter.Type.SCHEDULER:
                        default:
                                logger.info(logMessage);
                }
        }
}

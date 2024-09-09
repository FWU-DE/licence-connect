import { LoggingLevel } from './logging-level';

export type LoggerConfig = {
  loggingLevel: LoggingLevel;
  loggingPath: string;
  loggingFile: string;
};

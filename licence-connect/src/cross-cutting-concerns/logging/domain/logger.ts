export const loggingLevel = [
  'trace',
  'debug',
  'info',
  'warn',
  'error',
  'fatal',
];
export type LoggingLevel = (typeof loggingLevel)[number];

export interface LCLogger {
  debug(debugOutput: string): void;
  info(infoOutput: string): void;
  warn(warnOutput: string): void;
  error(errorOutput: string): void;
}

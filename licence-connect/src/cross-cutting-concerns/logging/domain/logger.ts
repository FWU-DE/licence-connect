export const LOGGER_TOKEN = 'LCLogger';

export interface LCLogger {
  debug(debugOutput: string): void;
  info(infoOutput: string): void;
  warn(warnOutput: string): void;
  error(errorOutput: string): void;
}

export const loggingLevel = [
  'trace',
  'debug',
  'info',
  'warn',
  'error',
  'fatal',
];
export type LoggingLevel = (typeof loggingLevel)[number];
